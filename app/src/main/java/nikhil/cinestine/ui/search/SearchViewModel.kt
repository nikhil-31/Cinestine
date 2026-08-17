package nikhil.cinestine.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.SearchHit
import nikhil.cinestine.model.SearchScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class SearchUiState(
    val query: String = "",
    val hits: List<SearchHit> = emptyList(),
    val favouriteKeys: Set<String> = emptySet(),
    val scope: SearchScope = SearchScope.MOVIE,
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false
) {
    val isIdle: Boolean get() = query.isBlank() && !isLoading && error == null
    val isEmpty: Boolean get() = query.isNotBlank() && hits.isEmpty() && !isLoading && error == null
    val showSave: Boolean get() = scope == SearchScope.MOVIE || scope == SearchScope.TV
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val scope = MutableStateFlow(SearchScope.MOVIE)
    private val listState = MutableStateFlow(SearchUiState())
    private var loadJob: Job? = null

    val currentQuery: String get() = query.value
    val currentScope: SearchScope get() = scope.value

    val uiState: StateFlow<SearchUiState> = combine(
        listState,
        repository.observeFavouriteKeys()
    ) { list, keys ->
        list.copy(favouriteKeys = keys)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    init {
        viewModelScope.launch {
            combine(
                query.map { it.trim() }.debounce(QUERY_DEBOUNCE_MS).distinctUntilChanged(),
                scope
            ) { value, type -> value to type }
                .collect { (value, type) ->
                    if (value.isBlank()) {
                        loadJob?.cancel()
                        listState.value = SearchUiState(scope = type)
                    } else {
                        loadPage(query = value, page = 1, reset = true, type = type)
                    }
                }
        }
    }

    fun submitQuery(value: String) {
        query.value = value
    }

    fun setScope(type: SearchScope) {
        scope.value = type
    }

    fun setMediaType(type: MediaType?) {
        setScope(if (type == MediaType.TV) SearchScope.TV else SearchScope.MOVIE)
    }

    fun clear() {
        query.value = ""
    }

    fun loadNextPage() {
        val state = listState.value
        if (state.query.isBlank() || state.isLoading || state.endReached) return
        loadPage(query = state.query, page = state.page + 1, reset = false, type = scope.value)
    }

    fun toggleFavourite(movie: Movie) {
        viewModelScope.launch {
            val currentlyFavourite = uiState.value.favouriteKeys.contains(movie.favouriteKey)
            repository.toggleFavourite(movie, currentlyFavourite)
        }
    }

    private fun loadPage(query: String, page: Int, reset: Boolean, type: SearchScope) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            listState.update {
                it.copy(
                    query = query,
                    scope = type,
                    isLoading = true,
                    error = null,
                    hits = if (reset) emptyList() else it.hits
                )
            }
            runCatching { repository.search(query, page, type) }
                .onSuccess { hits ->
                    listState.update { current ->
                        val combined = if (reset) hits else current.hits + hits
                        current.copy(
                            query = query,
                            scope = type,
                            hits = combined,
                            isLoading = false,
                            page = page,
                            endReached = hits.isEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    listState.update {
                        it.copy(
                            query = query,
                            scope = type,
                            isLoading = false,
                            error = error.message ?: "Unable to search titles"
                        )
                    }
                }
        }
    }

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(repository) as T
        }
    }

    private companion object {
        const val QUERY_DEBOUNCE_MS = 300L
    }
}
