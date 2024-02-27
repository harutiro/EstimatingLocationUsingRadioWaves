package net.harutiro.estimatinglocationusingradiowaves.Repository

import android.app.Activity
import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.harutiro.estimatinglocationusingradiowaves.API.WiFiApi
import net.harutiro.estimatinglocationusingradiowaves.Utils.DateUtils
import net.harutiro.estimatinglocationusingradiowaves.Utils.extension.SensorExtension

class WiFiRepository(context: Context) : SensorBase(context) {

    override val sensorType: Int = SensorExtension.TYPE_WIFI
    override val sensorName: String = "WiFi"

    val TAG: String = "WiFiRepository"

    @Volatile
    private var isScanning = false

    val wifiApi = WiFiApi()

    fun getPermission(activity: Activity){
        wifiApi.getPermission(context, activity)
    }


    override suspend fun start(filename: String, samplingFrequency: Double) {
        super.start(filename, samplingFrequency)
        wifiApi.getScanResults(context){ scanResults ->
            val time = DateUtils.getTimeStamp()
            for (scanResult in scanResults){
                val data = "$time , ${scanResult.BSSID} , ${scanResult.level}"
                addQueue(
                    sensorName = sensorName,
                    timeStamp = time,
                    data = data
                )
            }
        }

        val scanInterval = 1000L
        isScanning = true

        // 以下の処理を止めるまで繰り返す
        while(isScanning){
            withContext(Dispatchers.Default){
                wifiApi.scanWiFi(context)
                delay(scanInterval)
            }
        }
    }

    override fun stop(): Completable {
        isScanning = false
        return super.stop()
    }
}