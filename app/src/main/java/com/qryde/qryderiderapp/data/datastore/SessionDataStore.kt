package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qryde.qryderiderapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Small persistent state only (logged-in user id/name/phone) - see section 9 of the
 * architecture doc for why this is DataStore rather than Room.
 */
class SessionDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
    }

    val currentUser: Flow<User?> = dataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID] ?: return@map null
        val name = prefs[Keys.USER_NAME] ?: return@map null
        val phone = prefs[Keys.USER_PHONE] ?: return@map null
        User(id = id, name = name, phoneNumber = phone)
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = user.id
            prefs[Keys.USER_NAME] = user.name
            prefs[Keys.USER_PHONE] = user.phoneNumber
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
