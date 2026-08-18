package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.RecurringTrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecurringTripsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val TRIPS_JSON = stringPreferencesKey("recurring_trips")
    }

    val current: Flow<List<RecurringTrip>> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.TRIPS_JSON] ?: return@map emptyList()
        json.decodeFromString<List<RecurringTrip>>(raw)
    }

    suspend fun save(trips: List<RecurringTrip>) {
        dataStore.edit { prefs -> prefs[Keys.TRIPS_JSON] = json.encodeToString(trips) }
    }
}
