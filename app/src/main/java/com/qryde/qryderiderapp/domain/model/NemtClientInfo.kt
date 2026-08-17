package com.qryde.qryderiderapp.domain.model

import kotlinx.serialization.Serializable

/** 5CMD - the rider's NEMT client record for their preferred community, if any. */
@Serializable
data class NemtClientInfo(
    val nemtClientId: String,
    val dateOfBirth: String,
    val medicaidNumber: String,
    val nemtRegionId: String,
    val nemtPortalId: String,
    val tenantId: String?
)
