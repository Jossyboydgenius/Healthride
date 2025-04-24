import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// Modal dialog for selecting appointment type
class AppointmentTypeModal extends StatefulWidget {
  /// Current selected appointment type
  final String? initialValue;

  /// Callback when an appointment type is selected
  final Function(String) onSelected;

  /// Creates an appointment type modal
  const AppointmentTypeModal({
    super.key,
    this.initialValue,
    required this.onSelected,
  });

  @override
  State<AppointmentTypeModal> createState() => _AppointmentTypeModalState();
}

class _AppointmentTypeModalState extends State<AppointmentTypeModal> {
  late TextEditingController _searchController;
  late List<String> _filteredAppointmentTypes;
  final List<String> _appointmentTypes = [
    "Accident Scene to Emergency Room",
    "Airport to Hospital",
    "Airport to Hotel (Medical Stay)",
    "Airport to Rehabilitation Center",
    "Airport to Residence (Post-Treatment)",
    "Ambulance Transfer Between Hospitals",
    "Annual Physical",
    "Assisted Living Facility to Appointment",
    "Assisted Living Facility to Hospital",
    "Blood Transfusion Appointment",
    "Cancer Center to Home",
    "Cardiology",
    "Chemotherapy",
    "Childcare Facility to Doctor's Office",
    "Chiropractor Appointment",
    "Community Event Medical Support Transport",
    "Dental Appointment",
    "Dermatology",
    "Dialysis",
    "Doctor's Office to Home",
    "Doctor's Office to Specialist",
    "Drug Rehabilitation Center Appointment",
    "Emergency Room to Home",
    "Emergency Room to Inpatient Care",
    "Emergency Room to Rehabilitation Center",
    "Endocrinology",
    "ENT (Ear, Nose, and Throat) Appointment",
    "Eye Exam",
    "Fall at Home Requiring Medical Attention",
    "Fitness Center Injury Requiring Medical Attention",
    "Gastroenterology",
    "Group Home to Medical Appointment",
    "Gym Injury Requiring Medical Attention",
    "Health Screening Event Transport",
    "Home to Appointment",
    "Home to Dialysis",
    "Home to Hospital (Emergency)",
    "Home to Hospital (Scheduled Admission)",
    "Home to Lab Work",
    "Home to Mental Health Counseling",
    "Home to Pharmacy",
    "Home to Physical Therapy",
    "Home to Post-Operative Checkup",
    "Home to Pre-Operative Consult",
    "Home to Radiation Therapy",
    "Home to Specialist Visit",
    "Hospice Facility to Appointment",
    "Hospice Facility to Hospital",
    "Hospital Discharge",
    "Hospital to Airport",
    "Hospital to Assisted Living Facility",
    "Hospital to Home",
    "Hospital to Long-Term Care Facility",
    "Hospital to Rehabilitation Center",
    "Imaging (CT Scan)",
    "Imaging (MRI)",
    "Imaging (PET Scan)",
    "Imaging (Ultrasound)",
    "Imaging (X-Ray)",
    "Inpatient Rehabilitation to Home",
    "Lab Work / Blood Draw",
    "Long-Term Care Facility to Appointment",
    "Long-Term Care Facility to Hospital",
    "Medical Equipment Delivery/Pickup",
    "Medical Supply Store Visit",
    "Mental Health / Counseling",
    "Nursing Home to Appointment",
    "Nursing Home to Hospital",
    "Occupational Therapy",
    "Oncology",
    "Ophthalmology",
    "Orthodontist Appointment",
    "Orthopedics",
    "Pain Management Clinic Visit",
    "Pharmacy Pickup",
    "Physical Therapy",
    "Podiatrist Appointment",
    "Post-Operative Checkup",
    "Pre-Operative Consult",
    "Primary Care Visit",
    "Psychiatrist Appointment",
    "Pulmonology",
    "Radiation Therapy",
    "Recreational Area Injury Requiring Medical Attention",
    "Rehabilitation Center to Appointment",
    "Rehabilitation Center to Home",
    "School Nurse to Doctor's Office",
    "School Nurse to Home",
    "School Nurse to Hospital",
    "Skilled Nursing Facility to Appointment",
    "Skilled Nursing Facility to Hospital",
    "Social Event Injury Requiring Medical Attention",
    "Specialist Visit",
    "Speech Therapy",
    "Sports Arena Injury Requiring Medical Attention",
    "Substance Abuse Treatment Center Appointment",
    "Urgent Care Visit",
    "Urology",
    "Vaccination Appointment",
    "Workplace Injury Requiring Medical Attention",
    "Other Medical Appointment"
  ];

  @override
  void initState() {
    super.initState();
    _searchController = TextEditingController();
    _filteredAppointmentTypes = _appointmentTypes;

    _searchController.addListener(() {
      _filterAppointmentTypes();
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _filterAppointmentTypes() {
    final query = _searchController.text.toLowerCase();

    setState(() {
      if (query.isEmpty) {
        _filteredAppointmentTypes = _appointmentTypes;
      } else {
        _filteredAppointmentTypes = _appointmentTypes
            .where((type) => type.toLowerCase().contains(query))
            .toList();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      insetPadding: const EdgeInsets.symmetric(horizontal: 16),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(24, 24, 24, 16),
            child: Row(
              children: [
                const Expanded(
                  child: Text(
                    'Select Appointment Type',
                    style: TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Colors.black87,
                    ),
                  ),
                ),
                GestureDetector(
                  onTap: () => Navigator.of(context).pop(),
                  child: Container(
                    width: 40,
                    height: 40,
                    decoration: const BoxDecoration(
                      color: Color(0xFFF1F5F9),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.close,
                      color: Colors.black54,
                    ),
                  ),
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24),
            child: Container(
              height: 56,
              decoration: BoxDecoration(
                // color: const Color(0xFFF1F5F9),
                borderRadius: BorderRadius.circular(12),
              ),
              child: TextField(
                controller: _searchController,
                decoration: const InputDecoration(
                  prefixIcon: Icon(
                    Icons.search,
                    color: Colors.black54,
                  ),
                  border: InputBorder.none,
                  hintText: 'Search',
                  hintStyle: TextStyle(
                    color: Colors.black38,
                    fontSize: 16,
                  ),
                  contentPadding: EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 16,
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),
          ConstrainedBox(
            constraints: BoxConstraints(
              maxHeight: MediaQuery.of(context).size.height * 0.6,
            ),
            child: ListView.builder(
              shrinkWrap: true,
              itemCount: _filteredAppointmentTypes.length,
              itemBuilder: (context, index) {
                final type = _filteredAppointmentTypes[index];
                return ListTile(
                  contentPadding:
                      const EdgeInsets.symmetric(horizontal: 24, vertical: 4),
                  leading: Container(
                    width: 48,
                    height: 48,
                    decoration: BoxDecoration(
                      color: const Color(0xFFE1F5FE),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Icon(
                      Icons.medical_services_outlined,
                      color: AppColor.primaryBlue,
                      size: 24,
                    ),
                  ),
                  title: Text(
                    type,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  onTap: () {
                    widget.onSelected(type);
                    Navigator.of(context).pop();
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

/// Shows the appointment type selection modal
Future<void> showAppointmentTypeModal({
  required BuildContext context,
  String? initialValue,
  required Function(String) onSelected,
}) async {
  await showDialog(
    context: context,
    builder: (context) => AppointmentTypeModal(
      initialValue: initialValue,
      onSelected: onSelected,
    ),
  );
}
