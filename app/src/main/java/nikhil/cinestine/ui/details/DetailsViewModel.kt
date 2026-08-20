package nikhil.cinestine.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.analytics.AppAnalytics
import nikhil.cinestine.model.CastMember
import nikhil.cinestine.model.Episode
import nikhil.cinestine.model.MediaImage
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.Review
import nikhil.cinestine.model.Season
import nikhil.cinestine.model.TitleDetails
import nikhil.cinestine.model.Trailer
import nikhil.cinestine.model.WatchAvailability
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class DetailsUiState(
    val movie: Movie? = null,
    val details: TitleDetails? = null,
    val trailers: List<Trailer> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val cast: List<CastMember> = emptyList(),
    val recommendations: List<Movie> = emptyList(),
    val watch: WatchAvailability? = null,
    val images: List<MediaImage> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val selectedSeason: Int? = null,
    val episodes: List<Episode> = emptyList(),
    val episodesLoading: Boolean = false,
    val isFavourite: Boolean = false
)

class DetailsViewModel(
    private val repository: MovieRepository,
    private val analytics: AppAnalytics
) : ViewModel() {

    private val movie = MutableStateFlow<Movie?>(null)
    private val extras = MutableStateFlow(Extras())
    private var loadJob: Job? = null
    private var seasonJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailsUiState> = combine(
        movie,
        extras,
        movie.flatMapLatest { current ->
            if (current == null) {
                flowOf(false)
            } else {
                repository.observeIsFavourite(current.id, current.mediaType)
            }
        }
    ) { currentMovie, extra, favourite ->
        DetailsUiState(
            movie = currentMovie,
            details = extra.details,
            trailers = extra.trailers,
            reviews = extra.reviews,
            cast = extra.cast,
            recommendations = extra.recommendations,
            watch = extra.watch,
            images = extra.images,
            seasons = extra.details?.seasons.orEmpty(),
            selectedSeason = extra.selectedSeason,
            episodes = extra.episodes,
            episodesLoading = extra.episodesLoading,
            isFavourite = favourite
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailsUiState())

    fun show(movie: Movie) {
        viewModelScope.launch {
            runCatching { repository.recordViewed(movie) }
        }
        analytics.viewItem(movie)
        if (this.movie.value?.favouriteKey == movie.favouriteKey && extras.value.details != null) return
        loadJob?.cancel()
        seasonJob?.cancel()
        this.movie.value = movie
        extras.value = Extras()
        loadJob = viewModelScope.launch {
            coroutineScope {
                val detailsDeferred = async {
                    runCatching { repository.details(movie.id, movie.mediaType) }
                }
                val trailersDeferred = async {
                    runCatching { repository.trailers(movie.id, movie.mediaType) }
                }
                val reviewsDeferred = async {
                    runCatching { repository.reviews(movie.id, movie.mediaType) }
                }
                val castDeferred = async {
                    runCatching { repository.cast(movie.id, movie.mediaType) }
                }
                val similarDeferred = async {
                    runCatching { repository.recommendations(movie.id, movie.mediaType) }
                }
                val watchDeferred = async {
                    runCatching { repository.watchAvailability(movie.id, movie.mediaType) }
                }
                val certDeferred = async {
                    runCatching { repository.certification(movie.id, movie.mediaType) }
                }
                val imagesDeferred = async {
                    runCatching { repository.images(movie.id, movie.mediaType) }
                }
                val keywordsDeferred = async {
                    runCatching { repository.keywords(movie.id, movie.mediaType) }
                }
                fun <T> Result<T>.orCancel(): Result<T> =
                    onFailure { if (it is CancellationException) throw it }
                val details = detailsDeferred.await().orCancel().getOrNull()
                val certification = certDeferred.await().orCancel().getOrDefault("")
                val keywords = keywordsDeferred.await().orCancel().getOrDefault(emptyList())
                extras.update {
                    it.copy(
                        details = details?.copy(certification = certification, keywords = keywords),
                        trailers = trailersDeferred.await().orCancel().getOrDefault(emptyList()),
                        reviews = reviewsDeferred.await().orCancel().getOrDefault(emptyList()),
                        cast = castDeferred.await().orCancel().getOrDefault(emptyList()),
                        recommendations = similarDeferred.await().orCancel().getOrDefault(emptyList()),
                        watch = watchDeferred.await().orCancel().getOrDefault(null),
                        images = imagesDeferred.await().orCancel().getOrDefault(emptyList())
                    )
                }
                if (movie.mediaType == MediaType.TV) {
                    val defaultSeason = details?.nextEpisode?.seasonNumber
                        ?: details?.seasons?.firstOrNull { it.seasonNumber > 0 }?.seasonNumber
                        ?: details?.seasons?.firstOrNull()?.seasonNumber
                    if (defaultSeason != null) selectSeason(defaultSeason)
                }
            }
        }
    }

    fun selectSeason(seasonNumber: Int) {
        val current = movie.value ?: return
        if (current.mediaType != MediaType.TV) return
        if (extras.value.selectedSeason == seasonNumber && extras.value.episodes.isNotEmpty()) return
        seasonJob?.cancel()
        extras.update { it.copy(selectedSeason = seasonNumber, episodes = emptyList(), episodesLoading = true) }
        seasonJob = viewModelScope.launch {
            runCatching { repository.seasonEpisodes(current.id, seasonNumber) }
                .onSuccess { episodes ->
                    extras.update { it.copy(episodes = episodes, episodesLoading = false) }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    extras.update { it.copy(episodesLoading = false) }
                }
        }
    }

    fun toggleFavourite() {
        val current = movie.value ?: return
        viewModelScope.launch {
            repository.toggleFavourite(current, uiState.value.isFavourite)
        }
    }

    fun reloadRegionSensitive() {
        val current = movie.value ?: return
        viewModelScope.launch {
            val watch = runCatching { repository.watchAvailability(current.id, current.mediaType) }
                .onFailure { if (it is CancellationException) throw it }
                .getOrNull()
            val certification = runCatching { repository.certification(current.id, current.mediaType) }
                .onFailure { if (it is CancellationException) throw it }
                .getOrDefault("")
            extras.update {
                it.copy(
                    watch = watch,
                    details = it.details?.copy(certification = certification)
                )
            }
        }
    }

    private data class Extras(
        val details: TitleDetails? = null,
        val trailers: List<Trailer> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val cast: List<CastMember> = emptyList(),
        val recommendations: List<Movie> = emptyList(),
        val watch: WatchAvailability? = null,
        val images: List<MediaImage> = emptyList(),
        val selectedSeason: Int? = null,
        val episodes: List<Episode> = emptyList(),
        val episodesLoading: Boolean = false
    )

    class Factory(
        private val repository: MovieRepository,
        private val analytics: AppAnalytics
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailsViewModel(repository, analytics) as T
        }
    }
}
