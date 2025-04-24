import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A modern styled card with rounded corners and elevation
class ModernCard extends StatelessWidget {
  /// The card content
  final Widget child;

  /// Card elevation
  final double elevation;

  /// Additional padding around the content
  final EdgeInsetsGeometry padding;

  /// Card width
  final double? width;

  /// Card height
  final double? height;

  /// Margin around the card
  final EdgeInsetsGeometry? margin;

  /// Function called when the card is tapped
  final VoidCallback? onTap;

  /// Card background color
  final Color backgroundColor;

  /// Border radius of the card
  final double borderRadius;

  /// Creates a modern card
  const ModernCard({
    super.key,
    required this.child,
    this.elevation = 4.0,
    this.padding = const EdgeInsets.all(16.0),
    this.width,
    this.height,
    this.margin,
    this.onTap,
    this.backgroundColor = AppColor.surfaceWhite,
    this.borderRadius = 16.0,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height,
      margin: margin,
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(borderRadius),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            offset: const Offset(0, 2),
            blurRadius: elevation * 2,
          ),
          BoxShadow(
            color: AppColor.primaryBlue.withOpacity(0.05),
            offset: Offset(0, elevation),
            blurRadius: elevation * 3,
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(borderRadius),
          splashColor: AppColor.primaryBlue.withOpacity(0.05),
          highlightColor: AppColor.primaryBlue.withOpacity(0.02),
          child: Padding(
            padding: padding,
            child: child,
          ),
        ),
      ),
    );
  }
}
