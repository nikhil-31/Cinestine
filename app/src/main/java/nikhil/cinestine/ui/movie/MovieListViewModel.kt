package nikhil.cinestine.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.Genre
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

data class MovieListUiState(
    val movies: List<Movie> = emptyList(),
    val favouriteKeys: Set<String> = emptySet(),
    val mediaType: MediaType = MediaType.MOVIE,
    val category: MovieCategory = MovieCategory.POPULAR,
    val filter: DiscoverFilter = DiscoverFilter(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false
)

class MovieListViewModel(
    private val repository: MovieRepository,
    initialCategory: MovieCategory
) : ViewModel() {

    private val listState = MutableStateFlow(MovieListUiState(category = initialCategory))
    private var loadJob: Job? = null
    private var mediaType: MediaType = MediaType.MOVIE
    private var category: MovieCategory = initialCategory
    private var filter: DiscoverFilter = DiscoverFilter()

    val currentMediaType: MediaType get() = mediaType
    val currentCategory: MovieCategory get() = category
    val currentFilter: DiscoverFilter get() = filter

    val uiState: StateFlow<MovieListUiState> = combine(
        listState,
        repository.observeFavouriteKeys()
    ) { list, keys ->
        list.copy(favouriteKeys = keys)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MovieListUiState())

    init {
        refresh()
    }

    fun setMediaType(mediaType: MediaType) {
        if (this.mediaType == mediaType) return
        this.mediaType = mediaType
        filter = filter.copy(genreIds = emptySet())
        loadPage(page = 1, reset = true)
    }

    fun setCategory(category: MovieCategory) {
        if (this.category == category) return
        this.category = category
        loadPage(page = 1, reset = true)
    }

    fun setFilter(filter: DiscoverFilter) {
        if (this.filter == filter) return
        this.filter = filter
        loadPage(page = 1, reset = true)
    }

    suspend fun genres(): List<Genre> = repository.genres(mediaType)

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
                    mediaType = mediaType,
                    category = category,
                    filter = filter,
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
                } else {
                    repository.titles(mediaType, category, page)
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
                            endReached = movies.isEmpty(),
                            mediaType = mediaType,
                            category = category,
                            filter = filter
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
        private val category: MovieCategory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieListViewModel(repository, category) as T
        }
    }
}
