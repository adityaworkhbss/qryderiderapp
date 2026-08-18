package com.qryde.qryderiderapp.presentation.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@Composable
fun TripsScreen(
    onViewReceipt: (tripId: String) -> Unit,
    onCancelTrip: (tripId: String) -> Unit,
    onBookAgain: () -> Unit,
    onNewTrip: () -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val trips by viewModel.trips.collectAsStateWithLifecycle()

    TripsContent(
        trips = trips,
        selectedStatus = selectedStatus,
        onStatusSelected = viewModel::onStatusSelected,
        onViewReceipt = onViewReceipt,
        onCancelTrip = onCancelTrip,
        onBookAgain = onBookAgain,
        onNewTrip = onNewTrip
    )
}

@Composable
private fun TripsContent(
    trips: List<TripSummary>,
    selectedStatus: TripStatus,
    onStatusSelected: (TripStatus) -> Unit,
    onViewReceipt: (String) -> Unit,
    onCancelTrip: (String) -> Unit,
    onBookAgain: () -> Unit,
    onNewTrip: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewTrip, containerColor = QrydePrimary) {
                Icon(Icons.Filled.Add, contentDescription = "Book a trip", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                "Trips",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(QrydeFieldBackground)
            ) {
                TripStatus.entries.forEach { status ->
                    val label = status.name.lowercase().replaceFirstChar(Char::uppercase)
                    val selected = status == selectedStatus
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (selected) QrydePrimary else Color.Transparent)
                            .clickable { onStatusSelected(status) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (selected) Color.White else Color(0xFF6B6B6B), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filtered = trips.filter { it.status == selectedStatus }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { trip ->
                    TripCard(
                        trip = trip,
                        onViewReceipt = { onViewReceipt(trip.id) },
                        onCancelTrip = { onCancelTrip(trip.id) },
                        onBookAgain = onBookAgain
                    )
                }
            }
        }
    }
}

@Composable
private fun TripCard(
    trip: TripSummary,
    onViewReceipt: () -> Unit,
    onCancelTrip: () -> Unit,
    onBookAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = QrydePrimary, modifier = Modifier.padding(end = 8.dp))
            Text(trip.dateLabel, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = QrydePrimary, modifier = Modifier.padding(end = 8.dp))
            Column {
                Text("PICKUP", style = MaterialTheme.typography.labelLarge, color = QrydePrimary, fontWeight = FontWeight.Bold)
                Text(trip.pickup, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.padding(end = 8.dp))
            Column {
                Text("DROP-OFF", style = MaterialTheme.typography.labelLarge, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                Text(trip.dropoff, fontWeight = FontWeight.Medium)
                trip.dropoffDetail?.let {
                    Text(it, style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            when (trip.status) {
                TripStatus.UPCOMING -> {
                    OutlinedButton(onClick = onCancelTrip, shape = RoundedCornerShape(24.dp)) {
                        Text("Cancel Trip")
                    }
                }
                TripStatus.COMPLETED -> {
                    TextButton(onClick = onViewReceipt) {
                        Text("View Receipt", color = QrydePrimary, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(onClick = onBookAgain, shape = RoundedCornerShape(24.dp)) {
                        Text("Book Again")
                    }
                }
                TripStatus.CANCELLED -> {
                    OutlinedButton(onClick = onBookAgain, shape = RoundedCornerShape(24.dp)) {
                        Text("Book Again")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripsContentPreview() {
    TripsContent(
        trips = SampleTrips,
        selectedStatus = TripStatus.COMPLETED,
        onStatusSelected = {},
        onViewReceipt = {},
        onCancelTrip = {},
        onBookAgain = {},
        onNewTrip = {}
    )
}
