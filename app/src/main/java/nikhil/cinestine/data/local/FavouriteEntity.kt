package nikhil.cinestine.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import nikhil.cinestine.model.Movie

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey val id: String,
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
    adult = adult
)

fun Movie.toEntity() = FavouriteEntity(
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
    adult = adult
)
