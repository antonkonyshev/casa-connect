package name.antonkonyshev.home

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.devices.Device
import java.net.InetAddress
import java.util.Date
import kotlin.math.roundToLong

@Dao
interface DeviceDao {
    @Query("select * from device order by ordering desc")
    fun getAll(): List<Device>

    @Query("select * from device order by ordering desc")
    fun getAllFlow(): Flow<List<Device>>

    @Query("select * from device where service = :service order by ordering desc")
    fun byService(service: String): List<Device>

    @Query("select * from device where service = :service order by ordering desc")
    fun byServiceFlow(service: String): Flow<List<Device>>

    @Query("select * from device where available = :available order by ordering desc")
    fun byAvailability(available: Boolean): List<Device>

    @Query("select * from device where id = :id limit 1")
    fun byId(id: String): Device

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: Device): Long

    @Query("update device set ip = :ip, available = :available, updated = strftime('%s', 'now') where id = :id")
    fun updateStateById(id: String, ip: InetAddress, available: Boolean? = true): Int

    @Query("update device set available = :available, updated = strftime('%s', 'now') where id = :id")
    fun updateAvailabilityById(id: String, available: Boolean): Int
}

@TypeConverters(Converters::class)
@Database(entities = [Device::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var _instance: AppDatabase? = null

        fun instance(context: Context): AppDatabase {
            return (_instance ?: synchronized(this) {
                _instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "home_database"
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
        if (value != null && value.isNotEmpty()) {
            return InetAddress.getByName(value)
        } else {
            return null
        }
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