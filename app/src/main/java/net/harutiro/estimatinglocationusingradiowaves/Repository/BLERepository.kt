package net.harutiro.estimatinglocationusingradiowaves.Repository

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.reactivex.rxjava3.core.Completable
import net.harutiro.estimatinglocationusingradiowaves.API.BLEApi
import net.harutiro.estimatinglocationusingradiowaves.Utils.DateUtils
import net.harutiro.estimatinglocationusingradiowaves.Utils.extension.SensorExtension
import org.altbeacon.beacon.Beacon


class BLERepository(context: Context): SensorBase(context) {

    override val sensorType: Int = SensorExtension.TYPE_BLEBEACON
    override val sensorName: String = "BLEBeacon"

    var lifecycleOwner: LifecycleOwner? = null

    val TAG: String = "BLERepository"

    val bleApi = BLEApi()

    fun getPermission(activity: Activity){
        bleApi.getPermission(context, activity)
    }

    override suspend fun start(filename: String, samplingFrequency: Double) {
        super.start(filename, samplingFrequency)

        if (lifecycleOwner != null) {
            bleApi.startBLEBeaconScan(context, lifecycleOwner!!){ beacons: Collection<Beacon> ->
                //ここにビーコンの情報を受け取る処理を書く
                val time = DateUtils.getTimeStamp()

                for(beacon in beacons){
                    val uuid = beacon.id1
                    val rssi = beacon.rssi

                    val data = "$time , $uuid , $rssi"
                    addQueue(
                        sensorName = sensorName,
                        timeStamp = time,
                        data = data
                    )
                }
            }
        }
    }

    override fun stop(): Completable {
        bleApi.stopBLEBeaconScan(context)

        return super.stop()
    }
}