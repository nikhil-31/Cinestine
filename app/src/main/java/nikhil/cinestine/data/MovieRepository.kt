package nikhil.cinestine.data

import nikhil.cinestine.data.local.FavouriteDao
import nikhil.cinestine.data.local.RecentDao
import nikhil.cinestine.data.local.toEntity
import nikhil.cinestine.data.local.toMovie
import nikhil.cinestine.data.local.toRecentEntity
import nikhil.cinestine.data.remote.AggregateCreditsDto
import nikhil.cinestine.data.remote.CastDto
import nikhil.cinestine.data.remote.CollectionDto
import nikhil.cinestine.data.remote.CollectionSummaryDto
import nikhil.cinestine.data.remote.ContentRatingsDto
import nikhil.cinestine.data.remote.ImageDto
import nikhil.cinestine.data.remote.CreditTitleDto
import nikhil.cinestine.data.remote.CreditsDto
import nikhil.cinestine.data.remote.ImagesDto
import nikhil.cinestine.data.remote.MovieDto
import nikhil.cinestine.data.remote.MovieDetailsDto
import nikhil.cinestine.data.remote.PersonDto
import nikhil.cinestine.data.remote.PersonSearchDto
import nikhil.cinestine.data.remote.ReleaseDatesDto
import nikhil.cinestine.data.remote.TmdbApi
import nikhil.cinestine.data.remote.TvDetailsDto
import nikhil.cinestine.data.remote.TvDto
import nikhil.cinestine.data.remote.TvEpisodeDto
import nikhil.cinestine.data.remote.TvSeasonSummaryDto
import nikhil.cinestine.data.remote.WatchProvidersDto
import nikhil.cinestine.model.CastMember
import nikhil.cinestine.model.CollectionSummary
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.Episode
import nikhil.cinestine.model.EpisodeDetails
import nikhil.cinestine.model.Genre
import nikhil.cinestine.model.MediaImage
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import nikhil.cinestine.model.Person
import nikhil.cinestine.model.Review
import nikhil.cinestine.model.SearchHit
import nikhil.cinestine.model.SearchScope
import nikhil.cinestine.model.Season
import nikhil.cinestine.model.TaggedLink
import nikhil.cinestine.model.TitleDetails
import nikhil.cinestine.model.Trailer
import nikhil.cinestine.model.WatchAvailability
import nikhil.cinestine.model.WatchProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class MovieRepository(
    private val api: TmdbApi,
    private val favouriteDao: FavouriteDao,
    private val recentDao: RecentDao,
    private val regionProvider: () -> String = {
        val country = Locale.getDefault().country
        if (country.length == 2) country else "US"
    },
    private val onFavouriteChanged: (Movie, Boolean) -> Unit = { _, _ -> }
) {
    private var cachedMovieGenres: List<Genre>? = null
    private var cachedTvGenres: List<Genre>? = null

    suspend fun titles(mediaType: MediaType, category: MovieCategory, page: Int): List<Movie> {
        val region = currentRegion()
        return when (mediaType) {
            MediaType.MOVIE -> {
                val response = when (category) {
                    MovieCategory.POPULAR -> api.popular(page)
                    MovieCategory.TOP_RATED -> api.topRated(page)
                    MovieCategory.TRENDING -> api.trendingMovies(page)
                    MovieCategory.NOW_PLAYING -> api.nowPlaying(page, region)
                    MovieCategory.UPCOMING -> api.upcoming(page, region)
                }
                response.results.map { it.toMovie() }
            }
            MediaType.TV -> {
                val response = when (category) {
                    MovieCategory.POPULAR -> api.popularTv(page)
                    MovieCategory.TOP_RATED -> api.topRatedTv(page)
                    MovieCategory.TRENDING -> api.trendingTv(page)
                    MovieCategory.NOW_PLAYING -> api.airingToday(page)
                    MovieCategory.UPCOMING -> api.onTheAir(page)
                }
                response.results.map { it.toMovie() }
            }
        }
    }

    suspend fun discover(
        mediaType: MediaType,
        filter: DiscoverFilter,
        sortBy: String,
        page: Int
    ): List<Movie> {
        val genres = filter.genreIds.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(",")
        val keywords = filter.keywordIds.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(",")
        val companies = filter.companyIds.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(",")
        val networks = filter.networkIds.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(",")
        return when (mediaType) {
            MediaType.MOVIE -> api.discoverMovies(
                page, sortBy, genres, filter.year, filter.minScore, keywords, companies
            ).results.map { it.toMovie() }
            MediaType.TV -> api.discoverTv(
                page, sortBy, genres, filter.year, filter.minScore, keywords, companies, networks
            ).results.map { it.toMovie() }
        }
    }

    suspend fun genres(mediaType: MediaType): List<Genre> {
        return when (mediaType) {
            MediaType.MOVIE -> cachedMovieGenres ?: api.movieGenres().genres
                .map { Genre(it.id, it.name) }
                .also { cachedMovieGenres = it }
            MediaType.TV -> cachedTvGenres ?: api.tvGenres().genres
                .map { Genre(it.id, it.name) }
                .also { cachedTvGenres = it }
        }
    }

    suspend fun search(query: String, page: Int, mediaType: MediaType?): List<Movie> {
        return when (mediaType) {
            MediaType.MOVIE -> api.search(query, page).results.map { it.toMovie() }
            MediaType.TV -> api.searchTv(query, page).results.map { it.toMovie() }
            null -> searchAll(query, page)
        }
    }

    suspend fun search(query: String, page: Int, scope: SearchScope): List<SearchHit> {
        return when (scope) {
            SearchScope.MOVIE -> api.search(query, page).results.map { it.toMovie().toHit() }
            SearchScope.TV -> api.searchTv(query, page).results.map { it.toMovie().toHit() }
            SearchScope.PERSON -> api.searchPeople(query, page).results.map { it.toHit() }
            SearchScope.COLLECTION -> api.searchCollections(query, page).results.map { it.toHit() }
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
            .map {
                Trailer(
                    id = it.id,
                    key = it.key,
                    name = it.name,
                    site = it.site,
                    type = it.type,
                    official = it.official
                )
            }
    }

    suspend fun cast(id: String, mediaType: MediaType): List<CastMember> {
        return when (mediaType) {
            MediaType.MOVIE -> api.movieCredits(id).toCast()
            MediaType.TV -> api.tvCredits(id).toCast()
        }
    }

    suspend fun recommendations(id: String, mediaType: MediaType): List<Movie> {
        val recommended = when (mediaType) {
            MediaType.MOVIE -> api.movieRecommendations(id).results.map { it.toMovie() }
            MediaType.TV -> api.tvRecommendations(id).results.map { it.toMovie() }
        }
        if (recommended.isNotEmpty()) return recommended.take(12)
        return when (mediaType) {
            MediaType.MOVIE -> api.movieSimilar(id).results.map { it.toMovie() }
            MediaType.TV -> api.tvSimilar(id).results.map { it.toMovie() }
        }.take(12)
    }

    suspend fun watchAvailability(id: String, mediaType: MediaType): WatchAvailability? {
        val dto = when (mediaType) {
            MediaType.MOVIE -> api.movieWatchProviders(id)
            MediaType.TV -> api.tvWatchProviders(id)
        }
        return dto.toAvailability(currentRegion())
    }

    suspend fun certification(id: String, mediaType: MediaType): String {
        return when (mediaType) {
            MediaType.MOVIE -> api.movieReleaseDates(id).pick(currentRegion())
            MediaType.TV -> api.tvContentRatings(id).pick(currentRegion())
        }
    }

    suspend fun images(id: String, mediaType: MediaType): List<MediaImage> {
        val dto = when (mediaType) {
            MediaType.MOVIE -> api.movieImages(id)
            MediaType.TV -> api.tvImages(id)
        }
        return dto.toImages()
    }

    suspend fun person(id: String): Person {
        val details = api.person(id)
        val credits = api.personCredits(id)
        val images = runCatching { api.personImages(id).profiles.toImages() }.getOrDefault(emptyList())
        val cast = credits.cast
            .map { it.toMovie() }
            .distinctBy { it.favouriteKey }
            .sortedByDescending { it.popularity.toDoubleOrNull() ?: 0.0 }
            .take(40)
        val crew = credits.crew
            .filter { it.department.equals("Directing", true) || it.job.equals("Director", true) }
            .map { it.toMovie() }
            .distinctBy { it.favouriteKey }
            .sortedByDescending { it.popularity.toDoubleOrNull() ?: 0.0 }
            .take(40)
        return details.toPerson(cast, crew, images)
    }

    suspend fun collection(id: String): CollectionSummary {
        return api.collection(id).toCollection()
    }

    suspend fun keywords(id: String, mediaType: MediaType): List<TaggedLink> {
        val dto = when (mediaType) {
            MediaType.MOVIE -> api.movieKeywords(id)
            MediaType.TV -> api.tvKeywords(id)
        }
        return dto.all.filter { it.name.isNotBlank() }.take(16).map {
            TaggedLink(it.id, it.name, TaggedLink.Kind.KEYWORD)
        }
    }

    suspend fun episodeDetails(tvId: String, seasonNumber: Int, episodeNumber: Int): EpisodeDetails {
        return coroutineScope {
            val episodeDeferred = async { api.tvEpisode(tvId, seasonNumber, episodeNumber) }
            val creditsDeferred = async { runCatching { api.tvEpisodeCredits(tvId, seasonNumber, episodeNumber) } }
            val videosDeferred = async { runCatching { api.tvEpisodeVideos(tvId, seasonNumber, episodeNumber) } }
            val imagesDeferred = async { runCatching { api.tvEpisodeImages(tvId, seasonNumber, episodeNumber) } }
            val episode = episodeDeferred.await()
            val guests = episode.guestStars.toCastMembers().ifEmpty {
                creditsDeferred.await().getOrNull()?.cast?.toCastMembers().orEmpty()
            }
            val videos = videosDeferred.await().getOrNull()?.results.orEmpty()
                .filter { it.site.equals("YouTube", ignoreCase = true) && it.key.isNotBlank() }
                .map {
                    Trailer(it.id, it.key, it.name, it.site, it.type, it.official)
                }
            val images = imagesDeferred.await().getOrNull()?.toImages().orEmpty()
            EpisodeDetails(episode = episode.toEpisode(), guestStars = guests, videos = videos, images = images)
        }
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
            onFavouriteChanged(movie, false)
        } else {
            favouriteDao.upsert(movie.toEntity())
            recordViewed(movie)
            onFavouriteChanged(movie, true)
        }
    }

    fun observeRecentlyViewed(): Flow<List<Movie>> {
        return recentDao.observeNewest(RECENT_LIMIT).map { list -> list.map { it.toMovie() } }
    }

    suspend fun recordViewed(movie: Movie) {
        recentDao.upsert(movie.toRecentEntity())
        recentDao.allNewestFirst().drop(RECENT_LIMIT).forEach { extra ->
            recentDao.delete(extra.id, extra.mediaType)
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
            status = status,
            collection = belongsToCollection?.toSummary(),
            companies = productionCompanies
                .filter { it.name.isNotBlank() && it.id > 0 }
                .map { TaggedLink(it.id, it.name, TaggedLink.Kind.COMPANY) }
        )
    }

    private fun TvDetailsDto.toTitleDetails(): TitleDetails {
        val networkLinks = networks
            .filter { it.name.isNotBlank() && it.id > 0 }
            .map { TaggedLink(it.id, it.name, TaggedLink.Kind.NETWORK) }
        return TitleDetails(
            tagline = tagline.trim(),
            overview = overview,
            runtimeMinutes = episodeRunTime.firstOrNull { it > 0 },
            genres = genres.map { it.name }.filter { it.isNotBlank() },
            status = status,
            seasonCount = numberOfSeasons,
            episodeCount = numberOfEpisodes,
            networks = networkLinks.map { it.name }.distinct(),
            networkLinks = networkLinks,
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

    private fun CreditsDto.toCast(): List<CastMember> {
        return cast.sortedBy { it.order }.take(16).map {
            CastMember(
                id = it.id.toString(),
                name = it.name,
                character = it.character,
                profilePath = it.profilePath.toImageUrl("w185")
            )
        }
    }

    private fun AggregateCreditsDto.toCast(): List<CastMember> {
        return cast.sortedBy { it.order }.take(16).map {
            CastMember(
                id = it.id.toString(),
                name = it.name,
                character = it.roles.firstOrNull()?.character.orEmpty(),
                profilePath = it.profilePath.toImageUrl("w185")
            )
        }
    }

    private fun WatchProvidersDto.toAvailability(region: String): WatchAvailability? {
        val local = results[region] ?: results["US"] ?: return null
        val seen = linkedSetOf<Int>()
        val providers = (local.flatrate + local.rent + local.buy).mapNotNull { provider ->
            if (!seen.add(provider.providerId) || provider.providerName.isBlank()) {
                null
            } else {
                WatchProvider(
                    id = provider.providerId,
                    name = provider.providerName,
                    logoPath = provider.logoPath.toImageUrl("w92")
                )
            }
        }
        if (providers.isEmpty()) return null
        return WatchAvailability(link = local.link, providers = providers)
    }

    private fun ReleaseDatesDto.pick(region: String): String {
        val country = results.firstOrNull { it.country == region }
            ?: results.firstOrNull { it.country == "US" }
            ?: return ""
        return country.releaseDates
            .sortedBy { if (it.type == 3) 0 else 1 }
            .firstOrNull { it.certification.isNotBlank() }
            ?.certification
            .orEmpty()
    }

    private fun ContentRatingsDto.pick(region: String): String {
        return (
            results.firstOrNull { it.country == region }
                ?: results.firstOrNull { it.country == "US" }
            )?.rating.orEmpty()
    }

    private fun ImagesDto.toImages(): List<MediaImage> {
        return (backdrops + posters)
            .filter { it.filePath.isNotBlank() }
            .sortedByDescending { it.voteAverage }
            .distinctBy { it.filePath }
            .take(16)
            .map { MediaImage(url = it.filePath.toImageUrl("w780")) }
    }

    private fun PersonDto.toPerson(
        castCredits: List<Movie>,
        crewCredits: List<Movie>,
        images: List<MediaImage>
    ) = Person(
        id = id.toString(),
        name = name,
        biography = biography.trim(),
        birthday = birthday.orEmpty(),
        placeOfBirth = placeOfBirth.orEmpty(),
        profilePath = profilePath.toImageUrl(),
        department = knownForDepartment,
        castCredits = castCredits,
        crewCredits = crewCredits,
        images = images
    )

    private fun CollectionDto.toCollection() = CollectionSummary(
        id = id.toString(),
        name = name,
        posterPath = posterPath.toImageUrl(),
        backdropPath = backdropPath.toImageUrl(),
        overview = overview,
        parts = parts.map { it.toMovie() }
    )

    private fun CollectionSummaryDto.toSummary() = CollectionSummary(
        id = id.toString(),
        name = name,
        posterPath = posterPath.toImageUrl(),
        backdropPath = backdropPath.toImageUrl(),
        overview = overview
    )

    private fun CollectionSummaryDto.toHit() = SearchHit(
        key = "COLLECTION:$id",
        title = name,
        imagePath = posterPath.toImageUrl(),
        badge = "Collection",
        collectionId = id.toString()
    )

    private fun PersonSearchDto.toHit() = SearchHit(
        key = "PERSON:$id",
        title = name,
        imagePath = profilePath.toImageUrl("w185"),
        badge = knownForDepartment.ifBlank { "Person" },
        personId = id.toString()
    )

    private fun Movie.toHit() = SearchHit(
        key = favouriteKey,
        title = originalTitle,
        imagePath = posterPath,
        rating = voteAverage.takeIf { it > 0f },
        badge = if (mediaType == MediaType.TV) "TV" else "Movie",
        movie = this
    )

    private fun List<CastDto>.toCastMembers(): List<CastMember> {
        return take(16).map {
            CastMember(
                id = it.id.toString(),
                name = it.name,
                character = it.character,
                profilePath = it.profilePath.toImageUrl("w185")
            )
        }
    }

    private fun List<ImageDto>.toImages(): List<MediaImage> {
        return filter { it.filePath.isNotBlank() }
            .sortedByDescending { it.voteAverage }
            .distinctBy { it.filePath }
            .take(16)
            .map { MediaImage(url = it.filePath.toImageUrl("w780")) }
    }

    private fun CreditTitleDto.toMovie(): Movie {
        val isTv = mediaType.equals("tv", ignoreCase = true)
        val title = if (isTv) name else title
        val original = if (isTv) originalName.ifBlank { name } else originalTitle.ifBlank { this.title }
        return Movie(
            id = id.toString(),
            title = title,
            originalTitle = original,
            overview = overview,
            posterPath = posterPath.toImageUrl(),
            backdropPath = backdropPath.toImageUrl(),
            voteAverage = voteAverage,
            releaseDate = if (isTv) firstAirDate else releaseDate,
            popularity = popularity.toString(),
            voteCount = voteCount.toString(),
            originalLanguage = originalLanguage,
            adult = adult.toString(),
            mediaType = if (isTv) MediaType.TV else MediaType.MOVIE
        )
    }

    private fun String?.toImageUrl(size: String = "w500"): String {
        val path = this?.takeIf { it.isNotBlank() && it != "null" } ?: return ""
        return "https://image.tmdb.org/t/p/$size$path"
    }

    fun currentRegion(): String = regionProvider()

    private companion object {
        const val RECENT_LIMIT = 30
    }
}
