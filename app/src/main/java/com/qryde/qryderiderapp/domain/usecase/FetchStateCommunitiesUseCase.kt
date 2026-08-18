package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.StateCommunity
import com.qryde.qryderiderapp.domain.repository.StateCommunityRepository
import javax.inject.Inject

class FetchStateCommunitiesUseCase @Inject constructor(
    private val repository: StateCommunityRepository
) {
    suspend operator fun invoke(stateCode: String): AppResult<List<StateCommunity>> =
        repository.fetchCommunitiesForState(stateCode)
}
