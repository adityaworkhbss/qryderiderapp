package com.qryde.qryderiderapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BraintreeDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val CUSTOMER_ID = stringPreferencesKey("braintree_customer_id")
        val CLIENT_TOKEN = stringPreferencesKey("braintree_client_token")
    }

    val clientToken: Flow<String?> = dataStore.data.map { it[Keys.CLIENT_TOKEN] }

    suspend fun save(customerId: String?, clientToken: String) {
        dataStore.edit { prefs ->
            if (customerId != null) {
                prefs[Keys.CUSTOMER_ID] = customerId
            } else {
                prefs.remove(Keys.CUSTOMER_ID)
            }
            prefs[Keys.CLIENT_TOKEN] = clientToken
        }
    }
}
