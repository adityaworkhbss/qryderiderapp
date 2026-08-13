package com.qryde.qryderiderapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Community(
    val id: String,
    val name: String,
    val isPreferred: Boolean,
    val resultCombo: String,
    val address: String,
    val webUrl: String,
    val communityAccess: String,
    val requestType: String,
    val phone: String?,
    val email: String?,
    val logoUrl: String?,
    val description: String?,
    val latitude: String?,
    val longitude: String?
)
