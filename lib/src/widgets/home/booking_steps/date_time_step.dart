import 'package:flutter/material.dart';
import '../../../constants/app_color.dart';
import 'package:intl/intl.dart';

/// A step for selecting date and time
class DateTimeStep extends StatelessWidget {
  /// The selected date
  final DateTime selectedDate;

  /// The selected time
  final String? selectedTime;

  /// Callback when a date is selected
  final Function(DateTime) onDateSelected;

  /// Callback when a time is selected
  final Function(String) onTimeSelected;

  /// Creates a date time step
  const DateTimeStep({
    Key? key,
    required this.selectedDate,
    required this.selectedTime,
    required this.onDateSelected,
    required this.onTimeSelected,
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
                Icons.calendar_today,
                color: AppColor.primaryPurple,
                size: 24,
              ),
            ),
            const SizedBox(width: 12),
            const Text(
              'Select Date',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppColor.textDarkBlue,
              ),
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Quick date selector
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppColor.backgroundGray.withOpacity(0.3),
            borderRadius: BorderRadius.circular(16),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Quick Select:',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                  color: AppColor.textDarkBlue,
                ),
              ),
              const SizedBox(height: 16),
              DateSelector(
                selectedDate: selectedDate,
                onDateSelected: onDateSelected,
              ),
            ],
          ),
        ),

        const SizedBox(height: 24),

        // Time selector
        Container(
          width: double.infinity,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Row(
                children: [
                  Icon(
                    Icons.access_time_rounded,
                    color: AppColor.primaryPurple,
                  ),
                  SizedBox(width: 8),
                  Text(
                    'Select Time',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              TimeSelector(
                selectedTime: selectedTime,
                onTimeSelected: onTimeSelected,
              ),
            ],
          ),
        ),
      ],
    );
  }
}

/// A widget for selecting a date
class DateSelector extends StatelessWidget {
  /// The selected date
  final DateTime selectedDate;

  /// Callback when a date is selected
  final Function(DateTime) onDateSelected;

  /// Creates a date selector
  const DateSelector({
    Key? key,
    required this.selectedDate,
    required this.onDateSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Generate next 7 days
    final now = DateTime.now();
    final dates = List<DateTime>.generate(7, (i) {
      return DateTime(now.year, now.month, now.day + i);
    });

    return SizedBox(
      height: 80,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: dates.length,
        itemBuilder: (context, index) {
          final date = dates[index];
          final isSelected = _isSameDay(date, selectedDate);
          final isToday = _isSameDay(date, now);

          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: DateChip(
              date: date,
              isSelected: isSelected,
              isToday: isToday,
              onTap: () => onDateSelected(date),
            ),
          );
        },
      ),
    );
  }

  bool _isSameDay(DateTime a, DateTime b) {
    return a.year == b.year && a.month == b.month && a.day == b.day;
  }
}

/// A widget for displaying a selectable date
class DateChip extends StatelessWidget {
  /// The date to display
  final DateTime date;

  /// Whether this date is selected
  final bool isSelected;

  /// Whether this date is today
  final bool isToday;

  /// Callback when this date is tapped
  final VoidCallback onTap;

  /// Creates a date chip
  const DateChip({
    Key? key,
    required this.date,
    required this.isSelected,
    required this.isToday,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final dayOfWeek = DateFormat('EEE').format(date);
    final dayOfMonth = date.day.toString();

    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 80,
        decoration: BoxDecoration(
          color: isSelected ? AppColor.primaryBlue : Colors.white,
          borderRadius: BorderRadius.circular(24),
          boxShadow: [
            if (isSelected)
              BoxShadow(
                color: AppColor.primaryBlue.withOpacity(0.3),
                blurRadius: 8,
                offset: const Offset(0, 4),
              ),
          ],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              dayOfWeek,
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w500,
                color: isSelected ? Colors.white : AppColor.textMediumGray,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              dayOfMonth,
              style: TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
                color: isSelected ? Colors.white : AppColor.textDarkBlue,
              ),
            ),
            if (isToday)
              Text(
                'Today',
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  color: isSelected ? Colors.white : AppColor.primaryBlue,
                ),
              ),
          ],
        ),
      ),
    );
  }
}

/// A widget for selecting a time
class TimeSelector extends StatelessWidget {
  /// The selected time
  final String? selectedTime;

  /// Callback when a time is selected
  final Function(String) onTimeSelected;

  /// Creates a time selector
  const TimeSelector({
    Key? key,
    required this.selectedTime,
    required this.onTimeSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => _showTimePicker(context),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(
          horizontal: 16,
          vertical: 20,
        ),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(24),
          border: Border.all(
            color: AppColor.borderColor,
            width: 1.5,
          ),
        ),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: AppColor.primaryPurple.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.access_time_rounded,
                color: AppColor.primaryPurple,
                size: 24,
              ),
            ),
            const SizedBox(width: 16),
            Text(
              selectedTime ?? 'Select time',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w500,
                color: selectedTime != null
                    ? AppColor.textDarkBlue
                    : AppColor.textMediumGray,
              ),
            ),
            const Spacer(),
            const Icon(
              Icons.keyboard_arrow_down_rounded,
              color: AppColor.textMediumGray,
            ),
          ],
        ),
      ),
    );
  }

  void _showTimePicker(BuildContext context) async {
    final initialTime = selectedTime != null
        ? _parseTimeString(selectedTime!)
        : TimeOfDay.now();

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
      onTimeSelected(formattedTime);
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
