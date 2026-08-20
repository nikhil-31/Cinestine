package nikhil.cinestine.ui.hot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.R
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.Genre
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException

enum class HotScope { ALL, MOVIE, TV }

data class HotRow(
    val key: String,
    val titleRes: Int,
    val mediaType: MediaType,
    val category: MovieCategory,
    val movies: List<Movie>,
    val page: Int = 1,
    val endReached: Boolean = false,
    val showSeeAll: Boolean = true
)

data class HotUiState(
    val scope: HotScope = HotScope.ALL,
    val filter: DiscoverFilter = DiscoverFilter(),
    val rows: List<HotRow> = emptyList(),
    val favouriteKeys: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class HotViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val listState = MutableStateFlow(HotUiState(isLoading = true))
    val uiState: StateFlow<HotUiState> = combine(
        listState,
        repository.observeFavouriteKeys(),
        repository.observeRecentlyViewed()
    ) { list, keys, recent ->
        val scoped = when (list.scope) {
            HotScope.ALL -> recent
            HotScope.MOVIE -> recent.filter { it.mediaType == MediaType.MOVIE }
            HotScope.TV -> recent.filter { it.mediaType == MediaType.TV }
        }
        val rows = if (scoped.isEmpty()) {
            list.rows
        } else {
            listOf(recentRow(scoped)) + list.rows
        }
        list.copy(favouriteKeys = keys, rows = rows)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HotUiState(isLoading = true))

    private var scope: HotScope = HotScope.ALL
    private var filter: DiscoverFilter = DiscoverFilter()
    private var loadJob: Job? = null
    private val pageJobs = mutableMapOf<String, Job>()

    val currentMediaType: MediaType
        get() = if (scope == HotScope.TV) MediaType.TV else MediaType.MOVIE
    val currentFilter: DiscoverFilter get() = filter

    init {
        refresh()
    }

    fun setScope(scope: HotScope) {
        if (this.scope == scope) return
        this.scope = scope
        filter = filter.copy(genreIds = emptySet())
        refresh(keepExisting = false)
    }

    fun setFilter(filter: DiscoverFilter) {
        if (this.filter == filter) return
        this.filter = filter
        refresh(keepExisting = false)
    }

    suspend fun genres(): List<Genre> = repository.genres(currentMediaType)

    fun toggleFavourite(movie: Movie) {
        viewModelScope.launch {
            val currentlyFavourite = uiState.value.favouriteKeys.contains(movie.favouriteKey)
            repository.toggleFavourite(movie, currentlyFavourite)
        }
    }

    fun loadNextPage(key: String) {
        val row = listState.value.rows.find { it.key == key } ?: return
        if (row.endReached || pageJobs[key]?.isActive == true) return
        pageJobs[key] = viewModelScope.launch {
            runCatching { fetch(row.mediaType, row.category, row.page + 1) }
                .onSuccess { more ->
                    listState.update { current ->
                        current.copy(
                            rows = current.rows.map { existing ->
                                if (existing.key != key) existing else existing.copy(
                                    movies = existing.movies + more,
                                    page = existing.page + 1,
                                    endReached = more.isEmpty()
                                )
                            }
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                }
        }
    }

    fun refresh() {
        refresh(keepExisting = listState.value.rows.isNotEmpty())
    }

    private fun refresh(keepExisting: Boolean) {
        loadJob?.cancel()
        pageJobs.values.forEach { it.cancel() }
        pageJobs.clear()
        loadJob = viewModelScope.launch {
            listState.update {
                it.copy(
                    scope = scope,
                    filter = filter,
                    isLoading = !keepExisting,
                    isRefreshing = keepExisting,
                    error = null,
                    rows = if (keepExisting) it.rows else emptyList()
                )
            }
            val specs = railsFor(scope)
            runCatching {
                supervisorScope {
                    specs.map { spec ->
                        async { spec to runCatching { fetch(spec.mediaType, spec.category, 1) } }
                    }.awaitAll()
                }
            }
                .onSuccess { results ->
                    if (results.all { it.second.isFailure }) {
                        val error = results.first().second.exceptionOrNull()
                        if (error is CancellationException) throw error
                        listState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = error?.message ?: "Unable to load titles"
                            )
                        }
                        return@onSuccess
                    }
                    val rows = results.mapNotNull { (spec, result) ->
                        val movies = result.getOrDefault(emptyList())
                        if (movies.isEmpty()) return@mapNotNull null
                        HotRow(
                            key = spec.key,
                            titleRes = spec.titleRes,
                            mediaType = spec.mediaType,
                            category = spec.category,
                            movies = movies,
                            page = 1,
                            endReached = movies.isEmpty()
                        )
                    }
                    listState.update {
                        it.copy(
                            rows = rows,
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            scope = scope,
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
                            error = error.message ?: "Unable to load titles"
                        )
                    }
                }
        }
    }

    private suspend fun fetch(mediaType: MediaType, category: MovieCategory, page: Int): List<Movie> {
        val applyFilter = filter.isActive && scope != HotScope.ALL
        return if (applyFilter) {
            val sort = if (category == MovieCategory.TOP_RATED) "vote_average.desc" else "popularity.desc"
            repository.discover(mediaType, filter, sort, page)
        } else {
            repository.titles(mediaType, category, page)
        }
    }

    private fun railsFor(scope: HotScope): List<RailSpec> {
        val types = when (scope) {
            HotScope.ALL -> listOf(MediaType.MOVIE, MediaType.TV)
            HotScope.MOVIE -> listOf(MediaType.MOVIE)
            HotScope.TV -> listOf(MediaType.TV)
        }
        return listOf(
            MovieCategory.TRENDING,
            MovieCategory.NOW_PLAYING,
            MovieCategory.UPCOMING,
            MovieCategory.POPULAR,
            MovieCategory.TOP_RATED
        ).flatMap { category ->
            types.map { type ->
                RailSpec(type, category, titleRes(type, category))
            }
        }
    }

    private fun titleRes(type: MediaType, category: MovieCategory): Int {
        val tv = type == MediaType.TV
        return when (category) {
            MovieCategory.TRENDING -> if (tv) R.string.rail_trending_tv else R.string.rail_trending_movies
            MovieCategory.NOW_PLAYING -> if (tv) R.string.title_airing_today else R.string.title_now_playing
            MovieCategory.UPCOMING -> if (tv) R.string.title_on_the_air else R.string.title_upcoming
            MovieCategory.POPULAR -> if (tv) R.string.rail_popular_tv else R.string.rail_popular_movies
            MovieCategory.TOP_RATED -> if (tv) R.string.rail_top_rated_tv else R.string.rail_top_rated_movies
        }
    }

    private fun recentRow(movies: List<Movie>) = HotRow(
        key = RECENT_KEY,
        titleRes = R.string.rail_recently_viewed,
        mediaType = movies.firstOrNull()?.mediaType ?: MediaType.MOVIE,
        category = MovieCategory.POPULAR,
        movies = movies,
        page = 1,
        endReached = true,
        showSeeAll = false
    )

    private data class RailSpec(
        val mediaType: MediaType,
        val category: MovieCategory,
        val titleRes: Int
    ) {
        val key: String get() = "${mediaType.name}:${category.name}"
    }

    private companion object {
        const val RECENT_KEY = "recent"
    }

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HotViewModel(repository) as T
        }
    }
}
