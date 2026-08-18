package com.qryde.qryderiderapp.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.model.RecurringTrip
import com.qryde.qryderiderapp.domain.model.TripPaymentStatus
import com.qryde.qryderiderapp.domain.model.paymentStatus
import com.qryde.qryderiderapp.domain.usecase.FetchRecurringTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val fetchRecurringTripsUseCase: FetchRecurringTripsUseCase
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow(TripStatus.UPCOMING)
    val selectedStatus: StateFlow<TripStatus> = _selectedStatus.asStateFlow()

    private val _trips = MutableStateFlow(SampleTrips.filterNot { it.status == TripStatus.UPCOMING })
    val trips: StateFlow<List<TripSummary>> = _trips.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = fetchRecurringTripsUseCase()) {
                is AppResult.Success -> {
                    val upcoming = result.data.map { it.toTripSummary() }
                    _trips.update { current -> upcoming + current }
                }
                is AppResult.Error -> AppLogger.w(TAG, "Failed to fetch recurring trips: ${result.message}")
            }
        }
    }

    fun onStatusSelected(status: TripStatus) {
        _selectedStatus.value = status
    }

    private fun RecurringTrip.toTripSummary(): TripSummary {
        val dateLabel = if (pickupTime.isNotBlank()) "$travelDate, $pickupTime" else travelDate
        val dropoffDetail = listOf(recurringDaysLabel, paymentStatus().toLabel())
            .filter { it.isNotBlank() }
            .joinToString(" · ")
        return TripSummary(
            id = tripId,
            status = TripStatus.UPCOMING,
            dateLabel = dateLabel,
            pickup = pickupAddress,
            dropoff = dropoffAddress,
            dropoffDetail = dropoffDetail.ifBlank { null }
        )
    }

    private fun TripPaymentStatus.toLabel(): String = when (this) {
        TripPaymentStatus.PAID -> "Paid"
        TripPaymentStatus.UNPAID -> "Unpaid"
        TripPaymentStatus.PAY_ON_BOARD -> "Pay on board"
    }

    private companion object {
        const val TAG = "Trips"
    }
}
