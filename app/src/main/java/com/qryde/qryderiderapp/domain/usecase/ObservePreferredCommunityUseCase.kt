package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.domain.repository.PreferredCommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePreferredCommunityUseCase @Inject constructor(
    private val repository: PreferredCommunityRepository
) {
    operator fun invoke(): Flow<String?> = repository.observePreferredCommunityId()
}
