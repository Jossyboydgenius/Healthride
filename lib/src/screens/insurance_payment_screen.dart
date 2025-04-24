import 'package:flutter/material.dart';
import '../constants/app_color.dart';
import '../models/user.dart';

// Insurance Info model
class InsuranceInfo {
  final String provider;
  final String planType;
  final String memberID;
  final String groupNumber;
  final String expirationDate;
  final String status;

  InsuranceInfo({
    required this.provider,
    required this.planType,
    required this.memberID,
    required this.groupNumber,
    required this.expirationDate,
    required this.status,
  });
}

// Payment Method model
enum PaymentType { creditCard, insurance, paypal }

class PaymentMethod {
  final String id;
  final PaymentType type;
  final String name;
  final String details;
  final bool isDefault;

  PaymentMethod({
    required this.id,
    required this.type,
    required this.name,
    required this.details,
    required this.isDefault,
  });
}

class InsurancePaymentScreen extends StatefulWidget {
  final User? user;

  const InsurancePaymentScreen({
    Key? key,
    this.user,
  }) : super(key: key);

  @override
  State<InsurancePaymentScreen> createState() => _InsurancePaymentScreenState();
}

class _InsurancePaymentScreenState extends State<InsurancePaymentScreen>
    with SingleTickerProviderStateMixin {
  bool _showContent = false;
  int _selectedSection = 0;

  // Sample data
  late InsuranceInfo _insuranceInfo;
  late List<PaymentMethod> _paymentMethods;

  @override
  void initState() {
    super.initState();

    // Initialize sample data
    _insuranceInfo = InsuranceInfo(
      provider: widget.user?.insuranceProvider ?? "BlueCross BlueShield",
      planType: "PPO Gold",
      memberID: "XYZ123456789",
      groupNumber: "G-5555555",
      expirationDate: "12/31/2025",
      status: "Active",
    );

    _paymentMethods = [
      PaymentMethod(
        id: "pm_1",
        type: PaymentType.creditCard,
        name: "Visa ending in 4242",
        details: "Expires 12/25",
        isDefault: true,
      ),
      PaymentMethod(
        id: "pm_2",
        type: PaymentType.insurance,
        name: widget.user?.insuranceProvider ?? "BlueCross BlueShield",
        details: "Billing through insurance",
        isDefault: false,
      ),
    ];

    // Trigger animation after a delay
    Future.delayed(const Duration(milliseconds: 100), () {
      if (mounted) {
        setState(() {
          _showContent = true;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      initialIndex: _selectedSection,
      child: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.white,
          elevation: 0,
          title: Text(
            _selectedSection == 0 ? 'Insurance Information' : 'Payment Methods',
            style: const TextStyle(
              color: AppColor.textDarkBlue,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          leading: IconButton(
            icon: const Icon(Icons.arrow_back, color: AppColor.textDarkBlue),
            onPressed: () => Navigator.pop(context),
          ),
          bottom: PreferredSize(
            preferredSize: const Size.fromHeight(60),
            child: Container(
              decoration: const BoxDecoration(
                border: Border(
                  bottom: BorderSide(
                    color: Color(0xFFE5E7EB),
                    width: 0.5,
                  ),
                ),
              ),
              child: TabBar(
                onTap: (index) {
                  setState(() {
                    _selectedSection = index;
                  });
                },
                indicator: const BoxDecoration(
                  border: Border(
                    bottom: BorderSide(
                      color: AppColor.primaryBlue,
                      width: 3.0,
                    ),
                  ),
                ),
                labelColor: AppColor.primaryBlue,
                unselectedLabelColor: AppColor.textMediumGray,
                tabs: [
                  _buildCustomTab(
                      0, Icons.medical_services_outlined, 'Insurance'),
                  _buildCustomTab(1, Icons.payment_outlined, 'Payment'),
                ],
              ),
            ),
          ),
        ),
        body: TabBarView(
          children: [
            _buildInsuranceSection(),
            _buildPaymentMethodsSection(),
          ],
        ),
      ),
    );
  }

  Widget _buildCustomTab(int index, IconData icon, String label) {
    final isSelected = _selectedSection == index;
    return Tab(
      height: 50,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            icon,
            size: 20,
            color: isSelected ? AppColor.primaryBlue : AppColor.textMediumGray,
          ),
          const SizedBox(width: 8),
          Text(
            label,
            style: TextStyle(
              fontSize: 16,
              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInsuranceSection() {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        // Insurance Card
        Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          elevation: 2,
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Insurance provider and status
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      "BlueCross BlueShield",
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: AppColor.textDarkBlue,
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 10,
                        vertical: 5,
                      ),
                      decoration: BoxDecoration(
                        color: AppColor.successGreen.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: const Row(
                        children: [
                          Icon(
                            Icons.check_circle,
                            size: 14,
                            color: AppColor.successGreen,
                          ),
                          SizedBox(width: 4),
                          Text(
                            "Active",
                            style: TextStyle(
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                              color: AppColor.successGreen,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),

                // Plan type
                _buildInsuranceDetailRow(
                  icon: Icons.policy_outlined,
                  label: "Plan Type",
                  value: "PPO Gold",
                ),
                const Divider(
                    height: 24, thickness: 0.5, color: Color(0xFFE5E7EB)),

                // Member ID
                _buildInsuranceDetailRow(
                  icon: Icons.badge_outlined,
                  label: "Member ID",
                  value: "XYZ123456789",
                ),
                const Divider(
                    height: 24, thickness: 0.5, color: Color(0xFFE5E7EB)),

                // Group Number
                _buildInsuranceDetailRow(
                  icon: Icons.group_outlined,
                  label: "Group Number",
                  value: "G-5555555",
                ),
                const Divider(
                    height: 24, thickness: 0.5, color: Color(0xFFE5E7EB)),

                // Expiration
                _buildInsuranceDetailRow(
                  icon: Icons.calendar_today_outlined,
                  label: "Expiration",
                  value: "12/31/2025",
                ),
              ],
            ),
          ),
        ),

        const SizedBox(height: 16),

        // Verification Status
        Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          elevation: 0,
          color: AppColor.primaryBlue.withOpacity(0.1),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                Container(
                  width: 40,
                  height: 40,
                  decoration: const BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.white,
                  ),
                  child: const Icon(
                    Icons.verified_outlined,
                    color: AppColor.primaryBlue,
                    size: 24,
                  ),
                ),
                const SizedBox(width: 16),
                const Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Verified Account",
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                          color: AppColor.textDarkBlue,
                        ),
                      ),
                      Text(
                        "Your insurance has been verified and is active for rides",
                        style: TextStyle(
                          fontSize: 14,
                          color: AppColor.textMediumGray,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),

        const SizedBox(height: 16),

        // Update Button
        ElevatedButton.icon(
          onPressed: () {
            // Update insurance info
          },
          icon: const Icon(Icons.edit, size: 20),
          label: const Text(
            "Update Insurance Information",
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
          style: ElevatedButton.styleFrom(
            backgroundColor: AppColor.primaryBlue,
            foregroundColor: Colors.white,
            minimumSize: const Size(double.infinity, 56),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(28),
            ),
          ),
        ),

        const SizedBox(height: 16),

        // Coverage Information
        Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          elevation: 2,
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  "Coverage Information",
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: AppColor.textDarkBlue,
                  ),
                ),
                const SizedBox(height: 16),
                _buildCoverageItem(
                  title: "Medical Transport",
                  details: "Covered at 100% with prior authorization",
                  isCovered: true,
                ),
                const Divider(
                    height: 24, thickness: 0.5, color: Color(0xFFE5E7EB)),
                _buildCoverageItem(
                  title: "Non-Emergency Transport",
                  details: "Covered at 80%, \$25 copay may apply",
                  isCovered: true,
                ),
                const Divider(
                    height: 24, thickness: 0.5, color: Color(0xFFE5E7EB)),
                _buildCoverageItem(
                  title: "Out of Network",
                  details: "Not covered",
                  isCovered: false,
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildInsuranceDetailRow({
    required IconData icon,
    required String label,
    required String value,
  }) {
    return Padding(
      padding: const EdgeInsets.only(top: 12),
      child: Row(
        children: [
          Container(
            width: 36,
            height: 36,
            decoration: const BoxDecoration(
              shape: BoxShape.circle,
              color: AppColor.primaryBlueLight,
            ),
            child: Icon(
              icon,
              size: 20,
              color: AppColor.primaryBlue,
            ),
          ),
          const SizedBox(width: 12),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),
              Text(
                value,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                  color: AppColor.textDarkBlue,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCoverageItem({
    required String title,
    required String details,
    required bool isCovered,
  }) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          width: 36,
          height: 36,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: isCovered
                ? AppColor.successGreen.withOpacity(0.1)
                : AppColor.errorRed.withOpacity(0.1),
          ),
          child: Icon(
            isCovered ? Icons.check_circle : Icons.cancel,
            size: 20,
            color: isCovered ? AppColor.successGreen : AppColor.errorRed,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                  color: AppColor.textDarkBlue,
                ),
              ),
              Text(
                details,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildPaymentMethodsSection() {
    return Container(
      color: AppColor.backgroundGray,
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Payment methods list
            Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              elevation: 2,
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      "Payment Methods",
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: AppColor.textDarkBlue,
                      ),
                    ),

                    const SizedBox(height: 16),
                    const Divider(thickness: 0.5, color: Color(0xFFE5E7EB)),
                    const SizedBox(height: 8),

                    // Credit card
                    _buildPaymentMethodItem(
                      icon: Icons.credit_card,
                      iconColor: AppColor.primaryBlue,
                      iconBackground: AppColor.primaryBlueLight,
                      title: "Visa ending in 4242",
                      subtitle: "Expires 12/25",
                      isDefault: true,
                    ),

                    const SizedBox(height: 16),
                    const Divider(thickness: 0.5, color: Color(0xFFE5E7EB)),
                    const SizedBox(height: 16),

                    // Insurance
                    _buildPaymentMethodItem(
                      icon: Icons.shield_outlined,
                      iconColor: AppColor.primaryPurple,
                      iconBackground: AppColor.primaryPurpleLight,
                      title: "BlueCross BlueShield",
                      subtitle: "Billing through insurance",
                      isDefault: false,
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 16),

            // Add Payment Method Button
            ElevatedButton.icon(
              onPressed: () {
                // Add payment method
              },
              icon: const Icon(Icons.add, size: 20),
              label: const Text(
                "Add Payment Method",
                style: TextStyle(fontWeight: FontWeight.w600),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColor.primaryBlue,
                foregroundColor: Colors.white,
                minimumSize: const Size(double.infinity, 56),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(28),
                ),
              ),
            ),

            const SizedBox(height: 16),

            // Billing History
            Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              elevation: 2,
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          "Billing History",
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: AppColor.textDarkBlue,
                          ),
                        ),
                        TextButton(
                          onPressed: () {
                            // View all history
                          },
                          child: const Text(
                            "View All",
                            style: TextStyle(
                              color: AppColor.primaryBlue,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    const Divider(thickness: 0.5, color: Color(0xFFE5E7EB)),
                    const SizedBox(height: 16),
                    _buildBillingHistoryItem(
                      date: "Mar 15, 2025",
                      description: "Ride to Medical Center",
                      paymentType: "Covered by Insurance",
                      amount: "\$28.50",
                    ),
                    const Divider(
                        height: 32, thickness: 0.5, color: Color(0xFFE5E7EB)),
                    _buildBillingHistoryItem(
                      date: "Mar 10, 2025",
                      description: "Ride to City Hospital",
                      paymentType: "Covered by Insurance",
                      amount: "\$32.75",
                    ),
                    const Divider(
                        height: 32, thickness: 0.5, color: Color(0xFFE5E7EB)),
                    _buildBillingHistoryItem(
                      date: "Feb 28, 2025",
                      description: "Ride to Therapy Center",
                      paymentType: "Credit Card Payment",
                      amount: "\$15.00",
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPaymentMethodItem({
    required IconData icon,
    required Color iconColor,
    required Color iconBackground,
    required String title,
    required String subtitle,
    required bool isDefault,
  }) {
    return Row(
      children: [
        // Icon
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: iconBackground,
          ),
          child: Icon(
            icon,
            color: iconColor,
            size: 24,
          ),
        ),
        const SizedBox(width: 16),
        // Content
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                  if (isDefault) ...[
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 2,
                      ),
                      decoration: BoxDecoration(
                        color: AppColor.primaryBlue.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Text(
                        "DEFAULT",
                        style: TextStyle(
                          fontSize: 10,
                          fontWeight: FontWeight.bold,
                          color: AppColor.primaryBlue,
                        ),
                      ),
                    ),
                  ],
                ],
              ),
              Text(
                subtitle,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),
            ],
          ),
        ),
        // Edit icon
        IconButton(
          icon: const Icon(
            Icons.edit,
            color: AppColor.textMediumGray,
            size: 20,
          ),
          onPressed: () {
            // Edit payment method
          },
        ),
      ],
    );
  }

  Widget _buildBillingHistoryItem({
    required String date,
    required String description,
    required String paymentType,
    required String amount,
  }) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Date column
        SizedBox(
          width: 70,
          child: Text(
            date,
            style: const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w500,
              color: AppColor.textMediumGray,
            ),
          ),
        ),
        // Description column
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                description,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                  color: AppColor.textDarkBlue,
                ),
              ),
              Text(
                paymentType,
                style: const TextStyle(
                  fontSize: 12,
                  color: AppColor.textMediumGray,
                ),
              ),
            ],
          ),
        ),
        // Amount
        Text(
          amount,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
            color: AppColor.textDarkBlue,
          ),
        ),
      ],
    );
  }
}
