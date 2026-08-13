package com.qryde.qryderiderapp.data.remote.api

import com.qryde.qryderiderapp.data.remote.dto.LoginRequestDto
import com.qryde.qryderiderapp.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Real endpoint contract for the login vertical slice. Not yet called by
 * AuthRepositoryImpl - see that class for why it currently returns fake data.
 * Swap the fake logic for a call to this API once a backend is available;
 * the rest of the app (Domain/Presentation) does not need to change.
 */
interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
