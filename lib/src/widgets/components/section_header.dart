import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A section header with title and optional action button
class SectionHeader extends StatelessWidget {
  /// Section title
  final String title;

  /// Optional widget to display on the right side (e.g., a button)
  final Widget? action;

  /// Padding around the header
  final EdgeInsetsGeometry padding;

  /// Creates a section header
  const SectionHeader({
    super.key,
    required this.title,
    this.action,
    this.padding = const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: padding,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            title,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppColor.textDarkBlue,
                  fontWeight: FontWeight.w600,
                ),
          ),
          if (action != null) action!,
        ],
      ),
    );
  }
}
