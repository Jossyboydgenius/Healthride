import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../constants/app_color.dart';

/// A step for additional information
class AdditionalInfoStep extends StatelessWidget {
  /// Additional notes for the ride
  final String notes;

  /// Whether this is a round trip
  final bool isRoundTrip;

  /// Whether the rider has a companion
  final bool hasCompanion;

  /// The return date for a round trip
  final DateTime returnDate;

  /// The return time for a round trip
  final String? returnTime;

  /// Callback when notes change
  final Function(String) onNotesChanged;

  /// Callback when round trip option changes
  final Function(bool) onRoundTripChanged;

  /// Callback when companion option changes
  final Function(bool) onCompanionChanged;

  /// Callback when return date is selected
  final Function(DateTime) onReturnDateSelected;

  /// Callback when return time is selected
  final Function(String) onReturnTimeSelected;

  /// Creates an additional info step
  const AdditionalInfoStep({
    Key? key,
    required this.notes,
    required this.isRoundTrip,
    required this.hasCompanion,
    required this.returnDate,
    required this.returnTime,
    required this.onNotesChanged,
    required this.onRoundTripChanged,
    required this.onCompanionChanged,
    required this.onReturnDateSelected,
    required this.onReturnTimeSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: AppColor.primaryPurple.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Icon(
                Icons.fact_check_outlined,
                color: AppColor.primaryPurple,
                size: 24,
              ),
            ),
            const SizedBox(width: 12),
            const Text(
              'Fill in your requirements',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppColor.textDarkBlue,
              ),
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Companion section
        Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 10,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Row(
                children: [
                  Icon(
                    Icons.people,
                    color: AppColor.primaryBlue,
                    size: 24,
                  ),
                  SizedBox(width: 12),
                  Text(
                    'Passengers',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          color: AppColor.primaryBlue.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(16),
                        ),
                        child: const Icon(
                          Icons.group,
                          color: AppColor.primaryBlue,
                        ),
                      ),
                      const SizedBox(width: 16),
                      const Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Bring a Companion',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                              color: AppColor.textDarkBlue,
                            ),
                          ),
                          SizedBox(height: 4),
                          Text(
                            'Add one additional passenger to your ride',
                            style: TextStyle(
                              fontSize: 14,
                              color: AppColor.textMediumGray,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                  Switch(
                    value: hasCompanion,
                    onChanged: onCompanionChanged,
                    activeColor: AppColor.primaryBlue,
                    activeTrackColor: AppColor.primaryBlue.withOpacity(0.3),
                  ),
                ],
              ),
            ],
          ),
        ),

        const SizedBox(height: 16),

        // Notes section
        Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 10,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Row(
                children: [
                  Icon(
                    Icons.description_outlined,
                    color: AppColor.primaryPurple,
                    size: 24,
                  ),
                  SizedBox(width: 12),
                  Text(
                    'Additional Information',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              TextField(
                controller: TextEditingController(text: notes)
                  ..selection = TextSelection.fromPosition(
                    TextPosition(offset: notes.length),
                  ),
                onChanged: onNotesChanged,
                maxLines: 4,
                style: const TextStyle(
                  fontSize: 16,
                  color: AppColor.textDarkBlue,
                ),
                decoration: InputDecoration(
                  hintText: 'Additional Notes (Optional)',
                  hintStyle: const TextStyle(
                    color: AppColor.textMediumGray,
                  ),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(
                      color: AppColor.borderColor,
                      width: 1.5,
                    ),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(
                      color: AppColor.borderColor,
                      width: 1.5,
                    ),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(16),
                    borderSide: const BorderSide(
                      color: AppColor.primaryPurple,
                      width: 1.5,
                    ),
                  ),
                  contentPadding: const EdgeInsets.all(16),
                ),
              ),
            ],
          ),
        ),

        // Toggle for round trip
        Container(
          margin: const EdgeInsets.only(top: 16),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 10,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          color: AppColor.primaryPurple.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(16),
                        ),
                        child: const Icon(
                          Icons.repeat,
                          color: AppColor.primaryPurple,
                        ),
                      ),
                      const SizedBox(width: 16),
                      const Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Round Trip',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                              color: AppColor.textDarkBlue,
                            ),
                          ),
                          SizedBox(height: 4),
                          Text(
                            'Schedule a return ride',
                            style: TextStyle(
                              fontSize: 14,
                              color: AppColor.textMediumGray,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                  Switch(
                    value: isRoundTrip,
                    onChanged: onRoundTripChanged,
                    activeColor: AppColor.primaryPurple,
                    activeTrackColor: AppColor.primaryPurple.withOpacity(0.3),
                  ),
                ],
              ),
              if (isRoundTrip) ...[
                const SizedBox(height: 16),
                const Divider(height: 1, thickness: 1),
                const SizedBox(height: 16),
                const Text(
                  'Return Trip Details',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: _DateSelector(
                        label: 'Return Date',
                        value: DateFormat('MMM d, yyyy').format(returnDate),
                        onTap: () => _showDatePicker(context),
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: _TimeSelector(
                        label: 'Return Time',
                        value: returnTime ?? 'Select time',
                        onTap: () => _showTimePicker(context),
                      ),
                    ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ],
    );
  }

  void _showDatePicker(BuildContext context) async {
    final pickedDate = await showDatePicker(
      context: context,
      initialDate: returnDate,
      firstDate: DateTime.now(),
      lastDate: DateTime.now().add(const Duration(days: 365)),
      builder: (context, child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: const ColorScheme.light(
              primary: AppColor.primaryPurple,
              onPrimary: Colors.white,
              surface: Colors.white,
              onSurface: AppColor.textDarkBlue,
            ),
          ),
          child: child!,
        );
      },
    );

    if (pickedDate != null) {
      onReturnDateSelected(pickedDate);
    }
  }

  void _showTimePicker(BuildContext context) async {
    final initialTime =
        returnTime != null ? _parseTimeString(returnTime!) : TimeOfDay.now();

    final pickedTime = await showTimePicker(
      context: context,
      initialTime: initialTime,
      builder: (context, child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: const ColorScheme.light(
              primary: AppColor.primaryPurple,
              onPrimary: Colors.white,
              surface: Colors.white,
              onSurface: AppColor.textDarkBlue,
            ),
          ),
          child: child!,
        );
      },
    );

    if (pickedTime != null) {
      final formattedTime = _formatTimeOfDay(pickedTime);
      onReturnTimeSelected(formattedTime);
    }
  }

  TimeOfDay _parseTimeString(String timeString) {
    final parts = timeString.split(' ');
    final timeParts = parts[0].split(':');
    final hour = int.parse(timeParts[0]);
    final minute = int.parse(timeParts[1]);
    final isPM = parts[1].toLowerCase() == 'pm';

    if (isPM && hour < 12) {
      return TimeOfDay(hour: hour + 12, minute: minute);
    } else if (!isPM && hour == 12) {
      return TimeOfDay(hour: 0, minute: minute);
    } else {
      return TimeOfDay(hour: hour, minute: minute);
    }
  }

  String _formatTimeOfDay(TimeOfDay time) {
    final hour = time.hourOfPeriod == 0 ? 12 : time.hourOfPeriod;
    final minute = time.minute.toString().padLeft(2, '0');
    final period = time.period == DayPeriod.am ? 'AM' : 'PM';
    return '$hour:$minute $period';
  }
}

/// A selector for dates
class _DateSelector extends StatelessWidget {
  /// The label text
  final String label;

  /// The current value
  final String value;

  /// Callback when tapped
  final VoidCallback onTap;

  /// Creates a date selector
  const _DateSelector({
    Key? key,
    required this.label,
    required this.value,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          border: Border.all(
            color: AppColor.borderColor,
            width: 1.5,
          ),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              label,
              style: const TextStyle(
                fontSize: 12,
                color: AppColor.textMediumGray,
              ),
            ),
            const SizedBox(height: 4),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  value,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                  ),
                ),
                const Icon(
                  Icons.calendar_today,
                  size: 18,
                  color: AppColor.primaryPurple,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

/// A selector for times
class _TimeSelector extends StatelessWidget {
  /// The label text
  final String label;

  /// The current value
  final String value;

  /// Callback when tapped
  final VoidCallback onTap;

  /// Creates a time selector
  const _TimeSelector({
    Key? key,
    required this.label,
    required this.value,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          border: Border.all(
            color: AppColor.borderColor,
            width: 1.5,
          ),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              label,
              style: const TextStyle(
                fontSize: 12,
                color: AppColor.textMediumGray,
              ),
            ),
            const SizedBox(height: 4),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  value,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                  ),
                ),
                const Icon(
                  Icons.access_time,
                  size: 18,
                  color: AppColor.primaryPurple,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
