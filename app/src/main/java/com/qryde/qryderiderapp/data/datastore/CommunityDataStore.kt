package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.Community
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CommunityDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    private object Keys {
        val COMMUNITIES_JSON = stringPreferencesKey("joined_communities")
    }

    val current: Flow<List<Community>> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.COMMUNITIES_JSON] ?: return@map emptyList()
        json.decodeFromString<List<Community>>(raw)
    }

    suspend fun save(communities: List<Community>) {
        dataStore.edit { prefs ->
            prefs[Keys.COMMUNITIES_JSON] = json.encodeToString(communities)
        }
    }
}
