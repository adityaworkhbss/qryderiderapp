package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.AddressSuggestion

interface AddressSearchRepository {
    suspend fun searchAddresses(query: String): AppResult<List<AddressSuggestion>>
}
