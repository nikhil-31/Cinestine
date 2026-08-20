package nikhil.cinestine.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class DiscoverResultsUiState(
    val movies: List<Movie> = emptyList(),
    val favouriteKeys: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false
)

class DiscoverResultsViewModel(
    private val repository: MovieRepository,
    private val mediaType: MediaType,
    private val filter: DiscoverFilter,
    private val category: MovieCategory? = null
) : ViewModel() {

    private val listState = MutableStateFlow(DiscoverResultsUiState())
    private var loadJob: Job? = null

    val uiState: StateFlow<DiscoverResultsUiState> = combine(
        listState,
        repository.observeFavouriteKeys()
    ) { list, keys ->
        list.copy(favouriteKeys = keys)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiscoverResultsUiState())

    init {
        loadPage(page = 1, reset = true)
    }

    fun refresh() {
        loadPage(page = 1, reset = true)
    }

    fun loadNextPage() {
        val state = listState.value
        if (state.isLoading || state.endReached) return
        loadPage(page = state.page + 1, reset = false)
    }

    fun toggleFavourite(movie: Movie) {
        viewModelScope.launch {
            val currentlyFavourite = uiState.value.favouriteKeys.contains(movie.favouriteKey)
            repository.toggleFavourite(movie, currentlyFavourite)
        }
    }

    private fun loadPage(page: Int, reset: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            listState.update {
                val keep = reset && it.movies.isNotEmpty()
                it.copy(
                    isLoading = reset && !keep,
                    isRefreshing = keep,
                    error = null,
                    movies = if (reset && !keep) emptyList() else it.movies
                )
            }
            runCatching {
                if (filter.isActive) {
                    val sort = if (category == MovieCategory.TOP_RATED) {
                        "vote_average.desc"
                    } else {
                        "popularity.desc"
                    }
                    repository.discover(mediaType, filter, sort, page)
                } else if (category != null) {
                    repository.titles(mediaType, category, page)
                } else {
                    repository.discover(mediaType, filter, "popularity.desc", page)
                }
            }
                .onSuccess { movies ->
                    listState.update { current ->
                        val combined = if (reset) movies else current.movies + movies
                        current.copy(
                            movies = combined,
                            isLoading = false,
                            isRefreshing = false,
                            page = page,
                            endReached = movies.isEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    listState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = if (it.movies.isEmpty()) {
                                error.message ?: "Unable to load titles"
                            } else {
                                null
                            }
                        )
                    }
                }
        }
    }

    class Factory(
        private val repository: MovieRepository,
        private val mediaType: MediaType,
        private val filter: DiscoverFilter,
        private val category: MovieCategory? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DiscoverResultsViewModel(repository, mediaType, filter, category) as T
        }
    }
}
