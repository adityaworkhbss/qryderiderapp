package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * The rider's explicitly-chosen community id (from the 20SC state-selection
 * flow, see CommunitySelectionViewModel). Distinct from [CommunityDataStore],
 * which holds the list of communities the rider has already *joined* (20AUC) -
 * this is a single override id, checked first wherever "the preferred
 * community" is needed.
 */
class PreferredCommunityDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val COMMUNITY_ID = stringPreferencesKey("preferred_community_id")
    }

    val current: Flow<String?> = dataStore.data.map { it[Keys.COMMUNITY_ID] }

    suspend fun save(communityId: String) {
        dataStore.edit { prefs -> prefs[Keys.COMMUNITY_ID] = communityId }
    }
}
