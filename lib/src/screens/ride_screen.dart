import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import '../models/ride.dart';
import '../models/ride_status.dart';
import '../widgets/ride_card.dart';
import '../widgets/components/modern_card.dart';
import 'package:intl/intl.dart';

/// Screen that displays a list of upcoming and past rides
class RideScreen extends StatefulWidget {
  /// Creates a ride screen
  const RideScreen({super.key});

  @override
  State<RideScreen> createState() => _RideScreenState();
}

class _RideScreenState extends State<RideScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  bool _showContent = false;

  // Mock data for testing
  List<Ride> upcomingRides = [];
  List<Ride> pastRides = [];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _loadRideData();

    // Start animations
    Future.delayed(const Duration(milliseconds: 300), () {
      if (mounted) {
        setState(() {
          _showContent = true;
        });
      }
    });
  }

  void _loadRideData() {
    // Mock data
    upcomingRides = [
      Ride(
        id: 'ride1',
        userId: 'test_id',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '456 Hospital Ave, Medville',
        dateTime: DateTime.now().add(const Duration(days: 1)),
        status: RideStatus.scheduled,
        fare: 25.0,
        distance: 12.5,
        duration: 25,
        passengerName: 'John Doe',
        rideType: 'AMBULATORY',
        driverName: 'Michael Johnson',
        vehicleInfo: 'Toyota Camry - White (ABC123)',
      ),
      Ride(
        id: 'ride2',
        userId: 'test_id',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '789 Clinic Rd, Healtown',
        dateTime: DateTime.now().add(const Duration(days: 5)),
        status: RideStatus.scheduled,
        fare: 30.0,
        distance: 15.0,
        duration: 35,
        passengerName: 'John Doe',
        rideType: 'AMBULATORY',
      ),
    ];

    pastRides = [
      Ride(
        id: 'ride3',
        userId: 'test_id',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '456 Hospital Ave, Medville',
        dateTime: DateTime.now().subtract(const Duration(days: 1)),
        status: RideStatus.completed,
        fare: 28.0,
        distance: 13.0,
        duration: 30,
        passengerName: 'John Doe',
        rideType: 'AMBULATORY',
        driverName: 'Sarah Williams',
        vehicleInfo: 'Honda Accord - Silver (XYZ789)',
        actualPrice: 32.75,
      ),
      Ride(
        id: 'ride4',
        userId: 'test_id',
        pickupAddress: '123 Home St, Anytown',
        destinationAddress: '321 Therapy Center, Welltown',
        dateTime: DateTime.now().subtract(const Duration(days: 5)),
        status: RideStatus.cancelled,
        fare: 35.0,
        distance: 18.0,
        duration: 40,
        passengerName: 'John Doe',
        rideType: 'AMBULATORY',
      ),
    ];
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'My Rides',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w600,
            color: AppColor.textDarkBlue,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: const IconThemeData(color: AppColor.textDarkBlue),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            // Navigate back to the modern home screen
            Navigator.of(context).pushReplacementNamed('/home');
          },
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_alt_outlined),
            onPressed: () {
              // Filter functionality
            },
          ),
        ],
      ),
      extendBodyBehindAppBar: true,
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Color(0xFFF8FAFC),
              Color(0xFFF1F5FD),
            ],
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              // Custom Tabs
              _buildCustomTabs(),

              // Ride list
              Expanded(
                child: AnimatedOpacity(
                  opacity: _showContent ? 1.0 : 0.0,
                  duration: const Duration(milliseconds: 500),
                  curve: Curves.easeInOut,
                  child: AnimatedSlide(
                    offset: _showContent ? Offset.zero : const Offset(0, 0.05),
                    duration: const Duration(milliseconds: 500),
                    curve: Curves.easeOutCubic,
                    child: TabBarView(
                      controller: _tabController,
                      children: [
                        // Upcoming rides tab
                        _buildRideList(upcomingRides, "No upcoming rides"),

                        // Past rides tab
                        _buildRideList(pastRides, "No past rides"),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCustomTabs() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
      child: Container(
        height: 48,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(50),
          color: Colors.white,
          border: Border.all(color: Colors.grey.shade300),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 4,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: TabBar(
          controller: _tabController,
          indicator: BoxDecoration(
            borderRadius: BorderRadius.circular(50),
            gradient: const LinearGradient(
              colors: [
                AppColor.primaryBlue,
                AppColor.primaryPurple,
              ],
            ),
          ),
          // Make the tab indicator take up full height of the tab bar
          indicatorSize: TabBarIndicatorSize.tab,
          // No padding around the tab indicator
          padding: EdgeInsets.zero,
          labelPadding: EdgeInsets.zero,
          // Use a custom divider decoration
          dividerColor: Colors.transparent,
          // Text styling
          labelColor: Colors.white,
          unselectedLabelColor: AppColor.textDarkBlue,
          labelStyle: const TextStyle(
            fontWeight: FontWeight.w600,
            fontSize: 16,
          ),
          unselectedLabelStyle: const TextStyle(
            fontWeight: FontWeight.w400,
            fontSize: 16,
          ),
          tabs: const [
            Tab(
              text: 'Upcoming',
              height: 48,
            ),
            Tab(
              text: 'Past',
              height: 48,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRideList(List<Ride> rides, String emptyMessage) {
    if (rides.isEmpty) {
      return _buildEmptyState(emptyMessage);
    } else {
      return ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: rides.length,
        itemBuilder: (context, index) {
          final ride = rides[index];

          // Use a TweenAnimationBuilder to create a staggered animation effect
          return TweenAnimationBuilder<double>(
            tween: Tween(begin: 0.0, end: 1.0),
            duration: Duration(milliseconds: 300 + (index * 100)),
            builder: (context, value, child) {
              return Opacity(
                opacity: value,
                child: Transform.translate(
                  offset: Offset(0, 20 * (1 - value)),
                  child: child,
                ),
              );
            },
            child: Padding(
              padding: const EdgeInsets.only(bottom: 16.0),
              child: EnhancedRideCard(
                ride: ride,
                onTap: () {
                  // Navigate to ride details
                  Navigator.pushNamed(context, '/ride-details',
                      arguments: ride);
                },
              ),
            ),
          );
        },
      );
    }
  }

  Widget _buildEmptyState(String message) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildEmptyStateIllustration(),
          const SizedBox(height: 24),
          Text(
            message,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w500,
              color: AppColor.textDarkBlue,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Your rides will appear here once scheduled',
            style: TextStyle(
              fontSize: 14,
              color: AppColor.textMediumGray,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: () {
              // Book a ride
            },
            icon: const Icon(Icons.add, size: 20),
            label: const Text(
              'Book a Ride',
              style: TextStyle(fontWeight: FontWeight.w500),
            ),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColor.primaryBlue,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(50),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyStateIllustration() {
    return SizedBox(
      height: 180,
      width: 180,
      child: Stack(
        alignment: Alignment.center,
        children: [
          // Background circle
          Container(
            width: 160,
            height: 160,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: AppColor.primaryPurple.withOpacity(0.1),
            ),
          ),

          // Simple car illustration
          TweenAnimationBuilder<double>(
            tween: Tween(begin: 0.8, end: 1.1),
            duration: const Duration(milliseconds: 1500),
            curve: Curves.easeInOut,
            builder: (context, value, child) {
              return Transform.scale(
                scale: value,
                child: child,
              );
            },
            child: const Icon(
              Icons.directions_car_outlined,
              size: 80,
              color: AppColor.primaryBlue,
            ),
          ),
        ],
      ),
    );
  }
}

/// Enhanced version of the ride card that more closely matches the Kotlin implementation
class EnhancedRideCard extends StatelessWidget {
  /// The ride to display
  final Ride ride;

  /// Callback when the card is tapped
  final VoidCallback onTap;

  /// Creates an enhanced ride card
  const EnhancedRideCard({
    super.key,
    required this.ride,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ModernCard(
      onTap: onTap,
      elevation: 2,
      borderRadius: 24,
      padding: const EdgeInsets.all(20),
      child: Column(
        children: [
          // Top section with date, time and status
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Date with icon
              _buildIconPill(
                icon: Icons.calendar_today_outlined,
                iconColor: AppColor.primaryBlue,
                backgroundColor: AppColor.primaryBlue.withOpacity(0.1),
                text: _formatDate(ride.dateTime),
              ),

              // Time with icon
              _buildIconPill(
                icon: Icons.schedule_outlined,
                iconColor: AppColor.primaryPurple,
                backgroundColor: AppColor.primaryPurple.withOpacity(0.1),
                text: _formatTime(ride.dateTime),
              ),

              // Status badge
              StatusBadge(status: ride.status),
            ],
          ),

          const SizedBox(height: 16),

          // Divider with gradient
          Container(
            height: 1,
            width: double.infinity,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  AppColor.primaryBlue.withOpacity(0.1),
                  AppColor.primaryPurple.withOpacity(0.1),
                  AppColor.primaryBlue.withOpacity(0.1),
                ],
              ),
            ),
          ),

          const SizedBox(height: 16),

          // Route information with visual connector
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Source and destination icons with connector
              Column(
                children: [
                  // Source dot
                  Container(
                    width: 12,
                    height: 12,
                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      color: AppColor.primaryBlue,
                    ),
                  ),

                  // Connector line
                  Container(
                    width: 2,
                    height: 40,
                    decoration: const BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                        colors: [
                          AppColor.primaryBlue,
                          AppColor.primaryPurple,
                        ],
                      ),
                    ),
                  ),

                  // Destination dot
                  Container(
                    width: 12,
                    height: 12,
                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      color: AppColor.primaryPurple,
                    ),
                  ),
                ],
              ),

              const SizedBox(width: 16),

              // Addresses
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Pickup address
                    Text(
                      ride.pickupAddress,
                      style: const TextStyle(
                        fontSize: 14,
                        color: AppColor.textDarkBlue,
                      ),
                    ),

                    const SizedBox(height: 40),

                    // Destination address
                    Text(
                      ride.destinationAddress,
                      style: const TextStyle(
                        fontSize: 14,
                        color: AppColor.textDarkBlue,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),

          // Actions section for upcoming rides
          if (ride.status == RideStatus.scheduled) ...[
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton.icon(
                  onPressed: () {
                    // Cancel ride
                  },
                  icon: const Icon(
                    Icons.close,
                    size: 16,
                    color: AppColor.errorRed,
                  ),
                  label: const Text(
                    'Cancel',
                    style: TextStyle(
                      fontWeight: FontWeight.w500,
                      color: AppColor.errorRed,
                    ),
                  ),
                ),
                TextButton.icon(
                  onPressed: onTap,
                  label: const Text(
                    'View Details',
                    style: TextStyle(
                      fontWeight: FontWeight.w500,
                      color: AppColor.primaryBlue,
                    ),
                  ),
                  icon: const Icon(
                    Icons.arrow_forward,
                    size: 16,
                    color: AppColor.primaryBlue,
                  ),
                ),
              ],
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildIconPill({
    required IconData icon,
    required Color iconColor,
    required Color backgroundColor,
    required String text,
  }) {
    return Row(
      children: [
        Container(
          width: 32,
          height: 32,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: backgroundColor,
          ),
          child: Icon(
            icon,
            size: 16,
            color: iconColor,
          ),
        ),
        const SizedBox(width: 8),
        Text(
          text,
          style: const TextStyle(
            fontWeight: FontWeight.w500,
            fontSize: 14,
            color: AppColor.textDarkBlue,
          ),
        ),
      ],
    );
  }

  String _formatDate(DateTime dateTime) {
    final formatter = DateFormat('EEE, MMM d');
    return formatter.format(dateTime);
  }

  String _formatTime(DateTime dateTime) {
    final formatter = DateFormat('h:mm a');
    return formatter.format(dateTime);
  }
}
