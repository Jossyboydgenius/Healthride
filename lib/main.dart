import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'src/repository/auth_repository.dart';
import 'src/repository/mock_auth_repository.dart';
import 'src/repository/ride_repository.dart';
import 'src/repository/mock_ride_repository.dart';
import 'src/routes/app_routes.dart';
import 'src/theme/app_theme.dart';

/// The main entry point of the application
void main() {
  WidgetsFlutterBinding.ensureInitialized();

  // Set preferred orientations
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  // Set system UI overlay style for status bar
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.dark,
    ),
  );

  runApp(const HealthRideApp());
}

/// The root widget of the application
class HealthRideApp extends StatelessWidget {
  /// Creates the HealthRide app
  const HealthRideApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // Provide the AuthRepository implementation
        Provider<AuthRepository>(
          create: (_) => MockAuthRepository(),
        ),
        // Provide the RideRepository implementation
        Provider<RideRepository>(
          create: (_) => MockRideRepository(),
        ),
      ],
      child: MaterialApp(
        title: 'Health Ride',
        theme: ThemeData(
          fontFamily: 'SF Pro Display',
          useMaterial3: true,
          colorScheme: AppTheme.themeData.colorScheme,
          textTheme: AppTheme.themeData.textTheme,
          appBarTheme: AppTheme.themeData.appBarTheme,
          elevatedButtonTheme: AppTheme.themeData.elevatedButtonTheme,
          outlinedButtonTheme: AppTheme.themeData.outlinedButtonTheme,
          inputDecorationTheme: AppTheme.themeData.inputDecorationTheme,
          cardTheme: AppTheme.themeData.cardTheme,
          scaffoldBackgroundColor: AppTheme.themeData.scaffoldBackgroundColor,
          visualDensity: AppTheme.themeData.visualDensity,
        ),
        debugShowCheckedModeBanner: false,
        initialRoute: AppRoutes.splash,
        onGenerateRoute: AppRoutes.onGenerateRoute,
      ),
    );
  }
}
