package com.qryde.qryderiderapp.presentation.vui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@Composable
fun VuiScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(QrydeFieldBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Vui Assistant",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Ask Vui to book a ride, check your trips, or manage your account — coming soon.",
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VuiScreenPreview() {
    VuiScreen()
}
