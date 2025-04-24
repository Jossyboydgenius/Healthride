/// Represents a user in the app
class User {
  /// Unique identifier for the user
  final String id;

  /// Email address
  final String email;

  /// First name
  final String? firstName;

  /// Last name
  final String? lastName;

  /// Phone number
  final String? phone;

  /// Street address
  final String? address;

  /// City
  final String? city;

  /// State or province
  final String? state;

  /// ZIP code
  final String? zipCode;

  /// Insurance provider
  final String? insuranceProvider;

  /// Insurance policy number
  final String? policyNumber;

  /// Insurance card image URL
  final String? insuranceCardImageUrl;

  /// Identification card image URL
  final String? identificationCardImageUrl;

  /// Whether the user is verified
  final bool isVerified;

  /// Verification status
  final String? verificationStatus;

  /// Creates a user
  const User({
    required this.id,
    required this.email,
    this.firstName,
    this.lastName,
    this.phone,
    this.address,
    this.city,
    this.state,
    this.zipCode,
    this.insuranceProvider,
    this.policyNumber,
    this.insuranceCardImageUrl,
    this.identificationCardImageUrl,
    this.isVerified = false,
    this.verificationStatus,
  });

  /// Create a copy of this user with the given fields replaced
  User copyWith({
    String? id,
    String? email,
    String? firstName,
    String? lastName,
    String? phone,
    String? address,
    String? city,
    String? state,
    String? zipCode,
    String? insuranceProvider,
    String? policyNumber,
    String? insuranceCardImageUrl,
    String? identificationCardImageUrl,
    bool? isVerified,
    String? verificationStatus,
  }) {
    return User(
      id: id ?? this.id,
      email: email ?? this.email,
      firstName: firstName ?? this.firstName,
      lastName: lastName ?? this.lastName,
      phone: phone ?? this.phone,
      address: address ?? this.address,
      city: city ?? this.city,
      state: state ?? this.state,
      zipCode: zipCode ?? this.zipCode,
      insuranceProvider: insuranceProvider ?? this.insuranceProvider,
      policyNumber: policyNumber ?? this.policyNumber,
      insuranceCardImageUrl:
          insuranceCardImageUrl ?? this.insuranceCardImageUrl,
      identificationCardImageUrl:
          identificationCardImageUrl ?? this.identificationCardImageUrl,
      isVerified: isVerified ?? this.isVerified,
      verificationStatus: verificationStatus ?? this.verificationStatus,
    );
  }

  /// Convert user to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'firstName': firstName,
      'lastName': lastName,
      'phone': phone,
      'address': address,
      'city': city,
      'state': state,
      'zipCode': zipCode,
      'insuranceProvider': insuranceProvider,
      'policyNumber': policyNumber,
      'insuranceCardImageUrl': insuranceCardImageUrl,
      'identificationCardImageUrl': identificationCardImageUrl,
      'isVerified': isVerified,
      'verificationStatus': verificationStatus,
    };
  }

  /// Create user from JSON
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      firstName: json['firstName'],
      lastName: json['lastName'],
      phone: json['phone'],
      address: json['address'],
      city: json['city'],
      state: json['state'],
      zipCode: json['zipCode'],
      insuranceProvider: json['insuranceProvider'],
      policyNumber: json['policyNumber'],
      insuranceCardImageUrl: json['insuranceCardImageUrl'],
      identificationCardImageUrl: json['identificationCardImageUrl'],
      isVerified: json['isVerified'] ?? false,
      verificationStatus: json['verificationStatus'],
    );
  }
}
