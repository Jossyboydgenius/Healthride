import 'package:flutter/material.dart';
import '../../../constants/app_color.dart';
import '../../../models/payment_method.dart';

class PaymentMethodStep extends StatelessWidget {
  final PaymentMethod selectedMethod;
  final Function(PaymentMethod) onMethodSelected;
  final VoidCallback onBack;
  final VoidCallback onNext;

  const PaymentMethodStep({
    Key? key,
    required this.selectedMethod,
    required this.onMethodSelected,
    required this.onBack,
    required this.onNext,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.symmetric(horizontal: 24.0, vertical: 8.0),
          child: Text(
            'Select Payment Method',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        const SizedBox(height: 16),
        _buildPaymentMethodOptions(),
        const SizedBox(height: 16),
        _buildAddNewMethodButton(),
        const SizedBox(height: 24),
        _buildActionButtons(),
      ],
    );
  }

  Widget _buildPaymentMethodOptions() {
    return Column(
      children: [
        _PaymentMethodOption(
          title: 'Credit Card',
          subtitle: '**** **** **** 4321',
          icon: Icons.credit_card,
          isSelected: selectedMethod == PaymentMethod.creditCard,
          onTap: () => onMethodSelected(PaymentMethod.creditCard),
        ),
        const SizedBox(height: 12),
        _PaymentMethodOption(
          title: 'PayPal',
          subtitle: 'user@example.com',
          icon: Icons.paypal,
          isSelected: selectedMethod == PaymentMethod.paypal,
          onTap: () => onMethodSelected(PaymentMethod.paypal),
        ),
        const SizedBox(height: 12),
        _PaymentMethodOption(
          title: 'Apple Pay',
          subtitle: 'iPhone & Apple Watch',
          icon: Icons.apple,
          isSelected: selectedMethod == PaymentMethod.applePay,
          onTap: () => onMethodSelected(PaymentMethod.applePay),
        ),
        const SizedBox(height: 12),
        _PaymentMethodOption(
          title: 'Google Pay',
          subtitle: 'Instant Payment',
          icon: Icons.g_mobiledata,
          isSelected: selectedMethod == PaymentMethod.googlePay,
          onTap: () => onMethodSelected(PaymentMethod.googlePay),
        ),
        const SizedBox(height: 12),
        _PaymentMethodOption(
          title: 'Cash',
          subtitle: 'Pay after the ride',
          icon: Icons.attach_money,
          isSelected: selectedMethod == PaymentMethod.cash,
          onTap: () => onMethodSelected(PaymentMethod.cash),
        ),
      ],
    );
  }

  Widget _buildAddNewMethodButton() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24.0),
      child: OutlinedButton(
        onPressed: () {
          // Show add payment method dialog
        },
        style: OutlinedButton.styleFrom(
          side: const BorderSide(color: AppColor.borderColor),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
        ),
        child: const Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.add_circle_outline,
              color: AppColor.primaryPurple,
              size: 18,
            ),
            SizedBox(width: 8),
            Text(
              'Add New Payment Method',
              style: TextStyle(
                color: AppColor.primaryPurple,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActionButtons() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24.0),
      child: Row(
        children: [
          Expanded(
            flex: 2,
            child: OutlinedButton(
              onPressed: onBack,
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: AppColor.borderColor, width: 1.5),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                padding:
                    const EdgeInsets.symmetric(vertical: 16, horizontal: 32),
              ),
              child: const Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.arrow_back,
                    size: 18,
                    color: Colors.black,
                  ),
                  SizedBox(width: 8),
                  Text(
                    'Back',
                    style: TextStyle(
                      color: Colors.black,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            flex: 3,
            child: ElevatedButton(
              onPressed: onNext,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColor.primaryPurple,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text(
                'Continue',
                style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _PaymentMethodOption extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;
  final bool isSelected;
  final VoidCallback onTap;

  const _PaymentMethodOption({
    Key? key,
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.isSelected,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24.0),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            border: Border.all(
              color: isSelected ? AppColor.primaryPurple : AppColor.borderColor,
              width: 1.5,
            ),
            borderRadius: BorderRadius.circular(12),
            color: isSelected
                ? AppColor.primaryPurple.withOpacity(0.1)
                : Colors.white,
          ),
          child: Row(
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: isSelected
                      ? AppColor.primaryPurple.withOpacity(0.2)
                      : Colors.grey.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  icon,
                  color: isSelected ? AppColor.primaryPurple : Colors.grey,
                  size: 24,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                        color:
                            isSelected ? AppColor.primaryPurple : Colors.black,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      subtitle,
                      style: TextStyle(
                        fontSize: 13,
                        color: isSelected
                            ? AppColor.primaryPurple.withOpacity(0.8)
                            : Colors.grey,
                      ),
                    ),
                  ],
                ),
              ),
              Radio<bool>(
                value: true,
                groupValue: isSelected,
                onChanged: (_) => onTap(),
                activeColor: AppColor.primaryPurple,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
