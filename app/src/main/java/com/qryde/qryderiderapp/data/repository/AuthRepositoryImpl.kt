package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.data.datastore.SessionDataStore
import com.qryde.qryderiderapp.domain.model.User
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : AuthRepository {

    override suspend fun login(phoneNumber: String, password: String): AppResult<User> {
        delay(800)
        if (password.length < 4) {
            return AppResult.Error("Invalid phone number or password.")
        }
        val user = User(
            id = phoneNumber,
            name = "Rider $phoneNumber",
            phoneNumber = phoneNumber
        )
        sessionDataStore.saveUser(user)
        return AppResult.Success(user)
    }

    override suspend fun logout() {
        sessionDataStore.clear()
    }

    override fun observeCurrentUser(): Flow<User?> = sessionDataStore.currentUser
}
