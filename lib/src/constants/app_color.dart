import 'package:flutter/material.dart';

/// App color constants based on the original HealthRide Jetpack Compose theme
class AppColor {
  // Primary colors
  static const Color primaryBlue = Color(0xFF0080FF);
  static const Color primaryPurple = Color(0xFF7C3AED);
  static const Color primaryBlueLight = Color(0xFFE6ECFD);
  static const Color primaryPurpleLight = Color(0xFFF2EAFF);

  // Text colors
  static const Color textDarkBlue = Color(0xFF1E293B);
  static const Color textMediumGray = Color(0xFF64748B);
  static const Color textLightGray = Color(0xFFABB3C4);

  // Status colors
  static const Color successGreen = Color(0xFF22C55E);
  static const Color errorRed = Color(0xFFEF4444);
  static const Color warningYellow = Color(0xFFF59E0B);
  static const Color infoBlue = Color(0xFF3B82F6);

  // Background colors
  static const Color surfaceWhite = Color(0xFFFFFFFF);
  static const Color backgroundGray = Color(0xFFF8FAFC);
  static const Color backgroundBlueGray = Color(0xFFF0F4FF);

  // Gradient colors
  static const List<Color> primaryGradient = [primaryBlue, primaryPurple];

  // Status colors with opacity
  static Color successGreenLight = successGreen.withOpacity(0.1);
  static Color errorRedLight = errorRed.withOpacity(0.1);
  static Color warningYellowLight = warningYellow.withOpacity(0.1);
  static Color infoBlueLight = infoBlue.withOpacity(0.1);

  // New colors from the code block
  static const Color dividerColor = Color(0xFFE9ECF2);
  static const Color borderColor = Color(0xFFF1F1F5);
  static const Color buttonDisabledColor = Color(0xFFB0B7C3);
  static const Color buttonTextColor = Color(0xFFFFFFFF);
}
