package nikhil.cinestine.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import nikhil.cinestine.R
import java.util.Locale

object RegionPreferences {
    private const val PREFS = "region"
    private const val KEY_REGION = "watch_region"

    val options = listOf(
        "US", "GB", "CA", "AU", "IN", "DE", "FR", "ES", "IT",
        "JP", "KR", "BR", "MX", "NL", "IE", "NZ", "SG"
    )

    fun region(context: Context): String {
        val stored = storedCode(context)
        if (stored.length == 2) return stored
        val country = Locale.getDefault().country
        return if (country.length == 2) country else "US"
    }

    fun storedCode(context: Context): String =
        prefs(context).getString(KEY_REGION, "").orEmpty()

    fun setRegion(context: Context, code: String) {
        prefs(context).edit().putString(KEY_REGION, code).apply()
    }

    fun showPicker(context: Context, onPicked: () -> Unit) {
        val codes = listOf("") + options
        val labels = codes.map { code ->
            if (code.isEmpty()) {
                context.getString(R.string.region_device)
            } else {
                val name = Locale.Builder().setRegion(code).build().displayCountry.ifBlank { code }
                "$name ($code)"
            }
        }
        val current = storedCode(context)
        val checked = codes.indexOf(current).coerceAtLeast(0)
        AlertDialog.Builder(context)
            .setTitle(R.string.action_watch_region)
            .setSingleChoiceItems(labels.toTypedArray(), checked) { dialog, which ->
                setRegion(context, codes[which])
                dialog.dismiss()
                onPicked()
            }
            .show()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
