import 'package:healthride/src/models/ride_status.dart';

/// Represents a ride in the app
class Ride {
  /// Unique identifier for the ride
  final String id;

  /// User ID of the person who booked the ride
  final String? userId;

  /// The pickup address
  final String pickupAddress;

  /// The destination address
  final String destinationAddress;

  /// The date and time of the ride
  final DateTime dateTime;

  /// The status of the ride
  final RideStatus status;

  /// The fare for the ride
  final double fare;

  /// The distance of the ride in miles
  final double distance;

  /// The estimated duration of the ride in minutes
  final int duration;

  /// The name of the driver
  final String? driverName;

  /// The phone number of the driver
  final String? driverPhone;

  /// The vehicle model
  final String? vehicleModel;

  /// The vehicle license plate
  final String? vehiclePlate;

  /// Vehicle information (combined model and plate)
  final String? vehicleInfo;

  /// The name of the passenger
  final String? passengerName;

  /// The type of ride (ambulatory, wheelchair, etc.)
  final String? rideType;

  /// The actual price if different from estimated fare
  final double? actualPrice;

  /// Creates a new ride
  const Ride({
    required this.id,
    required this.pickupAddress,
    required this.destinationAddress,
    required this.dateTime,
    required this.status,
    this.fare = 0.0,
    this.distance = 0.0,
    this.duration = 0,
    this.userId,
    this.driverName,
    this.driverPhone,
    this.vehicleModel,
    this.vehiclePlate,
    this.vehicleInfo,
    this.passengerName,
    this.rideType,
    this.actualPrice,
  });

  /// Creates a copy of this ride with the given fields replaced with the new values
  Ride copyWith({
    String? id,
    String? userId,
    String? pickupAddress,
    String? destinationAddress,
    DateTime? dateTime,
    RideStatus? status,
    double? fare,
    double? distance,
    int? duration,
    String? driverName,
    String? driverPhone,
    String? vehicleModel,
    String? vehiclePlate,
    String? vehicleInfo,
    String? passengerName,
    String? rideType,
    double? actualPrice,
  }) {
    return Ride(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      pickupAddress: pickupAddress ?? this.pickupAddress,
      destinationAddress: destinationAddress ?? this.destinationAddress,
      dateTime: dateTime ?? this.dateTime,
      status: status ?? this.status,
      fare: fare ?? this.fare,
      distance: distance ?? this.distance,
      duration: duration ?? this.duration,
      driverName: driverName ?? this.driverName,
      driverPhone: driverPhone ?? this.driverPhone,
      vehicleModel: vehicleModel ?? this.vehicleModel,
      vehiclePlate: vehiclePlate ?? this.vehiclePlate,
      vehicleInfo: vehicleInfo ?? this.vehicleInfo,
      passengerName: passengerName ?? this.passengerName,
      rideType: rideType ?? this.rideType,
      actualPrice: actualPrice ?? this.actualPrice,
    );
  }

  /// Create a Ride from a JSON map
  factory Ride.fromJson(Map<String, dynamic> json) {
    return Ride(
      id: json['id'] ?? '',
      userId: json['userId'],
      pickupAddress: json['pickupAddress'] ?? '',
      destinationAddress: json['destinationAddress'] ?? '',
      dateTime: json['dateTime'] != null
          ? DateTime.parse(json['dateTime'])
          : DateTime.now(),
      status: _statusFromString(json['status'] ?? 'SCHEDULED'),
      fare: json['fare'] != null ? (json['fare'] as num).toDouble() : 0.0,
      distance:
          json['distance'] != null ? (json['distance'] as num).toDouble() : 0.0,
      duration: json['duration'] != null ? json['duration'] as int : 0,
      driverName: json['driverName'],
      driverPhone: json['driverPhone'],
      vehicleModel: json['vehicleModel'],
      vehiclePlate: json['vehiclePlate'],
      vehicleInfo: json['vehicleInfo'],
      passengerName: json['passengerName'],
      rideType: json['rideType'],
      actualPrice: json['actualPrice'] != null
          ? (json['actualPrice'] as num).toDouble()
          : null,
    );
  }

  /// Convert the Ride to a JSON map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'pickupAddress': pickupAddress,
      'destinationAddress': destinationAddress,
      'dateTime': dateTime.toIso8601String(),
      'status': status.name.toUpperCase(),
      'fare': fare,
      'distance': distance,
      'duration': duration,
      'driverName': driverName,
      'driverPhone': driverPhone,
      'vehicleModel': vehicleModel,
      'vehiclePlate': vehiclePlate,
      'vehicleInfo': vehicleInfo,
      'passengerName': passengerName,
      'rideType': rideType,
      'actualPrice': actualPrice,
    };
  }

  /// Convert a string to a RideStatus enum
  static RideStatus _statusFromString(String statusStr) {
    switch (statusStr.toUpperCase()) {
      case 'SCHEDULED':
        return RideStatus.scheduled;
      case 'EN_ROUTE':
      case 'ENROUTE':
        return RideStatus.enRoute;
      case 'ARRIVED':
        return RideStatus.arrived;
      case 'IN_PROGRESS':
        return RideStatus.inProgress;
      case 'COMPLETED':
        return RideStatus.completed;
      case 'CANCELLED':
        return RideStatus.cancelled;
      default:
        return RideStatus.scheduled;
    }
  }

  /// Format the date as a string
  String formatDate(String pattern) {
    final months = [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December'
    ];

    switch (pattern) {
      case 'MMM d, yyyy':
        return '${months[dateTime.month - 1].substring(0, 3)} ${dateTime.day}, ${dateTime.year}';
      case 'h:mm a':
        final hour = dateTime.hour > 12 ? dateTime.hour - 12 : dateTime.hour;
        final period = dateTime.hour >= 12 ? 'PM' : 'AM';
        return '${hour == 0 ? 12 : hour}:${dateTime.minute.toString().padLeft(2, '0')} $period';
      default:
        return dateTime.toString();
    }
  }
}
