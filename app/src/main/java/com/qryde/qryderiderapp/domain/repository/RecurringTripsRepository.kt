package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.RecurringTrip

interface RecurringTripsRepository {
    suspend fun fetchRecurringTrips(): AppResult<List<RecurringTrip>>
}
