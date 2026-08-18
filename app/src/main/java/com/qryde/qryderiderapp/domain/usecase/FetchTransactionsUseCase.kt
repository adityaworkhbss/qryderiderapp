package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.Transaction
import com.qryde.qryderiderapp.domain.repository.TransactionsRepository
import javax.inject.Inject

class FetchTransactionsUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(): AppResult<List<Transaction>> = repository.fetchTransactions()
}
