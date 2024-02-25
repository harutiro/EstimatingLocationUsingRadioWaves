package net.harutiro.estimatinglocationusingradiowaves.Presenter.ui.view

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.harutiro.estimatinglocationusingradiowaves.Presenter.ui.theme.EstimatingLocationUsingRadioWavesTheme
import net.harutiro.estimatinglocationusingradiowaves.ViewModel.MainViewModel

@Composable
fun MainView(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val context = LocalContext.current
    val activity: Activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    Column {
        Button(onClick = {
            // CoroutineScopeを作成
            viewModel.addSensor(lifecycleOwner)
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.start("pixel4")
            }
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
                viewModel.stop{}
            }
        }) {
            Text(text = "Stop")
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