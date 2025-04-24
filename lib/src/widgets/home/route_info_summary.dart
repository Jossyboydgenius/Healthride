import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A summary of route information
class RouteInfoSummary extends StatelessWidget {
  /// The distance of the route in kilometers
  final double distance;

  /// The duration of the route in minutes
  final int duration;

  /// The estimated price of the ride
  final double estimatedPrice;

  /// Creates a route info summary
  const RouteInfoSummary({
    Key? key,
    required this.distance,
    required this.duration,
    required this.estimatedPrice,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      color: Colors.white,
      elevation: 6,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            // Distance
            _RouteInfoItem(
              value: "${distance.round()} km",
              label: "Distance",
            ),

            _VerticalDivider(),

            // Duration
            _RouteInfoItem(
              value: "$duration min",
              label: "Duration",
            ),

            _VerticalDivider(),

            // Price
            _RouteInfoItem(
              value: "\$${estimatedPrice.toStringAsFixed(2)}",
              label: "Est. Price",
            ),
          ],
        ),
      ),
    );
  }
}

/// A vertical divider
class _VerticalDivider extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 36,
      width: 1,
      color: AppColor.textLightGray.withOpacity(0.2),
    );
  }
}

/// A route info item with a value and label
class _RouteInfoItem extends StatelessWidget {
  /// The value text
  final String value;

  /// The label text
  final String label;

  /// Creates a route info item
  const _RouteInfoItem({
    Key? key,
    required this.value,
    required this.label,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Text(
          value,
          style: const TextStyle(
            color: AppColor.textDarkBlue,
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: const TextStyle(
            color: AppColor.textMediumGray,
            fontSize: 12,
            fontWeight: FontWeight.normal,
          ),
        ),
      ],
    );
  }
}
