package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult

interface AvailableFundsRepository {
    /** 8FA - the current user's available funding-source balance. */
    suspend fun fetchAvailableFunds(): AppResult<Double>
}
