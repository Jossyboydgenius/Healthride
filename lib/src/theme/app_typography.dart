import 'package:flutter/material.dart';
import '../constants/app_color.dart';

/// App typography class that defines text styles for the Health Ride app
class AppTypography {
  /// Get the text theme data for the app
  static TextTheme get textTheme {
    return const TextTheme(
      // Display styles
      displayLarge: TextStyle(
        fontSize: 57.0,
        fontWeight: FontWeight.normal,
        letterSpacing: -0.25,
        color: AppColor.textDarkBlue,
        height: 1.12,
        fontFamily: 'Poppins',
      ),
      displayMedium: TextStyle(
        fontSize: 45.0,
        fontWeight: FontWeight.normal,
        letterSpacing: 0,
        color: AppColor.textDarkBlue,
        height: 1.16,
        fontFamily: 'Poppins',
      ),
      displaySmall: TextStyle(
        fontSize: 36.0,
        fontWeight: FontWeight.normal,
        letterSpacing: 0,
        color: AppColor.textDarkBlue,
        height: 1.22,
        fontFamily: 'Poppins',
      ),

      // Headline styles
      headlineLarge: TextStyle(
        fontSize: 32.0,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
        color: AppColor.textDarkBlue,
        height: 1.25,
        fontFamily: 'Poppins',
      ),
      headlineMedium: TextStyle(
        fontSize: 28.0,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
        color: AppColor.textDarkBlue,
        height: 1.29,
        fontFamily: 'Poppins',
      ),
      headlineSmall: TextStyle(
        fontSize: 24.0,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
        color: AppColor.textDarkBlue,
        height: 1.33,
        fontFamily: 'Poppins',
      ),

      // Title styles
      titleLarge: TextStyle(
        fontSize: 22.0,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
        color: AppColor.textDarkBlue,
        height: 1.27,
        fontFamily: 'Poppins',
      ),
      titleMedium: TextStyle(
        fontSize: 16.0,
        fontWeight: FontWeight.w600,
        letterSpacing: 0.15,
        color: AppColor.textDarkBlue,
        height: 1.5,
        fontFamily: 'Poppins',
      ),
      titleSmall: TextStyle(
        fontSize: 14.0,
        fontWeight: FontWeight.bold,
        letterSpacing: 0.1,
        color: AppColor.textDarkBlue,
        height: 1.43,
        fontFamily: 'Poppins',
      ),

      // Body styles
      bodyLarge: TextStyle(
        fontSize: 16.0,
        fontWeight: FontWeight.normal,
        letterSpacing: 0.5,
        color: AppColor.textDarkBlue,
        height: 1.5,
        fontFamily: 'Poppins',
      ),
      bodyMedium: TextStyle(
        fontSize: 14.0,
        fontWeight: FontWeight.normal,
        letterSpacing: 0.25,
        color: AppColor.textMediumGray,
        height: 1.43,
        fontFamily: 'Poppins',
      ),
      bodySmall: TextStyle(
        fontSize: 12.0,
        fontWeight: FontWeight.normal,
        letterSpacing: 0.4,
        color: AppColor.textMediumGray,
        height: 1.33,
        fontFamily: 'Poppins',
      ),

      // Label styles
      labelLarge: TextStyle(
        fontSize: 14.0,
        fontWeight: FontWeight.w500,
        letterSpacing: 0.1,
        color: Colors.white,
        height: 1.43,
        fontFamily: 'Poppins',
      ),
      labelMedium: TextStyle(
        fontSize: 12.0,
        fontWeight: FontWeight.w500,
        letterSpacing: 0.5,
        color: AppColor.textDarkBlue,
        height: 1.33,
        fontFamily: 'Poppins',
      ),
      labelSmall: TextStyle(
        fontSize: 11.0,
        fontWeight: FontWeight.w500,
        letterSpacing: 0.5,
        color: AppColor.textMediumGray,
        height: 1.45,
        fontFamily: 'Poppins',
      ),
    );
  }
}
