import 'package:flutter/material.dart';
import '../../constants/app_color.dart';
import '../../models/ride_status.dart';

/// A chip that shows the status of a ride
class StatusChip extends StatelessWidget {
  /// The status to display
  final RideStatus status;

  /// Creates a status chip
  const StatusChip({
    super.key,
    required this.status,
  });

  @override
  Widget build(BuildContext context) {
    // Define colors and text based on status
    final (backgroundColor, iconColor, text) = _getStatusProps(status);

    return Container(
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(16.0),
      ),
      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 6.0),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            Icons.check_circle_rounded,
            color: iconColor,
            size: 14.0,
          ),
          const SizedBox(width: 4.0),
          Text(
            text,
            style: TextStyle(
              fontSize: 12.0,
              fontWeight: FontWeight.w500,
              color: iconColor,
            ),
          ),
        ],
      ),
    );
  }

  /// Get the background color, icon color, and text for a status
  (Color, Color, String) _getStatusProps(RideStatus status) {
    switch (status) {
      case RideStatus.scheduled:
        return (
          AppColor.infoBlueLight,
          AppColor.infoBlue,
          'Scheduled',
        );
      case RideStatus.enRoute:
        return (
          AppColor.warningYellowLight,
          AppColor.warningYellow,
          'En Route',
        );
      case RideStatus.arrived:
        return (
          AppColor.warningYellowLight,
          AppColor.warningYellow,
          'Arrived',
        );
      case RideStatus.inProgress:
        return (
          AppColor.warningYellowLight,
          AppColor.warningYellow,
          'In Progress',
        );
      case RideStatus.completed:
        return (
          AppColor.successGreenLight,
          AppColor.successGreen,
          'Completed',
        );
      case RideStatus.cancelled:
        return (
          AppColor.errorRedLight,
          AppColor.errorRed,
          'Cancelled',
        );
    }
  }
}
