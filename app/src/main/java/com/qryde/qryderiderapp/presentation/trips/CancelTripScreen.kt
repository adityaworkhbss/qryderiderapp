package com.qryde.qryderiderapp.presentation.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

private val CancelReasons = listOf(
    "No longer needed",
    "Wrong destination",
    "Wrong schedule",
    "Driver delay",
    "Other reason"
)

@Composable
fun CancelTripScreen(
    tripId: String,
    onBack: () -> Unit,
    onCancelConfirmed: (tripId: String, reason: String, notes: String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(CancelReasons.first()) }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(QrydePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(28.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Why are you cancelling?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "We're sorry to see you go. Please let us know why you're cancelling so we can improve.",
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CancelReasons.forEach { reason ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { selectedReason = reason }
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                RadioButton(
                    selected = reason == selectedReason,
                    onClick = { selectedReason = reason },
                    colors = RadioButtonDefaults.colors(selectedColor = QrydePrimary)
                )
                Text(reason, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Additional notes (optional)".uppercase(), style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { if (it.length <= 200) notes = it },
            placeholder = { Text("Tell us more…") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )
        Text(
            "${notes.length}/200",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF9AA0A6),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCancelConfirmed(tripId, selectedReason, notes) },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Cancel Trip", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CancelTripScreenPreview() {
    CancelTripScreen(tripId = "TR-2568", onBack = {}, onCancelConfirmed = { _, _, _ -> })
}
