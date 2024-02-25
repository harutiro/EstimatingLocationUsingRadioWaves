package net.harutiro.estimatinglocationusingradiowaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import net.harutiro.estimatinglocationusingradiowaves.Presenter.ui.theme.EstimatingLocationUsingRadioWavesTheme
import net.harutiro.estimatinglocationusingradiowaves.Presenter.ui.view.MainView


class MainActivity : ComponentActivity() {

    //tagName
    private val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EstimatingLocationUsingRadioWavesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainView()
                }
            }
        }
    }
}

