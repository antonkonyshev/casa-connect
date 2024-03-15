package com.github.antonkonyshev.casaconnect.presentation

import android.content.Context
import com.github.antonkonyshev.casaconnect.R
import javax.inject.Singleton

@Singleton
class LocalizationUtils {
    companion object {
        fun localizeDefaultServiceName(name: String, context: Context): String {
            return when (name) {
                "Room" -> context.getString(R.string.room)
                "Kitchen" -> context.getString(R.string.kitchen)
                "Bathroom" -> context.getString(R.string.bathroom)
                "Balcony" -> context.getString(R.string.balcony)
                "Vestibule" -> context.getString(R.string.vestibule)
                "Entrance" -> context.getString(R.string.entrance)
                "Outdoors" -> context.getString(R.string.outdoors)
                "Yard" -> context.getString(R.string.yard)
                "Sauna" -> context.getString(R.string.sauna)
                else -> name
            }
        }
    }
}
