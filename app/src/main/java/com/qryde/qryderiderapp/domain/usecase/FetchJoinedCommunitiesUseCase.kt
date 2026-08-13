package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.Community
import com.qryde.qryderiderapp.domain.repository.CommunityRepository
import javax.inject.Inject

class FetchJoinedCommunitiesUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(userId: String): AppResult<List<Community>> =
        repository.fetchJoinedCommunities(userId)
}
