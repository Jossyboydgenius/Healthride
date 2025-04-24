/// Verification status of a user
enum VerificationStatus {
  /// Not submitted for verification yet
  notSubmitted,

  /// Submitted and waiting for verification
  pending,

  /// Verified
  verified,

  /// Rejected
  rejected;

  /// Get the string representation of the verification status
  String get name {
    switch (this) {
      case VerificationStatus.notSubmitted:
        return 'NOT_SUBMITTED';
      case VerificationStatus.pending:
        return 'PENDING';
      case VerificationStatus.verified:
        return 'VERIFIED';
      case VerificationStatus.rejected:
        return 'REJECTED';
    }
  }

  /// Create a VerificationStatus from a string
  static VerificationStatus fromString(String? status) {
    switch (status?.toUpperCase()) {
      case 'NOT_SUBMITTED':
        return VerificationStatus.notSubmitted;
      case 'PENDING':
        return VerificationStatus.pending;
      case 'VERIFIED':
        return VerificationStatus.verified;
      case 'REJECTED':
        return VerificationStatus.rejected;
      default:
        return VerificationStatus.notSubmitted;
    }
  }
}

/// Extensions to make working with the verification status easier
extension VerificationStatusExtension on VerificationStatus {
  /// Check if the status is verified
  bool get isVerified => this == VerificationStatus.verified;

  /// Check if the status is pending
  bool get isPending => this == VerificationStatus.pending;

  /// Check if the status is rejected
  bool get isRejected => this == VerificationStatus.rejected;

  /// Check if the status is not submitted
  bool get isNotSubmitted => this == VerificationStatus.notSubmitted;
}
