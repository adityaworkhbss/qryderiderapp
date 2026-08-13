package com.qryde.qryderiderapp.data.mapper

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.qryde.qryderiderapp.BuildConfig

private const val NULL_PLACEHOLDER = "..."
private const val OS_TYPE = "Android"
private const val LEGACY_FLAG = "0"
private const val DEFAULT_COMMUNITY = "QRyde"
private val COLUMN_SEPARATOR = 14.toChar()

@SuppressLint("HardwareIds")
fun resolveDeviceId(context: Context): String =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).orEmpty()

/**
 * Shared 100U wire format for both "register this device" (existing account,
 * called right after a successful login) and "create account" (signup, where
 * this call itself creates the account) - same 21 char14-delimited fields
 * either way, only userName/password/userId/email/phone actually vary.
 */
fun buildDeviceRegistrationData(
    deviceId: String,
    userName: String,
    isoCode: String,
    phoneNumber: String,
    password: String,
    userId: String,
    email: String,
    languageCode: String = "EN"
): String {
    val isoNumber = "$isoCode.$phoneNumber"
    return listOf(
        deviceId,
        NULL_PLACEHOLDER, // push notification registration id - not wired up yet
        OS_TYPE,
        isoNumber,
        userName,
        LEGACY_FLAG, // literal from the legacy payload, meaning unclear
        NULL_PLACEHOLDER, // profile image data - no avatar-upload feature yet
        NULL_PLACEHOLDER, // community id - this app has no community picker yet
        password,
        userId,
        email,
        NULL_PLACEHOLDER, // NEMT client id
        NULL_PLACEHOLDER, // NEMT region id
        NULL_PLACEHOLDER, // Medicaid/MMIS number
        NULL_PLACEHOLDER, // date of birth
        NULL_PLACEHOLDER, // NEMT site id
        NULL_PLACEHOLDER, // always empty in the legacy payload too
        DEFAULT_COMMUNITY,
        BuildConfig.APPLICATION_ID,
        languageCode,
        "" // preferred pronouns - only used by one legacy tenant, always empty here
    ).joinToString(COLUMN_SEPARATOR.toString())
}
