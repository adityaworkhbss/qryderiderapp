package com.qryde.qryderiderapp.domain.model

/** 25RT - a single funding-source transaction (trip payment, card purchase, refund, ...). */
data class Transaction(
    val transactionId: String,
    val userId: String,
    val transactionFor: String,
    val dateTime: String,
    val depositAmount: String,
    val paidAmount: String
)
