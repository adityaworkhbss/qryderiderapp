package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.ServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** The endpoint set is dynamic (see ServerConfigMapper), so it's persisted as one JSON blob. */
class ServerConfigDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val ENDPOINTS_JSON = stringPreferencesKey("server_config_endpoints")
    }

    val current: Flow<ServerConfig?> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.ENDPOINTS_JSON] ?: return@map null
        ServerConfig(json.decodeFromString<Map<String, String>>(raw))
    }

    suspend fun save(config: ServerConfig) {
        dataStore.edit { prefs ->
            prefs[Keys.ENDPOINTS_JSON] = json.encodeToString(config.endpoints)
        }
    }
}
