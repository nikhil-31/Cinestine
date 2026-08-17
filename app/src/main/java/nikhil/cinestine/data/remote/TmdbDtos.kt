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
data class TvPageDto(
    val results: List<TvDto> = emptyList()
)

@Serializable
data class TvDto(
    val id: Long,
    val name: String = "",
    @SerialName("original_name") val originalName: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0f,
    @SerialName("first_air_date") val firstAirDate: String = "",
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

@Serializable
data class GenreDto(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class MovieDetailsDto(
    val id: Long = 0,
    val tagline: String = "",
    val overview: String = "",
    val runtime: Int = 0,
    val status: String = "",
    val genres: List<GenreDto> = emptyList()
)

@Serializable
data class TvDetailsDto(
    val id: Long = 0,
    val tagline: String = "",
    val overview: String = "",
    val status: String = "",
    val genres: List<GenreDto> = emptyList(),
    @SerialName("episode_run_time") val episodeRunTime: List<Int> = emptyList(),
    @SerialName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int = 0,
    val networks: List<NetworkDto> = emptyList(),
    val seasons: List<TvSeasonSummaryDto> = emptyList(),
    @SerialName("next_episode_to_air") val nextEpisodeToAir: TvEpisodeDto? = null,
    @SerialName("last_episode_to_air") val lastEpisodeToAir: TvEpisodeDto? = null
)

@Serializable
data class NetworkDto(
    val name: String = "",
    @SerialName("logo_path") val logoPath: String? = null
)

@Serializable
data class TvSeasonSummaryDto(
    val id: Int = 0,
    val name: String = "",
    val overview: String = "",
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("episode_count") val episodeCount: Int = 0,
    @SerialName("air_date") val airDate: String = "",
    @SerialName("poster_path") val posterPath: String? = null
)

@Serializable
data class TvSeasonDto(
    val episodes: List<TvEpisodeDto> = emptyList()
)

@Serializable
data class TvEpisodeDto(
    val id: Long = 0,
    val name: String = "",
    val overview: String = "",
    @SerialName("episode_number") val episodeNumber: Int = 0,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("air_date") val airDate: String? = null,
    @SerialName("still_path") val stillPath: String? = null,
    val runtime: Int? = null
)
