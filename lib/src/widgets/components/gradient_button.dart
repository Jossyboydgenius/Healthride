import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A button with a gradient background
class GradientButton extends StatelessWidget {
  /// Button text
  final String text;

  /// Function called when the button is pressed
  final VoidCallback onPressed;

  /// Button width
  final double? width;

  /// Button height
  final double height;

  /// Button padding
  final EdgeInsets padding;

  /// Whether the button is in loading state
  final bool isLoading;

  /// Whether the button is enabled
  final bool enabled;

  /// Icon displayed before the text
  final IconData? icon;

  /// Creates a gradient button
  const GradientButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.width,
    this.height = 56,
    this.padding = const EdgeInsets.symmetric(horizontal: 16),
    this.isLoading = false,
    this.enabled = true,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height,
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: AppColor.primaryGradient,
          begin: Alignment.centerLeft,
          end: Alignment.centerRight,
        ),
        borderRadius: BorderRadius.circular(28),
        boxShadow: [
          BoxShadow(
            color: AppColor.primaryBlue.withOpacity(0.3),
            offset: const Offset(0, 4),
            blurRadius: 12,
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: (!enabled || isLoading) ? null : onPressed,
          borderRadius: BorderRadius.circular(28),
          child: Padding(
            padding: padding,
            child: Center(
              child: isLoading
                  ? const SizedBox(
                      width: 24,
                      height: 24,
                      child: CircularProgressIndicator(
                        color: Colors.white,
                        strokeWidth: 2,
                      ),
                    )
                  : Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        if (icon != null) ...[
                          Icon(
                            icon,
                            color: enabled
                                ? Colors.white
                                : Colors.white.withOpacity(0.6),
                            size: 20,
                          ),
                          const SizedBox(width: 8),
                        ],
                        Text(
                          text,
                          style: TextStyle(
                            color: enabled
                                ? Colors.white
                                : Colors.white.withOpacity(0.6),
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
            ),
          ),
        ),
      ),
    );
  }
}
