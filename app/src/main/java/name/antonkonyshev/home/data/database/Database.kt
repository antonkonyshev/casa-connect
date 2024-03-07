package name.antonkonyshev.home.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.InetAddress
import java.util.Date
import javax.inject.Singleton
import kotlin.math.roundToLong

@Singleton
@TypeConverters(Converters::class)
@Database(entities = [DeviceModel::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var _instance: AppDatabase? = null

        fun instance(context: Context): AppDatabase {
            return (_instance ?: synchronized(this) {
                _instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "home_database"
                ).build()
                return@synchronized _instance
            })!!
        }
    }
}

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listStringAdapter: JsonAdapter<List<String>> by lazy {
        return@lazy moshi.adapter(Types.newParameterizedType(List::class.java, String::class.java))
    }

    @TypeConverter
    fun listStringToJsonString(value: List<String>?): String? {
        return listStringAdapter.toJson(value)
    }

    @TypeConverter
    fun jsonStringToListString(value: String?): List<String>? {
        return value?.let { listStringAdapter.fromJson(it) }
    }

    @TypeConverter
    fun inetAddressToString(value: InetAddress?): String? {
        return value?.hostAddress
    }

    @TypeConverter
    fun stringToInetAddress(value: String?): InetAddress? {
        return if (!value.isNullOrEmpty()) InetAddress.getByName(value) else null
    }

    @TypeConverter
    fun dateToLong(value: Date?): Long? {
        return value?.let { (it.time / 1000.0F).roundToLong() }
    }

    @TypeConverter
    fun longToDate(value: Long?): Date? {
        return value?.let { Date(value * 1000) }
    }
}