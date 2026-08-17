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

    @GET("tv/popular")
    suspend fun popularTv(@Query("page") page: Int): TvPageDto

    @GET("tv/top_rated")
    suspend fun topRatedTv(@Query("page") page: Int): TvPageDto

    @GET("search/tv")
    suspend fun searchTv(@Query("query") query: String, @Query("page") page: Int): TvPageDto

    @GET("tv/{id}/videos")
    suspend fun tvVideos(@Path("id") id: String): VideoPageDto

    @GET("tv/{id}/reviews")
    suspend fun tvReviews(@Path("id") id: String): ReviewPageDto

    @GET("movie/{id}")
    suspend fun movieDetails(@Path("id") id: String): MovieDetailsDto

    @GET("tv/{id}")
    suspend fun tvDetails(@Path("id") id: String): TvDetailsDto

    @GET("tv/{id}/season/{season_number}")
    suspend fun tvSeason(
        @Path("id") id: String,
        @Path("season_number") seasonNumber: Int
    ): TvSeasonDto
}
