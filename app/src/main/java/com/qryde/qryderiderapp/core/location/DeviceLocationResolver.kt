package com.qryde.qryderiderapp.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.qryde.qryderiderapp.core.logging.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val stateCode: String?
)

@Serializable
private data class StateCodeEntry(val name: String, val code: String)

private const val STATE_CODES_ASSET = "state_codes.json"

class DeviceLocationResolver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    private val stateCodesByCountry: Map<String, Map<String, String>> by lazy { loadStateCodes() }

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    suspend fun resolveCurrentLocation(): DeviceLocation? {
        if (!hasLocationPermission()) return null
        val location = getLastKnownOrFreshLocation() ?: return null
        val stateCode = reverseGeocodeState(location.latitude, location.longitude)
        return DeviceLocation(location.latitude, location.longitude, stateCode)
    }

    @Suppress("MissingPermission")
    private suspend fun getLastKnownOrFreshLocation(): Location? = withContext(Dispatchers.IO) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { locationManager.isProviderEnabled(it) }

        val lastKnown = providers
            .mapNotNull { runCatching { locationManager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }

        lastKnown ?: requestSingleLocationUpdate(locationManager, providers)
    }

    @Suppress("MissingPermission")
    private suspend fun requestSingleLocationUpdate(
        locationManager: LocationManager,
        providers: List<String>
    ): Location? {
        val provider = providers.firstOrNull() ?: return null
        return suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)
                    if (continuation.isActive) continuation.resume(location)
                }
            }
            val requested = runCatching {
                locationManager.requestSingleUpdate(provider, listener, null)
            }.isSuccess
            if (!requested && continuation.isActive) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }
            continuation.invokeOnCancellation { locationManager.removeUpdates(listener) }
        }
    }

    private suspend fun reverseGeocodeState(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                @Suppress("DEPRECATION")
                val address = Geocoder(context, Locale.US).getFromLocation(latitude, longitude, 1)?.firstOrNull()
                resolveStateCode(address?.countryCode, address?.adminArea)
            }.getOrNull()
        }

    private fun resolveStateCode(countryCode: String?, adminArea: String?): String? {
        val name = adminArea?.trim()?.lowercase() ?: return null
        val country = countryCode?.trim()?.uppercase()

        val byCountry = country?.let { stateCodesByCountry[it]?.get(name) }
        val byAnyCountry = byCountry ?: stateCodesByCountry.values.firstNotNullOfOrNull { it[name] }

        return byAnyCountry
            ?: adminArea.trim().takeIf { it.length in 2..3 && it.all(Char::isLetter) }?.uppercase()
    }

    private fun loadStateCodes(): Map<String, Map<String, String>> = try {
        val raw = context.assets.open(STATE_CODES_ASSET).bufferedReader().use { it.readText() }
        json.decodeFromString<Map<String, List<StateCodeEntry>>>(raw)
            .mapValues { (_, entries) -> entries.associate { it.name.trim().lowercase() to it.code.trim().uppercase() } }
    } catch (e: Exception) {
        AppLogger.e(TAG, "Failed to load $STATE_CODES_ASSET", e)
        emptyMap()
    }

    private companion object {
        const val TAG = "DeviceLocation"
    }
}
