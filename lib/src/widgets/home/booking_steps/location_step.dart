import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../../constants/app_color.dart';
import '../address_search_bar.dart';

/// A step for selecting pickup and dropoff locations
class LocationStep extends StatelessWidget {
  /// The pickup location text
  final String pickupText;

  /// The dropoff location text
  final String dropoffText;

  /// The pickup location coordinates
  final LatLng? pickupLocation;

  /// The dropoff location coordinates
  final LatLng? dropoffLocation;

  /// Callback when pickup text changes
  final Function(String) onPickupTextChanged;

  /// Callback when dropoff text changes
  final Function(String) onDropoffTextChanged;

  /// Callback when pickup location is tapped
  final VoidCallback onPickupTap;

  /// Callback when dropoff location is tapped
  final VoidCallback onDropoffTap;

  /// Creates a location step
  const LocationStep({
    Key? key,
    required this.pickupText,
    required this.dropoffText,
    this.pickupLocation,
    this.dropoffLocation,
    required this.onPickupTextChanged,
    required this.onDropoffTextChanged,
    required this.onPickupTap,
    required this.onDropoffTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Enter Locations',
            style: Theme.of(context).textTheme.titleLarge,
          ),
          const SizedBox(height: 24),

          // Pickup address field
          Text(
            'Pickup Location',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppColor.textDarkBlue,
                  fontWeight: FontWeight.w600,
                ),
          ),
          const SizedBox(height: 8),
          AddressSearchBar(
            controller: TextEditingController(text: pickupText),
            placeholder: 'Enter pickup address',
            icon: Icons.location_on_outlined,
            iconColor: AppColor.primaryBlue,
            onTap: onPickupTap,
            onChanged: onPickupTextChanged,
          ),

          const SizedBox(height: 24),

          // Dropoff address field
          Text(
            'Destination',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppColor.textDarkBlue,
                  fontWeight: FontWeight.w600,
                ),
          ),
          const SizedBox(height: 8),
          AddressSearchBar(
            controller: TextEditingController(text: dropoffText),
            placeholder: 'Enter destination address',
            icon: Icons.flag_outlined,
            iconColor: AppColor.primaryPurple,
            onTap: onDropoffTap,
            onChanged: onDropoffTextChanged,
          ),

          // Locations validation message
          if (pickupText.isEmpty || dropoffText.isEmpty)
            Padding(
              padding: const EdgeInsets.only(top: 16.0),
              child: Text(
                'Please enter both pickup and destination addresses',
                style: TextStyle(
                  color: AppColor.errorRed,
                  fontSize: 14,
                ),
              ),
            ),

          const Spacer(),

          // Location shortcuts (recent or saved locations)
          if (pickupText.isEmpty || dropoffText.isEmpty)
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Saved Locations',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 8),
                _SavedLocationItem(
                  icon: Icons.home_outlined,
                  title: 'Home',
                  address: '123 Home Street, City',
                  onTap: () {
                    if (pickupText.isEmpty) {
                      onPickupTextChanged('123 Home Street, City');
                    } else if (dropoffText.isEmpty) {
                      onDropoffTextChanged('123 Home Street, City');
                    }
                  },
                ),
                const SizedBox(height: 8),
                _SavedLocationItem(
                  icon: Icons.business_outlined,
                  title: 'Work',
                  address: '456 Office Building, Downtown',
                  onTap: () {
                    if (pickupText.isEmpty) {
                      onPickupTextChanged('456 Office Building, Downtown');
                    } else if (dropoffText.isEmpty) {
                      onDropoffTextChanged('456 Office Building, Downtown');
                    }
                  },
                ),
                const SizedBox(height: 8),
                _SavedLocationItem(
                  icon: Icons.local_hospital_outlined,
                  title: 'Medical Center',
                  address: '789 Health Avenue, Medical District',
                  onTap: () {
                    if (pickupText.isEmpty) {
                      onPickupTextChanged(
                          '789 Health Avenue, Medical District');
                    } else if (dropoffText.isEmpty) {
                      onDropoffTextChanged(
                          '789 Health Avenue, Medical District');
                    }
                  },
                ),
              ],
            ),
        ],
      ),
    );
  }
}

/// A saved location item widget
class _SavedLocationItem extends StatelessWidget {
  final IconData icon;
  final String title;
  final String address;
  final VoidCallback onTap;

  const _SavedLocationItem({
    Key? key,
    required this.icon,
    required this.title,
    required this.address,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 12.0),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: AppColor.backgroundBlueGray,
                shape: BoxShape.circle,
              ),
              child: Icon(
                icon,
                color: AppColor.primaryBlue,
                size: 20,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    address,
                    style: const TextStyle(
                      color: AppColor.textMediumGray,
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ),
            Icon(
              Icons.arrow_forward_ios,
              size: 16,
              color: AppColor.textLightGray,
            ),
          ],
        ),
      ),
    );
  }
}
