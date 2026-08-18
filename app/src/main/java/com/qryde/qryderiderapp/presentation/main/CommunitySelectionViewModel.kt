package com.qryde.qryderiderapp.presentation.main

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.location.DeviceLocation
import com.qryde.qryderiderapp.core.location.DeviceLocationResolver
import com.qryde.qryderiderapp.core.utils.AppConfig
import com.qryde.qryderiderapp.domain.model.StateCommunity
import com.qryde.qryderiderapp.domain.usecase.FetchStateCommunitiesUseCase
import com.qryde.qryderiderapp.domain.usecase.ObservePreferredCommunityUseCase
import com.qryde.qryderiderapp.domain.usecase.SetPreferredCommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

sealed interface CommunitySelectionUiState {
    data object Hidden : CommunitySelectionUiState
    data object EnableLocationPrompt : CommunitySelectionUiState
    data object ResolvingLocation : CommunitySelectionUiState
    data class PickCommunity(
        val communities: List<StateCommunity>,
        val nearestCommunityId: String?
    ) : CommunitySelectionUiState
}

private const val LOCATION_TIMEOUT_MS = 15_000L

@HiltViewModel
class CommunitySelectionViewModel @Inject constructor(
    appConfig: AppConfig,
    private val observePreferredCommunityUseCase: ObservePreferredCommunityUseCase,
    private val setPreferredCommunityUseCase: SetPreferredCommunityUseCase,
    private val fetchStateCommunitiesUseCase: FetchStateCommunitiesUseCase,
    private val locationResolver: DeviceLocationResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunitySelectionUiState>(CommunitySelectionUiState.Hidden)
    val uiState: StateFlow<CommunitySelectionUiState> = _uiState.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    init {
        if (appConfig.isShowCommunitySelection) {
            viewModelScope.launch {
                val preferredCommunityId = observePreferredCommunityUseCase().first()
                if (preferredCommunityId.isNullOrBlank()) {
                    _uiState.value = CommunitySelectionUiState.EnableLocationPrompt
                }
            }
        }
    }

    fun onUseMyLocationClicked() {
        viewModelScope.launch {
            _uiState.value = CommunitySelectionUiState.ResolvingLocation

            val location = withTimeoutOrNull(LOCATION_TIMEOUT_MS) { locationResolver.resolveCurrentLocation() }
            val stateCode = location?.stateCode
            if (location == null || stateCode.isNullOrBlank()) {
                _errorEvent.emit("Could not determine your location. Please try again.")
                _uiState.value = CommunitySelectionUiState.EnableLocationPrompt
                return@launch
            }

            when (val result = fetchStateCommunitiesUseCase(stateCode)) {
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        _errorEvent.emit("No communities found near you.")
                        _uiState.value = CommunitySelectionUiState.Hidden
                    } else {
                        _uiState.value = CommunitySelectionUiState.PickCommunity(
                            communities = result.data,
                            nearestCommunityId = nearestCommunityId(result.data, location)
                        )
                    }
                }
                is AppResult.Error -> {
                    _errorEvent.emit(result.message)
                    _uiState.value = CommunitySelectionUiState.EnableLocationPrompt
                }
            }
        }
    }

    fun onCommunitySelected(community: StateCommunity) {
        viewModelScope.launch {
            setPreferredCommunityUseCase(community.id)
            _uiState.value = CommunitySelectionUiState.Hidden
        }
    }

    fun onSkipped() {
        _uiState.value = CommunitySelectionUiState.Hidden
    }

    private fun nearestCommunityId(communities: List<StateCommunity>, location: DeviceLocation): String? {
        var nearestId: String? = null
        var smallestDistanceMeters = Float.MAX_VALUE
        val results = FloatArray(1)

        for (community in communities) {
            val lat = community.latitude?.toDoubleOrNull() ?: continue
            val lng = community.longitude?.toDoubleOrNull() ?: continue
            Location.distanceBetween(location.latitude, location.longitude, lat, lng, results)
            if (results[0] < smallestDistanceMeters) {
                smallestDistanceMeters = results[0]
                nearestId = community.id
            }
        }
        return nearestId
    }
}
