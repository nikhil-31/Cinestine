package nikhil.cinestine.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun popular(@Query("page") page: Int): MoviePageDto

    @GET("movie/top_rated")
    suspend fun topRated(@Query("page") page: Int): MoviePageDto

    @GET("search/movie")
    suspend fun search(@Query("query") query: String, @Query("page") page: Int): MoviePageDto

    @GET("movie/{id}/videos")
    suspend fun videos(@Path("id") id: String): VideoPageDto

    @GET("movie/{id}/reviews")
    suspend fun reviews(@Path("id") id: String): ReviewPageDto
}
