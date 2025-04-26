package com.example.healthride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*
import kotlin.math.roundToInt

/**
 * A composable that displays a Google Map with custom styling and controls.
 */
@Composable
fun HealthRideMap(
    currentLocation: LatLng?,
    pickupLocation: LatLng?,
    dropoffLocation: LatLng?,
    routePoints: List<LatLng> = emptyList(),
    onMyLocationClick: () -> Unit,
    onMapClick: (LatLng) -> Unit = {},
    onMapLoaded: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val mapUiSettings by remember { 
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                myLocationButtonEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true
            )
        ) 
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = MapStyleOptions(HEALTH_RIDE_MAP_STYLE)
            )
        )
    }

    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation ?: DEFAULT_LOCATION, 
            DEFAULT_ZOOM
        )
    }

    // Update camera when location changes
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM),
                CAMERA_ANIMATION_DURATION_MS
            )
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapClick = onMapClick,
            onMapLoaded = onMapLoaded
        ) {
            // Current location marker
            currentLocation?.let {
                CurrentLocationMarker(position = it)
            }

            // Pickup location marker
            pickupLocation?.let {
                LocationMarker(
                    position = it,
                    title = "Pickup",
                    isPickup = true
                )
            }

            // Dropoff location marker
            dropoffLocation?.let {
                LocationMarker(
                    position = it,
                    title = "Dropoff",
                    isPickup = false
                )
            }

            // Route polyline
            if (routePoints.isNotEmpty()) {
                RoutePolyline(points = routePoints)
            }
        }

        // My location button
        FloatingActionButton(
            onClick = onMyLocationClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
                .shadow(4.dp, CircleShape),
            containerColor = Color.White,
            contentColor = HealthRideColors.PrimaryBlue
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                tint = HealthRideColors.PrimaryBlue
            )
        }
    }
}

@Composable
fun CurrentLocationMarker(
    position: LatLng,
    title: String = "Current Location"
) {
    Marker(
        state = rememberMarkerState(position = position),
        title = title,
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
    )
}

@Composable
fun LocationMarker(
    position: LatLng,
    title: String,
    isPickup: Boolean
) {
    val iconHue = if (isPickup) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
    Marker(
        state = rememberMarkerState(position = position),
        title = title,
        icon = BitmapDescriptorFactory.defaultMarker(iconHue)
    )
}

@Composable
fun RoutePolyline(
    points: List<LatLng>
) {
    Polyline(
        points = points,
        color = HealthRideColors.PrimaryBlue,
        width = 8f,
        jointType = JointType.ROUND,
        startCap = RoundCap(),
        endCap = RoundCap()
    )
}

/**
 * Displays route information summary (distance, time, price)
 */
@Composable
fun RouteInfoSummary(
    distance: Float, // in kilometers
    duration: Int, // in minutes
    estimatedPrice: Double,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Distance
            RouteInfoItem(
                value = "${distance.roundToInt()} km",
                label = "Distance"
            )
            
            VerticalDivider()
            
            // Duration
            RouteInfoItem(
                value = "$duration min",
                label = "Duration"
            )
            
            VerticalDivider()
            
            // Price
            RouteInfoItem(
                value = "$${String.format("%.2f", estimatedPrice)}",
                label = "Est. Price"
            )
        }
    }
}

@Composable
private fun RouteInfoItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = HealthRideColors.TextDark
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = HealthRideColors.TextMedium
        )
    }
}

@Composable
private fun RowScope.VerticalDivider() {
    Divider(
        color = HealthRideColors.TextLight.copy(alpha = 0.2f),
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .align(Alignment.CenterVertically)
    )
}

/**
 * MapView that shows a route between two points
 */
@Composable
fun RoutePreviewMap(
    pickupLocation: LatLng,
    dropoffLocation: LatLng,
    routePoints: List<LatLng>,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()
    
    // Calculate bounds to include both pickup and dropoff
    LaunchedEffect(pickupLocation, dropoffLocation) {
        // In a real app, you would compute the bounds that include both points plus some padding
        // For now, we'll just position the camera halfway between
        val midLat = (pickupLocation.latitude + dropoffLocation.latitude) / 2
        val midLng = (pickupLocation.longitude + dropoffLocation.longitude) / 2
        
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(midLat, midLng), 
            12f // Fixed zoom for this example
        )
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false,
            mapStyleOptions = MapStyleOptions(HEALTH_RIDE_MAP_STYLE)
        ),
        uiSettings = MapUiSettings(
            compassEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false
        )
    ) {
        // Route polyline
        Polyline(
            points = routePoints,
            color = HealthRideColors.PrimaryBlue,
            width = 8f,
            jointType = JointType.ROUND,
            startCap = RoundCap(),
            endCap = RoundCap()
        )
        
        // Pickup marker
        Marker(
            state = rememberMarkerState(position = pickupLocation),
            title = "Pickup",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )
        
        // Dropoff marker
        Marker(
            state = rememberMarkerState(position = dropoffLocation),
            title = "Dropoff",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )
    }
}

// Default location (New York City)
private val DEFAULT_LOCATION = LatLng(40.7128, -74.0060)
private const val DEFAULT_ZOOM = 14f
private const val CAMERA_ANIMATION_DURATION_MS = 300

// Custom map style (simplified version)
private const val HEALTH_RIDE_MAP_STYLE = """
[
  {
    "featureType": "administrative",
    "elementType": "geometry",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "featureType": "poi",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "labels.icon",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "featureType": "transit",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  }
]
"""

@Composable
fun MapView(
    pickupLocation: LatLng?,
    destinationLocation: LatLng?,
    routePoints: List<LatLng>,
    modifier: Modifier = Modifier
) {
    val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
    val initialLocation = pickupLocation ?: defaultLocation
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 13f)
    }
    
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            pickupLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Pickup"
                )
            }
            
            destinationLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Destination"
                )
            }
            
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = HealthRideColors.PrimaryBlue,
                    width = 8f
                )
            }
        }
        
        // Map control buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapControlButton(
                icon = Icons.Default.ZoomIn,
                onClick = {
                    cameraPositionState.position = CameraPosition.Builder()
                        .target(cameraPositionState.position.target)
                        .zoom(cameraPositionState.position.zoom + 1)
                        .build()
                }
            )
            
            MapControlButton(
                icon = Icons.Default.ZoomOut,
                onClick = {
                    cameraPositionState.position = CameraPosition.Builder()
                        .target(cameraPositionState.position.target)
                        .zoom(cameraPositionState.position.zoom - 1)
                        .build()
                }
            )
            
            MapControlButton(
                icon = Icons.Default.MyLocation,
                onClick = {
                    pickupLocation?.let {
                        cameraPositionState.position = CameraPosition.Builder()
                            .target(it)
                            .zoom(15f)
                            .build()
                    }
                }
            )
        }
    }
}

@Composable
fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HealthRideColors.TextDark
        )
    }
}

@Composable
fun RideSummary(
    distance: String,
    duration: String,
    estimatedPrice: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Distance
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = distance,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HealthRideColors.TextDark
                )
                
                Text(
                    text = "Distance",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium
                )
            }
            
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = HealthRideColors.BackgroundMedium
            )
            
            // Duration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HealthRideColors.TextDark
                )
                
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium
                )
            }
            
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = HealthRideColors.BackgroundMedium
            )
            
            // Price
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = estimatedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HealthRideColors.PrimaryBlue
                )
                
                Text(
                    text = "Est. Price",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium
                )
            }
        }
    }
}

@Composable
fun AddressSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HealthRideColors.TextMedium
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = HealthRideColors.TextMedium
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HealthRideColors.PrimaryBlue,
            unfocusedBorderColor = HealthRideColors.BackgroundMedium,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AddressSearchSection(
    pickupAddress: String,
    onPickupAddressChange: (String) -> Unit,
    destinationAddress: String,
    onDestinationAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AddressSearchBar(
                value = pickupAddress,
                onValueChange = onPickupAddressChange,
                placeholder = "Pickup location",
                icon = Icons.Default.MyLocation
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AddressSearchBar(
                value = destinationAddress,
                onValueChange = onDestinationAddressChange,
                placeholder = "Where to?",
                icon = Icons.Default.LocationOn
            )
        }
    }
} 