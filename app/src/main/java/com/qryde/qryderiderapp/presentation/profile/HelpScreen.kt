package com.qryde.qryderiderapp.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@Composable
fun HelpScreen(onBack: () -> Unit) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Help", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Qryde Corp. Support", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            HelpInfoRow(icon = Icons.Filled.Phone, text = "+1 (800) 555-0199")
            HelpInfoRow(icon = Icons.Filled.Email, text = "support@qryde.com")
            HelpInfoRow(icon = Icons.Filled.LocationOn, text = "500 Market Street, Suite 200, San Francisco, CA")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Send us a message", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Text("SUBJECT".uppercase(), style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            placeholder = { Text("What do you need help with?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("MESSAGE".uppercase(), style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Describe your issue…") },
            modifier = Modifier.fillMaxWidth().height(140.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Your message has been sent.", Toast.LENGTH_SHORT).show()
                subject = ""
                message = ""
            },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Send Message", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HelpInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, tint = QrydePrimary, modifier = Modifier.padding(end = 10.dp))
        Text(text, color = Color(0xFF6B6B6B))
    }
}

@Preview(showBackground = true)
@Composable
private fun HelpScreenPreview() {
    HelpScreen(onBack = {})
}
