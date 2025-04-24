import '../models/user.dart';

/// Interface for authentication repository
abstract class AuthRepository {
  /// Sign in with email and password
  Future<User> signIn(String email, String password);

  /// Sign up with email, password and user data
  Future<User> signUp(String email, String password, User user);

  /// Sign out the current user
  Future<void> signOut();

  /// Get the current authenticated user
  Future<User?> getCurrentUser();

  /// Check if a user is signed in
  Future<bool> isUserSignedIn();
}
