import 'package:flutter/material.dart';
import '../../../constants/app_color.dart';
import '../../../models/ride_type.dart';

/// A step for selecting the ride type
class RideTypeStep extends StatelessWidget {
  /// The currently selected ride type
  final RideType selectedType;

  /// Callback when a type is selected
  final Function(RideType) onTypeSelected;

  /// Creates a ride type step
  const RideTypeStep({
    Key? key,
    required this.selectedType,
    required this.onTypeSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: AppColor.primaryBlue.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Icon(
                Icons.alt_route_rounded,
                color: AppColor.primaryBlue,
                size: 24,
              ),
            ),
            const SizedBox(width: 12),
            const Text(
              'Select Ride Type',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppColor.textDarkBlue,
              ),
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Ambulatory option
        _RideTypeOption(
          title: 'Standard',
          subtitle: 'Ambulatory',
          description:
              'For patients who can walk and enter/exit the vehicle with minimal assistance',
          icon: Icons.directions_car_outlined,
          isSelected: selectedType == RideType.ambulatory,
          onTap: () => onTypeSelected(RideType.ambulatory),
        ),

        const SizedBox(height: 16),

        // Wheelchair option
        _RideTypeOption(
          title: 'Accessible',
          subtitle: 'Wheelchair',
          description:
              'For patients requiring wheelchair transportation with secure boarding assistance',
          icon: Icons.accessible_outlined,
          isSelected: selectedType == RideType.wheelchair,
          onTap: () => onTypeSelected(RideType.wheelchair),
        ),

        const SizedBox(height: 16),

        // Stretcher option
        _RideTypeOption(
          title: 'Medical',
          subtitle: 'Stretcher',
          description:
              'For patients who need to remain in a reclined position during transport',
          icon: Icons.medical_services_outlined,
          isSelected: selectedType == RideType.stretcher,
          onTap: () => onTypeSelected(RideType.stretcher),
        ),

        const SizedBox(height: 24),

        // Info box
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppColor.infoBlue.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: const Row(
            children: [
              Icon(
                Icons.info_outline,
                color: AppColor.primaryBlue,
                size: 24,
              ),
              SizedBox(width: 12),
              Expanded(
                child: Text(
                  'Select the ride type that best fits your needs. All options are covered by your insurance.',
                  style: TextStyle(
                    fontSize: 14,
                    color: AppColor.textDarkBlue,
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

/// A selectable ride type option
class _RideTypeOption extends StatelessWidget {
  /// The title of the ride type
  final String title;

  /// The subtitle of the ride type
  final String subtitle;

  /// The description of the ride type
  final String description;

  /// The icon for the ride type
  final IconData icon;

  /// Whether this option is selected
  final bool isSelected;

  /// Callback when this option is tapped
  final VoidCallback onTap;

  /// Creates a ride type option
  const _RideTypeOption({
    Key? key,
    required this.title,
    required this.subtitle,
    required this.description,
    required this.icon,
    required this.isSelected,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(24),
        gradient: isSelected
            ? const LinearGradient(
                colors: [
                  AppColor.primaryBlue,
                  AppColor.primaryPurple,
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              )
            : null,
        border: isSelected
            ? null
            : Border.all(
                color: AppColor.borderColor,
                width: 1.5,
              ),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(24),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Container(
                          width: 48,
                          height: 48,
                          decoration: BoxDecoration(
                            color: isSelected
                                ? Colors.white.withOpacity(0.2)
                                : AppColor.primaryBlue.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Icon(
                            icon,
                            color: isSelected
                                ? Colors.white
                                : AppColor.primaryBlue,
                            size: 24,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              title,
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: isSelected
                                    ? Colors.white
                                    : AppColor.textDarkBlue,
                              ),
                            ),
                            Text(
                              subtitle,
                              style: TextStyle(
                                fontSize: 14,
                                color: isSelected
                                    ? Colors.white.withOpacity(0.8)
                                    : AppColor.primaryPurple,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    Container(
                      width: 30,
                      height: 30,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color:
                            isSelected ? Colors.white : AppColor.backgroundGray,
                        border: isSelected
                            ? null
                            : Border.all(
                                color: AppColor.borderColor,
                                width: 1.5,
                              ),
                      ),
                      child: isSelected
                          ? const Icon(
                              Icons.check,
                              color: AppColor.primaryBlue,
                              size: 18,
                            )
                          : null,
                    ),
                  ],
                ),
                if (isSelected)
                  Container(
                    padding: const EdgeInsets.only(
                      left: 64,
                      top: 12,
                      right: 12,
                    ),
                    child: Text(
                      description,
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.white.withOpacity(0.9),
                      ),
                    ),
                  ),
                if (!isSelected)
                  Container(
                    padding: const EdgeInsets.only(
                      left: 64,
                      top: 12,
                      right: 12,
                    ),
                    child: Text(
                      description,
                      style: const TextStyle(
                        fontSize: 14,
                        color: AppColor.textMediumGray,
                      ),
                    ),
                  ),
                if (isSelected)
                  Padding(
                    padding: const EdgeInsets.only(top: 16.0, left: 64),
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 8,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: const Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(
                            Icons.check,
                            color: AppColor.primaryBlue,
                            size: 16,
                          ),
                          SizedBox(width: 8),
                          Text(
                            'Selected',
                            style: TextStyle(
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                              color: AppColor.primaryBlue,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
