package nikhil.cinestine.data

import nikhil.cinestine.data.local.FavouriteDao
import nikhil.cinestine.data.local.toEntity
import nikhil.cinestine.data.local.toMovie
import nikhil.cinestine.data.remote.MovieDto
import nikhil.cinestine.data.remote.TmdbApi
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import nikhil.cinestine.model.Review
import nikhil.cinestine.model.Trailer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepository(
    private val api: TmdbApi,
    private val favouriteDao: FavouriteDao
) {
    suspend fun movies(category: MovieCategory, page: Int): List<Movie> {
        val response = when (category) {
            MovieCategory.POPULAR -> api.popular(page)
            MovieCategory.TOP_RATED -> api.topRated(page)
        }
        return response.results.map { it.toMovie() }
    }

    suspend fun search(query: String, page: Int): List<Movie> {
        return api.search(query, page).results.map { it.toMovie() }
    }

    suspend fun trailers(movieId: String): List<Trailer> {
        return api.videos(movieId).results
            .filter { it.site.equals("YouTube", ignoreCase = true) && it.key.isNotBlank() }
            .map { Trailer(id = it.id, key = it.key, name = it.name, site = it.site) }
    }

    suspend fun reviews(movieId: String): List<Review> {
        return api.reviews(movieId).results.map {
            Review(id = it.id, author = it.author, content = it.content)
        }
    }

    fun observeFavourites(): Flow<List<Movie>> {
        return favouriteDao.observeAll().map { list -> list.map { it.toMovie() } }
    }

    fun observeFavouriteIds(): Flow<Set<String>> {
        return favouriteDao.observeAll().map { list -> list.map { it.id }.toSet() }
    }

    fun observeIsFavourite(id: String): Flow<Boolean> = favouriteDao.observeIsFavourite(id)

    suspend fun toggleFavourite(movie: Movie, currentlyFavourite: Boolean) {
        if (currentlyFavourite) {
            favouriteDao.delete(movie.id)
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
            adult = adult.toString()
        )
    }

    private fun String?.toImageUrl(): String {
        val path = this?.takeIf { it.isNotBlank() && it != "null" } ?: return ""
        return IMAGE_BASE_URL + path
    }

    private companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}
