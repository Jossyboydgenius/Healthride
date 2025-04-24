import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../constants/app_color.dart';

/// A map widget for the Health Ride app
class HealthRideMap extends StatefulWidget {
  /// The current location of the user
  final LatLng? currentLocation;

  /// The pickup location
  final LatLng? pickupLocation;

  /// The dropoff location
  final LatLng? dropoffLocation;

  /// The route points for drawing the route
  final List<LatLng> routePoints;

  /// Callback when the map is loaded
  final VoidCallback? onMapLoaded;

  /// Callback when the my location button is clicked
  final VoidCallback? onMyLocationClick;

  /// Creates a map widget
  const HealthRideMap({
    Key? key,
    this.currentLocation,
    this.pickupLocation,
    this.dropoffLocation,
    this.routePoints = const [],
    this.onMapLoaded,
    this.onMyLocationClick,
  }) : super(key: key);

  @override
  State<HealthRideMap> createState() => _HealthRideMapState();
}

class _HealthRideMapState extends State<HealthRideMap> {
  GoogleMapController? _mapController;
  final Set<Marker> _markers = {};
  final Set<Polyline> _polylines = {};

  @override
  void didUpdateWidget(HealthRideMap oldWidget) {
    super.didUpdateWidget(oldWidget);
    _updateMarkers();
    _updateCamera();
  }

  void _updateMarkers() {
    final markers = <Marker>{};

    // Add current location marker
    if (widget.currentLocation != null) {
      markers.add(Marker(
        markerId: const MarkerId('current_location'),
        position: widget.currentLocation!,
        icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue),
      ));
    }

    // Add pickup location marker
    if (widget.pickupLocation != null) {
      markers.add(Marker(
        markerId: const MarkerId('pickup'),
        position: widget.pickupLocation!,
        icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen),
      ));
    }

    // Add dropoff location marker
    if (widget.dropoffLocation != null) {
      markers.add(Marker(
        markerId: const MarkerId('dropoff'),
        position: widget.dropoffLocation!,
        icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed),
      ));
    }

    // Update polylines if there are route points
    final polylines = <Polyline>{};
    if (widget.routePoints.isNotEmpty) {
      polylines.add(Polyline(
        polylineId: const PolylineId('route'),
        points: widget.routePoints,
        color: AppColor.primaryBlue,
        width: 5,
      ));
    }

    setState(() {
      _markers.clear();
      _markers.addAll(markers);
      _polylines.clear();
      _polylines.addAll(polylines);
    });
  }

  void _updateCamera() {
    if (_mapController == null) return;

    // Determine camera position based on available locations
    LatLng target;
    double zoom = 14.0;

    if (widget.pickupLocation != null && widget.dropoffLocation != null) {
      // Center between pickup and dropoff
      target = LatLng(
        (widget.pickupLocation!.latitude + widget.dropoffLocation!.latitude) /
            2,
        (widget.pickupLocation!.longitude + widget.dropoffLocation!.longitude) /
            2,
      );

      // Calculate zoom to show both points
      final latDiff =
          (widget.pickupLocation!.latitude - widget.dropoffLocation!.latitude)
              .abs();
      final lngDiff =
          (widget.pickupLocation!.longitude - widget.dropoffLocation!.longitude)
              .abs();
      final maxDiff = latDiff > lngDiff ? latDiff : lngDiff;

      if (maxDiff > 0.05)
        zoom = 12.0;
      else if (maxDiff > 0.01) zoom = 13.0;
    } else if (widget.pickupLocation != null) {
      target = widget.pickupLocation!;
    } else if (widget.dropoffLocation != null) {
      target = widget.dropoffLocation!;
    } else if (widget.currentLocation != null) {
      target = widget.currentLocation!;
    } else {
      // Default to city center if no location is available
      target = const LatLng(37.7749, -122.4194); // San Francisco
    }

    _mapController!.animateCamera(CameraUpdate.newCameraPosition(
      CameraPosition(target: target, zoom: zoom),
    ));
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        GoogleMap(
          initialCameraPosition: CameraPosition(
            target: widget.currentLocation ?? const LatLng(37.7749, -122.4194),
            zoom: 14.0,
          ),
          markers: _markers,
          polylines: _polylines,
          zoomControlsEnabled: false,
          myLocationButtonEnabled: false,
          myLocationEnabled: true,
          mapToolbarEnabled: false,
          compassEnabled: false,
          onMapCreated: (controller) {
            _mapController = controller;
            _updateMarkers();
            _updateCamera();
            if (widget.onMapLoaded != null) {
              widget.onMapLoaded!();
            }
          },
          mapType: MapType.normal,
          rotateGesturesEnabled: true,
          scrollGesturesEnabled: true,
          zoomGesturesEnabled: true,
          tiltGesturesEnabled: false,
        ),

        // Add a custom my location button in the bottom right
        if (widget.onMyLocationClick != null)
          Positioned(
            right: 16,
            bottom: 16,
            child: FloatingActionButton(
              mini: true,
              backgroundColor: Colors.white,
              onPressed: widget.onMyLocationClick,
              child: Icon(
                Icons.my_location,
                color: AppColor.primaryBlue,
              ),
            ),
          ),
      ],
    );
  }
}
