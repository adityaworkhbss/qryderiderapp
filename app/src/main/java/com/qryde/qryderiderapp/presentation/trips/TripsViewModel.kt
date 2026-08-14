package com.qryde.qryderiderapp.presentation.trips

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor() : ViewModel() {

    private val _selectedStatus = MutableStateFlow(TripStatus.UPCOMING)
    val selectedStatus: StateFlow<TripStatus> = _selectedStatus.asStateFlow()

    val trips: List<TripSummary> = SampleTrips

    fun onStatusSelected(status: TripStatus) {
        _selectedStatus.value = status
    }
}
