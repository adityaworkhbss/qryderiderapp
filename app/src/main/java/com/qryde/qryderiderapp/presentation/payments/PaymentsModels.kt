package com.qryde.qryderiderapp.presentation.payments

enum class PaymentsTab {
    MY_QCARD,
    TRANSACTIONS,
    PURCHASES
}

data class PurchaseItem(
    val dateGroup: String,
    val title: String,
    val ticketType: String,
    val price: String
)

data class TransactionItem(
    val dateGroup: String,
    val title: String,
    val time: String,
    val transactionId: String,
    val paidMode: String,
    val amount: String,
    val isCredit: Boolean,
    val statusLabel: String
)

val SamplePurchases = listOf(
    PurchaseItem("Jul 15, 2025", "Mart Prepaid Card", "Prepaid", "$10.00"),
    PurchaseItem("Jul 11, 2025", "Commuter Shuttle Monthly Pass", "Prepaid", "$6.00"),
    PurchaseItem("Jul 11, 2025", "DTC Prepaid Card", "Prepaid", "$50.00")
)

val SampleTransactions = listOf(
    TransactionItem("Jul 15, 2025", "Credit Card Payment", "11:20 AM", "638746-1", "Community Fund", "-$2.00", isCredit = false, statusLabel = "PAID"),
    TransactionItem("Jul 12, 2025", "Amount Reverted back to Credit Card", "11:20 AM", "638746-1", "Community Fund", "+$4.00", isCredit = true, statusLabel = "Deposited"),
    TransactionItem("Jul 12, 2025", "Utilized Credit Card for Booking", "11:20 AM", "638746-1", "Community Fund", "-$2.00", isCredit = false, statusLabel = "PAID")
)
