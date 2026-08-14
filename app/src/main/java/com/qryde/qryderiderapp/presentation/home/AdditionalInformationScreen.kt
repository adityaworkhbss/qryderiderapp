package com.qryde.qryderiderapp.presentation.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.QrydeTextField

@Composable
fun AdditionalInformationScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: BookRideViewModel
) {
    val initial = viewModel.uiState.value

    var fundingSource by remember { mutableStateOf(initial.fundingSource) }
    var tripPurpose by remember { mutableStateOf(initial.tripPurpose) }
    var isPassengerExpanded by remember { mutableStateOf(true) }
    var passengerType by remember { mutableStateOf("") }
    var mobilityAids by remember { mutableStateOf("") }
    var isBookingAsCareGiver by remember { mutableStateOf(initial.isBookingAsCareGiver) }
    var careGiverFirstName by remember { mutableStateOf("") }
    var careGiverLastName by remember { mutableStateOf("") }
    var careGiverEmail by remember { mutableStateOf("") }
    var careGiverPhone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Additional information", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        QrydeTextField(
            value = fundingSource,
            onValueChange = { fundingSource = it },
            label = "Funding Source",
            placeholder = "Non_ADA",
            required = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydeTextField(
            value = tripPurpose,
            onValueChange = { tripPurpose = it },
            label = "Trip Purpose",
            placeholder = "Non_ADA",
            required = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Additional Passengers", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(QrydePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add passenger", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(QrydeFieldBackground)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isPassengerExpanded = !isPassengerExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(18.dp))
                    Text("Passenger 1", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp))
                }
                Icon(
                    if (isPassengerExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            if (isPassengerExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                DropdownField(
                    label = "Passenger Type",
                    value = passengerType,
                    placeholder = "Select",
                    options = listOf("Adult", "Child", "Senior"),
                    onSelected = { passengerType = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                DropdownField(
                    label = "Mobility Aids",
                    value = mobilityAids,
                    placeholder = "Select",
                    options = listOf("None", "Wheelchair", "Walker", "Cane"),
                    onSelected = { mobilityAids = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Booking as a Car Giver", fontWeight = FontWeight.Bold)
            Switch(
                checked = isBookingAsCareGiver,
                onCheckedChange = { isBookingAsCareGiver = it },
                colors = SwitchDefaults.colors(checkedTrackColor = QrydePrimary)
            )
        }

        if (isBookingAsCareGiver) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(QrydeFieldBackground)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(18.dp))
                    Text("Details", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                QrydeTextField(
                    value = careGiverFirstName,
                    onValueChange = { careGiverFirstName = it },
                    label = "First Name",
                    placeholder = "eg: John",
                    required = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                QrydeTextField(
                    value = careGiverLastName,
                    onValueChange = { careGiverLastName = it },
                    label = "Last Name",
                    placeholder = "Smith",
                    required = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                QrydeTextField(
                    value = careGiverEmail,
                    onValueChange = { careGiverEmail = it },
                    label = "Email Address",
                    placeholder = "example@example.com"
                )
                Spacer(modifier = Modifier.height(12.dp))
                QrydeTextField(
                    value = careGiverPhone,
                    onValueChange = { careGiverPhone = it },
                    label = "Phone Number",
                    placeholder = "(123) 456-7890",
                    required = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.onAdditionalInfoSaved(fundingSource, tripPurpose, isBookingAsCareGiver)
                onSaved()
            },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Save", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label + " *", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 16.dp)
            ) {
                Text(value.ifBlank { placeholder }, color = if (value.isBlank()) Color(0xFF9E9E9E) else Color.Unspecified)
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF9AA0A6))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
