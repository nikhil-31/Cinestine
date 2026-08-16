package nikhil.cinestine.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieListUiState(
    val movies: List<Movie> = emptyList(),
    val favouriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false
)

class MovieListViewModel(
    private val repository: MovieRepository,
    private val category: MovieCategory
) : ViewModel() {

    private val listState = MutableStateFlow(MovieListUiState())

    val uiState: StateFlow<MovieListUiState> = combine(
        listState,
        repository.observeFavouriteIds()
    ) { list, ids ->
        list.copy(favouriteIds = ids)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MovieListUiState())

    init {
        refresh()
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
            val currentlyFavourite = uiState.value.favouriteIds.contains(movie.id)
            repository.toggleFavourite(movie, currentlyFavourite)
        }
    }

    private fun loadPage(page: Int, reset: Boolean) {
        viewModelScope.launch {
            listState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.movies(category, page) }
                .onSuccess { movies ->
                    listState.update { current ->
                        val combined = if (reset) movies else current.movies + movies
                        current.copy(
                            movies = combined,
                            isLoading = false,
                            page = page,
                            endReached = movies.isEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    listState.update {
                        it.copy(isLoading = false, error = error.message ?: "Unable to load movies")
                    }
                }
        }
    }

    class Factory(
        private val repository: MovieRepository,
        private val category: MovieCategory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieListViewModel(repository, category) as T
        }
    }
}
