import 'ride.dart';
import 'ride_status.dart';
import 'ride_type.dart';

/// Sample data for the app
class SampleData {
  /// Get sample upcoming rides
  static List<Ride> get upcomingRides {
    return [
      Ride(
        id: 'R1001',
        userId: 'sample_user',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '456 Hospital Ave, Medville',
        dateTime: DateTime(2025, 3, 25, 9, 30),
        status: RideStatus.scheduled,
        fare: 25.0,
        distance: 12.5,
        duration: 25,
        driverName: 'Michael Johnson',
        vehicleInfo: 'Toyota Camry - White (ABC123)',
        passengerName: 'Yosef Schmukler',
        rideType: RideType.ambulatory.name,
      ),
      Ride(
        id: 'R1002',
        userId: 'sample_user',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '789 Clinic Rd, Healtown',
        dateTime: DateTime(2025, 3, 30, 14, 15),
        status: RideStatus.scheduled,
        fare: 30.0,
        distance: 15.0,
        duration: 35,
        passengerName: 'Yosef Schmukler',
        rideType: RideType.ambulatory.name,
      ),
    ];
  }

  /// Get sample past rides
  static List<Ride> get pastRides {
    return [
      Ride(
        id: 'R1000',
        userId: 'sample_user',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '456 Hospital Ave, Medville',
        dateTime: DateTime(2025, 3, 10, 10, 0),
        status: RideStatus.completed,
        fare: 26.50,
        distance: 10.0,
        duration: 22,
        driverName: 'Sarah Williams',
        vehicleInfo: 'Honda Accord - Silver (XYZ789)',
        actualPrice: 28.50,
        passengerName: 'Yosef Schmukler',
        rideType: RideType.ambulatory.name,
      ),
      Ride(
        id: 'R999',
        userId: 'sample_user',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '321 Therapy Center, Welltown',
        dateTime: DateTime(2025, 3, 5, 13, 45),
        status: RideStatus.completed,
        fare: 30.00,
        distance: 14.5,
        duration: 30,
        driverName: 'Robert Davis',
        vehicleInfo: 'Hyundai Sonata - Black (DEF456)',
        actualPrice: 32.75,
        passengerName: 'Yosef Schmukler',
        rideType: RideType.ambulatory.name,
      ),
      Ride(
        id: 'R998',
        userId: 'sample_user',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '987 Medical Building, Careville',
        dateTime: DateTime(2025, 2, 28, 16, 30),
        status: RideStatus.cancelled,
        fare: 35.00,
        distance: 18.0,
        duration: 40,
        passengerName: 'Yosef Schmukler',
        rideType: RideType.ambulatory.name,
      ),
    ];
  }
}
