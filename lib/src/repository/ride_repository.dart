import '../models/ride.dart';

/// Repository interface for ride-related operations
abstract class RideRepository {
  /// Schedules a new ride
  Future<Ride> scheduleRide(Ride ride);

  /// Schedules a round-trip (two separate rides)
  Future<Ride> scheduleRoundTrip({
    required DateTime pickup,
    required DateTime dropoff,
    required String pickupAddress,
    required String destinationAddress,
    required String notes,
  });

  /// Gets a ride by id
  Future<Ride> getRide(String rideId);

  /// Cancels a ride
  Future<bool> cancelRide(String rideId);

  /// Updates details of an existing ride
  Future<Ride> updateRideDetails(Ride ride);

  /// Gets all upcoming rides
  Future<List<Ride>> getUpcomingRides();

  /// Gets all past rides
  Future<List<Ride>> getPastRides();

  /// Searches rides by date range
  Future<List<Ride>> searchRidesByDate(DateTime startDate, DateTime endDate);

  /// Observes changes to upcoming rides
  Stream<List<Ride>> observeUpcomingRides();

  /// Observes changes to a specific ride
  Stream<Ride?> observeRide(String rideId);
}
