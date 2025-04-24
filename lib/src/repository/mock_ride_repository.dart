import 'dart:async';
import 'dart:math';

import '../models/ride.dart';
import '../models/ride_status.dart';
import 'ride_repository.dart';

/// Mock implementation of [RideRepository] for development and testing
class MockRideRepository implements RideRepository {
  final List<Ride> _rides = [];
  final _upcomingRidesController = StreamController<List<Ride>>.broadcast();
  final Map<String, StreamController<Ride?>> _rideControllers = {};

  MockRideRepository() {
    _initializeMockData();
  }

  void _initializeMockData() {
    final now = DateTime.now();

    // Add some past rides
    _rides.addAll([
      Ride(
        id: '1',
        pickupAddress: '123 Main St, New York, NY',
        destinationAddress: '456 Park Ave, New York, NY',
        dateTime: now.subtract(const Duration(days: 5)),
        status: RideStatus.completed,
      ),
      Ride(
        id: '2',
        pickupAddress: '789 Broadway, New York, NY',
        destinationAddress: '101 Fifth Ave, New York, NY',
        dateTime: now.subtract(const Duration(days: 2)),
        status: RideStatus.completed,
      ),
    ]);

    // Add some upcoming rides
    _rides.addAll([
      Ride(
        id: '3',
        pickupAddress: '222 Main St, Brooklyn, NY',
        destinationAddress: '333 Park Ave, Brooklyn, NY',
        dateTime: now.add(const Duration(days: 1)),
        status: RideStatus.scheduled,
      ),
      Ride(
        id: '4',
        pickupAddress: '444 Broadway, Queens, NY',
        destinationAddress: '555 Fifth Ave, Queens, NY',
        dateTime: now.add(const Duration(days: 3)),
        status: RideStatus.scheduled,
      ),
    ]);

    // Update the stream
    _notifyUpcomingRidesListeners();
  }

  @override
  Future<Ride> scheduleRide(Ride ride) async {
    await Future.delayed(const Duration(milliseconds: 800));

    final newRide = ride.copyWith(
      id: _generateId(),
      status: RideStatus.scheduled,
    );

    _rides.add(newRide);
    _notifyUpcomingRidesListeners();
    _notifyRideListeners(newRide.id, newRide);

    return newRide;
  }

  @override
  Future<Ride> scheduleRoundTrip({
    required DateTime pickup,
    required DateTime dropoff,
    required String pickupAddress,
    required String destinationAddress,
    required String notes,
  }) async {
    await Future.delayed(const Duration(milliseconds: 800));

    // Create outbound trip
    final outboundRide = Ride(
      id: _generateId(),
      pickupAddress: pickupAddress,
      destinationAddress: destinationAddress,
      dateTime: pickup,
      status: RideStatus.scheduled,
    );

    // Create return trip
    final returnRide = Ride(
      id: _generateId(),
      pickupAddress: destinationAddress,
      destinationAddress: pickupAddress,
      dateTime: dropoff,
      status: RideStatus.scheduled,
    );

    _rides.addAll([outboundRide, returnRide]);
    _notifyUpcomingRidesListeners();
    _notifyRideListeners(outboundRide.id, outboundRide);
    _notifyRideListeners(returnRide.id, returnRide);

    return outboundRide;
  }

  @override
  Future<Ride> getRide(String rideId) async {
    await Future.delayed(const Duration(milliseconds: 300));

    final ride = _rides.firstWhere(
      (ride) => ride.id == rideId,
      orElse: () => throw Exception('Ride not found'),
    );

    return ride;
  }

  @override
  Future<bool> cancelRide(String rideId) async {
    await Future.delayed(const Duration(milliseconds: 500));

    final index = _rides.indexWhere((ride) => ride.id == rideId);
    if (index == -1) return false;

    final updatedRide = _rides[index].copyWith(status: RideStatus.cancelled);
    _rides[index] = updatedRide;

    _notifyUpcomingRidesListeners();
    _notifyRideListeners(rideId, updatedRide);

    return true;
  }

  @override
  Future<Ride> updateRideDetails(Ride ride) async {
    await Future.delayed(const Duration(milliseconds: 500));

    final index = _rides.indexWhere((r) => r.id == ride.id);
    if (index == -1) throw Exception('Ride not found');

    _rides[index] = ride;
    _notifyUpcomingRidesListeners();
    _notifyRideListeners(ride.id, ride);

    return ride;
  }

  @override
  Future<List<Ride>> getUpcomingRides() async {
    await Future.delayed(const Duration(milliseconds: 300));
    final now = DateTime.now();
    return _rides
        .where((ride) =>
            ride.dateTime.isAfter(now) && ride.status != RideStatus.cancelled)
        .toList();
  }

  @override
  Future<List<Ride>> getPastRides() async {
    await Future.delayed(const Duration(milliseconds: 300));
    final now = DateTime.now();
    return _rides
        .where((ride) =>
            ride.dateTime.isBefore(now) ||
            ride.status == RideStatus.completed ||
            ride.status == RideStatus.cancelled)
        .toList();
  }

  @override
  Future<List<Ride>> searchRidesByDate(
      DateTime startDate, DateTime endDate) async {
    await Future.delayed(const Duration(milliseconds: 300));

    return _rides
        .where((ride) =>
            ride.dateTime.isAfter(startDate) && ride.dateTime.isBefore(endDate))
        .toList();
  }

  @override
  Stream<List<Ride>> observeUpcomingRides() {
    _refreshUpcomingRides();
    return _upcomingRidesController.stream;
  }

  @override
  Stream<Ride?> observeRide(String rideId) {
    if (!_rideControllers.containsKey(rideId)) {
      _rideControllers[rideId] = StreamController<Ride?>.broadcast();

      // Initialize with current value if it exists
      final ride = _rides.firstWhere(
        (ride) => ride.id == rideId,
        orElse: () => null as Ride,
      );

      if (ride != null) {
        _rideControllers[rideId]!.add(ride);
      }
    }

    return _rideControllers[rideId]!.stream;
  }

  // Helper methods
  void _notifyUpcomingRidesListeners() {
    _refreshUpcomingRides();
  }

  void _refreshUpcomingRides() async {
    final upcomingRides = await getUpcomingRides();
    _upcomingRidesController.add(upcomingRides);
  }

  void _notifyRideListeners(String rideId, Ride ride) {
    if (_rideControllers.containsKey(rideId)) {
      _rideControllers[rideId]!.add(ride);
    }
  }

  String _generateId() {
    final random = Random();
    return (random.nextInt(900) + 100).toString() +
        DateTime.now().millisecondsSinceEpoch.toString();
  }

  void dispose() {
    _upcomingRidesController.close();
    for (final controller in _rideControllers.values) {
      controller.close();
    }
  }
}
