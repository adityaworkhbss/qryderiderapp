package com.qryde.qryderiderapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferredCommunityRepository {
    fun observePreferredCommunityId(): Flow<String?>
    suspend fun setPreferredCommunityId(communityId: String)
}
