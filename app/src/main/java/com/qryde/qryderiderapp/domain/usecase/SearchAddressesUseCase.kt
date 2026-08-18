package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.AddressSuggestion
import com.qryde.qryderiderapp.domain.repository.AddressSearchRepository
import javax.inject.Inject

class SearchAddressesUseCase @Inject constructor(
    private val repository: AddressSearchRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<AddressSuggestion>> = repository.searchAddresses(query)
}
