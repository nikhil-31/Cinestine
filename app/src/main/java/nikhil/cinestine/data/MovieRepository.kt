package nikhil.cinestine.data

import nikhil.cinestine.data.local.FavouriteDao
import nikhil.cinestine.data.local.toEntity
import nikhil.cinestine.data.local.toMovie
import nikhil.cinestine.data.remote.MovieDto
import nikhil.cinestine.data.remote.MovieDetailsDto
import nikhil.cinestine.data.remote.TmdbApi
import nikhil.cinestine.data.remote.TvDetailsDto
import nikhil.cinestine.data.remote.TvDto
import nikhil.cinestine.data.remote.TvEpisodeDto
import nikhil.cinestine.data.remote.TvSeasonSummaryDto
import nikhil.cinestine.model.Episode
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import nikhil.cinestine.model.Review
import nikhil.cinestine.model.Season
import nikhil.cinestine.model.TitleDetails
import nikhil.cinestine.model.Trailer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepository(
    private val api: TmdbApi,
    private val favouriteDao: FavouriteDao
) {
    suspend fun titles(mediaType: MediaType, category: MovieCategory, page: Int): List<Movie> {
        return when (mediaType) {
            MediaType.MOVIE -> {
                val response = when (category) {
                    MovieCategory.POPULAR -> api.popular(page)
                    MovieCategory.TOP_RATED -> api.topRated(page)
                }
                response.results.map { it.toMovie() }
            }
            MediaType.TV -> {
                val response = when (category) {
                    MovieCategory.POPULAR -> api.popularTv(page)
                    MovieCategory.TOP_RATED -> api.topRatedTv(page)
                }
                response.results.map { it.toMovie() }
            }
        }
    }

    suspend fun search(query: String, page: Int, mediaType: MediaType?): List<Movie> {
        return when (mediaType) {
            MediaType.MOVIE -> api.search(query, page).results.map { it.toMovie() }
            MediaType.TV -> api.searchTv(query, page).results.map { it.toMovie() }
            null -> searchAll(query, page)
        }
    }

    private suspend fun searchAll(query: String, page: Int): List<Movie> = coroutineScope {
        val movies = async { runCatching { api.search(query, page).results.map { it.toMovie() } } }
        val shows = async { runCatching { api.searchTv(query, page).results.map { it.toMovie() } } }
        val movieResult = movies.await()
        val showResult = shows.await()
        if (movieResult.isFailure && showResult.isFailure) {
            throw movieResult.exceptionOrNull() ?: showResult.exceptionOrNull()!!
        }
        (movieResult.getOrDefault(emptyList()) + showResult.getOrDefault(emptyList()))
            .sortedByDescending { it.popularity.toDoubleOrNull() ?: 0.0 }
    }

    suspend fun trailers(id: String, mediaType: MediaType): List<Trailer> {
        val response = when (mediaType) {
            MediaType.MOVIE -> api.videos(id)
            MediaType.TV -> api.tvVideos(id)
        }
        return response.results
            .filter { it.site.equals("YouTube", ignoreCase = true) && it.key.isNotBlank() }
            .map { Trailer(id = it.id, key = it.key, name = it.name, site = it.site) }
    }

    suspend fun reviews(id: String, mediaType: MediaType): List<Review> {
        val response = when (mediaType) {
            MediaType.MOVIE -> api.reviews(id)
            MediaType.TV -> api.tvReviews(id)
        }
        return response.results.map {
            Review(id = it.id, author = it.author, content = it.content)
        }
    }

    suspend fun details(id: String, mediaType: MediaType): TitleDetails {
        return when (mediaType) {
            MediaType.MOVIE -> api.movieDetails(id).toTitleDetails()
            MediaType.TV -> api.tvDetails(id).toTitleDetails()
        }
    }

    suspend fun seasonEpisodes(tvId: String, seasonNumber: Int): List<Episode> {
        return api.tvSeason(tvId, seasonNumber).episodes.map { it.toEpisode() }
    }

    fun observeFavourites(): Flow<List<Movie>> {
        return favouriteDao.observeAll().map { list -> list.map { it.toMovie() } }
    }

    fun observeFavouriteKeys(): Flow<Set<String>> {
        return favouriteDao.observeAll().map { list ->
            list.map { "${it.mediaType}:${it.id}" }.toSet()
        }
    }

    fun observeIsFavourite(id: String, mediaType: MediaType): Flow<Boolean> {
        return favouriteDao.observeIsFavourite(id, mediaType.name)
    }

    suspend fun toggleFavourite(movie: Movie, currentlyFavourite: Boolean) {
        if (currentlyFavourite) {
            favouriteDao.delete(movie.id, movie.mediaType.name)
        } else {
            favouriteDao.upsert(movie.toEntity())
        }
    }

    private fun MovieDto.toMovie(): Movie {
        return Movie(
            id = id.toString(),
            title = title,
            originalTitle = originalTitle.ifBlank { title },
            overview = overview,
            posterPath = posterPath.toImageUrl(),
            backdropPath = backdropPath.toImageUrl(),
            voteAverage = voteAverage,
            releaseDate = releaseDate,
            popularity = popularity.toString(),
            voteCount = voteCount.toString(),
            originalLanguage = originalLanguage,
            adult = adult.toString(),
            mediaType = MediaType.MOVIE
        )
    }

    private fun TvDto.toMovie(): Movie {
        return Movie(
            id = id.toString(),
            title = name,
            originalTitle = originalName.ifBlank { name },
            overview = overview,
            posterPath = posterPath.toImageUrl(),
            backdropPath = backdropPath.toImageUrl(),
            voteAverage = voteAverage,
            releaseDate = firstAirDate,
            popularity = popularity.toString(),
            voteCount = voteCount.toString(),
            originalLanguage = originalLanguage,
            adult = adult.toString(),
            mediaType = MediaType.TV
        )
    }

    private fun MovieDetailsDto.toTitleDetails(): TitleDetails {
        return TitleDetails(
            tagline = tagline.trim(),
            overview = overview,
            runtimeMinutes = runtime.takeIf { it > 0 },
            genres = genres.map { it.name }.filter { it.isNotBlank() },
            status = status
        )
    }

    private fun TvDetailsDto.toTitleDetails(): TitleDetails {
        return TitleDetails(
            tagline = tagline.trim(),
            overview = overview,
            runtimeMinutes = episodeRunTime.firstOrNull { it > 0 },
            genres = genres.map { it.name }.filter { it.isNotBlank() },
            status = status,
            seasonCount = numberOfSeasons,
            episodeCount = numberOfEpisodes,
            networks = networks.map { it.name }.filter { it.isNotBlank() }.distinct(),
            seasons = seasons
                .filter { it.seasonNumber >= 0 && it.episodeCount > 0 }
                .map { it.toSeason() },
            nextEpisode = nextEpisodeToAir?.toEpisode(),
            lastEpisode = lastEpisodeToAir?.toEpisode()
        )
    }

    private fun TvSeasonSummaryDto.toSeason() = Season(
        id = id,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview,
        episodeCount = episodeCount,
        airDate = airDate,
        posterPath = posterPath.toImageUrl()
    )

    private fun TvEpisodeDto.toEpisode() = Episode(
        id = id.toString(),
        name = name,
        overview = overview,
        episodeNumber = episodeNumber,
        seasonNumber = seasonNumber,
        airDate = airDate.orEmpty(),
        stillPath = stillPath.toImageUrl(),
        runtimeMinutes = runtime?.takeIf { it > 0 }
    )

    private fun String?.toImageUrl(): String {
        val path = this?.takeIf { it.isNotBlank() && it != "null" } ?: return ""
        return IMAGE_BASE_URL + path
    }

    private companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}
