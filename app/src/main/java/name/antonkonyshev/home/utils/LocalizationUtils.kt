package name.antonkonyshev.home.utils

import android.content.Context
import android.os.Build
import java.util.Locale

fun getLocalUnits(units: String, context: Context): String {
    when (context.getResources().getConfiguration().getLocales().get(0).language) {
        "ru" ->
            when (units) {
                "mmHg" -> return "мм рт. ст."
                "m" -> return "м"
                "mgm3" -> return "мг/м3"
                "records" -> return "записей"
                else -> return units
            }
        else -> return units
    }
    return units
}

// TODO: better localization for units and default device names
fun getLocalServiceName(name: String, context: Context): String {
    when (context.getResources().getConfiguration().getLocales().get(0).language) {
        "ru" ->
            when (name.lowercase()) {
                "room" -> return "Комната"
                "kitchen" -> return "Кухня"
                "bathroom" -> return "Ванная"
                "balcony" -> return "Балкон"
                "vestibule" -> return "Прихожая"
                "outdoors" -> return "Улица"
                "yard" -> return "Двор"
                "sauna" -> return "Баня"
                else -> return name
            }
        else -> return name
    }
    return name
}