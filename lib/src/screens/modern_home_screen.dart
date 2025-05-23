import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:location/location.dart';
import 'package:provider/provider.dart';
import '../constants/app_color.dart';
import '../models/ride_type.dart';
import '../models/user.dart';
import '../repository/auth_repository.dart';
import '../repository/ride_repository.dart';
import '../routes/app_routes.dart';
import '../widgets/components/gradient_button.dart';
import '../widgets/components/modern_card.dart';
import '../widgets/components/bottom_navigation.dart';
import '../widgets/home/address_search_bar.dart';
import '../widgets/home/route_info_summary.dart';
import '../widgets/home/health_ride_map.dart';
import '../widgets/animations/loading_animation.dart';
import '../models/appointment_type.dart';
import '../screens/ride_screen.dart';
import '../screens/notifications_screen.dart';
import '../screens/profile_screen.dart';
import '../widgets/home/booking_modal.dart';
import '../widgets/home/dotted_line_painter.dart';

class ModernHomeScreen extends StatefulWidget {
  const ModernHomeScreen({super.key});

  @override
  State<ModernHomeScreen> createState() => _ModernHomeScreenState();
}

class _ModernHomeScreenState extends State<ModernHomeScreen> {
  // Map and location state
  final Location _location = Location();
  LatLng? _currentLocation;
  LatLng? _pickupLocation;
  LatLng? _dropoffLocation;
  final List<LatLng> _routePoints = [];
  bool _isLoadingLocation = false;

  // Address search state
  final TextEditingController _pickupController = TextEditingController();
  final TextEditingController _destinationController = TextEditingController();
  bool _showRecentPlaces = true;
  bool _isAddressSearchExpanded = false;

  // Booking state
  bool _showBookingModal = false;
  bool _showRoutePreview = false;
  int _currentBookingStep = 0;
  bool _isBookingLoading = false;
  bool _showBookingSuccessDialog = false;
  bool _isShowingAppointmentDropdown = false;

  // Booking details
  RideType _selectedRideType = RideType.ambulatory;
  DateTime _selectedDate = DateTime.now();
  String? _selectedTime = "9:00 AM";
  String _additionalNotes = "";
  bool _isRoundTrip = false;
  bool _hasCompanion = false;
  DateTime _returnDate = DateTime.now();
  String? _returnTime = "5:00 PM";

  // Route information (in a real app, these would be calculated)
  double _distance = 8.5;
  int _duration = 25;
  final double _estimatedPrice = 32.50;

  // User state
  User? _currentUser;
  bool _isLoadingUser = false;
  late final AuthRepository _authRepository;
  late final RideRepository _rideRepository;

  // Mock data for next appointment
  final Map<String, dynamic> _nextAppointment = {
    'date': DateTime(DateTime.now().year, 3, 25, 9, 30),
    'location': '456 Hospital Ave, Medville',
  };

  // Mock data for recent places
  final List<Map<String, dynamic>> _recentPlaces = [
    {
      'name': 'Memorial Hospital',
      'timeAgo': '3 days ago',
      'icon': Icons.local_hospital,
    },
    {
      'name': 'City Health Clinic',
      'timeAgo': 'Last week',
      'icon': Icons.healing,
    },
    {
      'name': 'Downtown Medical Center',
      'timeAgo': '2 weeks ago',
      'icon': Icons.medical_services,
    },
  ];

  // Bottom navigation
  String _currentRoute = 'home';

  // Add appointment type to the booking state
  AppointmentType? _selectedAppointmentType;

  @override
  void initState() {
    super.initState();
    _requestLocationPermission();
    // Prefill with current address
    _pickupController.text = "374 Scranton Ave";
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Get the repositories from the provider
    _authRepository = Provider.of<AuthRepository>(context, listen: false);
    _rideRepository = Provider.of<RideRepository>(context, listen: false);
    _loadCurrentUser();
  }

  @override
  void dispose() {
    _pickupController.dispose();
    _destinationController.dispose();
    super.dispose();
  }

  Future<void> _requestLocationPermission() async {
    final hasPermission = await _location.hasPermission();
    if (hasPermission == PermissionStatus.denied) {
      await _location.requestPermission();
    }
    _getCurrentLocation();
  }

  Future<void> _getCurrentLocation() async {
    setState(() => _isLoadingLocation = true);
    try {
      final locationData = await _location.getLocation();
      setState(() {
        _currentLocation = LatLng(
          locationData.latitude!,
          locationData.longitude!,
        );
      });
    } catch (e) {
      debugPrint('Error getting location: $e');
    } finally {
      setState(() => _isLoadingLocation = false);
    }
  }

  Future<void> _loadCurrentUser() async {
    setState(() => _isLoadingUser = true);
    try {
      _currentUser = await _authRepository.getCurrentUser();
    } catch (e) {
      debugPrint('Error loading user: $e');
      // If we can't load the user, they might not be authenticated
      // Navigate back to login
      if (mounted) {
        Navigator.of(context).pushReplacementNamed(AppRoutes.login);
      }
    } finally {
      if (mounted) {
        setState(() => _isLoadingUser = false);
      }
    }
  }

  void _onMapLoaded() {
    if (_currentLocation == null) {
      _getCurrentLocation();
    }
  }

  void _resetBooking() {
    setState(() {
      _showBookingModal = false;
      _showRoutePreview = false;
      _showRecentPlaces = true;
      _currentBookingStep = 0;
    });
  }

  String _getGreeting() {
    final hour = DateTime.now().hour;
    if (hour < 12) {
      return 'Good morning';
    } else if (hour < 17) {
      return 'Good afternoon';
    } else {
      return 'Good evening';
    }
  }

  // Get user's name with null safety
  String _getUserName() {
    return 'Yosef'; // Hardcode to "Yosef" instead of using _currentUser?.firstName
  }

  void _handleBottomNavTap(String route) {
    if (route != _currentRoute) {
      setState(() {
        _currentRoute = route;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingUser) {
      return const Scaffold(
        body: Center(
          child: LoadingAnimation(),
        ),
      );
    }

    return Scaffold(
      body: Stack(
        children: [
          // Map background
          HealthRideMap(
            currentLocation: _currentLocation,
            pickupLocation: _pickupLocation,
            dropoffLocation: _dropoffLocation,
            routePoints: _routePoints,
            onMapLoaded: _onMapLoaded,
            onMyLocationClick: _getCurrentLocation,
          ),

          // Content based on selected nav route
          if (_currentRoute == 'home')
            _buildHomeContent()
          else if (_currentRoute == 'rides')
            _buildRidesContent()
          else if (_currentRoute == 'notifications')
            _buildAlertsContent()
          else if (_currentRoute == 'profile')
            _buildProfileContent(),

          // Loading Indicator
          if (_isLoadingLocation)
            const Center(
              child: LoadingAnimation(),
            ),

          // Booking Modal
          if (_showBookingModal) _buildBookingModal(),

          // Route Preview
          if (_showRoutePreview) _buildRoutePreview(),
        ],
      ),
      bottomNavigationBar: HealthRideBottomNavigation(
        currentRoute: _currentRoute,
        onNavigate: _handleBottomNavTap,
      ),
    );
  }

  Widget _buildHomeContent() {
    // Early return for loading state
    if (_isLoadingUser) {
      return const Center(
        child: LoadingAnimation(),
      );
    }

    // Check if user is null after loading
    if (_currentUser == null) {
      // Handle case where user is null but loading is complete
      WidgetsBinding.instance.addPostFrameCallback((_) {
        Navigator.of(context).pushReplacementNamed(AppRoutes.login);
      });
      return const Center(
        child: Text('Redirecting to login...'),
      );
    }

    return SafeArea(
      child: Column(
        children: [
          // User greeting and next appointment - hide when search is expanded
          if (!_isAddressSearchExpanded)
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: ModernCard(
                elevation: 8.0,
                borderRadius: 24.0,
                padding: EdgeInsets.zero,
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: 24.0, vertical: 16.0),
                  child: Row(
                    children: [
                      // Greeting and name
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              _getGreeting(),
                              style: const TextStyle(
                                fontSize: 16,
                                color: AppColor.primaryBlue,
                                fontWeight: FontWeight.w500,
                                fontFamily: 'Poppins',
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              _getUserName(),
                              style: const TextStyle(
                                fontSize: 32,
                                fontWeight: FontWeight.bold,
                                color: AppColor.textDarkBlue,
                                fontFamily: 'Poppins',
                              ),
                            ),
                          ],
                        ),
                      ),

                      // Vertical divider
                      Container(
                        height: 60,
                        width: 2,
                        color: Colors.grey.withOpacity(0.2),
                        margin: const EdgeInsets.only(left: 6.0, right: 16.0),
                      ),

                      // Next appointment
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Row(
                              children: [
                                Icon(
                                  Icons.calendar_today,
                                  size: 16,
                                  color: AppColor.textMediumGray,
                                ),
                                SizedBox(width: 4),
                                Text(
                                  'Next Appointment',
                                  style: TextStyle(
                                    fontSize: 14,
                                    color: AppColor.textMediumGray,
                                    fontFamily: 'Poppins',
                                  ),
                                  maxLines: 1,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ],
                            ),
                            const SizedBox(height: 4),
                            const Text(
                              'Tue, Mar 25 • 9:30 am',
                              style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.bold,
                                color: AppColor.primaryPurple,
                                fontFamily: 'Poppins',
                              ),
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                            Text(
                              _nextAppointment['location'],
                              style: const TextStyle(
                                fontSize: 14,
                                color: AppColor.textMediumGray,
                                fontFamily: 'Poppins',
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),

          // Search Bar - adjust padding when expanded
          Padding(
            padding: _isAddressSearchExpanded
                ? const EdgeInsets.fromLTRB(
                    16.0, 24.0, 16.0, 8.0) // More top padding when expanded
                : const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
            child: AddressSearchBar(
              controller: _pickupController,
              destinationController: _destinationController,
              placeholder: 'Where would you like to go?',
              icon: Icons.search,
              iconColor: AppColor.primaryPurple,
              onTap: () {
                // Update state immediately on tap - before the address bar itself handles expansion
                setState(() {
                  _showRecentPlaces = false;
                  _isAddressSearchExpanded = true;
                });
                // No need to call any other methods since the AddressSearchBar will handle its own expansion
              },
              onChanged: (text) {
                // When searching, hide recent places
                if (text.isNotEmpty) {
                  setState(() {
                    _showRecentPlaces = false;
                  });
                }
              },
              // Add handlers for when the field is collapsed
              onFieldSubmitted: () {
                setState(() {
                  _isAddressSearchExpanded = false;
                  _showRecentPlaces = true;
                });
              },
              onSuggestionSelected: (suggestion) {
                setState(() {
                  _dropoffLocation = null; // Reset this if needed
                  _isAddressSearchExpanded = false;
                  _showRecentPlaces = true;
                });
              },
            ),
          ),

          // Recent Places Section
          if (_showRecentPlaces &&
              !_showBookingModal &&
              !_showRoutePreview &&
              !_isAddressSearchExpanded)
            Container(
              padding: const EdgeInsets.all(16.0),
              constraints:
                  const BoxConstraints(maxHeight: 340), // Limit maximum height
              child: ModernCard(
                elevation: 8.0,
                borderRadius: 24.0,
                padding: EdgeInsets.zero, // Remove default padding
                child: Column(
                  mainAxisSize: MainAxisSize.min, // Ensure minimum height
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Recent Places Header
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 16.0, vertical: 12.0), // Reduced padding
                      child: Row(
                        children: [
                          Container(
                            padding: const EdgeInsets.all(8.0),
                            decoration: const BoxDecoration(
                              color: Color(0xFFE6EFFF),
                              shape: BoxShape.circle,
                            ),
                            child: const Icon(
                              Icons.history,
                              color: AppColor.primaryBlue,
                              size: 20,
                            ),
                          ),
                          const SizedBox(width: 12),
                          const Text(
                            'Recent Places',
                            style: TextStyle(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                              color: AppColor.textDarkBlue,
                              fontFamily: 'Poppins',
                            ),
                          ),
                          const Spacer(),
                          TextButton(
                            onPressed: () {},
                            style: TextButton.styleFrom(
                              padding: EdgeInsets.zero,
                              tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                            ),
                            child: const Text(
                              'View All',
                              style: TextStyle(
                                color: AppColor.primaryBlue,
                                fontWeight: FontWeight.w500,
                                fontFamily: 'Poppins',
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),

                    // No divider below header

                    // Direct list of places with tighter spacing
                    for (int index = 0;
                        index < _recentPlaces.length;
                        index++) ...[
                      _buildRecentPlaceItem(index),
                      // Add divider after each item except the last
                      if (index < _recentPlaces.length - 1)
                        const Divider(
                          height: 1,
                          thickness: 1,
                          indent: 16, // Start from left edge
                          endIndent: 16, // Don't reach right edge
                          color: Color(0xFFF0F0FF),
                        ),
                    ],
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildRidesContent() {
    return const RideScreen();
  }

  Widget _buildAlertsContent() {
    return const NotificationsScreen();
  }

  Widget _buildProfileContent() {
    return ProfileScreen(user: _currentUser);
  }

  Widget _buildBookingModal() {
    return BookingModal(
      currentStep: _currentBookingStep,
      pickupAddress: _pickupController.text,
      destinationAddress: _destinationController.text,
      selectedAppointmentType: _selectedAppointmentType,
      selectedRideType: _selectedRideType,
      selectedDate: _selectedDate,
      selectedTime: _selectedTime,
      distance: _distance,
      duration: _duration,
      estimatedPrice: _estimatedPrice,
      additionalNotes: _additionalNotes,
      isRoundTrip: _isRoundTrip,
      hasCompanion: _hasCompanion,
      returnDate: _returnDate,
      returnTime: _returnTime,
      isLoading: _isBookingLoading,
      showAppointmentDropdown: _isShowingAppointmentDropdown,
      pickupLocation: _pickupLocation,
      dropoffLocation: _dropoffLocation,
      routePoints: _routePoints,
      onBackPressed: () {
        setState(() {
          _currentBookingStep--;
        });
      },
      onNextPressed: _handleNextStep,
      onClose: () {
        setState(() {
          _showBookingModal = false;
          _currentBookingStep = 0;
          _pickupController.clear();
          _destinationController.clear();
          _selectedAppointmentType = null;
        });
      },
      onRideTypeSelected: (type) {
        setState(() {
          _selectedRideType = type;
        });
      },
      onAppointmentTypeSelect: (type) {
        setState(() {
          _selectedAppointmentType = AppointmentType.values.firstWhere(
            (t) => t.name == type,
            orElse: () => AppointmentType.medical,
          );
        });
      },
      onShowAppointmentDropdown: (show) {
        setState(() {
          _isShowingAppointmentDropdown = show;
        });
      },
      onDateSelected: (date) {
        setState(() {
          _selectedDate = date;
        });
      },
      onTimeSelected: (time) {
        setState(() {
          _selectedTime = time;
        });
      },
      onNotesChanged: (notes) {
        setState(() {
          _additionalNotes = notes;
        });
      },
      onRoundTripChanged: (value) {
        setState(() {
          _isRoundTrip = value;
        });
      },
      onCompanionChanged: (value) {
        setState(() {
          _hasCompanion = value;
        });
      },
      onReturnDateSelected: (date) {
        setState(() {
          _returnDate = date;
        });
      },
      onReturnTimeSelected: (time) {
        setState(() {
          _returnTime = time;
        });
      },
      onDragUpdate: (progress) {
        // Handle drag updates if needed
      },
    );
  }

  Widget _buildRoutePreview() {
    return Positioned(
      bottom: 0,
      left: 0,
      right: 0,
      child: Container(
        padding: const EdgeInsets.all(24),
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius:
              BorderRadius.all(Radius.circular(28)), // All corners rounded
          border: Border.fromBorderSide(BorderSide(color: Colors.transparent)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header with close button
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Route Preview',
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                    fontFamily: 'Poppins',
                  ),
                ),
                InkWell(
                  onTap: () {
                    setState(() {
                      _showRoutePreview = false;
                      _showRecentPlaces = true;
                    });
                  },
                  child: Container(
                    width: 48,
                    height: 48,
                    decoration: BoxDecoration(
                      color: Colors.grey.withOpacity(0.1),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.close,
                      color: AppColor.textMediumGray,
                      size: 24,
                    ),
                  ),
                ),
              ],
            ),

            const SizedBox(height: 20),

            // Origin
            Row(
              children: [
                Container(
                  width: 48,
                  height: 48,
                  decoration: const BoxDecoration(
                    color: Color(0xFFE6EFFF),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(
                    Icons.my_location,
                    color: AppColor.primaryBlue,
                    size: 20,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Origin',
                        style: TextStyle(
                          fontSize: 14,
                          color: AppColor.textMediumGray,
                          fontFamily: 'Poppins',
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _pickupController.text.isEmpty
                            ? "374 Scranton Ave"
                            : _pickupController.text,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: AppColor.textDarkBlue,
                          fontFamily: 'Poppins',
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),

            // Dotted line
            Padding(
              padding: const EdgeInsets.only(left: 24),
              child: Container(
                height: 24,
                width: 2,
                margin: const EdgeInsets.symmetric(vertical: 8),
                child: CustomPaint(
                  painter: DottedLinePainter(),
                ),
              ),
            ),

            // Destination
            Row(
              children: [
                Container(
                  width: 48,
                  height: 48,
                  decoration: const BoxDecoration(
                    color: Color(0xFFF2EAFF),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(
                    Icons.place,
                    color: AppColor.primaryPurple,
                    size: 20,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Destination',
                        style: TextStyle(
                          fontSize: 14,
                          color: AppColor.textMediumGray,
                          fontFamily: 'Poppins',
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _destinationController.text.isEmpty
                            ? "Memorial Hospital"
                            : _destinationController.text,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: AppColor.textDarkBlue,
                          fontFamily: 'Poppins',
                        ),
                        overflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Route info summary
            RouteInfoSummary(
              distance: _distance,
              duration: _duration,
              estimatedPrice: _estimatedPrice,
            ),

            const SizedBox(height: 32),

            // Continue to Booking button
            GradientButton(
              text: 'Continue to Booking',
              onPressed: () {
                setState(() {
                  _showRoutePreview = false;
                  _showBookingModal = true;
                });
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRecentPlaceItem(int index) {
    final place = _recentPlaces[index];
    return InkWell(
      onTap: () {
        // Handle tap on recent place
        setState(() {
          // Set destination to the selected place
          _destinationController.text = place['name'] as String;

          // For demo purposes, use mock data
          // In a real app, you would get actual coordinates and calculate route
          _pickupLocation = _currentLocation ?? const LatLng(40.7128, -74.0060);
          _dropoffLocation =
              const LatLng(40.7282, -73.9942); // Mock destination

          // Set estimated distance and duration
          _distance = 5.2; // Miles
          _duration = 18; // Minutes

          // Hide recent places and show route preview
          _showRecentPlaces = false;
          _showRoutePreview = true;
        });
      },
      child: Padding(
        padding: const EdgeInsets.symmetric(
            horizontal: 16.0, vertical: 10.0), // Even more compact padding
        child: Row(
          children: [
            Container(
              width: 48,
              height: 48,
              decoration: const BoxDecoration(
                color: Color(0xFFE6EFFF), // Light blue background
                shape: BoxShape.circle,
              ),
              child: Icon(
                place['icon'] as IconData,
                color: AppColor.primaryBlue, // Blue icon to match screenshot
                size: 24,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                mainAxisSize: MainAxisSize.min, // Minimize height
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    place['name'] as String,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColor.textDarkBlue,
                      fontFamily: 'Poppins',
                    ),
                  ),
                  const SizedBox(height: 2), // Reduce spacing
                  Text(
                    place['timeAgo'] as String,
                    style: const TextStyle(
                      fontSize: 14,
                      color: AppColor.textMediumGray,
                      fontFamily: 'Poppins',
                    ),
                  ),
                ],
              ),
            ),
            Container(
              width: 36,
              height: 36,
              decoration: const BoxDecoration(
                color: Color(0xFFE6EFFF), // Light blue background
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.arrow_forward_ios,
                color: AppColor.primaryBlue, // Blue icon to match screenshot
                size: 18,
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _handleNextStep() {
    if (_currentBookingStep < 4) {
      setState(() {
        _currentBookingStep++;
      });
    } else {
      // Handle booking submission
      setState(() {
        _isBookingLoading = true;
      });

      // Simulate booking process
      Future.delayed(const Duration(milliseconds: 1500), () {
        setState(() {
          _isBookingLoading = false;
          _showBookingSuccessDialog = true;
        });

        // Hide success dialog after a delay
        Future.delayed(const Duration(milliseconds: 2000), () {
          setState(() {
            _showBookingSuccessDialog = false;
            _showBookingModal = false;
            _currentBookingStep = 0;
            _pickupController.clear();
            _destinationController.clear();
            _selectedAppointmentType = null;
          });
        });
      });
    }
  }
}
