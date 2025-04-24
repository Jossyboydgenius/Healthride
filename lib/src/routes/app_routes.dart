import 'package:flutter/material.dart';
import '../screens/splash_screen.dart';
import '../screens/welcome_screen.dart';
import '../screens/login_screen.dart';
import '../screens/registration_screen.dart';
import '../screens/insurance_upload_screen.dart';
import '../screens/verification_status_screen.dart';
import '../screens/modern_home_screen.dart';
import '../screens/ride_screen.dart';
import '../screens/notifications_screen.dart';

/// App routes for navigation
class AppRoutes {
  /// Splash screen route
  static const String splash = '/';

  /// Welcome screen route
  static const String welcome = '/welcome';

  /// Login screen route
  static const String login = '/login';

  /// Registration screen route
  static const String registration = '/registration';

  /// Insurance upload screen route
  static const String insuranceUpload = '/insurance-upload';

  /// Main/home screen route
  static const String home = '/home';

  /// Profile screen route
  static const String profile = '/profile';

  /// Book ride screen route
  static const String bookRide = '/book-ride';

  /// Ride history screen route
  static const String rideHistory = '/ride-history';

  /// Ride details screen route
  static const String rideDetails = '/ride-details';

  /// Settings screen route
  static const String settings = '/settings';

  /// Verification status screen route
  static const String verificationStatus = '/verification-status';

  /// Personal info screen route
  static const String personalInfo = '/personal-info';

  /// Insurance payment screen route
  static const String insurancePayment = '/insurance-payment';

  /// Settings and support screen route
  static const String settingsSupport = '/settings-support';

  /// Ride screen route
  static const String ride = '/ride';

  /// Notifications screen route
  static const String notifications = '/notifications';

  /// Generate the appropriate route based on settings
  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    switch (settings.name) {
      case splash:
        return MaterialPageRoute(builder: (_) => const SplashScreen());
      case welcome:
        return MaterialPageRoute(builder: (_) => const WelcomeScreen());
      case login:
        return MaterialPageRoute(builder: (_) => const LoginScreen());
      case registration:
        return MaterialPageRoute(builder: (_) => const RegistrationScreen());
      case insuranceUpload:
        return MaterialPageRoute(builder: (_) => const InsuranceUploadScreen());
      case verificationStatus:
        return MaterialPageRoute(
            builder: (_) => const VerificationStatusScreen());
      case home:
        return MaterialPageRoute(builder: (_) => const ModernHomeScreen());
      case ride:
        return MaterialPageRoute(builder: (_) => const RideScreen());
      case notifications:
        return MaterialPageRoute(builder: (_) => const NotificationsScreen());
      default:
        // For routes that don't have screen implementations yet
        return MaterialPageRoute(
          builder: (_) => Scaffold(
            appBar: AppBar(
              title: const Text('Under Construction'),
              leading: IconButton(
                icon: const Icon(Icons.arrow_back),
                onPressed: () => Navigator.of(_).pop(),
              ),
            ),
            body: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(
                    Icons.construction,
                    size: 64,
                    color: Colors.amber,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Screen for route "${settings.name}" is under construction',
                    textAlign: TextAlign.center,
                    style: const TextStyle(fontSize: 18),
                  ),
                ],
              ),
            ),
          ),
        );
    }
  }
}
