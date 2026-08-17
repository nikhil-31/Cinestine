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
    val site: String = "",
    val type: String = "Trailer",
    val official: Boolean = true
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
    val genres: List<GenreDto> = emptyList(),
    @SerialName("belongs_to_collection") val belongsToCollection: CollectionSummaryDto? = null,
    @SerialName("production_companies") val productionCompanies: List<CompanyDto> = emptyList()
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
    val id: Int = 0,
    val name: String = "",
    @SerialName("logo_path") val logoPath: String? = null
)

@Serializable
data class CompanyDto(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class CollectionSummaryDto(
    val id: Long = 0,
    val name: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null
)

@Serializable
data class CollectionDto(
    val id: Long = 0,
    val name: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    val parts: List<MovieDto> = emptyList()
)

@Serializable
data class KeywordDto(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class KeywordPageDto(
    val keywords: List<KeywordDto> = emptyList(),
    val results: List<KeywordDto> = emptyList()
) {
    val all: List<KeywordDto> get() = keywords.ifEmpty { results }
}

@Serializable
data class PersonSearchPageDto(
    val results: List<PersonSearchDto> = emptyList()
)

@Serializable
data class PersonSearchDto(
    val id: Long = 0,
    val name: String = "",
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("known_for_department") val knownForDepartment: String = ""
)

@Serializable
data class CollectionSearchPageDto(
    val results: List<CollectionSummaryDto> = emptyList()
)

@Serializable
data class PersonImagesDto(
    val profiles: List<ImageDto> = emptyList()
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
    val runtime: Int? = null,
    @SerialName("guest_stars") val guestStars: List<CastDto> = emptyList()
)

@Serializable
data class CreditsDto(
    val cast: List<CastDto> = emptyList()
)

@Serializable
data class CastDto(
    val id: Long = 0,
    val name: String = "",
    val character: String = "",
    @SerialName("profile_path") val profilePath: String? = null,
    val order: Int = 0
)

@Serializable
data class AggregateCreditsDto(
    val cast: List<AggregateCastDto> = emptyList()
)

@Serializable
data class AggregateCastDto(
    val id: Long = 0,
    val name: String = "",
    @SerialName("profile_path") val profilePath: String? = null,
    val order: Int = 0,
    val roles: List<RoleDto> = emptyList()
)

@Serializable
data class RoleDto(
    val character: String = ""
)

@Serializable
data class WatchProvidersDto(
    val results: Map<String, WatchProviderRegionDto> = emptyMap()
)

@Serializable
data class WatchProviderRegionDto(
    val link: String = "",
    val flatrate: List<WatchProviderDto> = emptyList(),
    val rent: List<WatchProviderDto> = emptyList(),
    val buy: List<WatchProviderDto> = emptyList()
)

@Serializable
data class WatchProviderDto(
    @SerialName("provider_id") val providerId: Int = 0,
    @SerialName("provider_name") val providerName: String = "",
    @SerialName("logo_path") val logoPath: String? = null
)

@Serializable
data class ReleaseDatesDto(
    val results: List<ReleaseDateCountryDto> = emptyList()
)

@Serializable
data class ReleaseDateCountryDto(
    @SerialName("iso_3166_1") val country: String = "",
    @SerialName("release_dates") val releaseDates: List<ReleaseDateDto> = emptyList()
)

@Serializable
data class ReleaseDateDto(
    val certification: String = "",
    val type: Int = 0
)

@Serializable
data class ContentRatingsDto(
    val results: List<ContentRatingDto> = emptyList()
)

@Serializable
data class ContentRatingDto(
    @SerialName("iso_3166_1") val country: String = "",
    val rating: String = ""
)

@Serializable
data class ImagesDto(
    val backdrops: List<ImageDto> = emptyList(),
    val posters: List<ImageDto> = emptyList()
)

@Serializable
data class ImageDto(
    @SerialName("file_path") val filePath: String = "",
    @SerialName("vote_average") val voteAverage: Float = 0f
)

@Serializable
data class PersonDto(
    val id: Long = 0,
    val name: String = "",
    val biography: String = "",
    val birthday: String? = null,
    @SerialName("place_of_birth") val placeOfBirth: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("known_for_department") val knownForDepartment: String = ""
)

@Serializable
data class PersonCreditsDto(
    val cast: List<CreditTitleDto> = emptyList(),
    val crew: List<CreditTitleDto> = emptyList()
)

@Serializable
data class CreditTitleDto(
    val id: Long = 0,
    val title: String = "",
    val name: String = "",
    @SerialName("original_title") val originalTitle: String = "",
    @SerialName("original_name") val originalName: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0f,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("first_air_date") val firstAirDate: String = "",
    val popularity: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("original_language") val originalLanguage: String = "",
    val adult: Boolean = false,
    @SerialName("media_type") val mediaType: String = "movie",
    val job: String = "",
    val department: String = ""
)

@Serializable
data class GenreListDto(
    val genres: List<GenreDto> = emptyList()
)
