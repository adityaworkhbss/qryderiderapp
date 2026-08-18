package com.qryde.qryderiderapp.domain.model

/** 20SC - a community available for joining in the rider's detected state. */
data class StateCommunity(
    val id: String,
    val name: String,
    val resultCombo: String,
    val address: String,
    val webUrl: String,
    val communityAccess: String,
    val status: String,
    val imageUrl: String,
    val phoneNumber: String,
    val email: String,
    val description: String,
    val latitude: String?,
    val longitude: String?
)
