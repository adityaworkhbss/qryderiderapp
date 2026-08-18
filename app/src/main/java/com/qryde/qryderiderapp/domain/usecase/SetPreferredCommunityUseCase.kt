package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.domain.repository.PreferredCommunityRepository
import javax.inject.Inject

class SetPreferredCommunityUseCase @Inject constructor(
    private val repository: PreferredCommunityRepository
) {
    suspend operator fun invoke(communityId: String) = repository.setPreferredCommunityId(communityId)
}
