package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.SuggestedAddresses

interface SuggestedAddressRepository {
    suspend fun fetchSuggestedAddresses(): AppResult<SuggestedAddresses>
}
