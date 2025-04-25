import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import '../routes/app_routes.dart';
import '../widgets/components/gradient_button.dart';

/// The welcome screen shown to users when they first open the app
class WelcomeScreen extends StatelessWidget {
  /// Creates a welcome screen
  const WelcomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Stack(
          children: [
            // Background elements
            _buildBackgroundElements(),

            // Main content
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const Spacer(flex: 2),

                  // Logo and title
                  _buildLogoAndTitle(),

                  const Spacer(flex: 1),

                  // Features list
                  _buildFeaturesList(),

                  const Spacer(flex: 2),

                  // Get started button
                  GradientButton(
                    text: 'Get Started',
                    onPressed: () => Navigator.of(context)
                        .pushReplacementNamed(AppRoutes.login),
                    height: 56.0,
                  ),

                  const SizedBox(height: 40),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBackgroundElements() {
    return Stack(
      children: [
        // Top-left blob
        Positioned(
          top: -100,
          left: -100,
          child: Container(
            width: 300,
            height: 300,
            decoration: BoxDecoration(
              color: AppColor.primaryBlue.withOpacity(0.1),
              shape: BoxShape.circle,
            ),
          ),
        ),

        // Bottom-right blob
        Positioned(
          bottom: -80,
          right: -80,
          child: Container(
            width: 250,
            height: 250,
            decoration: BoxDecoration(
              color: AppColor.primaryPurple.withOpacity(0.1),
              shape: BoxShape.circle,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildLogoAndTitle() {
    return Column(
      children: [
        // Logo
        Container(
          width: 120,
          height: 120,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(30),
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

        const SizedBox(height: 30),

        // Title and subtitle
        const Text(
          'Health Ride',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: 34,
            fontWeight: FontWeight.bold,
            color: AppColor.textDarkBlue,
            fontFamily: 'Poppins',
          ),
        ),

        const SizedBox(height: 12),

        const Text(
          'Medical Transportation\nMade Simple',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: 18,
            color: AppColor.textMediumGray,
            height: 1.4,
            fontFamily: 'Poppins',
          ),
        ),
      ],
    );
  }

  Widget _buildFeaturesList() {
    return Column(
      children: [
        _buildFeatureItem(
          icon: Icons.access_time,
          text: 'Book medical rides with ease',
        ),
        const SizedBox(height: 16),
        _buildFeatureItem(
          icon: Icons.emergency,
          text: 'Special vehicles for all needs',
        ),
        const SizedBox(height: 16),
        _buildFeatureItem(
          icon: Icons.health_and_safety,
          text: 'Insurance-supported transport',
        ),
      ],
    );
  }

  Widget _buildFeatureItem({required IconData icon, required String text}) {
    return Row(
      children: [
        Container(
          width: 48,
          height: 48,
          decoration: BoxDecoration(
            color: AppColor.primaryBlue.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Icon(
            icon,
            color: AppColor.primaryBlue,
            size: 24,
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Text(
            text,
            style: const TextStyle(
              fontSize: 16,
              color: AppColor.textDarkBlue,
              fontWeight: FontWeight.w500,
              fontFamily: 'Poppins',
            ),
          ),
        ),
      ],
    );
  }
}
