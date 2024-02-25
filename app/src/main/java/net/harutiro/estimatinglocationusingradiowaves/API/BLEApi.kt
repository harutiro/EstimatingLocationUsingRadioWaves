package net.harutiro.estimatinglocationusingradiowaves.API

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import pub.devrel.easypermissions.EasyPermissions

class BLEApi {

    // iBeaconのデータを認識するためのParserフォーマット
    val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    //パーミッション確認用のコード
    private val PERMISSION_REQUEST_CODE = 1
    //出力結果を保存する場所
    var outputText = ""


    val permissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
        )
    }else{
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    fun getPermission(context: Context, activity: Activity){
        //パーミッション確認
        if (!EasyPermissions.hasPermissions(context, *permissions)) {
            // パーミッションが許可されていない時の処理
            EasyPermissions.requestPermissions(activity, "パーミッションに関する説明", PERMISSION_REQUEST_CODE, *permissions)
        }
    }

    fun startBLEBeaconScan(context: Context, lifecycleOwner: LifecycleOwner , rangingObserver:Observer<Collection<Beacon>>){
        //パーミッションが許可された時にIbeaconが動く
        if(EasyPermissions.hasPermissions(context, *permissions)){
            //絞り込みをする部分
            //今回nullなので、全てを取得する。
            //id1:uuid id2:major id3:minor
            val mRegion = Region("unique-id-001", null, null, null)

            val beaconManager = BeaconManager.getInstanceForApplication(context)
            // Set up a Live Data observer so this Activity can get ranging callbacks
            // observer will be called each time the monitored regionState changes (inside vs. outside region)
            beaconManager.getRegionViewModel(mRegion).rangedBeacons.observe(lifecycleOwner, rangingObserver)
            beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_FORMAT))
            beaconManager.startRangingBeacons(mRegion)
        }
    }

    fun stopBLEBeaconScan(context:Context){
        val beaconManager = BeaconManager.getInstanceForApplication(context)
        beaconManager.removeAllRangeNotifiers()
    }
}