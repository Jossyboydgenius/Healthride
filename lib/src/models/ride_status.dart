/// The status of a ride
enum RideStatus {
  /// Ride has been scheduled but hasn't started yet
  scheduled,

  /// Driver is en route to pickup the passenger
  enRoute,

  /// Driver has arrived at the pickup location
  arrived,

  /// Passenger has been picked up and ride is in progress
  inProgress,

  /// Ride has been completed
  completed,

  /// Ride has been cancelled
  cancelled;

  /// Get the display name of the status
  String get displayName {
    switch (this) {
      case RideStatus.scheduled:
        return 'Scheduled';
      case RideStatus.enRoute:
        return 'En Route';
      case RideStatus.arrived:
        return 'Arrived';
      case RideStatus.inProgress:
        return 'In Progress';
      case RideStatus.completed:
        return 'Completed';
      case RideStatus.cancelled:
        return 'Cancelled';
    }
  }
}
