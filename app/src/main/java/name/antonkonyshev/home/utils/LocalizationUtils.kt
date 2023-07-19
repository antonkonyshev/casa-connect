package name.antonkonyshev.home.utils

import android.content.Context
import name.antonkonyshev.home.R

fun localizeDefaultServiceName(name: String, context: Context): String {
    when (name) {
        "Room" -> return context.getString(R.string.room)
        "Kitchen" -> return context.getString(R.string.kitchen)
        "Bathroom" -> return context.getString(R.string.bathroom)
        "Balcony" -> return context.getString(R.string.balcony)
        "Vestibule" -> return context.getString(R.string.vestibule)
        "Entrance" -> return context.getString(R.string.entrance)
        "Outdoors" -> return context.getString(R.string.outdoors)
        "Yard" -> return context.getString(R.string.yard)
        "Sauna" -> return context.getString(R.string.sauna)
        else -> return name
    }
}