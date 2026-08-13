package com.qryde.qryderiderapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val phoneNumber: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val token: String
)
