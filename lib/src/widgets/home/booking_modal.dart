import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../constants/app_color.dart';
import '../../models/appointment_type.dart';
import '../../models/ride_type.dart';
import 'booking_steps/additional_info_step.dart';
import 'booking_steps/date_time_step.dart';
import 'booking_steps/ride_type_step.dart';
import 'booking_steps/confirmation_step.dart';
import 'dotted_line_painter.dart';
import 'appointment_type_modal.dart';

/// A draggable bottom sheet for booking rides with multi-step flow
class BookingModal extends StatefulWidget {
  /// Current step in the booking process
  final int currentStep;

  /// Pickup address text
  final String pickupAddress;

  /// Destination address text
  final String destinationAddress;

  /// Currently selected appointment type
  final AppointmentType? selectedAppointmentType;

  /// Currently selected ride type
  final RideType selectedRideType;

  /// Selected date for the ride
  final DateTime selectedDate;

  /// Selected time for the ride (string format)
  final String? selectedTime;

  /// Distance of the ride
  final double distance;

  /// Duration of the ride in minutes
  final int duration;

  /// Estimated price of the ride
  final double estimatedPrice;

  /// Additional notes
  final String additionalNotes;

  /// Whether this is a round trip
  final bool isRoundTrip;

  /// Whether a companion will join
  final bool hasCompanion;

  /// Return date for round trips
  final DateTime returnDate;

  /// Return time for round trips
  final String? returnTime;

  /// Whether the booking is currently loading
  final bool isLoading;

  /// Whether to show the appointment type dropdown
  final bool showAppointmentDropdown;

  /// Pickup location coordinates
  final LatLng? pickupLocation;

  /// Dropoff location coordinates
  final LatLng? dropoffLocation;

  /// Route points for map display
  final List<LatLng> routePoints;

  /// Callback for going back to previous step
  final VoidCallback onBackPressed;

  /// Callback for proceeding to next step or booking
  final VoidCallback onNextPressed;

  /// Callback for closing the modal
  final VoidCallback onClose;

  /// Callback for when ride type is selected
  final Function(RideType) onRideTypeSelected;

  /// Callback for selecting appointment type
  final Function(String) onAppointmentTypeSelect;

  /// Callback for toggling appointment dropdown
  final Function(bool) onShowAppointmentDropdown;

  /// Callback for date selection
  final Function(DateTime) onDateSelected;

  /// Callback for time selection
  final Function(String?) onTimeSelected;

  /// Callback for additional notes changed
  final Function(String) onNotesChanged;

  /// Callback for round trip toggle
  final Function(bool) onRoundTripChanged;

  /// Callback for companion toggle
  final Function(bool) onCompanionChanged;

  /// Callback for return date selection
  final Function(DateTime) onReturnDateSelected;

  /// Callback for return time selection
  final Function(String?) onReturnTimeSelected;

  /// Callback for when user drags the sheet
  final Function(double) onDragUpdate;

  const BookingModal({
    Key? key,
    required this.currentStep,
    required this.pickupAddress,
    required this.destinationAddress,
    required this.selectedAppointmentType,
    required this.selectedRideType,
    required this.selectedDate,
    required this.selectedTime,
    required this.distance,
    required this.duration,
    required this.estimatedPrice,
    required this.additionalNotes,
    required this.isRoundTrip,
    required this.hasCompanion,
    required this.returnDate,
    required this.returnTime,
    required this.isLoading,
    required this.showAppointmentDropdown,
    required this.pickupLocation,
    required this.dropoffLocation,
    required this.routePoints,
    required this.onBackPressed,
    required this.onNextPressed,
    required this.onClose,
    required this.onRideTypeSelected,
    required this.onAppointmentTypeSelect,
    required this.onShowAppointmentDropdown,
    required this.onDateSelected,
    required this.onTimeSelected,
    required this.onNotesChanged,
    required this.onRoundTripChanged,
    required this.onCompanionChanged,
    required this.onReturnDateSelected,
    required this.onReturnTimeSelected,
    required this.onDragUpdate,
  }) : super(key: key);

  @override
  State<BookingModal> createState() => _BookingModalState();
}

class _BookingModalState extends State<BookingModal> {
  // Track the drag progress
  double _dragProgress = 0.0;

  // Default sheet heights based on step
  final Map<int, double> _stepHeights = {
    0: 0.5, // Trip Details
    1: 0.7, // Ride Type
    2: 0.75, // Date & Time
    3: 0.6, // Requirements
    4: 0.75, // Confirmation
  };

  // Min and max sheet heights
  final double _minSheetHeight = 0.45;
  final double _maxSheetHeight = 0.9;

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;

    // Get the current step height or default to 0.6
    final baseSheetHeightFraction = _stepHeights[widget.currentStep] ?? 0.6;

    // Calculate sheet height based on drag progress
    final sheetHeightFraction = _dragProgress > 0
        ? _minSheetHeight + (_maxSheetHeight - _minSheetHeight) * _dragProgress
        : baseSheetHeightFraction;

    return Positioned(
      bottom: 0,
      left: 0,
      right: 0,
      height: screenHeight * sheetHeightFraction,
      child: GestureDetector(
        onVerticalDragStart: (_) {
          // Initialize drag with current height
          setState(() {
            _dragProgress = (baseSheetHeightFraction - _minSheetHeight) /
                (_maxSheetHeight - _minSheetHeight);
          });
        },
        onVerticalDragUpdate: (details) {
          // Calculate new drag progress based on drag delta
          final delta = -details.delta.dy /
              (screenHeight * (_maxSheetHeight - _minSheetHeight));
          setState(() {
            _dragProgress = (_dragProgress + delta).clamp(0.0, 1.0);
          });

          // Notify parent of drag update
          widget.onDragUpdate(_dragProgress);

          // Close modal if dragged down significantly
          if (details.primaryDelta! > 12 &&
              _dragProgress < 0.1 &&
              widget.currentStep == 0) {
            widget.onClose();
          }
        },
        onVerticalDragEnd: (_) {
          // Snap to nearest position
          if (_dragProgress < 0.3) {
            setState(() {
              _dragProgress = 0.0;
            });
          } else if (_dragProgress > 0.7) {
            setState(() {
              _dragProgress = 1.0;
            });
          }

          // Notify parent of final position
          widget.onDragUpdate(_dragProgress);
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
                    onPressed: widget.onClose,
                  ),
                ),
              ),

              // Step indicator
              Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 24.0, vertical: 8.0),
                child: StepIndicator(
                  currentStep: widget.currentStep,
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
                    key: ValueKey<int>(widget.currentStep),
                    padding: const EdgeInsets.symmetric(
                        horizontal: 24.0, vertical: 12.0),
                    child: _buildBookingStep(),
                  ),
                ),
              ),

              // Navigation buttons
              Padding(
                padding: const EdgeInsets.all(24.0),
                child: Row(
                  mainAxisAlignment: widget.currentStep > 0
                      ? MainAxisAlignment.spaceBetween
                      : MainAxisAlignment.end,
                  children: [
                    // Back button - only shown after first step
                    if (widget.currentStep > 0)
                      OutlinedButton(
                        onPressed: widget.onBackPressed,
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
                      onPressed:
                          _canProceedToNextStep() ? widget.onNextPressed : null,
                      style: ElevatedButton.styleFrom(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30),
                        ),
                        backgroundColor: widget.currentStep == 4
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
                          if (widget.isLoading)
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
                              widget.currentStep < 4 ? "Continue" : "Book Ride",
                              style: const TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          const SizedBox(width: 8),
                          if (!widget.isLoading)
                            Icon(
                              widget.currentStep < 4
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
    if (widget.isLoading) return false;

    switch (widget.currentStep) {
      case 0: // Trip Details
        return widget.pickupAddress.isNotEmpty &&
            widget.destinationAddress.isNotEmpty &&
            widget.selectedAppointmentType != null;
      case 1: // Ride Type
        return widget.selectedRideType != null;
      case 2: // Date & Time
        return widget.selectedDate != null && widget.selectedTime != null;
      case 3: // Requirements
        return true; // No required fields
      case 4: // Confirmation
        return true; // Can always book
      default:
        return false;
    }
  }

  Widget _buildBookingStep() {
    switch (widget.currentStep) {
      case 0:
        return TripDetailsStep(
          pickupAddress: widget.pickupAddress,
          destinationAddress: widget.destinationAddress,
          selectedAppointmentType: widget.selectedAppointmentType?.name,
          onAppointmentTypeSelect: widget.onAppointmentTypeSelect,
          appointmentTypes: AppointmentType.values.map((t) => t.name).toList(),
          showAppointmentDropdown: widget.showAppointmentDropdown,
          onShowAppointmentDropdown: widget.onShowAppointmentDropdown,
        );
      case 1:
        return RideTypeStep(
          selectedType: widget.selectedRideType,
          onTypeSelected: widget.onRideTypeSelected,
        );
      case 2:
        return DateTimeStep(
          selectedDate: widget.selectedDate,
          selectedTime: widget.selectedTime,
          onDateSelected: widget.onDateSelected,
          onTimeSelected: widget.onTimeSelected,
        );
      case 3:
        return AdditionalInfoStep(
          notes: widget.additionalNotes,
          isRoundTrip: widget.isRoundTrip,
          hasCompanion: widget.hasCompanion,
          returnDate: widget.returnDate,
          returnTime: widget.returnTime,
          onNotesChanged: widget.onNotesChanged,
          onRoundTripChanged: widget.onRoundTripChanged,
          onCompanionChanged: widget.onCompanionChanged,
          onReturnDateSelected: widget.onReturnDateSelected,
          onReturnTimeSelected: widget.onReturnTimeSelected,
        );
      case 4:
        return ConfirmationStep(
          pickupAddress: widget.pickupAddress,
          destinationAddress: widget.destinationAddress,
          selectedDate: widget.selectedDate,
          selectedTime: widget.selectedTime,
          isRoundTrip: widget.isRoundTrip,
          returnDate: widget.isRoundTrip ? widget.returnDate : null,
          returnTime: widget.isRoundTrip ? widget.returnTime : null,
          distance: widget.distance,
          duration: widget.duration,
          estimatedPrice: widget.estimatedPrice,
          // Pass route points for map display
          pickupLocation: widget.pickupLocation,
          dropoffLocation: widget.dropoffLocation,
          routePoints: widget.routePoints,
        );
      default:
        return const SizedBox.shrink();
    }
  }
}

/// Widget to display the trip details step of the booking process
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
              onTap: () {
                showAppointmentTypeModal(
                  context: context,
                  initialValue: selectedAppointmentType,
                  onSelected: onAppointmentTypeSelect,
                );
              },
              child: Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: BoxDecoration(
                  color: const Color(0xFFF1F5F9),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(
                    color: const Color(0xFFE2E8F0),
                    width: 1,
                  ),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Text(
                        selectedAppointmentType ?? 'Select Appointment Type',
                        style: TextStyle(
                          fontSize: 16,
                          color: selectedAppointmentType != null
                              ? Colors.black87
                              : Colors.black38,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const Icon(
                      Icons.arrow_forward_ios,
                      size: 16,
                      color: Colors.black54,
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

/// A step indicator for the booking flow
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
