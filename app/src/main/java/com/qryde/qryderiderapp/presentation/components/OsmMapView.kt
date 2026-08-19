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
import com.qryde.qryderiderapp.BuildConfig
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

const val DefaultMapLatitude = 28.4595
const val DefaultMapLongitude = 77.0266

private val MapTilerTileSource: ITileSource? = BuildConfig.MAPTILER_API_KEY
    .takeIf { it.isNotBlank() }
    ?.let { apiKey ->
        XYTileSource(
            "MapTilerBasic",
            0,
            19,
            256,
            ".png?key=$apiKey",
            arrayOf("https://api.maptiler.com/maps/basic-v2/")
        )
    }

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    centerLatitude: Double = DefaultMapLatitude,
    centerLongitude: Double = DefaultMapLongitude,
    zoomLevel: Double = 16.0,
    onCenterChanged: ((latitude: Double, longitude: Double) -> Unit)? = null,
    onMapReady: ((MapView) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnCenterChanged by rememberUpdatedState(onCenterChanged)

    val mapView = remember {
        MapView(context).apply {
            setTileSource(MapTilerTileSource ?: TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(zoomLevel)
            controller.setCenter(GeoPoint(centerLatitude, centerLongitude))
        }
    }

    LaunchedEffect(mapView) {
        onMapReady?.invoke(mapView)
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
