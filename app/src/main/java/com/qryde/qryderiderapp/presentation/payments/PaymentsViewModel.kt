package com.qryde.qryderiderapp.presentation.payments

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PaymentsUiState(
    val selectedTab: PaymentsTab = PaymentsTab.PURCHASES,
    val availableBalanceCents: Int = 4_850,
    val showFilterSheet: Boolean = false,
    val selectedQuickFilter: String = "This Month",
    val startDateLabel: String = "Jul 01, 2025",
    val endDateLabel: String = "Jul 16, 2025"
)

@HiltViewModel
class PaymentsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentsUiState())
    val uiState: StateFlow<PaymentsUiState> = _uiState.asStateFlow()

    val purchases: List<PurchaseItem> = SamplePurchases
    val transactions: List<TransactionItem> = SampleTransactions

    fun onTabSelected(tab: PaymentsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onFilterSheetRequested() {
        _uiState.update { it.copy(showFilterSheet = true) }
    }

    fun onFilterSheetDismissed() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun onQuickFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedQuickFilter = filter) }
    }

    fun onFilterCleared() {
        _uiState.update { it.copy(selectedQuickFilter = "", startDateLabel = "", endDateLabel = "") }
    }

    fun onFilterApplied() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }
}
