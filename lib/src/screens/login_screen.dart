import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../constants/app_color.dart';
import '../repository/auth_repository.dart';
import '../routes/app_routes.dart';
import '../widgets/components/gradient_button.dart';

/// A login screen with email and password fields
class LoginScreen extends StatefulWidget {
  /// Creates a login screen
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;
  bool _passwordVisible = false;
  late final AuthRepository _authRepository;

  @override
  void initState() {
    super.initState();
    // Pre-fill with mock credentials for testing
    _emailController.text = "mock@example.com";
    _passwordController.text = "password";
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Get the repository from the provider
    _authRepository = Provider.of<AuthRepository>(context, listen: false);
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _signIn() async {
    // Clear any previous error messages
    setState(() {
      _errorMessage = null;
      _isLoading = true;
    });

    try {
      final email = _emailController.text.trim();
      final password = _passwordController.text;

      if (email.isEmpty || password.isEmpty) {
        setState(() {
          _errorMessage = 'Please enter both email and password';
          _isLoading = false;
        });
        return;
      }

      // Use the provider-injected repository for authentication
      final user = await _authRepository.signIn(email, password);

      if (!mounted) return;

      // Reset loading state
      setState(() {
        _isLoading = false;
      });

      // Successfully logged in
      Navigator.pushReplacementNamed(context, AppRoutes.home);
    } catch (e) {
      if (!mounted) return;

      setState(() {
        _errorMessage = 'Login failed: ${e.toString()}';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F9FF),
      body: SafeArea(
        child: Stack(
          children: [
            // Background elements
            _buildBackgroundElements(),

            // Main content
            SingleChildScrollView(
              padding: const EdgeInsets.all(24.0),
              child: ConstrainedBox(
                constraints: BoxConstraints(
                  minHeight: MediaQuery.of(context).size.height - 48.0,
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Logo and title
                    _buildHeaderSection(),

                    // Form fields
                    _buildLoginForm(),

                    // Register option
                    _buildRegisterSection(),
                  ],
                ),
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
        Positioned(
          top: -300,
          left: -300,
          child: Container(
            width: 600,
            height: 600,
            decoration: BoxDecoration(
              color: AppColor.primaryBlue.withOpacity(0.08),
              shape: BoxShape.circle,
            ),
          ),
        ),
        Positioned(
          bottom: -200,
          right: -200,
          child: Container(
            width: 500,
            height: 500,
            decoration: BoxDecoration(
              color: AppColor.primaryPurple.withOpacity(0.08),
              shape: BoxShape.circle,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildHeaderSection() {
    return Column(
      children: [
        const SizedBox(height: 32),

        // Logo
        Transform.rotate(
          angle: 0.26, // 15 degrees
          child: Container(
            width: 90,
            height: 90,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(26),
              gradient: const LinearGradient(
                colors: AppColor.primaryGradient,
              ),
              boxShadow: [
                BoxShadow(
                  color: AppColor.primaryBlue.withOpacity(0.5),
                  blurRadius: 20,
                  offset: const Offset(0, 10),
                ),
              ],
            ),
            child: const Center(
              child: Icon(
                Icons.local_hospital,
                color: Colors.white,
                size: 40,
              ),
            ),
          ),
        ),

        const SizedBox(height: 24),

        const Text(
          'Welcome to Health Ride',
          style: TextStyle(
            fontSize: 28,
            fontWeight: FontWeight.bold,
            color: AppColor.textDarkBlue,
            fontFamily: 'Poppins',
          ),
        ),

        const SizedBox(height: 8),

        const Text(
          'Medical transportation simplified',
          style: TextStyle(
            fontSize: 16,
            color: AppColor.textMediumGray,
            fontFamily: 'Poppins',
          ),
        ),
      ],
    );
  }

  Widget _buildLoginForm() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Error message if any
        if (_errorMessage != null)
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(12),
            margin: const EdgeInsets.only(bottom: 16),
            decoration: BoxDecoration(
              color: AppColor.errorRedLight,
              borderRadius: BorderRadius.circular(8),
            ),
            child: Text(
              _errorMessage!,
              style: const TextStyle(
                color: AppColor.errorRed,
                fontSize: 14,
                fontFamily: 'Poppins',
              ),
              textAlign: TextAlign.center,
            ),
          ),

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
          enabled: !_isLoading,
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
          ),
          obscureText: !_passwordVisible,
          textInputAction: TextInputAction.done,
          enabled: !_isLoading,
        ),

        // Forgot password
        Align(
          alignment: Alignment.centerRight,
          child: TextButton(
            onPressed: !_isLoading ? () {} : null,
            child: const Text(
              'Forgot password?',
              style: TextStyle(
                color: AppColor.primaryBlue,
                fontWeight: FontWeight.w500,
                fontFamily: 'Poppins',
              ),
            ),
          ),
        ),

        const SizedBox(height: 24),

        // Sign in button
        GradientButton(
          text: 'Sign In',
          onPressed: !_isLoading ? _signIn : () {},
          isLoading: _isLoading,
          enabled: !_isLoading,
        ),
      ],
    );
  }

  Widget _buildRegisterSection() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text(
          "Don't have an account?",
          style: TextStyle(
            color: AppColor.textMediumGray,
            fontFamily: 'Poppins',
          ),
        ),
        TextButton(
          onPressed: !_isLoading
              ? () => Navigator.of(context).pushNamed(AppRoutes.registration)
              : null,
          child: const Text(
            'Sign Up',
            style: TextStyle(
              color: AppColor.primaryBlue,
              fontWeight: FontWeight.bold,
              fontFamily: 'Poppins',
            ),
          ),
        ),
      ],
    );
  }
}
