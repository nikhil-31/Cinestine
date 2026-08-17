package nikhil.cinestine.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MediaType {
    MOVIE,
    TV
}

enum class SearchScope {
    MOVIE,
    TV,
    PERSON,
    COLLECTION
}

data class SearchHit(
    val key: String,
    val title: String,
    val imagePath: String,
    val rating: Float? = null,
    val badge: String? = null,
    val movie: Movie? = null,
    val personId: String? = null,
    val collectionId: String? = null
)

@Parcelize
data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val voteAverage: Float,
    val releaseDate: String,
    val popularity: String,
    val voteCount: String,
    val originalLanguage: String,
    val adult: String,
    val mediaType: MediaType = MediaType.MOVIE
) : Parcelable {
    val favouriteKey: String get() = "${mediaType.name}:$id"
}

enum class MovieCategory {
    POPULAR,
    TOP_RATED,
    TRENDING,
    NOW_PLAYING,
    UPCOMING
}

data class Trailer(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String = "Trailer",
    val official: Boolean = true
) {
    val youtubeUrl: String get() = "https://www.youtube.com/watch?v=$key"
    val thumbnailUrl: String get() = "https://img.youtube.com/vi/$key/mqdefault.jpg"
    val group: VideoGroup
        get() = when (type.lowercase()) {
            "trailer" -> VideoGroup.TRAILER
            "teaser" -> VideoGroup.TEASER
            else -> VideoGroup.CLIP
        }
}

enum class VideoGroup { TRAILER, TEASER, CLIP }

data class CastMember(
    val id: String,
    val name: String,
    val character: String,
    val profilePath: String
)

data class WatchProvider(
    val id: Int,
    val name: String,
    val logoPath: String
)

data class WatchAvailability(
    val link: String,
    val providers: List<WatchProvider>
)

data class MediaImage(
    val url: String
)

data class Genre(
    val id: Int,
    val name: String
)

@Parcelize
data class DiscoverFilter(
    val genreIds: Set<Int> = emptySet(),
    val year: Int? = null,
    val minScore: Float? = null,
    val keywordIds: Set<Int> = emptySet(),
    val companyIds: Set<Int> = emptySet(),
    val networkIds: Set<Int> = emptySet()
) : Parcelable {
    val isActive: Boolean
        get() = genreIds.isNotEmpty() ||
            year != null ||
            minScore != null ||
            keywordIds.isNotEmpty() ||
            companyIds.isNotEmpty() ||
            networkIds.isNotEmpty()
}

data class TaggedLink(
    val id: Int,
    val name: String,
    val kind: Kind
) {
    enum class Kind { KEYWORD, COMPANY, NETWORK }
}

data class CollectionSummary(
    val id: String,
    val name: String,
    val posterPath: String,
    val backdropPath: String = "",
    val overview: String = "",
    val parts: List<Movie> = emptyList()
)

data class Person(
    val id: String,
    val name: String,
    val biography: String,
    val birthday: String,
    val placeOfBirth: String,
    val profilePath: String,
    val department: String,
    val castCredits: List<Movie>,
    val crewCredits: List<Movie>,
    val images: List<MediaImage> = emptyList()
) {
    val credits: List<Movie>
        get() = (castCredits + crewCredits).distinctBy { it.favouriteKey }
}

data class EpisodeDetails(
    val episode: Episode,
    val guestStars: List<CastMember> = emptyList(),
    val videos: List<Trailer> = emptyList(),
    val images: List<MediaImage> = emptyList()
)

data class Review(
    val id: String,
    val author: String,
    val content: String
)

data class TitleDetails(
    val tagline: String = "",
    val overview: String = "",
    val runtimeMinutes: Int? = null,
    val genres: List<String> = emptyList(),
    val status: String = "",
    val seasonCount: Int = 0,
    val episodeCount: Int = 0,
    val networks: List<String> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val nextEpisode: Episode? = null,
    val lastEpisode: Episode? = null,
    val certification: String = "",
    val collection: CollectionSummary? = null,
    val keywords: List<TaggedLink> = emptyList(),
    val companies: List<TaggedLink> = emptyList(),
    val networkLinks: List<TaggedLink> = emptyList()
)

data class Season(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val episodeCount: Int,
    val airDate: String,
    val posterPath: String
)

data class Episode(
    val id: String,
    val name: String,
    val overview: String,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val airDate: String,
    val stillPath: String,
    val runtimeMinutes: Int?
)
