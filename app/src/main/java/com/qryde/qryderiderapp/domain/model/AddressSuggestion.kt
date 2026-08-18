package com.qryde.qryderiderapp.domain.model

data class AddressSuggestion(
    val title: String,
    val subtitle: String,
    val fullAddress: String,
    val latitude: Double?,
    val longitude: Double?
)
