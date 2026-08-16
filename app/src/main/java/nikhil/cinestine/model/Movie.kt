package nikhil.cinestine.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    val adult: String
) : Parcelable

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
