package com.qryde.qryderiderapp.presentation.splash

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.qryde.qryderiderapp.R
import com.qryde.qryderiderapp.core.permissions.RuntimePermissions

private val SplashBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2C771A),
        Color(0xFF256718),
        Color(0xFF1C5314),
        Color(0xFF164712),
        Color(0xFF103A0F)
    )
)

@Composable
fun SplashScreen(
    onConfigResolved: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { viewModel.onPermissionsResolved() }

    LaunchedEffect(Unit) {
        val missingPermissions = RuntimePermissions.REQUIRED.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isEmpty()) {
            viewModel.onPermissionsResolved()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                SplashNavigationEvent.ConfigResolved -> onConfigResolved()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    SplashContent(appName = viewModel.appName)
}

@Composable
private fun SplashContent(appName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackgroundGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.qryde_logo),
            contentDescription = appName,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashContentPreview() {
    SplashContent(appName = "QRyde")
}
