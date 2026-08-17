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

    @GET("trending/movie/week")
    suspend fun trendingMovies(@Query("page") page: Int): MoviePageDto

    @GET("trending/tv/week")
    suspend fun trendingTv(@Query("page") page: Int): TvPageDto

    @GET("movie/now_playing")
    suspend fun nowPlaying(@Query("page") page: Int, @Query("region") region: String): MoviePageDto

    @GET("movie/upcoming")
    suspend fun upcoming(@Query("page") page: Int, @Query("region") region: String): MoviePageDto

    @GET("tv/airing_today")
    suspend fun airingToday(@Query("page") page: Int): TvPageDto

    @GET("tv/on_the_air")
    suspend fun onTheAir(@Query("page") page: Int): TvPageDto

    @GET("movie/{id}/credits")
    suspend fun movieCredits(@Path("id") id: String): CreditsDto

    @GET("tv/{id}/aggregate_credits")
    suspend fun tvCredits(@Path("id") id: String): AggregateCreditsDto

    @GET("movie/{id}/recommendations")
    suspend fun movieRecommendations(@Path("id") id: String): MoviePageDto

    @GET("tv/{id}/recommendations")
    suspend fun tvRecommendations(@Path("id") id: String): TvPageDto

    @GET("movie/{id}/similar")
    suspend fun movieSimilar(@Path("id") id: String): MoviePageDto

    @GET("tv/{id}/similar")
    suspend fun tvSimilar(@Path("id") id: String): TvPageDto

    @GET("movie/{id}/watch/providers")
    suspend fun movieWatchProviders(@Path("id") id: String): WatchProvidersDto

    @GET("tv/{id}/watch/providers")
    suspend fun tvWatchProviders(@Path("id") id: String): WatchProvidersDto

    @GET("movie/{id}/release_dates")
    suspend fun movieReleaseDates(@Path("id") id: String): ReleaseDatesDto

    @GET("tv/{id}/content_ratings")
    suspend fun tvContentRatings(@Path("id") id: String): ContentRatingsDto

    @GET("movie/{id}/images")
    suspend fun movieImages(@Path("id") id: String): ImagesDto

    @GET("tv/{id}/images")
    suspend fun tvImages(@Path("id") id: String): ImagesDto

    @GET("person/{id}")
    suspend fun person(@Path("id") id: String): PersonDto

    @GET("person/{id}/combined_credits")
    suspend fun personCredits(@Path("id") id: String): PersonCreditsDto

    @GET("genre/movie/list")
    suspend fun movieGenres(): GenreListDto

    @GET("genre/tv/list")
    suspend fun tvGenres(): GenreListDto

    @GET("discover/tv")
    suspend fun discoverTv(
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") genres: String? = null,
        @Query("first_air_date_year") year: Int? = null,
        @Query("vote_average.gte") minScore: Float? = null,
        @Query("with_keywords") keywords: String? = null,
        @Query("with_companies") companies: String? = null,
        @Query("with_networks") networks: String? = null
    ): TvPageDto

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") genres: String? = null,
        @Query("primary_release_year") year: Int? = null,
        @Query("vote_average.gte") minScore: Float? = null,
        @Query("with_keywords") keywords: String? = null,
        @Query("with_companies") companies: String? = null
    ): MoviePageDto

    @GET("search/person")
    suspend fun searchPeople(@Query("query") query: String, @Query("page") page: Int): PersonSearchPageDto

    @GET("search/collection")
    suspend fun searchCollections(@Query("query") query: String, @Query("page") page: Int): CollectionSearchPageDto

    @GET("collection/{id}")
    suspend fun collection(@Path("id") id: String): CollectionDto

    @GET("movie/{id}/keywords")
    suspend fun movieKeywords(@Path("id") id: String): KeywordPageDto

    @GET("tv/{id}/keywords")
    suspend fun tvKeywords(@Path("id") id: String): KeywordPageDto

    @GET("person/{id}/images")
    suspend fun personImages(@Path("id") id: String): PersonImagesDto

    @GET("tv/{id}/season/{season_number}/episode/{episode_number}")
    suspend fun tvEpisode(
        @Path("id") id: String,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int
    ): TvEpisodeDto

    @GET("tv/{id}/season/{season_number}/episode/{episode_number}/credits")
    suspend fun tvEpisodeCredits(
        @Path("id") id: String,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int
    ): CreditsDto

    @GET("tv/{id}/season/{season_number}/episode/{episode_number}/videos")
    suspend fun tvEpisodeVideos(
        @Path("id") id: String,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int
    ): VideoPageDto

    @GET("tv/{id}/season/{season_number}/episode/{episode_number}/images")
    suspend fun tvEpisodeImages(
        @Path("id") id: String,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int
    ): ImagesDto
}
