package com.qryde.qryderiderapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.model.AddressSuggestion
import com.qryde.qryderiderapp.domain.model.SavedTripAddress
import com.qryde.qryderiderapp.domain.usecase.FetchSuggestedAddressesUseCase
import com.qryde.qryderiderapp.domain.usecase.SearchAddressesUseCase
import com.qryde.qryderiderapp.presentation.components.DefaultMapLatitude
import com.qryde.qryderiderapp.presentation.components.DefaultMapLongitude
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class BookRideUiState(
    val step: BookingStep = BookingStep.SEARCH,
    val searchQuery: String = "",
    val pickupAddress: String = "Your Current Location",
    val dropoffAddress: String = "",
    val recentAddresses: List<RecentAddress> = emptyList(),
    val savedTripAddresses: List<SavedTripAddress> = emptyList(),
    val quickPlaces: List<QuickPlace> = emptyList(),
    val isRecurring: Boolean = false,
    val selectedDays: Set<Int> = emptySet(),
    val startDateLabel: String = "",
    val endDateLabel: String = "",
    val timeLabel: String = "",
    val alternatePhone: String = "",
    val escortCount: Int = 0,
    val hasAdditionalInfo: Boolean = false,
    val fundingSource: String = "",
    val tripPurpose: String = "",
    val isBookingAsCareGiver: Boolean = false,
    val services: List<ServiceOption> = SampleServiceOptions,
    val selectedServiceId: String = "paratransit",
    val walletBalanceCents: Int = 10_000,
    val payWithCard: Boolean = false,
    val showDatePickerFor: DatePickerTarget? = null,
    val showTimePicker: Boolean = false,
    val showPaymentMethodSheet: Boolean = false,
    val showSetLocationSheet: Boolean = false,
    val mapPickerLatitude: Double = DefaultMapLatitude,
    val mapPickerLongitude: Double = DefaultMapLongitude
) {
    val canProceedFromRideDetails: Boolean
        get() = if (isRecurring) {
            selectedDays.isNotEmpty() && startDateLabel.isNotBlank() && endDateLabel.isNotBlank() && timeLabel.isNotBlank()
        } else {
            startDateLabel.isNotBlank() && timeLabel.isNotBlank()
        }
}

@HiltViewModel
class BookRideViewModel @Inject constructor(
    private val fetchSuggestedAddressesUseCase: FetchSuggestedAddressesUseCase,
    private val searchAddressesUseCase: SearchAddressesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookRideUiState())
    val uiState: StateFlow<BookRideUiState> = _uiState.asStateFlow()

    private val _addressSuggestions = MutableStateFlow<List<RecentAddress>>(emptyList())
    val addressSuggestions: StateFlow<List<RecentAddress>> = _addressSuggestions.asStateFlow()

    private val addressSearchCache = mutableMapOf<String, List<RecentAddress>>()

    init {
        fetchSuggestedAddresses()
        observeSearchQueryForLiveAddressSearch()
    }

    /**
     * Backend type-ahead address search (see AddressSearchRepository), debounced
     * and cached by sanitized query - mirrors the legacy client's RL search
     * (min 3 alphanumeric chars, 300ms debounce, in-memory cache by query).
     * Falls back to local filtering over recentAddresses (see HomeScreen) when
     * this community has no RL search configured, or while results are empty.
     */
    private fun observeSearchQueryForLiveAddressSearch() {
        viewModelScope.launch {
            _uiState.map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .collectLatest { query -> updateAddressSuggestions(query) }
        }
    }

    private suspend fun updateAddressSuggestions(query: String) {
        val sanitizedQuery = query.replace(NON_ALPHANUMERIC_REGEX, "")
        if (sanitizedQuery.length < MIN_SEARCH_QUERY_LENGTH) {
            _addressSuggestions.value = emptyList()
            return
        }

        addressSearchCache[sanitizedQuery]?.let {
            _addressSuggestions.value = it
            return
        }

        when (val result = searchAddressesUseCase(sanitizedQuery)) {
            is AppResult.Success -> {
                val suggestions = result.data.map { it.toRecentAddress() }
                addressSearchCache[sanitizedQuery] = suggestions
                _addressSuggestions.value = suggestions
            }
            is AppResult.Error -> {
                AppLogger.d(TAG, "Live address search unavailable, using local suggestions: ${result.message}")
                _addressSuggestions.value = emptyList()
            }
        }
    }

    private fun AddressSuggestion.toRecentAddress() = RecentAddress(
        id = fullAddress,
        title = title,
        subtitle = subtitle,
        destinationAddress = fullAddress
    )

    private fun fetchSuggestedAddresses() {
        viewModelScope.launch {
            when (val result = fetchSuggestedAddressesUseCase()) {
                is AppResult.Success -> {
                    val recentAddresses = result.data.recentAddresses.mapIndexed { index, address ->
                        RecentAddress(
                            id = index.toString(),
                            title = address.street,
                            subtitle = listOf(address.city, address.stateCode, address.zip, address.countryCode)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")
                        )
                    }
                    val quickPlaces = result.data.savedTripAddresses
                        .mapNotNull { it.toQuickPlace() }
                        .distinctBy { it.label.lowercase() }
                    _uiState.update {
                        it.copy(
                            recentAddresses = recentAddresses,
                            savedTripAddresses = result.data.savedTripAddresses,
                            quickPlaces = quickPlaces
                        )
                    }
                }
                is AppResult.Error -> AppLogger.w(TAG, "Failed to fetch suggested addresses: ${result.message}")
            }
        }
    }

    /**
     * The saved trip's dropoff is treated as the shortcut's destination; purpose
     * (e.g. "Home", "Work") is its label. Trips with no purpose or no dropoff
     * address aren't meaningful shortcuts, so they're dropped rather than shown
     * as a generic placeholder.
     */
    private fun SavedTripAddress.toQuickPlace(): QuickPlace? {
        if (purpose.isBlank()) return null
        val label = purpose
        val subtitle = listOf(dropoffCity, dropoffStateCode).filter { it.isNotBlank() }.joinToString(", ")
        val destinationAddress = listOf(dropoffStreet, dropoffCity, dropoffStateCode, dropoffZip)
            .filter { it.isNotBlank() }
            .joinToString(", ")
        if (destinationAddress.isBlank()) return null
        val icon = when {
            label.equals("home", ignoreCase = true) -> QuickPlaceIcon.HOME
            label.equals("work", ignoreCase = true) || label.equals("office", ignoreCase = true) -> QuickPlaceIcon.OFFICE
            else -> QuickPlaceIcon.OTHER
        }
        return QuickPlace(
            id = tripId.ifBlank { label },
            label = label,
            subtitle = subtitle,
            destinationAddress = destinationAddress,
            icon = icon
        )
    }

    fun onSearchQueryChanged(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onDestinationSelected(address: String) {
        _uiState.update { it.copy(dropoffAddress = address, step = BookingStep.RIDE_DETAILS) }
    }

    fun onTripTypeChanged(isRecurring: Boolean) {
        _uiState.update { it.copy(isRecurring = isRecurring) }
    }

    fun onDayToggled(day: Int) {
        _uiState.update {
            val days = if (day in it.selectedDays) it.selectedDays - day else it.selectedDays + day
            it.copy(selectedDays = days)
        }
    }

    fun onDatePickerRequested(target: DatePickerTarget) {
        _uiState.update { it.copy(showDatePickerFor = target) }
    }

    fun onDatePickerDismissed() {
        _uiState.update { it.copy(showDatePickerFor = null) }
    }

    fun onDateSelected(millis: Long) {
        val label = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(millis))
        _uiState.update {
            when (it.showDatePickerFor) {
                DatePickerTarget.START, DatePickerTarget.SINGLE -> it.copy(startDateLabel = label, showDatePickerFor = null)
                DatePickerTarget.END -> it.copy(endDateLabel = label, showDatePickerFor = null)
                null -> it
            }
        }
    }

    fun onTimePickerRequested() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    fun onTimePickerDismissed() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour % 12 == 0 -> 12
            else -> hour % 12
        }
        val label = "%d:%02d%s".format(displayHour, minute, amPm)
        _uiState.update { it.copy(timeLabel = label, showTimePicker = false) }
    }

    fun onAlternatePhoneChanged(value: String) {
        _uiState.update { it.copy(alternatePhone = value) }
    }

    fun onEscortCountChanged(delta: Int) {
        _uiState.update { it.copy(escortCount = (it.escortCount + delta).coerceAtLeast(0)) }
    }

    fun onAdditionalInfoSaved(fundingSource: String, tripPurpose: String, isBookingAsCareGiver: Boolean) {
        _uiState.update {
            it.copy(
                fundingSource = fundingSource,
                tripPurpose = tripPurpose,
                isBookingAsCareGiver = isBookingAsCareGiver,
                hasAdditionalInfo = true
            )
        }
    }

    fun onProceedToChooseService() {
        if (!_uiState.value.canProceedFromRideDetails) return
        _uiState.update { it.copy(step = BookingStep.CHOOSE_SERVICE) }
    }

    fun onServiceSelected(id: String) {
        _uiState.update { it.copy(selectedServiceId = id) }
    }

    fun onPaymentMethodSheetRequested() {
        _uiState.update { it.copy(showPaymentMethodSheet = true) }
    }

    fun onPaymentMethodSheetDismissed() {
        _uiState.update { it.copy(showPaymentMethodSheet = false) }
    }

    fun onPaymentMethodSelected(payWithCard: Boolean) {
        _uiState.update { it.copy(payWithCard = payWithCard, showPaymentMethodSheet = false) }
    }

    fun onSetLocationOnMapRequested() {
        _uiState.update {
            it.copy(
                showSetLocationSheet = true,
                mapPickerLatitude = DefaultMapLatitude,
                mapPickerLongitude = DefaultMapLongitude
            )
        }
    }

    fun onMapPickerCenterChanged(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(mapPickerLatitude = latitude, mapPickerLongitude = longitude) }
    }

    fun onSetLocationSheetDismissed() {
        _uiState.update { it.copy(showSetLocationSheet = false) }
    }

    fun onLocationOnMapSaved() {
        _uiState.update {
            it.copy(
                dropoffAddress = "Pinned location (${"%.4f".format(it.mapPickerLatitude)}, ${"%.4f".format(it.mapPickerLongitude)})",
                step = BookingStep.RIDE_DETAILS,
                showSetLocationSheet = false
            )
        }
    }

    fun onBackToSearch() {
        _uiState.update { it.copy(step = BookingStep.SEARCH) }
    }

    fun onBackToRideDetails() {
        _uiState.update { it.copy(step = BookingStep.RIDE_DETAILS) }
    }

    /** Resets the draft after a booking is (mock) confirmed. */
    fun onBookingConfirmed() {
        _uiState.value = BookRideUiState()
        fetchSuggestedAddresses()
    }

    private companion object {
        const val TAG = "SuggestedAddress"
        const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val MIN_SEARCH_QUERY_LENGTH = 3
        val NON_ALPHANUMERIC_REGEX = Regex("[^a-zA-Z0-9]")
    }
}
