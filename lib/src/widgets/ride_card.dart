import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../constants/app_color.dart';
import '../models/ride.dart';
import '../models/ride_status.dart';

/// A card that displays ride information
class RideCard extends StatelessWidget {
  /// The ride to display
  final Ride ride;

  /// Callback when the card is tapped
  final VoidCallback onTap;

  /// Creates a ride card
  const RideCard({
    super.key,
    required this.ride,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 6,
      shadowColor: AppColor.primaryPurple.withOpacity(0.1),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(20),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: [
              // Date and status row
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  // Date with icon
                  Row(
                    children: [
                      const Icon(
                        Icons.calendar_today_rounded,
                        size: 16,
                        color: AppColor.primaryPurple,
                      ),
                      const SizedBox(width: 6),
                      Text(
                        _formatDateTime(ride.dateTime),
                        style: const TextStyle(
                          fontWeight: FontWeight.w500,
                          color: AppColor.textDarkBlue,
                          fontSize: 13,
                        ),
                      ),
                    ],
                  ),
                  // Status badge
                  StatusBadge(status: ride.status),
                ],
              ),
              const SizedBox(height: 12),
              // Pickup address
              Row(
                children: [
                  const Icon(
                    Icons.adjust_outlined,
                    size: 18,
                    color: AppColor.primaryBlue,
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      ride.pickupAddress,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        color: AppColor.textDarkBlue,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 4),
              // Destination address
              Row(
                children: [
                  const Icon(
                    Icons.place_outlined,
                    size: 18,
                    color: AppColor.primaryPurple,
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      ride.destinationAddress,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        color: AppColor.textDarkBlue,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _formatDateTime(DateTime dateTime) {
    final DateFormat formatter = DateFormat('EEE, MMM d • h:mm a');
    return formatter.format(dateTime);
  }
}

/// A badge that displays ride status
class StatusBadge extends StatelessWidget {
  /// The ride status
  final RideStatus status;

  /// Creates a status badge
  const StatusBadge({
    super.key,
    required this.status,
  });

  @override
  Widget build(BuildContext context) {
    final Color backgroundColor;
    final Color textColor;
    final String text;

    switch (status) {
      case RideStatus.scheduled:
        backgroundColor = AppColor.primaryBlue.withOpacity(0.1);
        textColor = AppColor.primaryBlue;
        text = 'Scheduled';
        break;
      case RideStatus.enRoute:
        backgroundColor = AppColor.warningYellow.withOpacity(0.1);
        textColor = AppColor.warningYellow;
        text = 'En Route';
        break;
      case RideStatus.arrived:
        backgroundColor = AppColor.warningYellow.withOpacity(0.1);
        textColor = AppColor.warningYellow;
        text = 'Arrived';
        break;
      case RideStatus.inProgress:
        backgroundColor = AppColor.warningYellow.withOpacity(0.1);
        textColor = AppColor.warningYellow;
        text = 'In Progress';
        break;
      case RideStatus.completed:
        backgroundColor = AppColor.successGreen.withOpacity(0.1);
        textColor = AppColor.successGreen;
        text = 'Completed';
        break;
      case RideStatus.cancelled:
        backgroundColor = AppColor.errorRed.withOpacity(0.1);
        textColor = AppColor.errorRed;
        text = 'Cancelled';
        break;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        text,
        style: TextStyle(
          color: textColor,
          fontWeight: FontWeight.w500,
          fontSize: 12,
        ),
      ),
    );
  }
}
