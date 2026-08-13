package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.data.remote.dto.LoginResponseDto
import com.qryde.qryderiderapp.domain.model.User

fun LoginResponseDto.toDomain(): User = User(
    id = id,
    name = name,
    phoneNumber = phoneNumber
)
