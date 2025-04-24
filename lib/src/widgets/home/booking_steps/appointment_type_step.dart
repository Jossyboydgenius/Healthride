import 'package:flutter/material.dart';
import '../../../constants/app_color.dart';
import '../../../models/appointment_type.dart';

/// A step for selecting appointment type
class AppointmentTypeStep extends StatelessWidget {
  /// The selected appointment type
  final AppointmentTypeData? selectedType;

  /// Callback when an appointment type is selected
  final Function(AppointmentTypeData) onTypeSelected;

  /// Creates an appointment type step
  const AppointmentTypeStep({
    Key? key,
    this.selectedType,
    required this.onTypeSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final types = AppointmentTypeData.getTypes();

    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Select Appointment Type',
            style: Theme.of(context).textTheme.titleLarge,
          ),
          const SizedBox(height: 16),
          Expanded(
            child: ListView.builder(
              itemCount: types.length,
              itemBuilder: (context, index) {
                final type = types[index];
                final isSelected = selectedType?.id == type.id;

                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  elevation: isSelected ? 4 : 1,
                  color: isSelected
                      ? Theme.of(context).primaryColor.withOpacity(0.1)
                      : null,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                    side: BorderSide(
                      color: isSelected
                          ? Theme.of(context).primaryColor
                          : Colors.transparent,
                      width: 2,
                    ),
                  ),
                  child: InkWell(
                    onTap: () => onTypeSelected(type),
                    borderRadius: BorderRadius.circular(12),
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Row(
                        children: [
                          CircleAvatar(
                            backgroundColor:
                                Theme.of(context).primaryColor.withOpacity(0.2),
                            radius: 24,
                            child: Icon(
                              type.icon,
                              color: Theme.of(context).primaryColor,
                              size: 28,
                            ),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  type.name,
                                  style: Theme.of(context)
                                      .textTheme
                                      .titleMedium
                                      ?.copyWith(
                                        fontWeight: FontWeight.bold,
                                      ),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  type.description,
                                  style: Theme.of(context).textTheme.bodyMedium,
                                ),
                              ],
                            ),
                          ),
                          if (isSelected)
                            const Icon(
                              Icons.check_circle,
                              color: Colors.green,
                              size: 24,
                            ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

/// Appointment type option data
class AppointmentTypeOption {
  /// The appointment type
  final AppointmentType type;

  /// The title of the option
  final String title;

  /// The subtitle of the option
  final String subtitle;

  /// The icon representing the option
  final IconData icon;

  /// A description of the option
  final String description;

  /// The color theme for the option
  final Color color;

  /// Creates an appointment type option
  AppointmentTypeOption({
    required this.type,
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.description,
    required this.color,
  });
}

/// An appointment type option widget
class AppointmentTypeCard extends StatelessWidget {
  /// The option data
  final AppointmentTypeData appointmentType;

  /// Whether this option is selected
  final bool isSelected;

  /// Callback when this option is tapped
  final VoidCallback onTap;

  /// Creates an appointment type option widget
  const AppointmentTypeCard({
    Key? key,
    required this.appointmentType,
    required this.isSelected,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: isSelected ? AppColor.primaryBlue : Colors.transparent,
          width: 2,
        ),
      ),
      elevation: 2,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Row(
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: isSelected
                      ? AppColor.primaryBlue.withOpacity(0.1)
                      : AppColor.backgroundGray,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  appointmentType.icon,
                  color:
                      isSelected ? AppColor.primaryBlue : AppColor.textDarkBlue,
                  size: 28,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      appointmentType.name,
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                        color: isSelected
                            ? AppColor.primaryBlue
                            : AppColor.textDarkBlue,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      appointmentType.description,
                      style: TextStyle(
                        fontSize: 14,
                        color: AppColor.textMediumGray,
                      ),
                    ),
                  ],
                ),
              ),
              if (isSelected)
                Icon(
                  Icons.check_circle,
                  color: AppColor.primaryBlue,
                  size: 24,
                ),
            ],
          ),
        ),
      ),
    );
  }
}
