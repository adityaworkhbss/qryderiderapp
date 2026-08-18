package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.DeviceRegistrationInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DeviceRegistrationDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val INFO_JSON = stringPreferencesKey("device_registration_info")
    }

    val current: Flow<DeviceRegistrationInfo?> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.INFO_JSON] ?: return@map null
        json.decodeFromString<DeviceRegistrationInfo>(raw)
    }

    suspend fun save(info: DeviceRegistrationInfo) {
        dataStore.edit { prefs -> prefs[Keys.INFO_JSON] = json.encodeToString(info) }
    }
}
