import 'package:flutter/material.dart';

/// Appointment type enumeration
enum AppointmentType {
  /// Medical appointment (doctor visit, check-up)
  medical,

  /// Dialysis treatment
  dialysis,

  /// Therapy (physical, mental)
  therapy,

  /// Pharmacy visit
  pharmacy,

  /// Other health-related appointment
  other;
}

/// Detailed data for appointment types
class AppointmentTypeData {
  final String id;
  final String name;
  final String description;
  final IconData icon;

  const AppointmentTypeData({
    required this.id,
    required this.name,
    required this.description,
    required this.icon,
  });

  static List<AppointmentTypeData> getTypes() {
    return const [
      AppointmentTypeData(
        id: '1',
        name: 'Regular Checkup',
        description: 'Routine medical examination',
        icon: Icons.health_and_safety,
      ),
      AppointmentTypeData(
        id: '2',
        name: 'Specialist Consultation',
        description: 'See a medical specialist',
        icon: Icons.person,
      ),
      AppointmentTypeData(
        id: '3',
        name: 'Therapy Session',
        description: 'Physical or mental health therapy',
        icon: Icons.psychology,
      ),
      AppointmentTypeData(
        id: '4',
        name: 'Lab Test',
        description: 'Blood work or other laboratory tests',
        icon: Icons.biotech,
      ),
    ];
  }
}
