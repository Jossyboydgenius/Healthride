import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import 'app_typography.dart';

/// App theme class that defines UI styles for the HealthRide app
class AppTheme {
  /// Get the main app theme data
  static ThemeData get themeData {
    return ThemeData(
      useMaterial3: true,
      fontFamily: 'Poppins',
      colorScheme: _colorScheme,
      textTheme: AppTypography.textTheme,
      appBarTheme: _appBarTheme,
      elevatedButtonTheme: _elevatedButtonTheme,
      outlinedButtonTheme: _outlinedButtonTheme,
      inputDecorationTheme: _inputDecorationTheme,
      cardTheme: _cardTheme,
      scaffoldBackgroundColor: AppColor.backgroundGray,
      visualDensity: VisualDensity.adaptivePlatformDensity,
    );
  }

  /// Color scheme for the app
  static ColorScheme get _colorScheme {
    return const ColorScheme(
      brightness: Brightness.light,
      primary: AppColor.primaryBlue,
      onPrimary: Colors.white,
      secondary: AppColor.primaryPurple,
      onSecondary: Colors.white,
      error: AppColor.errorRed,
      onError: Colors.white,
      background: AppColor.backgroundGray,
      onBackground: AppColor.textDarkBlue,
      surface: AppColor.surfaceWhite,
      onSurface: AppColor.textDarkBlue,
    );
  }

  /// App bar theme
  static AppBarTheme get _appBarTheme {
    return const AppBarTheme(
      backgroundColor: Colors.transparent,
      elevation: 0,
      centerTitle: true,
      iconTheme: IconThemeData(color: AppColor.textDarkBlue),
      titleTextStyle: TextStyle(
        fontFamily: 'Poppins',
        fontSize: 18.0,
        fontWeight: FontWeight.w600,
        color: AppColor.textDarkBlue,
      ),
    );
  }

  /// Elevated button theme - primary action buttons
  static ElevatedButtonThemeData get _elevatedButtonTheme {
    return ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        elevation: 2,
        backgroundColor: AppColor.primaryBlue,
        foregroundColor: Colors.white,
        minimumSize: const Size(0, 56),
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 14.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(28.0),
        ),
        textStyle: const TextStyle(
          fontFamily: 'Poppins',
          fontSize: 16.0,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  /// Outlined button theme - secondary action buttons
  static OutlinedButtonThemeData get _outlinedButtonTheme {
    return OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(
        foregroundColor: AppColor.primaryBlue,
        minimumSize: const Size(0, 56),
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 14.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(28.0),
        ),
        side: const BorderSide(color: AppColor.primaryBlue, width: 1.5),
        textStyle: const TextStyle(
          fontFamily: 'Poppins',
          fontSize: 16.0,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  /// Input decoration theme for text fields
  static InputDecorationTheme get _inputDecorationTheme {
    return InputDecorationTheme(
      filled: true,
      fillColor: AppColor.surfaceWhite,
      contentPadding:
          const EdgeInsets.symmetric(horizontal: 20.0, vertical: 18.0),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(16.0),
        borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(16.0),
        borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(16.0),
        borderSide: const BorderSide(color: AppColor.primaryBlue, width: 1.5),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(16.0),
        borderSide: const BorderSide(color: AppColor.errorRed),
      ),
      labelStyle: const TextStyle(
        fontFamily: 'Poppins',
        color: AppColor.textMediumGray,
      ),
      hintStyle: const TextStyle(
        fontFamily: 'Poppins',
        color: AppColor.textLightGray,
      ),
    );
  }

  /// Card theme
  static CardTheme get _cardTheme {
    return CardTheme(
      elevation: 2,
      color: AppColor.surfaceWhite,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16.0),
      ),
    );
  }

  /// Custom gradient button decoration
  static BoxDecoration gradientButtonDecoration({double borderRadius = 28.0}) {
    return BoxDecoration(
      gradient: const LinearGradient(
        colors: AppColor.primaryGradient,
        begin: Alignment.centerLeft,
        end: Alignment.centerRight,
      ),
      borderRadius: BorderRadius.circular(borderRadius),
      boxShadow: [
        BoxShadow(
          color: AppColor.primaryBlue.withOpacity(0.3),
          offset: const Offset(0, 4),
          blurRadius: 12,
        ),
      ],
    );
  }

  /// Shadow for elevated components
  static List<BoxShadow> get elevation {
    return [
      BoxShadow(
        color: Colors.black.withOpacity(0.05),
        offset: const Offset(0, 2),
        blurRadius: 8,
      ),
      BoxShadow(
        color: AppColor.primaryBlue.withOpacity(0.07),
        offset: const Offset(0, 4),
        blurRadius: 16,
      ),
    ];
  }
}
