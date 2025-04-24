import 'package:flutter/material.dart';
import '../../constants/app_color.dart';
import '../../models/ride_type.dart';

class RideTypeInfoDialog extends StatefulWidget {
  final RideType rideType;
  final VoidCallback onDismiss;

  const RideTypeInfoDialog({
    Key? key,
    required this.rideType,
    required this.onDismiss,
  }) : super(key: key);

  @override
  State<RideTypeInfoDialog> createState() => _RideTypeInfoDialogState();
}

class _RideTypeInfoDialogState extends State<RideTypeInfoDialog> {
  bool _showDialog = false;

  @override
  void initState() {
    super.initState();
    // Trigger animation after a brief delay
    Future.delayed(const Duration(milliseconds: 50), () {
      if (mounted) {
        setState(() {
          _showDialog = true;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final info = _getRideTypeInfo(widget.rideType);

    return Dialog(
      backgroundColor: Colors.transparent,
      child: TweenAnimationBuilder<double>(
        duration: const Duration(milliseconds: 300),
        curve: Curves.fastOutSlowIn,
        tween: Tween(begin: 0.8, end: _showDialog ? 1.0 : 0.8),
        builder: (context, scale, child) {
          return Transform.scale(
            scale: scale,
            child: child,
          );
        },
        child: Container(
          padding: const EdgeInsets.all(24),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(28),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header with icon and title
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: AppColor.primaryPurple.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Icon(info.icon, color: AppColor.primaryPurple),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Text(
                      info.title,
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  IconButton(
                    onPressed: widget.onDismiss,
                    icon: const Icon(Icons.close),
                    color: AppColor.textMediumGray,
                  ),
                ],
              ),
              const SizedBox(height: 16),

              // Description
              Text(
                info.description,
                style: TextStyle(
                  fontSize: 16,
                  color: AppColor.textDarkBlue.withOpacity(0.8),
                  height: 1.5,
                ),
              ),
              const SizedBox(height: 24),

              // Features section
              const Text(
                'Features',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                  color: AppColor.primaryPurple,
                ),
              ),
              const SizedBox(height: 16),

              // Features list with animated items
              ...List.generate(info.features.length, (index) {
                return TweenAnimationBuilder<double>(
                  duration: Duration(milliseconds: 200 + (index * 100)),
                  curve: Curves.easeOut,
                  tween: Tween(begin: 0.0, end: _showDialog ? 1.0 : 0.0),
                  builder: (context, value, child) {
                    return Transform.translate(
                      offset: Offset(20 * (1 - value), 0),
                      child: Opacity(
                        opacity: value,
                        child: child,
                      ),
                    );
                  },
                  child: Padding(
                    padding: const EdgeInsets.only(bottom: 12),
                    child: Row(
                      children: [
                        Container(
                          width: 20,
                          height: 20,
                          decoration: BoxDecoration(
                            color: AppColor.primaryPurple.withOpacity(0.1),
                            shape: BoxShape.circle,
                          ),
                          child: const Icon(
                            Icons.check,
                            size: 16,
                            color: AppColor.primaryPurple,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            info.features[index],
                            style: TextStyle(
                              fontSize: 16,
                              color: AppColor.textDarkBlue.withOpacity(0.9),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              }),
            ],
          ),
        ),
      ),
    );
  }
}

class RideTypeInfo {
  final String title;
  final String description;
  final IconData icon;
  final List<String> features;

  const RideTypeInfo({
    required this.title,
    required this.description,
    required this.icon,
    required this.features,
  });
}

RideTypeInfo _getRideTypeInfo(RideType type) {
  switch (type) {
    case RideType.ambulatory:
      return const RideTypeInfo(
        title: "Standard (Ambulatory)",
        description:
            "For patients who can walk and transfer without significant assistance. Includes sedan or SUV transportation with a professional driver trained in patient transport.",
        icon: Icons.directions_car_outlined,
        features: [
          "Door-to-door service",
          "Professional medical transport driver",
          "Comfortable sedan or SUV vehicle",
          "Driver assistance with boarding"
        ],
      );
    case RideType.wheelchair:
      return const RideTypeInfo(
        title: "Wheelchair Accessible",
        description:
            "For patients requiring wheelchair transportation. Includes a wheelchair-accessible vehicle and drivers trained to assist with secure boarding, transport, and exiting.",
        icon: Icons.accessible_outlined,
        features: [
          "Wheelchair-accessible vehicle",
          "Hydraulic lift or ramp",
          "Secure wheelchair anchoring",
          "Specially trained drivers",
          "Full boarding and exiting assistance"
        ],
      );
    case RideType.stretcher:
      return const RideTypeInfo(
        title: "Medical Transport",
        description:
            "For patients requiring stretcher transportation. Includes a specially equipped medical transport vehicle and certified medical transport professionals.",
        icon: Icons.medical_services_outlined,
        features: [
          "Specially equipped medical vehicle",
          "Certified medical transport team",
          "Advanced medical equipment",
          "Secure patient stabilization",
          "Door-through-door service",
          "Continuous medical monitoring"
        ],
      );
    default:
      return const RideTypeInfo(
        title: "Unknown",
        description: "Description not available",
        icon: Icons.help_outline,
        features: [],
      );
  }
}
