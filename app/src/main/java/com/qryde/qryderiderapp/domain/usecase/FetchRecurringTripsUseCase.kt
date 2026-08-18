package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.RecurringTrip
import com.qryde.qryderiderapp.domain.repository.RecurringTripsRepository
import javax.inject.Inject

class FetchRecurringTripsUseCase @Inject constructor(
    private val repository: RecurringTripsRepository
) {
    suspend operator fun invoke(): AppResult<List<RecurringTrip>> = repository.fetchRecurringTrips()
}
