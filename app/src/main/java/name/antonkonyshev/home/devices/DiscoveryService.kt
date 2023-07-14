package name.antonkonyshev.home.devices

import android.net.LinkAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun getDeviceInfo(ip: Inet4Address) = withContext(Dispatchers.IO) {
    try {
        if (!ip.isReachable(2000)) {
            return@withContext false
        } else {
            return@withContext true
        }
    } catch (err: Exception) {
        return@withContext false
    }
    /*
    val client = OkHttpClient()
    val req = Request.Builder().url("http://" + ip.hostAddress + "/service").build()
    client.newCall(req).enqueue(object: Callback {
        override fun onFailure(call: Call, e: IOException) {
            it.resume(false)
        }
        override fun onResponse(call: Call, response: Response) {
            if (response.code == 200) {
                it.resume(true)
            }
            it.resume(false)
        }
    })
    */
}

fun discoverLocalDevices(linkAddress: LinkAddress) {
    val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter {
        it == linkAddress.address.hostAddress
    }
}
