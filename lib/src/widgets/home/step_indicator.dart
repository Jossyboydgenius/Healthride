import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

class StepIndicator extends StatelessWidget {
  final int currentStep;
  final int totalSteps;
  final List<String> titles;

  const StepIndicator({
    super.key,
    required this.currentStep,
    required this.totalSteps,
    required this.titles,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (currentStep < titles.length)
          Row(
            children: [
              Text(
                titles[currentStep],
                style: const TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.bold,
                  color: AppColor.textDarkBlue,
                  letterSpacing: -0.5,
                ),
              ),
              const Spacer(),
              // Step counter
              Container(
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      AppColor.primaryBlue.withOpacity(0.1),
                      AppColor.primaryPurple.withOpacity(0.1),
                    ],
                  ),
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.symmetric(
                  horizontal: 12,
                  vertical: 4,
                ),
                child: Text(
                  '${currentStep + 1}/$totalSteps',
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    color: AppColor.primaryPurple,
                  ),
                ),
              ),
            ],
          ),
        const SizedBox(height: 20),
        // Progress indicator
        ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: Container(
            height: 8,
            width: double.infinity,
            color: AppColor.textLightGray.withOpacity(0.1),
            child: FractionallySizedBox(
              alignment: Alignment.centerLeft,
              widthFactor: _calculateProgress(),
              child: Container(
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [
                      AppColor.primaryBlue,
                      AppColor.primaryPurple,
                    ],
                  ),
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  double _calculateProgress() {
    return currentStep / (totalSteps - 1).clamp(1, double.infinity);
  }
}
