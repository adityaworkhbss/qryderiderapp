package com.qryde.qryderiderapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecurringTrip(
    val tripId: String,
    val travelDate: String,
    val pickupTime: String,
    val dropoffTime: String,
    val pickupAddress: String,
    val dropoffAddress: String,
    val tripFare: String,
    val collectedFare: String,
    val disposition: String,
    val payUpFront: String,
    val operOnlinePayment: String,
    val operId: String,
    val providerName: String,
    val operatorPhone: String,
    val mobilities: String,
    val recurringDaysLabel: String,
    val notes: String
)

enum class TripPaymentStatus { PAID, UNPAID, PAY_ON_BOARD }

/** Mirrors the legacy client's aTripStatus derivation in process7GM(). */
fun RecurringTrip.paymentStatus(): TripPaymentStatus {
    val collectedFareAmount = collectedFare.toDoubleOrNull() ?: 0.0
    var status = if (collectedFareAmount > 0) TripPaymentStatus.PAID else TripPaymentStatus.UNPAID

    val tripWasTaken = disposition.equals("t", ignoreCase = true) && operId.isNotBlank()
    if (tripWasTaken) {
        when {
            payUpFront.equals("y", ignoreCase = true) && !operOnlinePayment.equals("y", ignoreCase = true) ->
                status = TripPaymentStatus.PAY_ON_BOARD
            payUpFront.equals("n", ignoreCase = true) ->
                status = TripPaymentStatus.PAY_ON_BOARD
        }
    }
    return status
}
