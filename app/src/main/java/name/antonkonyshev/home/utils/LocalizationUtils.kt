package name.antonkonyshev.home.utils

import android.content.Context
import android.os.Build
import java.util.Locale

fun getCurrentLocale(context: Context): Locale {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return context.getResources().getConfiguration().getLocales().get(0)
    } else {
        return context.getResources().getConfiguration().locale
    }
}

fun getLocalUnits(units: String, context: Context): String {
    when (getCurrentLocale(context).language) {
        "ru" ->
            when (units) {
                "mmHg" -> return "мм рт. ст."
                "m" -> return "м"
                "records" -> return "записей"
                else -> return units
            }
        else -> return units
    }
    return units
}