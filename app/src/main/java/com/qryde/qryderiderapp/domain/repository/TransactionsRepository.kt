package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.Transaction

interface TransactionsRepository {
    /** 25RT - the current user's funding-source transactions. */
    suspend fun fetchTransactions(): AppResult<List<Transaction>>
}
