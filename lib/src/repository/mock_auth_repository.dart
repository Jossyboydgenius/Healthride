import 'dart:async';
import '../models/user.dart';
import 'auth_repository.dart';

/// A mock implementation of the [AuthRepository] for testing and development.
/// Simulates authentication operations without requiring a real backend.
class MockAuthRepository implements AuthRepository {
  /// Internal flags to track authentication state
  bool _isLoggedIn = false;

  /// The current user, can be null if no user is logged in
  User? _currentUser;

  /// Simulates network delay for realistic testing
  final Duration _mockDelay = const Duration(milliseconds: 800);

  /// Sign in with email and password
  ///
  /// Returns a [User] if credentials are valid, throws [Exception] otherwise
  @override
  Future<User> signIn(String email, String password) async {
    // Simulate network delay
    await Future.delayed(_mockDelay);

    // Basic validation
    if (!_isValidEmail(email)) {
      throw Exception('Invalid email format');
    }

    if (password.isEmpty || password.length < 6) {
      throw Exception('Password must be at least 6 characters');
    }

    // For mock purposes, accept any valid email/password
    _currentUser = User(
      id: 'mock-user-${DateTime.now().millisecondsSinceEpoch}',
      email: email,
      firstName: 'Mock',
      lastName: 'User',
      phone: '+1234567890',
      isVerified: true,
      verificationStatus: 'verified',
    );

    _isLoggedIn = true;
    return _currentUser!;
  }

  /// Register a new user with email and password
  ///
  /// Returns the newly created [User], throws [Exception] if registration fails
  @override
  Future<User> signUp(String email, String password, User user) async {
    // Simulate network delay
    await Future.delayed(_mockDelay);

    // Basic validation
    if (!_isValidEmail(email)) {
      throw Exception('Invalid email format');
    }

    if (password.isEmpty || password.length < 6) {
      throw Exception('Password must be at least 6 characters');
    }

    // Create new user with unique ID and set verification status
    _currentUser = User(
      id: 'mock-user-${DateTime.now().millisecondsSinceEpoch}',
      email: email,
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone,
      isVerified: false,
      verificationStatus: 'pending',
    );

    _isLoggedIn = true;
    return _currentUser!;
  }

  /// Sign out the currently logged in user
  @override
  Future<void> signOut() async {
    // Simulate network delay
    await Future.delayed(_mockDelay);

    // Reset auth state
    _isLoggedIn = false;
    _currentUser = null;
  }

  /// Get the currently logged in user
  ///
  /// Returns the current [User] or null if no user is logged in
  @override
  Future<User?> getCurrentUser() async {
    // Simulate network delay
    await Future.delayed(_mockDelay ~/ 2);
    return _currentUser;
  }

  /// Check if a user is currently signed in
  ///
  /// Returns true if a user is signed in, false otherwise
  @override
  Future<bool> isUserSignedIn() async {
    // Simulate network delay
    await Future.delayed(_mockDelay ~/ 2);
    return _isLoggedIn;
  }

  // Helper method to validate email format
  bool _isValidEmail(String email) {
    final emailRegExp = RegExp(r'^[a-zA-Z0-9.]+@[a-zA-Z0-9]+\.[a-zA-Z]+');
    return emailRegExp.hasMatch(email);
  }
}
