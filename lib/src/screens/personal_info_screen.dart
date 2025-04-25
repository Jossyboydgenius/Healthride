import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import '../models/user.dart';
import '../widgets/components/gradient_button.dart';

class PersonalInfoScreen extends StatefulWidget {
  final User? user;

  const PersonalInfoScreen({
    Key? key,
    this.user,
  }) : super(key: key);

  @override
  State<PersonalInfoScreen> createState() => _PersonalInfoScreenState();
}

class _PersonalInfoScreenState extends State<PersonalInfoScreen>
    with SingleTickerProviderStateMixin {
  final _scrollController = ScrollController();
  bool _showContent = false;
  bool _showSuccessMessage = false;

  // Form controllers
  late TextEditingController _firstNameController;
  late TextEditingController _lastNameController;
  late TextEditingController _emailController;
  late TextEditingController _phoneController;
  late TextEditingController _addressController;
  late TextEditingController _cityController;
  late TextEditingController _stateController;
  late TextEditingController _zipCodeController;

  @override
  void initState() {
    super.initState();

    // Initialize controllers with user data
    _firstNameController =
        TextEditingController(text: widget.user?.firstName ?? "");
    _lastNameController =
        TextEditingController(text: widget.user?.lastName ?? "");
    _emailController = TextEditingController(text: widget.user?.email ?? "");
    _phoneController = TextEditingController(text: widget.user?.phone ?? "");
    _addressController =
        TextEditingController(text: widget.user?.address ?? "");
    _cityController = TextEditingController(text: widget.user?.city ?? "");
    _stateController = TextEditingController(text: widget.user?.state ?? "");
    _zipCodeController =
        TextEditingController(text: widget.user?.zipCode ?? "");

    // Trigger animation after a delay
    Future.delayed(const Duration(milliseconds: 100), () {
      if (mounted) {
        setState(() {
          _showContent = true;
        });
      }
    });
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _lastNameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    _addressController.dispose();
    _cityController.dispose();
    _stateController.dispose();
    _zipCodeController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  // Check if form has changes
  bool get _hasChanges {
    return _firstNameController.text != (widget.user?.firstName ?? "") ||
        _lastNameController.text != (widget.user?.lastName ?? "") ||
        _emailController.text != (widget.user?.email ?? "") ||
        _phoneController.text != (widget.user?.phone ?? "") ||
        _addressController.text != (widget.user?.address ?? "") ||
        _cityController.text != (widget.user?.city ?? "") ||
        _stateController.text != (widget.user?.state ?? "") ||
        _zipCodeController.text != (widget.user?.zipCode ?? "");
  }

  // Check if form is valid
  bool get _isFormValid {
    return _firstNameController.text.isNotEmpty &&
        _lastNameController.text.isNotEmpty &&
        _emailController.text.isNotEmpty &&
        _emailController.text.contains('@') &&
        _phoneController.text.isNotEmpty;
  }

  void _saveChanges() {
    // Mock saving changes
    setState(() {
      _showSuccessMessage = true;
    });

    // Hide success message after a delay
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        setState(() {
          _showSuccessMessage = false;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Personal Information',
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
      body: Container(
        width: double.infinity,
        height: double.infinity,
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Color(0xFFF8FAFC),
              Color(0xFFF5F7FD),
            ],
          ),
        ),
        child: Stack(
          children: [
            // Content
            AnimatedOpacity(
              opacity: _showContent ? 1.0 : 0.0,
              duration: const Duration(milliseconds: 500),
              curve: Curves.easeOut,
              child: AnimatedSlide(
                offset: _showContent ? Offset.zero : const Offset(0, 0.05),
                duration: const Duration(milliseconds: 500),
                curve: Curves.easeOutCubic,
                child: SingleChildScrollView(
                  controller: _scrollController,
                  padding:
                      const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      // Personal Information Card
                      Card(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(24),
                        ),
                        elevation: 4,
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text(
                                'Edit Your Information',
                                style: TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                  color: AppColor.textDarkBlue,
                                ),
                              ),

                              const SizedBox(height: 16),

                              // Name fields (side by side)
                              Row(
                                children: [
                                  // First Name
                                  Expanded(
                                    child: _buildFormField(
                                      controller: _firstNameController,
                                      label: 'First Name',
                                      icon: Icons.person_outline,
                                      textCapitalization:
                                          TextCapitalization.words,
                                    ),
                                  ),

                                  const SizedBox(width: 16),

                                  // Last Name
                                  Expanded(
                                    child: _buildFormField(
                                      controller: _lastNameController,
                                      label: 'Last Name',
                                      icon: Icons.person_outline,
                                      textCapitalization:
                                          TextCapitalization.words,
                                    ),
                                  ),
                                ],
                              ),

                              const SizedBox(height: 16),

                              // Email
                              _buildFormField(
                                controller: _emailController,
                                label: 'Email',
                                icon: Icons.email_outlined,
                                keyboardType: TextInputType.emailAddress,
                              ),

                              const SizedBox(height: 16),

                              // Phone
                              _buildFormField(
                                controller: _phoneController,
                                label: 'Phone',
                                icon: Icons.phone_outlined,
                                keyboardType: TextInputType.phone,
                              ),

                              const SizedBox(height: 16),

                              // Address
                              _buildFormField(
                                controller: _addressController,
                                label: 'Street Address',
                                icon: Icons.home_outlined,
                                textCapitalization: TextCapitalization.words,
                              ),

                              const SizedBox(height: 16),

                              // City, State, Zip (side by side)
                              Row(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  // City
                                  Expanded(
                                    flex: 2,
                                    child: _buildFormField(
                                      controller: _cityController,
                                      label: 'City',
                                      icon: Icons.location_city_outlined,
                                      textCapitalization:
                                          TextCapitalization.words,
                                    ),
                                  ),

                                  const SizedBox(width: 16),

                                  // State
                                  Expanded(
                                    child: _buildFormField(
                                      controller: _stateController,
                                      label: 'State',
                                      icon: Icons.map_outlined,
                                      textCapitalization:
                                          TextCapitalization.characters,
                                    ),
                                  ),

                                  const SizedBox(width: 16),

                                  // Zip Code
                                  Expanded(
                                    child: _buildFormField(
                                      controller: _zipCodeController,
                                      label: 'Zip',
                                      icon: Icons.pin_outlined,
                                      keyboardType: TextInputType.number,
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),

                      const SizedBox(height: 24),

                      // Save Button
                      GradientButton(
                        text: 'Save Changes',
                        onPressed: _isFormValid && _hasChanges
                            ? () => _saveChanges()
                            : () {},
                        enabled: _isFormValid && _hasChanges,
                      ),
                    ],
                  ),
                ),
              ),
            ),

            // Success message
            if (_showSuccessMessage)
              Positioned(
                top: 16,
                left: 16,
                right: 16,
                child: Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  decoration: BoxDecoration(
                    color: AppColor.successGreen.withOpacity(0.9),
                    borderRadius: BorderRadius.circular(8),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.1),
                        blurRadius: 8,
                        offset: const Offset(0, 2),
                      ),
                    ],
                  ),
                  child: const Row(
                    children: [
                      Icon(
                        Icons.check_circle,
                        color: Colors.white,
                      ),
                      SizedBox(width: 12),
                      Expanded(
                        child: Text(
                          'Your information has been updated successfully!',
                          style: TextStyle(
                            color: Colors.white,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildFormField({
    required TextEditingController controller,
    required String label,
    required IconData icon,
    TextInputType keyboardType = TextInputType.text,
    TextCapitalization textCapitalization = TextCapitalization.none,
  }) {
    return TextField(
      controller: controller,
      keyboardType: keyboardType,
      textCapitalization: textCapitalization,
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon, color: AppColor.primaryBlue),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColor.primaryBlue, width: 2),
        ),
        filled: true,
        fillColor: Colors.white,
        contentPadding:
            const EdgeInsets.symmetric(vertical: 16, horizontal: 16),
      ),
      style: const TextStyle(
        fontSize: 16,
        color: AppColor.textDarkBlue,
      ),
    );
  }
}
