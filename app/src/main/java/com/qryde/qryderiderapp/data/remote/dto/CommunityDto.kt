package com.qryde.qryderiderapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommunityInfoResponse(
    val CommunityInfo: List<CommunityDto> = emptyList()
)

@Serializable
data class CommunityDto(
    val CommunityId: String,
    val CommunityName: String,
    val pref_comm: String = "N",
    val result_combo: String = "",
    val address: String = "",
    val homeurl: String = "",
    val comm_access: String = "",
    val request_type: String = "",
    val Phone: String? = null,
    val email: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
)
