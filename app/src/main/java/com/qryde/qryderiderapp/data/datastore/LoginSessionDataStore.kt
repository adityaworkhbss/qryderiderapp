package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.LoginSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Holds the most recent successful 5G response so later steps that need it
 * (device registration via 100U) don't have to be threaded through several
 * screens of navigation args.
 */
class LoginSessionDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val SESSION_JSON = stringPreferencesKey("login_session")
    }

    val current: Flow<LoginSession?> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.SESSION_JSON] ?: return@map null
        json.decodeFromString<LoginSession>(raw)
    }

    suspend fun save(session: LoginSession) {
        dataStore.edit { prefs ->
            prefs[Keys.SESSION_JSON] = json.encodeToString(session)
        }
    }
}
