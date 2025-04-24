/// Ride type enum representing the possible types of rides
enum RideType {
  /// Standard ride for ambulatory passengers
  ambulatory,

  /// Ride for wheelchair users
  wheelchair,

  /// Ride for stretcher transportation
  stretcher,

  /// Ride for bariatric patients
  bariatric;

  /// Get the display name of the ride type
  String get displayName {
    switch (this) {
      case RideType.ambulatory:
        return 'Ambulatory';
      case RideType.wheelchair:
        return 'Wheelchair';
      case RideType.stretcher:
        return 'Stretcher';
      case RideType.bariatric:
        return 'Bariatric';
    }
  }

  /// Convert a string to a RideType enum
  static RideType fromString(String typeStr) {
    switch (typeStr.toUpperCase()) {
      case 'AMBULATORY':
        return RideType.ambulatory;
      case 'WHEELCHAIR':
        return RideType.wheelchair;
      case 'STRETCHER':
        return RideType.stretcher;
      case 'BARIATRIC':
        return RideType.bariatric;
      default:
        return RideType.ambulatory;
    }
  }
}
