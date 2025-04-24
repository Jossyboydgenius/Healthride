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
import '../widgets/home/booking_steps/additional_info_step.dart';
import '../widgets/home/booking_steps/date_time_step.dart';
import '../widgets/home/booking_steps/ride_type_step.dart';
import '../widgets/home/booking_steps/confirmation_step.dart';
import '../widgets/home/route_info_summary.dart';
import '../widgets/home/health_ride_map.dart';
import '../widgets/animations/loading_animation.dart';
import '../models/appointment_type.dart';
import '../screens/ride_screen.dart';
import '../screens/notifications_screen.dart';
import '../screens/profile_screen.dart';

// Import the necessary packages
import 'package:flutter/animation.dart';
import 'package:flutter/gestures.dart';

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
                                fontFamily: 'SF Pro Display',
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              _getUserName(),
                              style: const TextStyle(
                                fontSize: 32,
                                fontWeight: FontWeight.bold,
                                color: AppColor.textDarkBlue,
                                fontFamily: 'SF Pro Display',
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
                        margin: const EdgeInsets.symmetric(horizontal: 16.0),
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
                                    fontFamily: 'SF Pro Display',
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 4),
                            const Text(
                              'Tue, Mar 25 • 9:30 am',
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                                color: AppColor.primaryPurple,
                                fontFamily: 'SF Pro Display',
                              ),
                            ),
                            Text(
                              _nextAppointment['location'],
                              style: const TextStyle(
                                fontSize: 14,
                                color: AppColor.textMediumGray,
                                fontFamily: 'SF Pro Display',
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
                              fontFamily: 'SF Pro Display',
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
                                fontFamily: 'SF Pro Display',
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
                          color: Color(0xFFE5E7EB),
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
    final screenHeight = MediaQuery.of(context).size.height;
    final stepHeights = {
      0: 0.5, // Trip Details
      1: 0.7, // Ride Type
      2: 0.75, // Date & Time
      3: 0.6, // Requirements
      4: 0.75, // Confirmation
    };

    // Get the current step height or default to 0.6
    final sheetHeightFraction = stepHeights[_currentBookingStep] ?? 0.6;

    return AnimatedPositioned(
      duration: const Duration(milliseconds: 300),
      curve: Curves.fastOutSlowIn,
      bottom: 0,
      left: 0,
      right: 0,
      height: screenHeight * sheetHeightFraction,
      child: GestureDetector(
        onVerticalDragUpdate: (details) {
          // Prevent accidental closing
          if (_currentBookingStep == 0 && details.primaryDelta! > 10) {
            // Only allow closing if we're on the first step
            setState(() {
              _showBookingModal = false;
              _currentBookingStep = 0;
            });
          }
        },
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(28),
              topRight: Radius.circular(28),
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.1),
                blurRadius: 10,
                offset: const Offset(0, -2),
              ),
            ],
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Drag handle
              Center(
                child: Padding(
                  padding: const EdgeInsets.only(top: 12.0, bottom: 4.0),
                  child: Container(
                    width: 60,
                    height: 5,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(10),
                      gradient: LinearGradient(
                        colors: [
                          AppColor.primaryBlue.withOpacity(0.2),
                          AppColor.primaryPurple.withOpacity(0.2),
                        ],
                      ),
                    ),
                  ),
                ),
              ),

              // Close button
              Align(
                alignment: Alignment.topRight,
                child: Padding(
                  padding: const EdgeInsets.only(right: 16.0, top: 8.0),
                  child: IconButton(
                    icon:
                        const Icon(Icons.close, color: AppColor.textMediumGray),
                    onPressed: () {
                      setState(() {
                        _showBookingModal = false;
                        _currentBookingStep = 0;
                        _pickupController.clear();
                        _destinationController.clear();
                        _selectedAppointmentType = null;
                      });
                    },
                  ),
                ),
              ),

              // Step indicator
              Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 24.0, vertical: 8.0),
                child: StepIndicator(
                  currentStep: _currentBookingStep,
                  totalSteps: 5,
                  titles: const [
                    "Trip Details",
                    "Ride Type",
                    "Date & Time",
                    "Requirements",
                    "Confirm"
                  ],
                ),
              ),

              // Main content area with animated transitions
              Expanded(
                child: AnimatedSwitcher(
                  duration: const Duration(milliseconds: 300),
                  transitionBuilder:
                      (Widget child, Animation<double> animation) {
                    return FadeTransition(
                      opacity: animation,
                      child: SlideTransition(
                        position: Tween<Offset>(
                          begin: const Offset(1.0, 0.0),
                          end: Offset.zero,
                        ).animate(animation),
                        child: child,
                      ),
                    );
                  },
                  child: SingleChildScrollView(
                    key: ValueKey<int>(_currentBookingStep),
                    padding: const EdgeInsets.symmetric(
                        horizontal: 24.0, vertical: 12.0),
                    child: _buildBookingStep(_currentBookingStep),
                  ),
                ),
              ),

              // Navigation buttons
              Padding(
                padding: const EdgeInsets.all(24.0),
                child: Row(
                  mainAxisAlignment: _currentBookingStep > 0
                      ? MainAxisAlignment.spaceBetween
                      : MainAxisAlignment.end,
                  children: [
                    // Back button - only shown after first step
                    if (_currentBookingStep > 0)
                      OutlinedButton(
                        onPressed: () {
                          setState(() {
                            _currentBookingStep--;
                          });
                        },
                        style: OutlinedButton.styleFrom(
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(30),
                          ),
                          side: const BorderSide(
                            width: 1.5,
                            color: AppColor.primaryBlue,
                          ),
                          padding: const EdgeInsets.symmetric(
                              horizontal: 24, vertical: 16),
                        ),
                        child: const Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(Icons.arrow_back,
                                size: 18, color: AppColor.primaryBlue),
                            SizedBox(width: 8),
                            Text(
                              "Back",
                              style: TextStyle(
                                color: AppColor.primaryBlue,
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          ],
                        ),
                      ),

                    // Next/Book button
                    ElevatedButton(
                      onPressed: _canProceedToNextStep()
                          ? () => _handleNextStep()
                          : null,
                      style: ElevatedButton.styleFrom(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30),
                        ),
                        backgroundColor: _currentBookingStep == 4
                            ? AppColor.primaryPurple
                            : AppColor.primaryBlue,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 32, vertical: 16),
                        disabledBackgroundColor:
                            AppColor.textLightGray.withOpacity(0.2),
                        disabledForegroundColor: AppColor.textLightGray,
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          if (_isBookingLoading)
                            const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                color: Colors.white,
                                strokeWidth: 2,
                              ),
                            )
                          else
                            Text(
                              _currentBookingStep < 4
                                  ? "Continue"
                                  : "Book Ride",
                              style: const TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          const SizedBox(width: 8),
                          if (!_isBookingLoading)
                            Icon(
                              _currentBookingStep < 4
                                  ? Icons.arrow_forward
                                  : Icons.check_circle,
                              size: 18,
                              color: Colors.white,
                            ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  bool _canProceedToNextStep() {
    if (_isBookingLoading) return false;

    switch (_currentBookingStep) {
      case 0: // Trip Details
        return _pickupController.text.isNotEmpty &&
            _destinationController.text.isNotEmpty &&
            _selectedAppointmentType != null;
      case 1: // Ride Type
        return _selectedRideType != null;
      case 2: // Date & Time
        return _selectedDate != null && _selectedTime != null;
      case 3: // Requirements
        return true; // No required fields
      case 4: // Confirmation
        return true; // Can always book
      default:
        return false;
    }
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

  Widget _buildBookingStep(int step) {
    switch (step) {
      case 0:
        return TripDetailsStep(
          pickupAddress: _pickupController.text,
          destinationAddress: _destinationController.text,
          selectedAppointmentType: _selectedAppointmentType?.name,
          onAppointmentTypeSelect: (type) {
            setState(() {
              _selectedAppointmentType = AppointmentType.values.firstWhere(
                (t) => t.name == type,
                orElse: () => AppointmentType.medical,
              );
            });
          },
          appointmentTypes: AppointmentType.values.map((t) => t.name).toList(),
          showAppointmentDropdown: _isShowingAppointmentDropdown,
          onShowAppointmentDropdown: (show) {
            setState(() {
              _isShowingAppointmentDropdown = show;
            });
          },
        );
      case 1:
        return RideTypeStep(
          selectedType: _selectedRideType,
          onTypeSelected: (type) {
            setState(() {
              _selectedRideType = type;
            });
          },
        );
      case 2:
        return DateTimeStep(
          selectedDate: _selectedDate,
          selectedTime: _selectedTime,
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
        );
      case 3:
        return AdditionalInfoStep(
          notes: _additionalNotes,
          isRoundTrip: _isRoundTrip,
          hasCompanion: _hasCompanion,
          returnDate: _returnDate,
          returnTime: _returnTime,
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
        );
      case 4:
        return ConfirmationStep(
          pickupAddress: _pickupController.text,
          destinationAddress: _destinationController.text,
          selectedDate: _selectedDate,
          selectedTime: _selectedTime,
          isRoundTrip: _isRoundTrip,
          returnDate: _isRoundTrip ? _returnDate : null,
          returnTime: _isRoundTrip ? _returnTime : null,
          distance: _distance,
          duration: _duration,
          estimatedPrice: _estimatedPrice,
          // Pass route points for map display
          pickupLocation: _pickupLocation,
          dropoffLocation: _dropoffLocation,
          routePoints: _routePoints,
        );
      default:
        return const SizedBox.shrink();
    }
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
                    fontFamily: 'SF Pro Display',
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
                          fontFamily: 'SF Pro Display',
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
                          fontFamily: 'SF Pro Display',
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
                          fontFamily: 'SF Pro Display',
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
                          fontFamily: 'SF Pro Display',
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
                      fontFamily: 'SF Pro Display',
                    ),
                  ),
                  const SizedBox(height: 2), // Reduce spacing
                  Text(
                    place['timeAgo'] as String,
                    style: const TextStyle(
                      fontSize: 14,
                      color: AppColor.textMediumGray,
                      fontFamily: 'SF Pro Display',
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
}

// Custom painter for dotted line
class DottedLinePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    Paint paint = Paint()
      ..color = Colors.grey.withOpacity(0.5)
      ..strokeWidth = 2
      ..strokeCap = StrokeCap.round;

    double dashHeight = 4;
    double dashSpace = 4;
    double startY = 0;

    while (startY < size.height) {
      canvas.drawLine(Offset(0, startY), Offset(0, startY + dashHeight), paint);
      startY += dashHeight + dashSpace;
    }
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => false;
}

class StepIndicator extends StatelessWidget {
  final int currentStep;
  final int totalSteps;
  final List<String> titles;

  const StepIndicator({
    Key? key,
    required this.currentStep,
    required this.totalSteps,
    required this.titles,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Current step title
        Text(
          titles[currentStep],
          style: const TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.bold,
            color: AppColor.textDarkBlue,
            fontFamily: 'SF Pro Display',
          ),
        ),

        const SizedBox(height: 16),

        // Progress indicator row
        Row(
          children: List.generate(totalSteps, (index) {
            final isActive = index <= currentStep;
            final isFirst = index == 0;
            final isLast = index == totalSteps - 1;

            return Expanded(
              child: Row(
                children: [
                  // Circle indicator
                  Container(
                    width: 20,
                    height: 20,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: isActive
                          ? AppColor.primaryBlue
                          : Colors.grey.shade300,
                      border: Border.all(
                        color: isActive
                            ? AppColor.primaryBlue
                            : Colors.grey.shade300,
                        width: 2,
                      ),
                    ),
                    child: isActive
                        ? const Icon(
                            Icons.check,
                            size: 14,
                            color: Colors.white,
                          )
                        : null,
                  ),

                  // Line (except for last item)
                  if (!isLast)
                    Expanded(
                      child: Container(
                        height: 2,
                        color: index < currentStep
                            ? AppColor.primaryBlue
                            : Colors.grey.shade300,
                      ),
                    ),
                ],
              ),
            );
          }),
        ),

        const SizedBox(height: 8),

        // Step labels row
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: List.generate(totalSteps, (index) {
            final isActive = index <= currentStep;
            return Text(
              titles[index],
              style: TextStyle(
                fontSize: 12,
                fontWeight: isActive ? FontWeight.bold : FontWeight.normal,
                color: isActive ? AppColor.primaryBlue : Colors.grey,
              ),
            );
          }),
        ),
      ],
    );
  }
}

// Add TripDetailsStep widget for the first step
class TripDetailsStep extends StatelessWidget {
  final String pickupAddress;
  final String destinationAddress;
  final String? selectedAppointmentType;
  final Function(String) onAppointmentTypeSelect;
  final List<String> appointmentTypes;
  final bool showAppointmentDropdown;
  final Function(bool) onShowAppointmentDropdown;

  const TripDetailsStep({
    Key? key,
    required this.pickupAddress,
    required this.destinationAddress,
    this.selectedAppointmentType,
    required this.onAppointmentTypeSelect,
    required this.appointmentTypes,
    required this.showAppointmentDropdown,
    required this.onShowAppointmentDropdown,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Trip details header
        const Text(
          'Trip Details',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: AppColor.textDarkBlue,
          ),
        ),
        const SizedBox(height: 24),

        // Origin
        Row(
          children: [
            Container(
              width: 40,
              height: 40,
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
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    pickupAddress.isEmpty ? "Current Location" : pickupAddress,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),

        // Dotted line
        Padding(
          padding: const EdgeInsets.only(left: 20),
          child: SizedBox(
            height: 24,
            width: 2,
            child: CustomPaint(
              painter: DottedLinePainter(),
            ),
          ),
        ),

        // Destination
        Row(
          children: [
            Container(
              width: 40,
              height: 40,
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
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    destinationAddress,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColor.textDarkBlue,
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
          ],
        ),

        const SizedBox(height: 32),

        // Appointment Type
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Appointment Type',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: AppColor.textDarkBlue,
              ),
            ),
            const SizedBox(height: 8),
            GestureDetector(
              onTap: () => onShowAppointmentDropdown(true),
              child: Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.grey.shade300),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Text(
                        selectedAppointmentType ?? 'Select Appointment Type',
                        style: TextStyle(
                          fontSize: 16,
                          color: selectedAppointmentType != null
                              ? AppColor.textDarkBlue
                              : AppColor.textMediumGray,
                        ),
                      ),
                    ),
                    const Icon(Icons.arrow_drop_down,
                        color: AppColor.textMediumGray),
                  ],
                ),
              ),
            ),
          ],
        ),

        // Appointment Type Dropdown
        if (showAppointmentDropdown)
          Container(
            height: 200,
            margin: const EdgeInsets.only(top: 8),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey.shade300),
              borderRadius: BorderRadius.circular(8),
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: 4,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            child: ListView.builder(
              itemCount: appointmentTypes.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(appointmentTypes[index]),
                  onTap: () {
                    onAppointmentTypeSelect(appointmentTypes[index]);
                    onShowAppointmentDropdown(false);
                  },
                );
              },
            ),
          ),
      ],
    );
  }
}
