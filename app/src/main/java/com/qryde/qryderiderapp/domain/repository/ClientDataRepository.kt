package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.NemtClientInfo
import kotlinx.coroutines.flow.Flow

interface ClientDataRepository {
    /**
     * 5CMD - the rider's NEMT client record for their preferred community.
     * Skipped (returns Success(null) without hitting the network) when the
     * community's ClientType is PT1 (fixed-route public transit), matching
     * the legacy gate - NEMT client data doesn't apply there.
     */
    suspend fun fetchClientDataIfEligible(): AppResult<NemtClientInfo?>

    fun observeClientData(): Flow<NemtClientInfo?>
}
