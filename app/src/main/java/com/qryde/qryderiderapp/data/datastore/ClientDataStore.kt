package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.NemtClientInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ClientDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val CLIENT_INFO_JSON = stringPreferencesKey("nemt_client_info")
    }

    val current: Flow<NemtClientInfo?> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.CLIENT_INFO_JSON] ?: return@map null
        json.decodeFromString<NemtClientInfo>(raw)
    }

    suspend fun save(clientInfo: NemtClientInfo?) {
        dataStore.edit { prefs ->
            if (clientInfo != null) {
                prefs[Keys.CLIENT_INFO_JSON] = json.encodeToString(clientInfo)
            } else {
                prefs.remove(Keys.CLIENT_INFO_JSON)
            }
        }
    }
}
