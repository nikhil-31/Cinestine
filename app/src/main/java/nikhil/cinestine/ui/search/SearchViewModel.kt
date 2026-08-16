package nikhil.cinestine.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.Movie
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
    val movies: List<Movie> = emptyList(),
    val favouriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false
) {
    val isIdle: Boolean get() = query.isBlank() && !isLoading && error == null
    val isEmpty: Boolean get() = query.isNotBlank() && movies.isEmpty() && !isLoading && error == null
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val listState = MutableStateFlow(SearchUiState())
    private var loadJob: Job? = null

    val currentQuery: String get() = query.value

    val uiState: StateFlow<SearchUiState> = combine(
        listState,
        repository.observeFavouriteIds()
    ) { list, ids ->
        list.copy(favouriteIds = ids)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    init {
        viewModelScope.launch {
            query
                .map { it.trim() }
                .debounce(QUERY_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { value ->
                    if (value.isBlank()) {
                        loadJob?.cancel()
                        listState.value = SearchUiState()
                    } else {
                        loadPage(query = value, page = 1, reset = true)
                    }
                }
        }
    }

    fun submitQuery(value: String) {
        query.value = value
    }

    fun clear() {
        query.value = ""
    }

    fun loadNextPage() {
        val state = listState.value
        if (state.query.isBlank() || state.isLoading || state.endReached) return
        loadPage(query = state.query, page = state.page + 1, reset = false)
    }

    fun toggleFavourite(movie: Movie) {
        viewModelScope.launch {
            val currentlyFavourite = uiState.value.favouriteIds.contains(movie.id)
            repository.toggleFavourite(movie, currentlyFavourite)
        }
    }

    private fun loadPage(query: String, page: Int, reset: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            listState.update {
                it.copy(
                    query = query,
                    isLoading = true,
                    error = null,
                    movies = if (reset) emptyList() else it.movies
                )
            }
            runCatching { repository.search(query, page) }
                .onSuccess { movies ->
                    listState.update { current ->
                        val combined = if (reset) movies else current.movies + movies
                        current.copy(
                            query = query,
                            movies = combined,
                            isLoading = false,
                            page = page,
                            endReached = movies.isEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    listState.update {
                        it.copy(
                            query = query,
                            isLoading = false,
                            error = error.message ?: "Unable to search movies"
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
