package nikhil.cinestine.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviePageDto(
    val results: List<MovieDto> = emptyList()
)

@Serializable
data class MovieDto(
    val id: Long,
    val title: String = "",
    @SerialName("original_title") val originalTitle: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0f,
    @SerialName("release_date") val releaseDate: String = "",
    val popularity: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("original_language") val originalLanguage: String = "",
    val adult: Boolean = false
)

@Serializable
data class VideoPageDto(
    val results: List<VideoDto> = emptyList()
)

@Serializable
data class VideoDto(
    val id: String = "",
    val key: String = "",
    val name: String = "",
    val site: String = ""
)

@Serializable
data class ReviewPageDto(
    val results: List<ReviewDto> = emptyList()
)

@Serializable
data class ReviewDto(
    val id: String = "",
    val author: String = "",
    val content: String = ""
)
