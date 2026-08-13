package com.qryde.qryderiderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.qryde.qryderiderapp.core.designsystem.QrydeRiderTheme
import com.qryde.qryderiderapp.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrydeRiderTheme {
                AppNavGraph()
            }
        }
    }
}
