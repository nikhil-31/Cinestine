package nikhil.cinestine.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import nikhil.cinestine.model.Movie

class AppAnalytics(context: Context) {
    private val firebase = FirebaseAnalytics.getInstance(context.applicationContext)

    fun screen(name: String, clazz: String) {
        firebase.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, name)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, clazz)
        })
    }

    fun viewItem(movie: Movie) {
        firebase.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, itemParams(movie))
    }

    fun search(term: String, scope: String, resultCount: Int) {
        firebase.logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, term.take(100))
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, scope)
            putLong("number_of_results", resultCount.toLong())
        })
    }

    fun setSaved(movie: Movie, saved: Boolean) {
        val event = if (saved) {
            FirebaseAnalytics.Event.ADD_TO_WISHLIST
        } else {
            "remove_from_wishlist"
        }
        firebase.logEvent(event, itemParams(movie))
    }

    fun share(movie: Movie) {
        firebase.logEvent(FirebaseAnalytics.Event.SHARE, itemParams(movie).apply {
            putString(FirebaseAnalytics.Param.METHOD, "tmdb_link")
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, movie.mediaType.name.lowercase())
        })
    }

    private fun itemParams(movie: Movie): Bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.ITEM_ID, movie.id)
        putString(FirebaseAnalytics.Param.ITEM_NAME, movie.title.take(100))
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, movie.mediaType.name.lowercase())
    }
}
