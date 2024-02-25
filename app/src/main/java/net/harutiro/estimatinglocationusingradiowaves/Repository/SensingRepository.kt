package net.harutiro.estimatinglocationusingradiowaves.Repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class SensingRepository(context: Context) {

    val TAG: String = "SensingRepository"

    val wifiRepository = WiFiRepository(context)
    val bleRepository = BLERepository(context)

    fun getPermission(activity: Activity){
        wifiRepository.getPermission(activity)
        bleRepository.getPermission(activity)
    }

    suspend fun sensorStart(fileName:String, sensors: MutableList<SensorBase>, samplingFrequency:Double) {
        for (sensor in sensors) {
            sensor.init()
            sensor.start(
                filename = fileName,
                samplingFrequency = samplingFrequency
            )
            Log.d(TAG, "fileName = ${fileName}")
        }
    }

    fun sensorStop(sensors: MutableList<SensorBase>, onStopped:() -> Unit) {
        val a = Completable.concat(sensors.map { sensor ->
            sensor.stop()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "センサー停止 成功")
                    //センサーが終了した時にMainActivityに伝える。
                    onStopped()
                },
                { e ->
                    Log.e(TAG, "センサー停止 失敗", e)
                }
            )
    }

}