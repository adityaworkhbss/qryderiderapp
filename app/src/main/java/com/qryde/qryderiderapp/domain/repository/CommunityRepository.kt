package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.Community

interface CommunityRepository {
    suspend fun fetchJoinedCommunities(userId: String): AppResult<List<Community>>
}
