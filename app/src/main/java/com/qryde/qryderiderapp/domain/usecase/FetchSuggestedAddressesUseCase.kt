package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.SuggestedAddresses
import com.qryde.qryderiderapp.domain.repository.SuggestedAddressRepository
import javax.inject.Inject

class FetchSuggestedAddressesUseCase @Inject constructor(
    private val repository: SuggestedAddressRepository
) {
    suspend operator fun invoke(): AppResult<SuggestedAddresses> = repository.fetchSuggestedAddresses()
}
