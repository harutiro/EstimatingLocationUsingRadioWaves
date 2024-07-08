package net.harutiro.estimatinglocationusingradiowaves.ViewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.harutiro.estimatinglocationusingradiowaves.API.ApiResponse
import net.harutiro.estimatinglocationusingradiowaves.API.FileExplorerApi
import net.harutiro.estimatinglocationusingradiowaves.API.OtherFileStorageApi
import net.harutiro.estimatinglocationusingradiowaves.Repository.BLERepository
import net.harutiro.estimatinglocationusingradiowaves.Repository.SensingRepository
import net.harutiro.estimatinglocationusingradiowaves.Repository.SensorBase
import net.harutiro.estimatinglocationusingradiowaves.Repository.WiFiRepository
import java.io.File

class MainViewModel (application: Application): AndroidViewModel(application) {

    val TAG: String = "MainViewModel"

    var targetSensors: MutableList<SensorBase> = mutableListOf()
    private val context get() = getApplication<Application>().applicationContext

    val apiResponse = ApiResponse(context)

    val sensorRepository = SensingRepository(context)

    var sensorStartFlag = false

    val fileExplorerApi = FileExplorerApi(context)



    fun addSensor(lifecycleOwner: LifecycleOwner){
        val bleRepository = BLERepository(context)
        bleRepository.lifecycleOwner = lifecycleOwner
        targetSensors.add(bleRepository)
        targetSensors.add(WiFiRepository(context))
    }
    fun getPermission(activity: Activity){
        sensorRepository.getPermission(activity)
    }
    suspend fun start(fileName:String){

        val samplingFrequency = -1.0

        sensorRepository.sensorStart(
            fileName = fileName,
            sensors = targetSensors,
            samplingFrequency = samplingFrequency
        )
        sensorStartFlag = true
    }

    fun stop(onStopped:() -> Unit){
        sensorRepository.sensorStop(
            sensors = targetSensors,
            onStopped = { sensorFileList ->
                val bleFile = sensorFileList[0]
                val wifiFile = sensorFileList[1]

                if(bleFile != null && wifiFile != null){
                    apiResponse.postCsvData(wifiFile, bleFile)
                }
                onStopped()
            }
        )
        sensorStartFlag = false
    }

    fun timerStart(fileName:String,onStopped:() -> Unit){
        viewModelScope.launch {
            start(fileName)
            Log.d("Timer", "タイマー開始")
            delay(10000)
            Log.d("Timer", "タイマー終了")
            stop(onStopped)
            onStopped()
        }
    }

    fun scanFile() : List<File>{
        val fileList = fileExplorerApi.scanFile()
        Log.d(TAG,fileList.toString())
        return fileList
    }

    fun postCsvData(wifiFile: File, bleFile:File){
        apiResponse.postCsvData(wifiFile, bleFile)
    }
}