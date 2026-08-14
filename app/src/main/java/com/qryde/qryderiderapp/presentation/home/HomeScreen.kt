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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.CurrentLocationPinOverlay
import com.qryde.qryderiderapp.presentation.components.OsmMapView
import com.qryde.qryderiderapp.presentation.components.QrydeTextField

@Composable
fun HomeScreen(
    onOpenAdditionalInformation: () -> Unit,
    viewModel: BookRideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onDestinationSelected = viewModel::onDestinationSelected,
        onTripTypeChanged = viewModel::onTripTypeChanged,
        onDayToggled = viewModel::onDayToggled,
        onDatePickerRequested = viewModel::onDatePickerRequested,
        onDatePickerDismissed = viewModel::onDatePickerDismissed,
        onDateSelected = viewModel::onDateSelected,
        onTimePickerRequested = viewModel::onTimePickerRequested,
        onTimePickerDismissed = viewModel::onTimePickerDismissed,
        onTimeSelected = viewModel::onTimeSelected,
        onAlternatePhoneChanged = viewModel::onAlternatePhoneChanged,
        onEscortCountChanged = viewModel::onEscortCountChanged,
        onOpenAdditionalInformation = onOpenAdditionalInformation,
        onProceedToChooseService = viewModel::onProceedToChooseService,
        onServiceSelected = viewModel::onServiceSelected,
        onPaymentMethodSheetRequested = viewModel::onPaymentMethodSheetRequested,
        onPaymentMethodSheetDismissed = viewModel::onPaymentMethodSheetDismissed,
        onPaymentMethodSelected = viewModel::onPaymentMethodSelected,
        onBackToSearch = viewModel::onBackToSearch,
        onBackToRideDetails = viewModel::onBackToRideDetails,
        onBookingConfirmed = viewModel::onBookingConfirmed,
        onSetLocationOnMapRequested = viewModel::onSetLocationOnMapRequested,
        onMapPickerCenterChanged = viewModel::onMapPickerCenterChanged,
        onSetLocationSheetDismissed = viewModel::onSetLocationSheetDismissed,
        onLocationOnMapSaved = viewModel::onLocationOnMapSaved
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: BookRideUiState,
    onSearchQueryChanged: (String) -> Unit,
    onDestinationSelected: (String) -> Unit,
    onTripTypeChanged: (Boolean) -> Unit,
    onDayToggled: (Int) -> Unit,
    onDatePickerRequested: (DatePickerTarget) -> Unit,
    onDatePickerDismissed: () -> Unit,
    onDateSelected: (Long) -> Unit,
    onTimePickerRequested: () -> Unit,
    onTimePickerDismissed: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onAlternatePhoneChanged: (String) -> Unit,
    onEscortCountChanged: (Int) -> Unit,
    onOpenAdditionalInformation: () -> Unit,
    onProceedToChooseService: () -> Unit,
    onServiceSelected: (String) -> Unit,
    onPaymentMethodSheetRequested: () -> Unit,
    onPaymentMethodSheetDismissed: () -> Unit,
    onPaymentMethodSelected: (Boolean) -> Unit,
    onBackToSearch: () -> Unit,
    onBackToRideDetails: () -> Unit,
    onBookingConfirmed: () -> Unit,
    onSetLocationOnMapRequested: () -> Unit,
    onMapPickerCenterChanged: (Double, Double) -> Unit,
    onSetLocationSheetDismissed: () -> Unit,
    onLocationOnMapSaved: () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetPeekHeight = 420.dp,
        sheetContent = {
            when (uiState.step) {
                BookingStep.SEARCH -> SearchSheetContent(
                    uiState = uiState,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onDestinationSelected = onDestinationSelected,
                    onSetLocationOnMapRequested = onSetLocationOnMapRequested
                )
                BookingStep.RIDE_DETAILS -> RideDetailsSheetContent(
                    uiState = uiState,
                    onBack = onBackToSearch,
                    onTripTypeChanged = onTripTypeChanged,
                    onDayToggled = onDayToggled,
                    onDatePickerRequested = onDatePickerRequested,
                    onTimePickerRequested = onTimePickerRequested,
                    onAlternatePhoneChanged = onAlternatePhoneChanged,
                    onEscortCountChanged = onEscortCountChanged,
                    onOpenAdditionalInformation = onOpenAdditionalInformation,
                    onProceed = onProceedToChooseService
                )
                BookingStep.CHOOSE_SERVICE -> ChooseServiceSheetContent(
                    uiState = uiState,
                    onBack = onBackToRideDetails,
                    onServiceSelected = onServiceSelected,
                    onPaymentMethodSheetRequested = onPaymentMethodSheetRequested,
                    onBookNow = onBookingConfirmed
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            OsmMapView(modifier = Modifier.fillMaxSize())
            CurrentLocationPinOverlay(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (uiState.showDatePickerFor != null) {
        AppDatePickerDialog(onDismiss = onDatePickerDismissed, onDateSelected = onDateSelected)
    }
    if (uiState.showTimePicker) {
        AppTimePickerDialog(onDismiss = onTimePickerDismissed, onTimeSelected = onTimeSelected)
    }
    if (uiState.showPaymentMethodSheet) {
        SelectPaymentMethodSheet(onDismiss = onPaymentMethodSheetDismissed, onSelected = onPaymentMethodSelected)
    }
    if (uiState.showSetLocationSheet) {
        SetLocationOnMapSheet(
            latitude = uiState.mapPickerLatitude,
            longitude = uiState.mapPickerLongitude,
            onCenterChanged = onMapPickerCenterChanged,
            onDismiss = onSetLocationSheetDismissed,
            onSave = onLocationOnMapSaved
        )
    }
}

@Composable
private fun SearchSheetContent(
    uiState: BookRideUiState,
    onSearchQueryChanged: (String) -> Unit,
    onDestinationSelected: (String) -> Unit,
    onSetLocationOnMapRequested: () -> Unit
) {
    var isSearchFocused by remember { mutableStateOf(false) }
    val suggestions = remember(uiState.searchQuery, uiState.recentAddresses) {
        if (uiState.searchQuery.isBlank()) {
            uiState.recentAddresses
        } else {
            uiState.recentAddresses.filter {
                it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(uiState.searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("Where you want to go?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        QrydeTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged,
            label = "",
            placeholder = "Search your destination",
            leadingIcon = Icons.Filled.Search,
            onFocusChanged = { isSearchFocused = it }
        )

        if (isSearchFocused) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    if (suggestions.isEmpty()) {
                        Text(
                            "No matching addresses",
                            color = Color(0xFF9AA0A6),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    } else {
                        suggestions.forEach { address ->
                            RecentAddressRow(address = address, onClick = { onDestinationSelected(address.title) })
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickPlaceChip(
                    icon = Icons.Filled.Home,
                    title = "Home",
                    subtitle = "Sector 15, Noida, uttar...",
                    onClick = { onDestinationSelected("Sector 15, Noida, Uttar Pradesh") },
                    modifier = Modifier.weight(1f)
                )
                QuickPlaceChip(
                    icon = Icons.Filled.Business,
                    title = "Office",
                    subtitle = "Sector 18, Haryana",
                    onClick = { onDestinationSelected("Sector 18, Haryana") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onSetLocationOnMapRequested)
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = QrydePrimary)
                Text(
                    "Set Location on map",
                    color = QrydePrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuickPlaceChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(QrydeFieldBackground)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
            Text(subtitle, style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B), maxLines = 1)
        }
    }
}

@Composable
private fun RecentAddressRow(address: RecentAddress, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFF9AA0A6))
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(address.title, fontWeight = FontWeight.Medium)
            Text(address.subtitle, style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B))
        }
    }
}

@Composable
private fun RideDetailsSheetContent(
    uiState: BookRideUiState,
    onBack: () -> Unit,
    onTripTypeChanged: (Boolean) -> Unit,
    onDayToggled: (Int) -> Unit,
    onDatePickerRequested: (DatePickerTarget) -> Unit,
    onTimePickerRequested: () -> Unit,
    onAlternatePhoneChanged: (String) -> Unit,
    onEscortCountChanged: (Int) -> Unit,
    onOpenAdditionalInformation: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 4.dp).size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Ride Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(QrydeFieldBackground)
                .padding(12.dp)
        ) {
            Text(uiState.pickupAddress, fontWeight = FontWeight.Medium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(uiState.dropoffAddress.ifBlank { "Destination" }, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(QrydeFieldBackground)
        ) {
            TripTypeTab("Single Trip", selected = !uiState.isRecurring, modifier = Modifier.weight(1f)) {
                onTripTypeChanged(false)
            }
            TripTypeTab("Reccuring", selected = uiState.isRecurring, modifier = Modifier.weight(1f)) {
                onTripTypeChanged(true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Select Date and time", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isRecurring) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
                dayLabels.forEachIndexed { index, label ->
                    DayChip(
                        label = label,
                        selected = index in uiState.selectedDays,
                        onClick = { onDayToggled(index) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField(
                    label = uiState.startDateLabel.ifBlank { "Start Date" },
                    onClick = { onDatePickerRequested(DatePickerTarget.START) },
                    modifier = Modifier.weight(1f)
                )
                DateField(
                    label = uiState.endDateLabel.ifBlank { "End Date" },
                    onClick = { onDatePickerRequested(DatePickerTarget.END) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            DateField(
                label = uiState.timeLabel.ifBlank { "Time" },
                onClick = onTimePickerRequested,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField(
                    label = uiState.startDateLabel.ifBlank { "Start Date" },
                    onClick = { onDatePickerRequested(DatePickerTarget.SINGLE) },
                    modifier = Modifier.weight(1f)
                )
                DateField(
                    label = uiState.timeLabel.ifBlank { "Time" },
                    onClick = onTimePickerRequested,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenAdditionalInformation)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Additional Information" + if (uiState.hasAdditionalInfo) " ✓" else "",
                fontWeight = FontWeight.SemiBold,
                color = if (uiState.hasAdditionalInfo) QrydePrimary else MaterialTheme.colorScheme.onBackground
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9AA0A6)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Advanced option", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        QrydeTextField(
            value = uiState.alternatePhone,
            onValueChange = onAlternatePhoneChanged,
            label = "Alternative Phone(Alternative)",
            placeholder = "e.g- 9876543210",
            leadingIcon = Icons.Filled.Phone
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Number of Escorts", fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onEscortCountChanged(-1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease")
                }
                Text(uiState.escortCount.toString(), fontWeight = FontWeight.Bold)
                IconButton(onClick = { onEscortCountChanged(1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onProceed,
            enabled = uiState.canProceedFromRideDetails,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Book trip", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TripTypeTab(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) QrydePrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) Color.White else Color(0xFF6B6B6B), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (selected) QrydePrimary else QrydeFieldBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) Color.White else Color(0xFF6B6B6B), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DateField(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(QrydeFieldBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Text(label, color = Color(0xFF6B6B6B))
        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ChooseServiceSheetContent(
    uiState: BookRideUiState,
    onBack: () -> Unit,
    onServiceSelected: (String) -> Unit,
    onPaymentMethodSheetRequested: () -> Unit,
    onBookNow: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Choose a Service",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f, fill = false).height(360.dp)) {
            items(uiState.services) { service ->
                ServiceRow(
                    service = service,
                    selected = service.id == uiState.selectedServiceId,
                    onClick = { onServiceSelected(service.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QrydeFieldBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Filled.CreditCard, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(16.dp))
                Text(
                    "Wallet $${uiState.walletBalanceCents / 100}",
                    modifier = Modifier.padding(start = 6.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            OutlinedButton(
                onClick = onPaymentMethodSheetRequested,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Filled.CreditCard, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(16.dp))
                Text(
                    if (uiState.payWithCard) "Pay with Card" else "Select payment",
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBookNow,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp)
        ) {
            Text("Book Now", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ServiceRow(service: ServiceOption, selected: Boolean, onClick: () -> Unit) {
    val icon = when (service.id) {
        "fixed_bus_route", "paratransit" -> Icons.Filled.DirectionsBus
        "monthly_ride_services" -> Icons.Filled.CalendarMonth
        "evening_ride_services" -> Icons.Filled.NightsStay
        "microtransit" -> Icons.Filled.LocalShipping
        else -> Icons.Filled.DirectionsBus
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) QrydePrimary.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(QrydePrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(service.name, fontWeight = FontWeight.SemiBold)
                if (service.hasInfo) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = QrydePrimary,
                        modifier = Modifier.padding(start = 4.dp).size(14.dp)
                    )
                }
            }
            Row(modifier = Modifier.padding(top = 2.dp)) {
                Text("${service.durationMinutes} Min", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B))
                Text(
                    "  ${service.distanceMiles} miles",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF6B6B6B)
                )
                Text("  ${service.fare}", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6B6B))
            }
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(QrydePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun HomeContentSearchPreview() {
    HomeContent(
        uiState = BookRideUiState(),
        onSearchQueryChanged = {},
        onDestinationSelected = {},
        onTripTypeChanged = {},
        onDayToggled = {},
        onDatePickerRequested = {},
        onDatePickerDismissed = {},
        onDateSelected = {},
        onTimePickerRequested = {},
        onTimePickerDismissed = {},
        onTimeSelected = { _, _ -> },
        onAlternatePhoneChanged = {},
        onEscortCountChanged = {},
        onOpenAdditionalInformation = {},
        onProceedToChooseService = {},
        onServiceSelected = {},
        onPaymentMethodSheetRequested = {},
        onPaymentMethodSheetDismissed = {},
        onPaymentMethodSelected = {},
        onBackToSearch = {},
        onBackToRideDetails = {},
        onBookingConfirmed = {},
        onSetLocationOnMapRequested = {},
        onMapPickerCenterChanged = { _, _ -> },
        onSetLocationSheetDismissed = {},
        onLocationOnMapSaved = {}
    )
}
