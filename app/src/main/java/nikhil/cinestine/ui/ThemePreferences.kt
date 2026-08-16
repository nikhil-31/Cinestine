package nikhil.cinestine.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

object ThemePreferences {
    private const val PREFS = "theme"
    private const val KEY_NIGHT_MODE = "night_mode"

    fun apply(context: Context) {
        AppCompatDelegate.setDefaultNightMode(nightMode(context))
    }

    fun nightMode(context: Context): Int {
        return prefs(context).getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setNightMode(context: Context, mode: Int) {
        prefs(context).edit().putInt(KEY_NIGHT_MODE, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun toggleLightDark(context: Context) {
        val next = if (isNight(context)) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        setNightMode(context, next)
    }

    fun isNight(context: Context): Boolean {
        val mask = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mask == Configuration.UI_MODE_NIGHT_YES
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
