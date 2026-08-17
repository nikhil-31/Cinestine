package nikhil.cinestine.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MediaType {
    MOVIE,
    TV
}

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
    TOP_RATED
}

data class Trailer(
    val id: String,
    val key: String,
    val name: String,
    val site: String
) {
    val youtubeUrl: String get() = "https://www.youtube.com/watch?v=$key"
    val thumbnailUrl: String get() = "https://img.youtube.com/vi/$key/mqdefault.jpg"
}

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
    val lastEpisode: Episode? = null
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
