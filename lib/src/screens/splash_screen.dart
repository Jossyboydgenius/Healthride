import 'package:flutter/material.dart';
import 'dart:async';
import 'package:provider/provider.dart';
import '../constants/app_color.dart';
import '../repository/auth_repository.dart';
import '../routes/app_routes.dart';
import '../widgets/animations/loading_animation.dart';

/// An enhanced splash screen with animations
class SplashScreen extends StatefulWidget {
  /// Creates a splash screen
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeInAnimation;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    );

    _fadeInAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.6, curve: Curves.easeOut),
      ),
    );

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.6, curve: Curves.easeOut),
      ),
    );

    _controller.forward();

    // Navigate to the appropriate screen after a delay
    _navigateToNextScreen();
  }

  Future<void> _navigateToNextScreen() async {
    await Future.delayed(const Duration(seconds: 2));

    if (!mounted) return;

    // Check if user is already signed in
    final authRepository = Provider.of<AuthRepository>(context, listen: false);
    final isSignedIn = await authRepository.isUserSignedIn();

    if (!mounted) return;

    // Navigate to the appropriate screen
    if (isSignedIn) {
      Navigator.of(context).pushReplacementNamed(AppRoutes.home);
    } else {
      Navigator.of(context).pushReplacementNamed(AppRoutes.welcome);
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Center(
          child: AnimatedBuilder(
            animation: _controller,
            builder: (context, child) {
              return Opacity(
                opacity: _fadeInAnimation.value,
                child: Transform.scale(
                  scale: _scaleAnimation.value,
                  child: child,
                ),
              );
            },
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Logo container with gradient background
                Container(
                  width: 120,
                  height: 120,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(26.0),
                    gradient: const LinearGradient(
                      colors: AppColor.primaryGradient,
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    boxShadow: [
                      BoxShadow(
                        color: AppColor.primaryBlue.withOpacity(0.3),
                        blurRadius: 20,
                        offset: const Offset(0, 10),
                      ),
                    ],
                  ),
                  child: Center(
                    child: Transform.rotate(
                      angle: 0.26, // ~15 degrees
                      child: const Icon(
                        Icons.local_hospital,
                        color: Colors.white,
                        size: 60,
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 40),

                // App name
                Text(
                  'Health Ride',
                  style: TextStyle(
                    fontSize: 32,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                    fontFamily: 'Poppins',
                  ),
                ),

                const SizedBox(height: 8),

                // Tagline
                Text(
                  'Medical transportation simplified',
                  style: TextStyle(
                    fontSize: 16,
                    color: AppColor.textMediumGray,
                    fontFamily: 'Poppins',
                  ),
                ),

                const SizedBox(height: 60),

                // Loading animation
                const LoadingAnimation(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
