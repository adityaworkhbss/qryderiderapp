package com.qryde.qryderiderapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginSession(
    val userId: String,
    val phoneNumber: String,
    val supplierId: String,
    val userName: String,
    val email: String,
    val isoCode: String,
    val requiresPasswordReset: Boolean
)
