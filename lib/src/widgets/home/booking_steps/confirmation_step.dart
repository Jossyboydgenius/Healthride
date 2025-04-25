import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:intl/intl.dart';
import '../../../constants/app_color.dart';
import '../route_info_summary.dart';

class ConfirmationStep extends StatelessWidget {
  final String pickupAddress;
  final String destinationAddress;
  final DateTime selectedDate;
  final String? selectedTime;
  final bool isRoundTrip;
  final DateTime? returnDate;
  final String? returnTime;
  final double distance;
  final int duration;
  final double estimatedPrice;
  final LatLng? pickupLocation;
  final LatLng? dropoffLocation;
  final List<LatLng> routePoints;

  const ConfirmationStep({
    super.key,
    required this.pickupAddress,
    required this.destinationAddress,
    required this.selectedDate,
    required this.selectedTime,
    required this.isRoundTrip,
    this.returnDate,
    this.returnTime,
    required this.distance,
    required this.duration,
    required this.estimatedPrice,
    this.pickupLocation,
    this.dropoffLocation,
    this.routePoints = const [],
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Ready to Book container
        Container(
          width: double.infinity,
          decoration: BoxDecoration(
            color: const Color(0xFFECFCF4),
            borderRadius: BorderRadius.circular(16),
          ),
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: const BoxDecoration(
                  color: Color(0xFF10B981),
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.check,
                  color: Colors.white,
                  size: 30,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Ready to Book',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF10B981),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Please review your ride details',
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.black87.withOpacity(0.7),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),

        const SizedBox(height: 24),

        const Text(
          'Ride Summary',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
            color: AppColor.textDarkBlue,
          ),
        ),
        const SizedBox(height: 16),

        // Location details
        Container(
          decoration: BoxDecoration(
            color: AppColor.backgroundGray,
            borderRadius: BorderRadius.circular(16),
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              _buildAddressRow(
                icon: Icons.adjust,
                iconColor: AppColor.primaryBlue,
                title: 'From',
                address: pickupAddress,
              ),
              const SizedBox(height: 16),
              _buildAddressRow(
                icon: Icons.place,
                iconColor: AppColor.primaryPurple,
                title: 'To',
                address: destinationAddress,
              ),
              const SizedBox(height: 16),
              _buildInfoRow(
                icon: Icons.calendar_today,
                title: 'Date & Time',
                info:
                    '${DateFormat('EEE, MMM d').format(selectedDate)} • $selectedTime',
              ),
              if (isRoundTrip && returnDate != null && returnTime != null) ...[
                const SizedBox(height: 16),
                _buildInfoRow(
                  icon: Icons.repeat,
                  title: 'Return Trip',
                  info:
                      '${DateFormat('EEE, MMM d').format(returnDate!)} • $returnTime',
                ),
              ],
            ],
          ),
        ),

        const SizedBox(height: 24),

        // Map preview
        if (pickupLocation != null && dropoffLocation != null) ...[
          Container(
            height: 200,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: 8,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            clipBehavior: Clip.antiAlias,
            child: GoogleMap(
              initialCameraPosition: CameraPosition(
                target: _calculateCenter(),
                zoom: 13,
              ),
              markers: {
                Marker(
                  markerId: const MarkerId('pickup'),
                  position: pickupLocation!,
                  icon: BitmapDescriptor.defaultMarkerWithHue(
                      BitmapDescriptor.hueBlue),
                ),
                Marker(
                  markerId: const MarkerId('dropoff'),
                  position: dropoffLocation!,
                  icon: BitmapDescriptor.defaultMarkerWithHue(
                      BitmapDescriptor.hueMagenta),
                ),
              },
              polylines: routePoints.isNotEmpty
                  ? {
                      Polyline(
                        polylineId: const PolylineId('route'),
                        points: routePoints,
                        color: AppColor.primaryBlue,
                        width: 5,
                      ),
                    }
                  : {},
              zoomControlsEnabled: false,
              myLocationButtonEnabled: false,
              mapToolbarEnabled: false,
              scrollGesturesEnabled: false,
              zoomGesturesEnabled: false,
              tiltGesturesEnabled: false,
              rotateGesturesEnabled: false,
            ),
          ),
          const SizedBox(height: 24),
        ],

        // Route info
        RouteInfoSummary(
          distance: distance,
          duration: duration,
          estimatedPrice: estimatedPrice,
        ),

        const SizedBox(height: 24),

        // Payment information
        Container(
          decoration: BoxDecoration(
            color: AppColor.backgroundGray,
            borderRadius: BorderRadius.circular(16),
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Payment',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: AppColor.textDarkBlue,
                ),
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      color: AppColor.primaryBlue.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Icon(
                      Icons.health_and_safety,
                      color: AppColor.primaryBlue,
                      size: 20,
                    ),
                  ),
                  const SizedBox(width: 16),
                  const Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Insurance Coverage',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                            color: AppColor.textDarkBlue,
                          ),
                        ),
                        SizedBox(height: 4),
                        Text(
                          'This ride is covered by your insurance',
                          style: TextStyle(
                            fontSize: 14,
                            color: AppColor.textMediumGray,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildAddressRow({
    required IconData icon,
    required Color iconColor,
    required String title,
    required String address,
  }) {
    return Row(
      children: [
        Icon(
          icon,
          color: iconColor,
          size: 20,
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                address,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: AppColor.textDarkBlue,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildInfoRow({
    required IconData icon,
    required String title,
    required String info,
  }) {
    return Row(
      children: [
        Icon(
          icon,
          color: AppColor.primaryBlue,
          size: 20,
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                info,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: AppColor.textDarkBlue,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  LatLng _calculateCenter() {
    if (pickupLocation != null && dropoffLocation != null) {
      return LatLng(
        (pickupLocation!.latitude + dropoffLocation!.latitude) / 2,
        (pickupLocation!.longitude + dropoffLocation!.longitude) / 2,
      );
    }
    return pickupLocation ?? dropoffLocation ?? const LatLng(0, 0);
  }
}
