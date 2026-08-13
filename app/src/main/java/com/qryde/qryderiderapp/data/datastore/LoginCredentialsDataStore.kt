package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.LoginCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginCredentialsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val USER_ID = stringPreferencesKey("login_user_id")
        val PASSWORD = stringPreferencesKey("login_password")
    }

    val current: Flow<LoginCredentials?> = dataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID] ?: return@map null
        val password = prefs[Keys.PASSWORD] ?: return@map null
        LoginCredentials(userId, password)
    }

    suspend fun save(credentials: LoginCredentials) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = credentials.userId
            prefs[Keys.PASSWORD] = credentials.password
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.PASSWORD)
        }
    }
}
