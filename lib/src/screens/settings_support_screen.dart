import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../constants/app_color.dart';

class SettingsSupportScreen extends StatefulWidget {
  const SettingsSupportScreen({Key? key}) : super(key: key);

  @override
  State<SettingsSupportScreen> createState() => _SettingsSupportScreenState();
}

class _SettingsSupportScreenState extends State<SettingsSupportScreen> {
  // Animation states
  bool _showContent = false;
  bool _showSuccessDialog = false;
  String _dialogMessage = "";

  // Settings states
  bool _isNotificationsEnabled = true;
  bool _isLocationEnabled = true;
  bool _isReminderEnabled = true;
  bool _isDarkMode = false;
  bool _isAccessibilityEnabled = false;
  bool _isBiometricEnabled = false;
  String _measurementSystem = "Imperial";
  String _languagePreference = "English";

  // Measurement system options
  final List<String> _measurementOptions = ["Imperial", "Metric"];

  // Language options
  final List<String> _languageOptions = [
    "English",
    "Spanish",
    "French",
    "German",
    "Chinese"
  ];

  @override
  void initState() {
    super.initState();

    // Trigger animation after a delay
    Future.delayed(const Duration(milliseconds: 100), () {
      if (mounted) {
        setState(() {
          _showContent = true;
        });
      }
    });
  }

  void _showSuccess(String message) {
    setState(() {
      _showSuccessDialog = true;
      _dialogMessage = message;
    });

    // Hide dialog after a delay
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _showSuccessDialog = false;
          _dialogMessage = "";
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Settings & Support',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w600,
            color: AppColor.textDarkBlue,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: const IconThemeData(color: AppColor.textDarkBlue),
      ),
      body: Stack(
        children: [
          Container(
            color: AppColor.backgroundGray,
            child: AnimatedOpacity(
              opacity: _showContent ? 1.0 : 0.0,
              duration: const Duration(milliseconds: 500),
              curve: Curves.easeOut,
              child: AnimatedSlide(
                offset: _showContent ? Offset.zero : const Offset(0, 0.05),
                duration: const Duration(milliseconds: 500),
                curve: Curves.easeOutCubic,
                child: SingleChildScrollView(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Notification Settings Card
                      _buildSettingsCard(
                        title: "Notifications",
                        icon: Icons.notifications_outlined,
                        iconTint: AppColor.primaryBlue,
                        children: [
                          _buildSwitchItem(
                            title: "Enable Notifications",
                            description:
                                "Receive important updates about your rides",
                            value: _isNotificationsEnabled,
                            onChanged: (value) {
                              setState(() {
                                _isNotificationsEnabled = value;
                              });
                            },
                          ),
                          _buildSwitchItem(
                            title: "Ride Reminders",
                            description:
                                "Get reminded 1 hour before scheduled rides",
                            value: _isReminderEnabled,
                            onChanged: (value) {
                              setState(() {
                                _isReminderEnabled = value;
                              });
                            },
                            enabled: _isNotificationsEnabled,
                          ),
                          _buildSwitchItem(
                            title: "Driver Updates",
                            description:
                                "Get notified when your driver is assigned or nearby",
                            value: _isNotificationsEnabled,
                            onChanged: (value) {
                              // This is controlled by the main notification toggle
                            },
                            enabled: _isNotificationsEnabled,
                            isLast: true,
                          ),
                        ],
                      ),

                      const SizedBox(height: 16),

                      // Privacy & Security Card
                      _buildSettingsCard(
                        title: "Privacy & Security",
                        icon: Icons.security_outlined,
                        iconTint: AppColor.primaryPurple,
                        children: [
                          _buildSwitchItem(
                            title: "Location Services",
                            description:
                                "Allow the app to access your location",
                            value: _isLocationEnabled,
                            onChanged: (value) {
                              setState(() {
                                _isLocationEnabled = value;
                              });
                            },
                          ),
                          _buildSwitchItem(
                            title: "Biometric Authentication",
                            description:
                                "Use fingerprint or face ID to sign in",
                            value: _isBiometricEnabled,
                            onChanged: (value) {
                              setState(() {
                                _isBiometricEnabled = value;
                              });
                            },
                          ),
                          _buildClickableItem(
                            title: "Privacy Policy",
                            description: "Read about how we handle your data",
                            onTap: () async {
                              final Uri url =
                                  Uri.parse('https://example.com/privacy');
                              if (await canLaunchUrl(url)) {
                                await launchUrl(url);
                              }
                            },
                            isLast: true,
                          ),
                        ],
                      ),

                      const SizedBox(height: 16),

                      // Appearance Card
                      _buildSettingsCard(
                        title: "Appearance",
                        icon: Icons.palette_outlined,
                        iconTint: const Color(0xFF4EA8DE), // Light blue
                        children: [
                          _buildSwitchItem(
                            title: "Dark Mode",
                            description: "Switch between light and dark themes",
                            value: _isDarkMode,
                            onChanged: (value) {
                              setState(() {
                                _isDarkMode = value;
                              });
                            },
                          ),
                          _buildSwitchItem(
                            title: "Accessibility Features",
                            description:
                                "Larger text, increased contrast, etc.",
                            value: _isAccessibilityEnabled,
                            onChanged: (value) {
                              setState(() {
                                _isAccessibilityEnabled = value;
                              });
                            },
                            isLast: true,
                          ),
                        ],
                      ),

                      const SizedBox(height: 16),

                      // Preferences Card
                      _buildSettingsCard(
                        title: "Preferences",
                        icon: Icons.tune_outlined,
                        iconTint: const Color(0xFFE76F51), // Orange
                        children: [
                          _buildDropdownItem(
                            title: "Language",
                            description: "Choose your preferred language",
                            value: _languagePreference,
                            options: _languageOptions,
                            onChanged: (value) {
                              if (value != null) {
                                setState(() {
                                  _languagePreference = value;
                                });
                              }
                            },
                          ),
                          _buildDropdownItem(
                            title: "Measurement System",
                            description:
                                "Choose between Imperial and Metric units",
                            value: _measurementSystem,
                            options: _measurementOptions,
                            onChanged: (value) {
                              if (value != null) {
                                setState(() {
                                  _measurementSystem = value;
                                });
                              }
                            },
                            isLast: true,
                          ),
                        ],
                      ),

                      const SizedBox(height: 16),

                      // Support Card
                      _buildSettingsCard(
                        title: "Help & Support",
                        icon: Icons.help_outline,
                        iconTint: const Color(0xFF38B000), // Green
                        children: [
                          _buildClickableItem(
                            title: "Contact Support",
                            description: "Get help with your account or rides",
                            onTap: () async {
                              final Uri emailUri = Uri(
                                scheme: 'mailto',
                                path: 'support@healthride.com',
                                queryParameters: {'subject': 'Support Request'},
                              );
                              if (await canLaunchUrl(emailUri)) {
                                await launchUrl(emailUri);
                              }
                            },
                          ),
                          _buildClickableItem(
                            title: "FAQs",
                            description: "Find answers to common questions",
                            onTap: () async {
                              final Uri url =
                                  Uri.parse('https://example.com/faq');
                              if (await canLaunchUrl(url)) {
                                await launchUrl(url);
                              }
                            },
                          ),
                          _buildClickableItem(
                            title: "Report a Problem",
                            description:
                                "Let us know if something isn't working",
                            onTap: () {
                              _showSuccess(
                                  "Problem reported successfully. We'll get back to you soon.");
                            },
                            isLast: true,
                          ),
                        ],
                      ),

                      const SizedBox(height: 16),

                      // About App Card
                      _buildSettingsCard(
                        title: "About",
                        icon: Icons.info_outline,
                        iconTint: AppColor.textDarkBlue,
                        children: [
                          _buildClickableItem(
                            title: "Version",
                            description: "1.0.0 (build 100)",
                            onTap: () {
                              // Perhaps show a dialog with version details
                            },
                          ),
                          _buildClickableItem(
                            title: "Terms of Service",
                            description: "Read our terms and conditions",
                            onTap: () async {
                              final Uri url =
                                  Uri.parse('https://example.com/terms');
                              if (await canLaunchUrl(url)) {
                                await launchUrl(url);
                              }
                            },
                          ),
                          _buildClickableItem(
                            title: "Open Source Licenses",
                            description: "View third-party licenses",
                            onTap: () {
                              // Show licenses page
                            },
                            isLast: true,
                          ),
                        ],
                      ),

                      const SizedBox(height: 24),
                    ],
                  ),
                ),
              ),
            ),
          ),

          // Success Dialog
          if (_showSuccessDialog)
            Center(
              child: Container(
                margin: const EdgeInsets.all(32),
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.1),
                      blurRadius: 16,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 60,
                      height: 60,
                      decoration: BoxDecoration(
                        color: AppColor.successGreen.withOpacity(0.1),
                        shape: BoxShape.circle,
                      ),
                      child: const Icon(
                        Icons.check_circle,
                        size: 32,
                        color: AppColor.successGreen,
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      _dialogMessage,
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w500,
                        color: AppColor.textDarkBlue,
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

  Widget _buildSettingsCard({
    required String title,
    required IconData icon,
    required Color iconTint,
    required List<Widget> children,
  }) {
    return Card(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  width: 36,
                  height: 36,
                  decoration: BoxDecoration(
                    color: iconTint.withOpacity(0.1),
                    shape: BoxShape.circle,
                  ),
                  child: Icon(
                    icon,
                    size: 20,
                    color: iconTint,
                  ),
                ),
                const SizedBox(width: 12),
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            const Divider(color: Color(0xFFF0F4F8)),
            const SizedBox(height: 16),
            ...children,
          ],
        ),
      ),
    );
  }

  Widget _buildSwitchItem({
    required String title,
    required String description,
    required bool value,
    required ValueChanged<bool> onChanged,
    bool enabled = true,
    bool isLast = false,
  }) {
    return Column(
      children: [
        Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                      color: enabled
                          ? AppColor.textDarkBlue
                          : AppColor.textLightGray,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    description,
                    style: TextStyle(
                      fontSize: 14,
                      color: enabled
                          ? AppColor.textMediumGray
                          : AppColor.textLightGray,
                    ),
                  ),
                ],
              ),
            ),
            Switch(
              value: value,
              onChanged: enabled ? onChanged : null,
              activeColor: AppColor.primaryBlue,
              trackColor: MaterialStateProperty.resolveWith((states) {
                if (states.contains(MaterialState.disabled)) {
                  return AppColor.textLightGray.withOpacity(0.3);
                }
                return value ? AppColor.primaryBlue.withOpacity(0.3) : null;
              }),
            ),
          ],
        ),
        if (!isLast) ...[
          const SizedBox(height: 16),
          const Divider(color: Color(0xFFF0F4F8)),
          const SizedBox(height: 16),
        ],
      ],
    );
  }

  Widget _buildClickableItem({
    required String title,
    required String description,
    required VoidCallback onTap,
    bool isLast = false,
  }) {
    return Column(
      children: [
        InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(8),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w500,
                          color: AppColor.textDarkBlue,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        description,
                        style: const TextStyle(
                          fontSize: 14,
                          color: AppColor.textMediumGray,
                        ),
                      ),
                    ],
                  ),
                ),
                const Icon(
                  Icons.arrow_forward_ios,
                  size: 16,
                  color: AppColor.textLightGray,
                ),
              ],
            ),
          ),
        ),
        if (!isLast) ...[
          const SizedBox(height: 16),
          const Divider(color: Color(0xFFF0F4F8)),
          const SizedBox(height: 16),
        ],
      ],
    );
  }

  Widget _buildDropdownItem({
    required String title,
    required String description,
    required String value,
    required List<String> options,
    required ValueChanged<String?> onChanged,
    bool isLast = false,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w500,
            color: AppColor.textDarkBlue,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          description,
          style: const TextStyle(
            fontSize: 14,
            color: AppColor.textMediumGray,
          ),
        ),
        const SizedBox(height: 12),
        Container(
          width: double.infinity,
          padding: const EdgeInsets.symmetric(horizontal: 16),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: const Color(0xFFE5E7EB)),
            color: Colors.white,
          ),
          child: DropdownButton<String>(
            value: value,
            onChanged: onChanged,
            items: options.map((option) {
              return DropdownMenuItem<String>(
                value: option,
                child: Text(option),
              );
            }).toList(),
            underline: const SizedBox(), // Remove underline
            isExpanded: true,
            icon: const Icon(Icons.keyboard_arrow_down,
                color: AppColor.textMediumGray),
          ),
        ),
        if (!isLast) ...[
          const SizedBox(height: 16),
          const Divider(color: Color(0xFFF0F4F8)),
          const SizedBox(height: 16),
        ],
      ],
    );
  }
}
