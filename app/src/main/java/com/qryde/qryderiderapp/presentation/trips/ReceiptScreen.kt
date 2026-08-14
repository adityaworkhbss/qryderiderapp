package com.qryde.qryderiderapp.presentation.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@Composable
fun ReceiptScreen(
    tripId: String,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Receipt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Download, contentDescription = "Download", tint = QrydePrimary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Share, contentDescription = "Share", tint = QrydePrimary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Trip Id: $tripId", fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp)) {
                    Text("Paid", color = QrydePrimary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = QrydePrimary, modifier = Modifier.padding(end = 8.dp))
                Text("Today, 4:30 PM", color = Color(0xFF6B6B6B))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = QrydePrimary, modifier = Modifier.padding(end = 8.dp))
                Column {
                    Text("PICKUP", style = MaterialTheme.typography.labelLarge, color = QrydePrimary, fontWeight = FontWeight.Bold)
                    Text("123 Park Ave, New York", fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.padding(end = 8.dp))
                Column {
                    Text("DROP-OFF", style = MaterialTheme.typography.labelLarge, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                    Text("JFK International Airport", fontWeight = FontWeight.Medium)
                    Text("Terminal 4, Gate B23", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Fare Breakdown", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            FareRow(label = "Base Fare", amount = "$32.00")
            FareRow(label = "Distance", detail = "18.4 mi", amount = "$18.40")
            FareRow(label = "Wait Time", detail = "5 min", amount = "$2.50")
            FareRow(label = "Tax", amount = "$4.10")

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", color = QrydePrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("$57.00", color = QrydePrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Wallet,
                    contentDescription = null,
                    tint = QrydePrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(QrydeFieldBackground)
                        .padding(6.dp)
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Payment Method", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B))
                    Text("Wallet", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Download Receipt", color = QrydePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FareRow(label: String, detail: String? = null, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(label, color = Color(0xFF6B6B6B))
            detail?.let { Text(it, style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6)) }
        }
        Text(amount, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptScreenPreview() {
    ReceiptScreen(tripId = "TR-2568", onBack = {})
}
