package nikhil.cinestine

import android.app.Application
import android.content.Context
import androidx.room.Room
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
    lateinit var repository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        ThemePreferences.apply(this)
        val database = Room.databaseBuilder(this, AppDatabase::class.java, "cinestine.db")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val url = request.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                    .build()
                chain.proceed(request.newBuilder().url(url).build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
        val api = Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TmdbApi::class.java)
        repository = MovieRepository(api, database.favouriteDao()) {
            RegionPreferences.region(this)
        }
    }

    private companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    }
}

val Context.cinestineApp: CinestineApp
    get() = applicationContext as CinestineApp
