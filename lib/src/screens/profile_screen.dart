import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import '../models/user.dart';
import '../routes/app_routes.dart';
import '../screens/personal_info_screen.dart';
import '../screens/insurance_payment_screen.dart';
import '../screens/settings_support_screen.dart';

/// Screen that displays user profile information
class ProfileScreen extends StatefulWidget {
  /// User data to display
  final User? user;

  /// Creates a profile screen
  const ProfileScreen({
    super.key,
    this.user,
  });

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen>
    with SingleTickerProviderStateMixin {
  bool _showBackground = false;
  bool _showContent = false;
  late AnimationController _headerScaleController;
  late Animation<double> _headerScaleAnimation;
  bool _showSignOutDialog = false;

  @override
  void initState() {
    super.initState();
    _headerScaleController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    );

    _headerScaleAnimation = Tween<double>(
      begin: 0.8,
      end: 1.0,
    ).animate(
      CurvedAnimation(
        parent: _headerScaleController,
        curve: Curves.elasticOut,
      ),
    );

    // Start animations after a delay
    Future.delayed(const Duration(milliseconds: 100), () {
      if (mounted) {
        setState(() {
          _showBackground = true;
        });
      }
    });

    Future.delayed(const Duration(milliseconds: 300), () {
      if (mounted) {
        _headerScaleController.forward();
      }
    });

    Future.delayed(const Duration(milliseconds: 500), () {
      if (mounted) {
        setState(() {
          _showContent = true;
        });
      }
    });
  }

  @override
  void dispose() {
    _headerScaleController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    Widget content = _buildBody();

    if (_showSignOutDialog) {
      content = Stack(
        children: [
          content,
          Container(
            color: Colors.black.withOpacity(0.4),
            child: Center(
              child: Container(
                margin: const EdgeInsets.symmetric(horizontal: 32),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(24),
                ),
                padding: const EdgeInsets.all(24),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text(
                      "Sign Out",
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.w600,
                        color: AppColor.textDarkBlue,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      "Are you sure you want to sign out of your account?",
                      style: TextStyle(
                        fontSize: 16,
                        color: AppColor.textMediumGray,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 24),
                    OutlinedButton(
                      onPressed: () {
                        setState(() {
                          _showSignOutDialog = false;
                        });
                      },
                      style: OutlinedButton.styleFrom(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(50),
                        ),
                        side: const BorderSide(
                          color: Color(0xFFE0E0E0),
                          width: 1,
                        ),
                        minimumSize: const Size(double.infinity, 48),
                      ),
                      child: const Text(
                        "Cancel",
                        style: TextStyle(
                          color: AppColor.textDarkBlue,
                        ),
                      ),
                    ),
                    const SizedBox(height: 8),
                    ElevatedButton(
                      onPressed: () {
                        setState(() {
                          _showSignOutDialog = false;
                        });
                        // Implement sign out functionality
                        Navigator.pushReplacementNamed(
                            context, AppRoutes.login);
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColor.errorRed,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(50),
                        ),
                        minimumSize: const Size(double.infinity, 48),
                      ),
                      child: const Text("Sign Out"),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Profile',
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: AppColor.textDarkBlue,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: const IconThemeData(color: AppColor.textDarkBlue),
      ),
      extendBodyBehindAppBar: true,
      body: content,
    );
  }

  Widget _buildBody() {
    return Container(
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            Color(0xFFF0F4FF), // Light blue
            Color(0xFFF5F0FF), // Light purple
            Color(0xFFF8FBFF), // White-ish
          ],
        ),
      ),
      child: Stack(
        children: [
          // Decorative blobs in background
          if (_showBackground) ...[
            _buildDecorationBlob(
              color: AppColor.primaryBlue,
              size: 300,
              xOffset: -150,
              yOffset: -50,
              opacity: 0.08,
            ),
            _buildDecorationBlob(
              color: AppColor.primaryPurple,
              size: 280,
              xOffset: 150,
              yOffset: 400,
              opacity: 0.08,
            ),
          ],

          // Main content
          SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Column(
                children: [
                  // Profile header with animation
                  AnimatedBuilder(
                    animation: _headerScaleAnimation,
                    builder: (context, child) {
                      return Transform.scale(
                        scale: _headerScaleAnimation.value,
                        child: _buildProfileHeader(),
                      );
                    },
                  ),

                  const SizedBox(height: 24),

                  // Main content with animation
                  AnimatedOpacity(
                    opacity: _showContent ? 1.0 : 0.0,
                    duration: const Duration(milliseconds: 500),
                    curve: Curves.easeOut,
                    child: AnimatedSlide(
                      offset:
                          _showContent ? Offset.zero : const Offset(0, 0.05),
                      duration: const Duration(milliseconds: 500),
                      curve: Curves.easeOutCubic,
                      child: _buildSettingsContent(),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildProfileHeader() {
    final User? user = widget.user;
    String displayName = "Guest User";
    if (user != null) {
      if (user.firstName != null && user.lastName != null) {
        displayName = "${user.firstName} ${user.lastName}";
      }
    }

    final String initial =
        displayName.isNotEmpty ? displayName[0].toUpperCase() : "U";

    return Column(
      children: [
        const SizedBox(height: 16),

        // Profile avatar with pulsating animation
        TweenAnimationBuilder<double>(
          tween: Tween(begin: 1.0, end: 1.05),
          duration: const Duration(milliseconds: 1500),
          curve: Curves.easeInOut,
          builder: (context, value, child) {
            return Transform.scale(
              scale: value,
              child: Container(
                width: 100,
                height: 100,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  boxShadow: [
                    BoxShadow(
                      color: AppColor.primaryBlue.withOpacity(0.2),
                      blurRadius: 8,
                      offset: const Offset(0, 2),
                    ),
                  ],
                  color: Colors.white,
                  border: Border.all(
                    width: 2,
                    color: AppColor.primaryBlue.withOpacity(0.5),
                  ),
                ),
                padding: const EdgeInsets.all(4),
                child: Container(
                  decoration: const BoxDecoration(
                    shape: BoxShape.circle,
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [
                        AppColor.primaryBlue,
                        AppColor.primaryPurple,
                      ],
                    ),
                  ),
                  child: Center(
                    child: Text(
                      initial,
                      style: const TextStyle(
                        fontSize: 36,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
              ),
            );
          },
        ),

        const SizedBox(height: 16),

        // User name
        Text(
          displayName,
          style: const TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
            color: AppColor.textDarkBlue,
            fontFamily: "SF Pro Display",
          ),
        ),

        const SizedBox(height: 8),

        // Email
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.email_outlined,
              size: 16,
              color: AppColor.textMediumGray,
            ),
            const SizedBox(width: 4),
            Text(
              widget.user?.email ?? '',
              style: const TextStyle(
                fontSize: 14,
                color: AppColor.textMediumGray,
                fontFamily: "SF Pro Display",
              ),
            ),
          ],
        ),

        const SizedBox(height: 16),

        // Verification status badge
        if (widget.user != null) _buildVerificationBadge(),
      ],
    );
  }

  Widget _buildVerificationBadge() {
    String? status = widget.user?.verificationStatus;

    if (status == null) {
      return const SizedBox();
    }

    late Color backgroundColor;
    late Color textColor;
    late IconData icon;
    late String text;

    switch (status.toUpperCase()) {
      case "VERIFIED":
        backgroundColor = AppColor.successGreen.withOpacity(0.1);
        textColor = AppColor.successGreen;
        icon = Icons.verified_user_rounded;
        text = "Verified Account";
        break;
      case "PENDING":
        backgroundColor = AppColor.warningYellow.withOpacity(0.1);
        textColor = AppColor.warningYellow;
        icon = Icons.pending_rounded;
        text = "Verification Pending";
        break;
      default:
        backgroundColor = AppColor.primaryBlue.withOpacity(0.1);
        textColor = AppColor.primaryBlue;
        icon = Icons.account_circle_outlined;
        text = "Complete Verification";
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(50),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            icon,
            size: 16,
            color: textColor,
          ),
          const SizedBox(width: 4),
          Text(
            text,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w500,
              color: textColor,
              fontFamily: "SF Pro Display",
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSettingsContent() {
    return Column(
      children: [
        // Personal Information
        _buildSettingsCard(
          title: 'Personal Information',
          icon: Icons.person_outline,
          iconColor: AppColor.primaryBlue,
          onTap: () {
            Navigator.push(
              context,
              PageRouteBuilder(
                pageBuilder: (context, animation, secondaryAnimation) =>
                    const PersonalInfoScreen(),
                transitionsBuilder:
                    (context, animation, secondaryAnimation, child) {
                  const begin = Offset(0.0, 1.0);
                  const end = Offset.zero;
                  const curve = Curves.easeOutQuint;

                  var tween = Tween(begin: begin, end: end)
                      .chain(CurveTween(curve: curve));

                  return SlideTransition(
                    position: animation.drive(tween),
                    child: child,
                  );
                },
                transitionDuration: const Duration(milliseconds: 500),
              ),
            );
          },
          content: [
            _buildSettingsItem(
              title: 'Email',
              value: widget.user?.email ?? 'mock@example.com',
              icon: Icons.email_outlined,
              iconColor: AppColor.primaryBlue,
            ),
            _buildSettingsItem(
              title: 'Phone',
              value: widget.user?.phone ?? '+1 (555) 000-0000',
              icon: Icons.phone_outlined,
              iconColor: AppColor.primaryBlue,
            ),
            _buildSettingsItem(
              title: 'Address',
              value: '123 Mock St, Mockville, MK 00000',
              icon: Icons.home_outlined,
              iconColor: AppColor.primaryBlue,
              isLast: true,
            ),
          ],
        ),
        const SizedBox(height: 16),

        // Insurance & Payment
        _buildSettingsCard(
          title: 'Insurance & Payment',
          icon: Icons.shield_outlined,
          iconColor: AppColor.primaryBlue,
          onTap: () {
            Navigator.push(
              context,
              PageRouteBuilder(
                pageBuilder: (context, animation, secondaryAnimation) =>
                    const InsurancePaymentScreen(),
                transitionsBuilder:
                    (context, animation, secondaryAnimation, child) {
                  const begin = Offset(0.0, 1.0);
                  const end = Offset.zero;
                  const curve = Curves.easeOutQuint;

                  var tween = Tween(begin: begin, end: end)
                      .chain(CurveTween(curve: curve));

                  return SlideTransition(
                    position: animation.drive(tween),
                    child: child,
                  );
                },
                transitionDuration: const Duration(milliseconds: 500),
              ),
            );
          },
          content: [
            _buildSettingsItem(
              title: 'Insurance Provider',
              value: widget.user?.insuranceProvider ?? 'Mock Insurance Co',
              icon: Icons.health_and_safety_outlined,
              iconColor: AppColor.primaryBlue,
              isLast: true,
            ),
          ],
        ),
        const SizedBox(height: 16),

        // Settings & Support
        _buildSettingsCard(
          title: 'Settings & Support',
          icon: Icons.settings_outlined,
          iconColor: AppColor.primaryBlue,
          onTap: () {
            Navigator.push(
              context,
              PageRouteBuilder(
                pageBuilder: (context, animation, secondaryAnimation) =>
                    const SettingsSupportScreen(),
                transitionsBuilder:
                    (context, animation, secondaryAnimation, child) {
                  const begin = Offset(0.0, 1.0);
                  const end = Offset.zero;
                  const curve = Curves.easeOutQuint;

                  var tween = Tween(begin: begin, end: end)
                      .chain(CurveTween(curve: curve));

                  return SlideTransition(
                    position: animation.drive(tween),
                    child: child,
                  );
                },
                transitionDuration: const Duration(milliseconds: 500),
              ),
            );
          },
          content: [
            _buildSettingsItem(
              title: 'Notifications',
              value: 'Manage your notifications',
              icon: Icons.notifications_outlined,
              iconColor: AppColor.primaryBlue,
            ),
            _buildSettingsItem(
              title: 'Privacy & Security',
              value: 'Manage privacy settings',
              icon: Icons.security_outlined,
              iconColor: AppColor.primaryBlue,
            ),
            _buildSettingsItem(
              title: 'Help & Support',
              value: 'Get assistance',
              icon: Icons.help_outline,
              iconColor: AppColor.primaryBlue,
              isLast: true,
            ),
          ],
        ),
        const SizedBox(height: 16),

        // App Information
        Card(
          elevation: 2,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(20.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Container(
                          width: 36,
                          height: 36,
                          decoration: const BoxDecoration(
                            shape: BoxShape.circle,
                            color: Color(0xFFE6F0FF),
                          ),
                          child: const Icon(
                            Icons.info_outline,
                            size: 20,
                            color: AppColor.primaryBlue,
                          ),
                        ),
                        const SizedBox(width: 12),
                        const Text(
                          'HealthRide',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: AppColor.textDarkBlue,
                          ),
                        ),
                      ],
                    ),
                    const Icon(
                      Icons.chevron_right,
                      color: AppColor.textLightGray,
                    ),
                  ],
                ),
              ),
              const Divider(thickness: 0.5, color: Color(0xFFE5E7EB)),
              Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                child: Row(
                  children: [
                    Container(
                      width: 36,
                      height: 36,
                      decoration: const BoxDecoration(
                        shape: BoxShape.circle,
                        color: Color(0xFFE6F0FF),
                      ),
                      child: const Icon(
                        Icons.update,
                        size: 20,
                        color: AppColor.primaryBlue,
                      ),
                    ),
                    const SizedBox(width: 12),
                    const Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Version',
                          style: TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.w500,
                            color: AppColor.textDarkBlue,
                          ),
                        ),
                        Text(
                          '1.0.0',
                          style: TextStyle(
                            fontSize: 12,
                            color: AppColor.textMediumGray,
                          ),
                        ),
                      ],
                    ),
                    const Spacer(),
                    const Text(
                      '© 2025 HealthRide Inc.',
                      style: TextStyle(
                        fontSize: 12,
                        color: AppColor.textMediumGray,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 40),

        // Sign Out Button
        Padding(
          padding: const EdgeInsets.only(bottom: 40),
          child: TextButton(
            onPressed: () {
              // Show sign out dialog
              setState(() {
                _showSignOutDialog = true;
              });
            },
            style: TextButton.styleFrom(
              foregroundColor: AppColor.errorRed,
              minimumSize: const Size(double.infinity, 50),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(50),
              ),
              side: BorderSide(color: AppColor.errorRed.withOpacity(0.3)),
            ),
            child: const Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.logout,
                  size: 20,
                  color: AppColor.errorRed,
                ),
                SizedBox(width: 8),
                Text(
                  'Sign Out',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w500,
                    color: AppColor.errorRed,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildSettingsCard({
    required String title,
    required IconData icon,
    required VoidCallback onTap,
    required Color iconColor,
    required List<Widget> content,
  }) {
    return TweenAnimationBuilder<double>(
      tween: Tween(begin: 1.0, end: 1.0),
      duration: const Duration(milliseconds: 150),
      builder: (context, scale, child) {
        return Transform.scale(
          scale: scale,
          child: child,
        );
      },
      child: Card(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        elevation: 2,
        child: Column(
          children: [
            InkWell(
              onTap: onTap,
              borderRadius: BorderRadius.circular(16),
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        // Icon with background
                        Container(
                          width: 36,
                          height: 36,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: iconColor.withOpacity(0.1),
                          ),
                          child: Icon(
                            icon,
                            size: 20,
                            color: iconColor,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Text(
                          title,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: AppColor.textDarkBlue,
                          ),
                        ),
                      ],
                    ),
                    const Icon(
                      Icons.chevron_right,
                      color: AppColor.textLightGray,
                    ),
                  ],
                ),
              ),
            ),
            if (content.isNotEmpty) ...[
              const Divider(thickness: 0.5, color: Color(0xFFE5E7EB)),
              Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                child: Column(
                  children: content,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildSettingsItem({
    required String title,
    required String value,
    required IconData icon,
    required Color iconColor,
    bool isLast = false,
  }) {
    return Column(
      children: [
        Row(
          children: [
            Container(
              width: 36,
              height: 36,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: iconColor.withOpacity(0.1),
              ),
              child: Icon(
                icon,
                size: 20,
                color: iconColor,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                  Text(
                    value,
                    style: const TextStyle(
                      fontSize: 12,
                      color: AppColor.textMediumGray,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
          ],
        ),
        if (!isLast)
          const Padding(
            padding: EdgeInsets.only(left: 48, right: 12),
            child: Divider(thickness: 0.5, color: Color(0xFFE5E7EB)),
          ),
        if (!isLast) const SizedBox(height: 8),
      ],
    );
  }

  Widget _buildDecorationBlob({
    required Color color,
    required double size,
    required double xOffset,
    required double yOffset,
    required double opacity,
  }) {
    return Positioned(
      top: yOffset,
      left: xOffset,
      child: TweenAnimationBuilder<double>(
        tween: Tween(begin: 0.0, end: 1.0),
        duration: const Duration(seconds: 1),
        builder: (context, value, child) {
          return Opacity(
            opacity: value * opacity,
            child: TweenAnimationBuilder<double>(
              tween: Tween(begin: 1.0, end: 1.2),
              duration: const Duration(seconds: 5),
              curve: Curves.easeInOut,
              builder: (context, scale, _) {
                return TweenAnimationBuilder<Offset>(
                  tween: Tween(
                    begin: Offset.zero,
                    end: const Offset(20, 20),
                  ),
                  duration: const Duration(seconds: 7),
                  curve: Curves.easeInOut,
                  builder: (context, offset, _) {
                    return Transform.translate(
                      offset: offset,
                      child: Transform.scale(
                        scale: scale,
                        child: Container(
                          width: size,
                          height: size,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: color.withOpacity(opacity),
                          ),
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          );
        },
      ),
    );
  }
}
