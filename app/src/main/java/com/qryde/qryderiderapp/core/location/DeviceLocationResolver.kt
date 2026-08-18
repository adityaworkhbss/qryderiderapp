package com.qryde.qryderiderapp.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val stateCode: String?
)

/**
 * Plain LocationManager + Geocoder - no Play Services location dependency,
 * matching this app's use of osmdroid instead of Google Maps elsewhere.
 */
class DeviceLocationResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
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
                Geocoder(context, Locale.US).getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                    ?.adminArea
            }.getOrNull()
        }
}
