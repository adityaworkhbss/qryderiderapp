package com.qryde.qryderiderapp.presentation.home

import android.graphics.Point
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.domain.model.AddressSuggestion
import com.qryde.qryderiderapp.presentation.components.AddressSearchField
import com.qryde.qryderiderapp.presentation.components.AddressSuggestionsCard
import com.qryde.qryderiderapp.presentation.components.OsmMapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLocationOnMapSheet(
    latitude: Double,
    longitude: Double,
    searchQuery: String,
    searchSuggestions: List<AddressSuggestion>,
    onSearchQueryChanged: (String) -> Unit,
    onSuggestionSelected: (AddressSuggestion) -> Unit,
    onLocateMeClicked: () -> Unit,
    onCenterChanged: (latitude: Double, longitude: Double) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        // A single fillMaxSize() Box, not a Column + weight(1f) - the latter
        // needs a definite bounded height from its parent to split space
        // correctly, which ModalBottomSheet doesn't reliably hand down; a Box
        // with fillMaxSize() always claims the sheet's full allotted area,
        // with every other element (search bar, pin, buttons) as an overlay.
        Box(modifier = Modifier.fillMaxSize()) {
            OsmMapView(
                modifier = Modifier.fillMaxSize(),
                centerLatitude = latitude,
                centerLongitude = longitude,
                onCenterChanged = onCenterChanged,
                onMapReady = { mapView = it }
            )

            DraggableMapPin(
                mapView = mapView,
                latitude = latitude,
                longitude = longitude,
                onDragged = onCenterChanged,
                modifier = Modifier.align(Alignment.Center)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                AddressSearchField(value = searchQuery, onValueChange = onSearchQueryChanged)
                if (searchQuery.isNotBlank() && searchSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AddressSuggestionsCard(
                        items = searchSuggestions,
                        title = { it.title },
                        subtitle = { it.subtitle },
                        onItemClick = onSuggestionSelected
                    )
                }
            }

            FloatingActionButton(
                onClick = onLocateMeClicked,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 96.dp),
                containerColor = Color.White,
                contentColor = QrydePrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Use my current location")
            }

            Button(
                onClick = onSave,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(52.dp)
            ) {
                Text("Save Address", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * A real drag-and-drop pin, implemented with Compose's own gesture handling
 * rather than osmdroid's Marker overlay - osmdroid's Marker fights with the
 * MapView's own pan gesture for the same touch stream (both are native View
 * touch handlers), which is what made it feel small/janky before. Compose
 * intercepts a touch that starts on this composable before it ever reaches
 * the AndroidView underneath, so there's no gesture conflict: dragging the
 * pin never pans the map, and panning the map elsewhere is untouched.
 */
@Composable
private fun DraggableMapPin(
    mapView: MapView?,
    latitude: Double,
    longitude: Double,
    onDragged: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var originScreenPoint by remember { mutableStateOf<Point?>(null) }

    // Once the map recenters to wherever we just dropped the pin, its
    // on-screen position is back at the center again - safe to zero out.
    LaunchedEffect(latitude, longitude) {
        dragOffset = Offset.Zero
    }

    // Measured on an unoffset wrapper, so this stays fixed at the pin's rest
    // position instead of chasing the drag itself (onGloballyPositioned would
    // otherwise report the post-offset position if measured on the same node
    // the offset is applied to).
    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            val centerInParent = coordinates.positionInParent() + Offset(
                coordinates.size.width / 2f,
                coordinates.size.height / 2f
            )
            originScreenPoint = Point(centerInParent.x.roundToInt(), centerInParent.y.roundToInt())
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                .pointerInput(mapView) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            val map = mapView
                            val origin = originScreenPoint
                            if (map != null && origin != null) {
                                val droppedPoint = map.projection.fromPixels(
                                    origin.x + dragOffset.x.roundToInt(),
                                    origin.y + dragOffset.y.roundToInt()
                                ) as GeoPoint
                                onDragged(droppedPoint.latitude, droppedPoint.longitude)
                            }
                        },
                        onDragCancel = { isDragging = false; dragOffset = Offset.Zero }
                    ) { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    }
                }
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Drag to set your location",
                tint = QrydePrimary,
                modifier = Modifier.size(if (isDragging) 96.dp else 80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isDragging) "Move to set your location" else "Current Location",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QrydePrimary)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

