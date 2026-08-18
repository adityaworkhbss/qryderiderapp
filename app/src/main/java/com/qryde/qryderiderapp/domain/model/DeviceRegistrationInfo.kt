package com.qryde.qryderiderapp.domain.model

import kotlinx.serialization.Serializable

/** Cached 100U success payload ("OK^<siteVersion>^<nemtVersion>^<userFsId>^<affiliation>"). */
@Serializable
data class DeviceRegistrationInfo(
    val userFsId: String
)
