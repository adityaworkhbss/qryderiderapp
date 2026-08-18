package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.StateCommunity

interface StateCommunityRepository {
    /** 20SC - communities available for joining in the given US state. */
    suspend fun fetchCommunitiesForState(stateCode: String): AppResult<List<StateCommunity>>
}
