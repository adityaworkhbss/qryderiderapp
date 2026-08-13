package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.OeRegistryValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OeRegistryDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val VALUES_JSON = stringPreferencesKey("oe_registry_values")
    }

    val current: Flow<OeRegistryValues?> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.VALUES_JSON] ?: return@map null
        OeRegistryValues(json.decodeFromString<Map<String, String>>(raw))
    }

    suspend fun save(values: OeRegistryValues) {
        dataStore.edit { prefs ->
            prefs[Keys.VALUES_JSON] = json.encodeToString(values.values)
        }
    }
}
