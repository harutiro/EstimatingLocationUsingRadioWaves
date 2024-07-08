package net.harutiro.estimatinglocationusingradiowaves.Presenter.ui.view

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.harutiro.estimatinglocationusingradiowaves.Presenter.ui.theme.EstimatingLocationUsingRadioWavesTheme
import net.harutiro.estimatinglocationusingradiowaves.ViewModel.MainViewModel
import java.io.File
import java.util.concurrent.TimeUnit

@Composable
fun MainView(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val TAG = "MainView"

    val context = LocalContext.current
    val activity: Activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    var filePathList = remember { mutableStateListOf<File>() }
    val isLoading = remember { mutableStateOf(false) }

    var bleFile by remember { mutableStateOf<File?>(null) }
    var wifiFile by remember { mutableStateOf<File?>(null) }

    // 最初にFileScanを行う
    LaunchedEffect(isLoading){
        filePathList.clear()
        filePathList.addAll(viewModel.scanFile())
    }

    LaunchedEffect(Unit){
        filePathList.clear()
        filePathList.addAll(viewModel.scanFile())
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ){
            Button(onClick = {
                // CoroutineScopeを作成
                viewModel.addSensor(lifecycleOwner)
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.start("pixel4")
                }
                isLoading.value = true
            }) {
                Text(text = "Start")
            }

            Button(onClick = {
                // CoroutineScopeを作成
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getPermission(activity)
                }
            }) {
                Text(text = "Permission")
            }

            Button(onClick = {
                // CoroutineScopeを作成
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.stop {
                        isLoading.value = false
                    }
                }
            }) {
                Text(text = "Stop")
            }

            Button(onClick = {
                // CoroutineScopeを作成
                isLoading.value = true
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.timerStart("pixel4") {
                        // stopの処理
                        isLoading.value = false
                    }
                }
            }) {
                Text(text = "10秒間のタイマーです。")
            }

            filePathList.forEach { filePath ->
                Text(
                    text = filePath.name,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .clickable {
                            if (filePath.name.contains("BLE")) {
                                bleFile = filePath
                            } else if (filePath.name.contains("WiFi")) {
                                wifiFile = filePath
                            }
                        }
                )
            }

            Button(
                onClick = {
                    if (wifiFile != null && bleFile != null) {
                        viewModel.postCsvData(wifiFile!!, bleFile!!)
                    }
                },
                enabled = wifiFile != null && bleFile != null
            ) {
                Text(text = "送信する")
            }
            Text(text = "wifiFile: ${wifiFile?.name}")
            Text(text = "bleFile: ${bleFile?.name}")
        }

        if(isLoading.value){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EstimatingLocationUsingRadioWavesTheme {
        MainView()
    }
}