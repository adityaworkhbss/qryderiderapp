package com.qryde.qryderiderapp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * Default map center: Sector 49, Gurugram - matches the mock pickup location
 * used across the booking flow's sample data.
 */
const val DefaultMapLatitude = 28.4595
const val DefaultMapLongitude = 77.0266

/**
 * Plain OSM tile surface - no marker rendering here. Callers that want a
 * pin drawn on top (e.g. a fixed "current location" indicator) should
 * layer [CurrentLocationPinOverlay] over this in a Box, since Compose text
 * and images render more crisply than anything baked into a map overlay
 * bitmap.
 */
@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    centerLatitude: Double = DefaultMapLatitude,
    centerLongitude: Double = DefaultMapLongitude,
    zoomLevel: Double = 16.0,
    onCenterChanged: ((latitude: Double, longitude: Double) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnCenterChanged by rememberUpdatedState(onCenterChanged)

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(zoomLevel)
            controller.setCenter(GeoPoint(centerLatitude, centerLongitude))
        }
    }

    DisposableEffect(mapView) {
        val listener = object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val center = mapView.mapCenter
                latestOnCenterChanged?.invoke(center.latitude, center.longitude)
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean = false
        }
        mapView.addMapListener(listener)
        onDispose { mapView.removeMapListener(listener) }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    LaunchedEffect(centerLatitude, centerLongitude) {
        mapView.controller.setCenter(GeoPoint(centerLatitude, centerLongitude))
    }

    AndroidView(modifier = modifier, factory = { mapView })
}
