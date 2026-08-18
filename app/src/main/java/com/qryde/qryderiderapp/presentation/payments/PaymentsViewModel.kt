package com.qryde.qryderiderapp.presentation.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.model.Transaction
import com.qryde.qryderiderapp.domain.usecase.FetchAvailableFundsUseCase
import com.qryde.qryderiderapp.domain.usecase.FetchTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class PaymentsUiState(
    val selectedTab: PaymentsTab = PaymentsTab.PURCHASES,
    val availableFunds: Double = 0.0,
    val transactions: List<TransactionItem> = emptyList(),
    val showFilterSheet: Boolean = false,
    val selectedQuickFilter: String = "This Month",
    val startDateLabel: String = "",
    val endDateLabel: String = ""
)

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val fetchTransactionsUseCase: FetchTransactionsUseCase,
    private val fetchAvailableFundsUseCase: FetchAvailableFundsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentsUiState())
    val uiState: StateFlow<PaymentsUiState> = _uiState.asStateFlow()

    val purchases: List<PurchaseItem> = SamplePurchases

    init {
        viewModelScope.launch {
            fetchTransactions()
            fetchAvailableFunds()
        }
    }

    private suspend fun fetchTransactions() {
        when (val result = fetchTransactionsUseCase()) {
            is AppResult.Success -> {
                val items = result.data.map { it.toUiModel() }
                _uiState.update { it.copy(transactions = items) }
            }
            is AppResult.Error -> AppLogger.w(TAG, "Failed to fetch transactions: ${result.message}")
        }
    }

    private suspend fun fetchAvailableFunds() {
        when (val result = fetchAvailableFundsUseCase()) {
            is AppResult.Success -> _uiState.update { it.copy(availableFunds = result.data) }
            is AppResult.Error -> AppLogger.w(TAG, "Failed to fetch available funds: ${result.message}")
        }
    }

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

    private fun Transaction.toUiModel(): TransactionItem {
        val parsedDateTime = runCatching { INPUT_FORMAT.parse(dateTime) }.getOrNull()
        val dateGroup = parsedDateTime?.let { DATE_GROUP_FORMAT.format(it) } ?: dateTime
        val time = parsedDateTime?.let { TIME_FORMAT.format(it) } ?: ""

        return TransactionItem(
            dateGroup = dateGroup,
            title = transactionFor,
            time = time,
            transactionId = transactionId,
            depositAmount = "$${depositAmount.ifBlank { "0.00" }}",
            paidAmount = "$${paidAmount.ifBlank { "0.00" }}"
        )
    }

    private companion object {
        const val TAG = "Payments"
        val INPUT_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val DATE_GROUP_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val TIME_FORMAT = SimpleDateFormat("h:mm a", Locale.US)
    }
}
