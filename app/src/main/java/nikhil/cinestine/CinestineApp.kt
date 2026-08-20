package nikhil.cinestine

import android.app.Application
import android.content.Context
import androidx.room.Room
import nikhil.cinestine.analytics.AppAnalytics
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.data.local.AppDatabase
import nikhil.cinestine.data.remote.TmdbApi
import nikhil.cinestine.ui.RegionPreferences
import nikhil.cinestine.ui.ThemePreferences
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class CinestineApp : Application() {
    lateinit var analytics: AppAnalytics
        private set
    lateinit var repository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        ThemePreferences.apply(this)
        analytics = AppAnalytics(this)
        val database = Room.databaseBuilder(this, AppDatabase::class.java, "cinestine.db")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
            .build()
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    // Registered ahead of the auth interceptor so the logged URL never carries the API key.
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                }
            }
            .addInterceptor { chain ->
                val request = chain.request()
                val url = request.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                    .build()
                chain.proceed(request.newBuilder().url(url).build())
            }
            .build()
        val api = Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TmdbApi::class.java)
        repository = MovieRepository(
            api,
            database.favouriteDao(),
            database.recentDao(),
            regionProvider = { RegionPreferences.region(this) },
            onFavouriteChanged = { movie, saved -> analytics.setSaved(movie, saved) }
        )
    }

    private companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    }
}

val Context.cinestineApp: CinestineApp
    get() = applicationContext as CinestineApp
