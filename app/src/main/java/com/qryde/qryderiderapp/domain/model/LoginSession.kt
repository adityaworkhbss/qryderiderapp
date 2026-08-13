package com.qryde.qryderiderapp.domain.model

import kotlinx.serialization.Serializable

/**
 * The handful of 5G login-response fields this app actually uses. The real
 * response is a ~60-field positional record carrying a lot of legacy
 * multi-tenant/feature data (NEMT, Medicaid, Uber/Lyft, RydeLog, PT1
 * communities, ...) that this app has no screens for yet - see LoginMapper
 * for the full field layout and why only these are extracted.
 */
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
