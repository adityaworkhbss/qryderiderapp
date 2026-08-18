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
    val depositAmount: String,
    val paidAmount: String
)

val SamplePurchases = listOf(
    PurchaseItem("Jul 15, 2025", "Mart Prepaid Card", "Prepaid", "$10.00"),
    PurchaseItem("Jul 11, 2025", "Commuter Shuttle Monthly Pass", "Prepaid", "$6.00"),
    PurchaseItem("Jul 11, 2025", "DTC Prepaid Card", "Prepaid", "$50.00")
)
