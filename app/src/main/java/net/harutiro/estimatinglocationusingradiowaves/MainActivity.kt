package net.harutiro.estimatinglocationusingradiowaves

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.harutiro.estimatinglocationusingradiowaves.ui.theme.EstimatingLocationUsingRadioWavesTheme
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : ComponentActivity() {

    //tagName
    private val TAG: String = "MainActivity"
    // iBeaconのデータを認識するためのParserフォーマット
    val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    //パーミッション確認用のコード
    private val PERMISSION_REQUEST_CODE = 1
    //出力結果を保存する場所
    var outputText = ""

    //どのパーミッションを許可したいかリスト化する
    val permissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )
    }else{
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //パーミッション確認
        //TODO:ロケーションの取得の常に許可をできるようにする
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            // パーミッションが許可されていない時の処理
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", PERMISSION_REQUEST_CODE, *permissions)
        }

        //取得した時の動作部分
        val rangingObserver = Observer<Collection<Beacon>> { beacons ->
            Log.d(TAG + "_BLE", "Ranged: ${beacons.count()} beacons")
            outputText += "Ranged: ${beacons.count()} beacons\n"

            for (beacon: Beacon in beacons) {
                Log.d(TAG + "_BLE", "$beacon about ${beacon.rssi} meters away")
                outputText += "$beacon about ${beacon.rssi} meters away\n"
            }
        }

        //パーミッションが許可された時にIbeaconが動く
        if(EasyPermissions.hasPermissions(this, *permissions)){
            //絞り込みをする部分
            //今回nullなので、全てを取得する。
            //id1:uuid id2:major id3:minor
            val mRegion = Region("unique-id-001", null, null, null)

            val beaconManager = BeaconManager.getInstanceForApplication(this)
            // Set up a Live Data observer so this Activity can get ranging callbacks
            // observer will be called each time the monitored regionState changes (inside vs. outside region)
            beaconManager.getRegionViewModel(mRegion).rangedBeacons.observe(this, rangingObserver)
            beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_FORMAT))
            beaconManager.startRangingBeacons(mRegion)
        }

        // wifi系
        val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                Log.d(TAG + "_WiFi","なんか受信はしたっぽい")
                Log.d(TAG + "_WiFi",success.toString())
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val results = wifiManager.scanResults
                    for( item in results){
                        Log.d(TAG + "_WiFi","timestamp:${item.timestamp} SSID: ${item.SSID}, rssi: ${item.level}, BSSID: ${item.BSSID}")
                    }
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        this.registerReceiver(wifiScanReceiver, intentFilter)

        setContent {
            EstimatingLocationUsingRadioWavesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                    Button(onClick = {
                        // CoroutineScopeを作成
                        CoroutineScope(Dispatchers.Main).launch {
                            // 3秒間隔でスキャンを行う10回
                            repeat(10) {
                                // 以下の処理はUIスレッド以外で行う
                                withContext(Dispatchers.IO) {
                                    wifiManager.startScan()
                                }
                                delay(3000)
                            }
                        }
                    }) {
                        Text(text = "Start")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EstimatingLocationUsingRadioWavesTheme {
        Greeting("Android")
    }
}