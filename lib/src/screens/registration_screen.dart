import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import '../routes/app_routes.dart';
import '../widgets/components/gradient_button.dart';

/// Multi-step registration screen for new users
class RegistrationScreen extends StatefulWidget {
  /// Creates a registration screen
  const RegistrationScreen({super.key});

  @override
  State<RegistrationScreen> createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends State<RegistrationScreen> {
  // Controllers for form fields
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _phoneController = TextEditingController();
  final _addressController = TextEditingController();
  final _cityController = TextEditingController();
  final _stateController = TextEditingController();
  final _zipController = TextEditingController();
  final _insuranceProviderController = TextEditingController();
  final _policyNumberController = TextEditingController();

  // Current step in the registration process
  int _currentStep = 0;
  bool _isLoading = false;
  bool _passwordVisible = false;
  String? _errorMessage;

  // List of steps in the registration process
  final List<String> _stepTitles = [
    'Personal Information',
    'Set Up Your Account',
    'Add Your Address',
    'Insurance Information'
  ];

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _phoneController.dispose();
    _addressController.dispose();
    _cityController.dispose();
    _stateController.dispose();
    _zipController.dispose();
    _insuranceProviderController.dispose();
    _policyNumberController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep < _stepTitles.length - 1) {
      setState(() {
        _currentStep += 1;
      });
    } else {
      _handleRegistration();
    }
  }

  void _previousStep() {
    if (_currentStep > 0) {
      setState(() {
        _currentStep -= 1;
      });
    }
  }

  void _handleRegistration() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Simulate registration
      await Future.delayed(const Duration(seconds: 2));

      if (mounted) {
        Navigator.of(context).pushReplacementNamed(AppRoutes.insuranceUpload);
      }
    } catch (e) {
      setState(() {
        _errorMessage = 'Registration failed: ${e.toString()}';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColor.backgroundGray,
      appBar: AppBar(
        title: const Text(
          'Create Your Account',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
            color: AppColor.textDarkBlue,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            if (_currentStep > 0) {
              _previousStep();
            } else {
              Navigator.of(context).pop();
            }
          },
        ),
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Progress indicators
            Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(_stepTitles.length, (index) {
                  return _buildStepIndicator(index);
                }),
              ),
            ),

            // Step title
            Padding(
              padding: const EdgeInsets.only(bottom: 24.0),
              child: Text(
                _stepTitles[_currentStep],
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: AppColor.textDarkBlue,
                ),
              ),
            ),

            // Error message if any
            if (_errorMessage != null)
              Container(
                width: double.infinity,
                margin:
                    const EdgeInsets.symmetric(horizontal: 24.0, vertical: 8.0),
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColor.errorRedLight,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  _errorMessage!,
                  style: const TextStyle(
                    color: AppColor.errorRed,
                    fontSize: 14,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),

            // Form content
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24.0),
                child: _buildCurrentStep(),
              ),
            ),

            // Navigation buttons
            Padding(
              padding: const EdgeInsets.all(24.0),
              child: Row(
                children: [
                  if (_currentStep > 0)
                    Expanded(
                      child: OutlinedButton(
                        onPressed: !_isLoading ? _previousStep : null,
                        style: OutlinedButton.styleFrom(
                          padding: const EdgeInsets.symmetric(vertical: 16.0),
                          side: const BorderSide(color: AppColor.primaryBlue),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(28.0),
                          ),
                        ),
                        child: const Text('Back'),
                      ),
                    ),
                  if (_currentStep > 0) const SizedBox(width: 16.0),
                  Expanded(
                    child: GradientButton(
                      text: _currentStep == _stepTitles.length - 1
                          ? 'Create Account'
                          : 'Next',
                      onPressed: !_isLoading ? _nextStep : () {},
                      isLoading: _isLoading,
                      enabled: !_isLoading,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStepIndicator(int index) {
    final isCompleted = index < _currentStep;
    final isActive = index == _currentStep;

    return Expanded(
      child: Row(
        children: [
          // Line before (except for first item)
          if (index > 0)
            Expanded(
              child: Container(
                height: 2,
                color: index <= _currentStep
                    ? AppColor.primaryBlue
                    : AppColor.textLightGray,
              ),
            ),

          // Circle indicator with number
          Container(
            width: 30,
            height: 30,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: isCompleted
                  ? AppColor.primaryBlue
                  : isActive
                      ? Colors.white
                      : AppColor.textLightGray,
              border: Border.all(
                color: isActive ? AppColor.primaryBlue : Colors.transparent,
                width: 2,
              ),
            ),
            child: isCompleted
                ? const Center(
                    child: Icon(
                      Icons.check,
                      size: 20,
                      color: Colors.white,
                    ),
                  )
                : Center(
                    child: Text(
                      '${index + 1}',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: isActive ? AppColor.primaryBlue : Colors.white,
                      ),
                    ),
                  ),
          ),

          // Line after (except for last item)
          if (index < _stepTitles.length - 1)
            Expanded(
              child: Container(
                height: 2,
                color: index < _currentStep
                    ? AppColor.primaryBlue
                    : AppColor.textLightGray,
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildCurrentStep() {
    switch (_currentStep) {
      case 0:
        return _buildPersonalStep();
      case 1:
        return _buildAccountStep();
      case 2:
        return _buildAddressStep();
      case 3:
        return _buildInsuranceStep();
      default:
        return Container();
    }
  }

  Widget _buildAccountStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Create your account',
          style: TextStyle(
            fontSize: 16,
            color: AppColor.textMediumGray,
          ),
        ),
        const SizedBox(height: 24),

        // Email field
        TextField(
          controller: _emailController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Email address',
            prefixIcon: const Icon(
              Icons.email,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          keyboardType: TextInputType.emailAddress,
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),

        // Password field
        TextField(
          controller: _passwordController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Password',
            prefixIcon: const Icon(
              Icons.lock,
              color: AppColor.primaryBlue,
            ),
            suffixIcon: IconButton(
              icon: Icon(
                _passwordVisible ? Icons.visibility_off : Icons.visibility,
                color: _passwordVisible
                    ? AppColor.primaryBlue
                    : AppColor.textMediumGray,
              ),
              onPressed: () {
                setState(() {
                  _passwordVisible = !_passwordVisible;
                });
              },
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
            helperText: 'Password must be at least 8 characters',
          ),
          obscureText: !_passwordVisible,
          textInputAction: TextInputAction.done,
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  Widget _buildPersonalStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Tell us about yourself',
          style: TextStyle(
            fontSize: 16,
            color: AppColor.textMediumGray,
          ),
        ),
        const SizedBox(height: 24),

        // First name field
        TextField(
          controller: _firstNameController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'First name',
            prefixIcon: const Icon(
              Icons.person,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),

        // Last name field
        TextField(
          controller: _lastNameController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Last name',
            prefixIcon: const Icon(
              Icons.person,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),

        // Phone field
        TextField(
          controller: _phoneController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Phone number',
            prefixIcon: const Icon(
              Icons.phone,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          keyboardType: TextInputType.phone,
          textInputAction: TextInputAction.done,
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  Widget _buildAddressStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Where do you live?',
          style: TextStyle(
            fontSize: 16,
            color: AppColor.textMediumGray,
          ),
        ),
        const SizedBox(height: 24),

        // Address field
        TextField(
          controller: _addressController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Street address',
            prefixIcon: const Icon(
              Icons.home,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),

        // City field
        TextField(
          controller: _cityController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'City',
            prefixIcon: const Icon(
              Icons.location_city,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),

        // State and Zip Row
        Row(
          children: [
            // State field
            Expanded(
              flex: 3,
              child: TextField(
                controller: _stateController,
                decoration: InputDecoration(
                  filled: true,
                  fillColor: Colors.white,
                  labelText: 'State',
                  prefixIcon: const Icon(
                    Icons.map,
                    color: AppColor.primaryBlue,
                  ),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(color: AppColor.primaryBlue),
                  ),
                ),
                textInputAction: TextInputAction.next,
              ),
            ),
            const SizedBox(width: 16),
            // Zip field
            Expanded(
              flex: 2,
              child: TextField(
                controller: _zipController,
                decoration: InputDecoration(
                  filled: true,
                  fillColor: Colors.white,
                  labelText: 'Zip',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(color: AppColor.primaryBlue),
                  ),
                ),
                keyboardType: TextInputType.number,
                textInputAction: TextInputAction.done,
              ),
            ),
          ],
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  Widget _buildInsuranceStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Insurance Information',
          style: TextStyle(
            fontSize: 16,
            color: AppColor.textMediumGray,
          ),
        ),
        const SizedBox(height: 24),

        // Insurance Provider field
        TextField(
          controller: _insuranceProviderController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Insurance Provider',
            hintText: 'Enter your insurance provider',
            prefixIcon: const Icon(
              Icons.shield_outlined,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),

        // Policy Number field
        TextField(
          controller: _policyNumberController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            labelText: 'Policy Number',
            hintText: 'Enter your policy number',
            prefixIcon: const Icon(
              Icons.pin_outlined,
              color: AppColor.primaryBlue,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: AppColor.primaryBlue),
            ),
          ),
          textInputAction: TextInputAction.done,
        ),
        const SizedBox(height: 24),

        // Note about next steps
        const Padding(
          padding: EdgeInsets.all(16.0),
          child: Text(
            "Note: You'll be asked to upload your insurance card and ID in the next step.",
            style: TextStyle(
              fontSize: 14,
              color: AppColor.textMediumGray,
            ),
            textAlign: TextAlign.center,
          ),
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}
