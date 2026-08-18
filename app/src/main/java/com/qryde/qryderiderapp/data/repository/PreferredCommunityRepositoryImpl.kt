package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.data.datastore.PreferredCommunityDataStore
import com.qryde.qryderiderapp.domain.repository.PreferredCommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferredCommunityRepositoryImpl @Inject constructor(
    private val preferredCommunityDataStore: PreferredCommunityDataStore
) : PreferredCommunityRepository {

    override fun observePreferredCommunityId(): Flow<String?> = preferredCommunityDataStore.current

    override suspend fun setPreferredCommunityId(communityId: String) {
        preferredCommunityDataStore.save(communityId)
    }
}
