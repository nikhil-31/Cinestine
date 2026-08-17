package nikhil.cinestine.data.local

import androidx.room.Entity
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie

@Entity(tableName = "favourites", primaryKeys = ["id", "mediaType"])
data class FavouriteEntity(
    val id: String,
    val mediaType: String,
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
    val savedAt: Long = 0L
)

fun FavouriteEntity.toMovie() = Movie(
    id = id,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    popularity = popularity,
    voteCount = voteCount,
    originalLanguage = originalLanguage,
    adult = adult,
    mediaType = runCatching { MediaType.valueOf(mediaType) }.getOrDefault(MediaType.MOVIE),
    savedAt = savedAt
)

fun Movie.toEntity() = FavouriteEntity(
    id = id,
    mediaType = mediaType.name,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    popularity = popularity,
    voteCount = voteCount,
    originalLanguage = originalLanguage,
    adult = adult,
    savedAt = if (savedAt > 0L) savedAt else System.currentTimeMillis()
)
