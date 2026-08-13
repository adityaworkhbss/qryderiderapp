package com.qryde.qryderiderapp.domain.model

data class NewAccountDetails(
    val firstName: String,
    val lastName: String,
    val userId: String,
    val email: String,
    val password: String,
    val isoCode: String,
    val phoneNumber: String
)
