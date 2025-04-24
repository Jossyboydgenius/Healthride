import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../constants/app_color.dart';
import '../models/verification_status.dart';
import '../routes/app_routes.dart';

/// A screen that displays the status of verification
class VerificationStatusScreen extends StatelessWidget {
  /// Creates a verification status screen
  const VerificationStatusScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // For demo purposes, we'll use PENDING status
    // In a real app, this would come from a service or provider
    final status = VerificationStatus.pending;
    final submissionDate = DateTime.now().subtract(const Duration(days: 1));
    final estimatedCompletionDate = DateTime.now().add(const Duration(days: 2));

    final progress = _getProgressForStatus(status);
    final dateFormatter = DateFormat('MMM d, yyyy');
    final formattedSubmissionDate = dateFormatter.format(submissionDate);
    final formattedCompletionDate =
        dateFormatter.format(estimatedCompletionDate);

    return Scaffold(
      appBar: status != VerificationStatus.pending
          ? AppBar(
              title: const Text('Verification Status'),
              backgroundColor: Colors.transparent,
              elevation: 0,
            )
          : null,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              // Status indicator
              _StatusIndicator(status: status),
              const SizedBox(height: 32),

              // Progress tracker
              _VerificationProgressTracker(progress: progress, status: status),
              const SizedBox(height: 32),

              // Status card
              Container(
                width: double.infinity,
                decoration: BoxDecoration(
                  color: _getBackgroundColorForStatus(status),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color: _getBorderColorForStatus(status),
                    width: 1,
                  ),
                ),
                padding: const EdgeInsets.all(24.0),
                child: Column(
                  children: [
                    // Status icon
                    Icon(
                      _getIconForStatus(status),
                      size: 48,
                      color: _getColorForStatus(status),
                    ),
                    const SizedBox(height: 16),

                    // Status title and message
                    Text(
                      _getTitleForStatus(status),
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: AppColor.textDarkBlue,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      _getMessageForStatus(status),
                      style: const TextStyle(
                        fontSize: 16,
                        color: AppColor.textMediumGray,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 24),

                    // Details
                    if (status == VerificationStatus.pending ||
                        status == VerificationStatus.verified)
                      Column(
                        children: [
                          _DetailRow(
                            icon: Icons.calendar_today,
                            title: 'Submission Date',
                            value: formattedSubmissionDate,
                          ),
                          const SizedBox(height: 16),
                          _DetailRow(
                            icon: Icons.access_time,
                            title: 'Estimated Completion',
                            value: formattedCompletionDate,
                          ),
                        ],
                      ),

                    if (status == VerificationStatus.rejected)
                      Column(
                        children: [
                          _DetailRow(
                            icon: Icons.error_outline,
                            title: 'Rejection Reason',
                            value:
                                'The insurance card image was unclear. Please upload a clearer image.',
                            valueColor: AppColor.errorRed,
                          ),
                          const SizedBox(height: 16),
                          _DetailRow(
                            icon: Icons.calendar_today,
                            title: 'Rejection Date',
                            value: formattedSubmissionDate,
                          ),
                        ],
                      ),
                  ],
                ),
              ),
              const SizedBox(height: 32),

              // Action Buttons
              _buildActionsForStatus(status, context),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildActionsForStatus(
      VerificationStatus status, BuildContext context) {
    switch (status) {
      case VerificationStatus.pending:
        return Row(
          children: [
            Expanded(
              child: OutlinedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.help_outline),
                label: const Text('Contact Support'),
                style: OutlinedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
                  side: const BorderSide(color: AppColor.primaryBlue),
                ),
              ),
            ),
          ],
        );

      case VerificationStatus.verified:
        return Row(
          children: [
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  Navigator.of(context).pushReplacementNamed(AppRoutes.home);
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColor.primaryBlue,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
                ),
                child: const Text(
                  'Continue to Home',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        );

      case VerificationStatus.rejected:
        return Column(
          children: [
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: () {
                  Navigator.of(context)
                      .pushReplacementNamed(AppRoutes.insuranceUpload);
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColor.primaryBlue,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
                ),
                child: const Text(
                  'Resubmit Documents',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              height: 56,
              child: OutlinedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.help_outline),
                label: const Text('Contact Support'),
                style: OutlinedButton.styleFrom(
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
                  side: const BorderSide(color: AppColor.primaryBlue),
                ),
              ),
            ),
          ],
        );

      case VerificationStatus.notSubmitted:
        return SizedBox(
          width: double.infinity,
          height: 56,
          child: ElevatedButton(
            onPressed: () {
              Navigator.of(context)
                  .pushReplacementNamed(AppRoutes.insuranceUpload);
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColor.primaryBlue,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(28),
              ),
            ),
            child: const Text(
              'Start Verification',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        );
    }
  }

  double _getProgressForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
      case VerificationStatus.rejected:
        return 0.5;
      case VerificationStatus.verified:
        return 1.0;
      case VerificationStatus.notSubmitted:
        return 0.0;
    }
  }

  IconData _getIconForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
        return Icons.hourglass_top_outlined;
      case VerificationStatus.verified:
        return Icons.check_circle_outline;
      case VerificationStatus.rejected:
        return Icons.highlight_off_outlined;
      case VerificationStatus.notSubmitted:
        return Icons.info_outline;
    }
  }

  String _getTitleForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
        return 'Verification in Progress';
      case VerificationStatus.verified:
        return 'Verification Successful';
      case VerificationStatus.rejected:
        return 'Verification Failed';
      case VerificationStatus.notSubmitted:
        return 'Start Verification';
    }
  }

  String _getMessageForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
        return 'We\'re reviewing your documents. This typically takes 1-2 business days.';
      case VerificationStatus.verified:
        return 'Your account has been successfully verified. You can now access all features.';
      case VerificationStatus.rejected:
        return 'Your verification was unsuccessful. Please review the details below and resubmit.';
      case VerificationStatus.notSubmitted:
        return 'Please upload your insurance and identification documents to get started.';
    }
  }

  Color _getColorForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
        return AppColor.infoBlue;
      case VerificationStatus.verified:
        return AppColor.successGreen;
      case VerificationStatus.rejected:
        return AppColor.errorRed;
      case VerificationStatus.notSubmitted:
        return AppColor.textMediumGray;
    }
  }

  Color _getBackgroundColorForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
        return AppColor.infoBlue.withOpacity(0.05);
      case VerificationStatus.verified:
        return AppColor.successGreen.withOpacity(0.05);
      case VerificationStatus.rejected:
        return AppColor.errorRed.withOpacity(0.05);
      case VerificationStatus.notSubmitted:
        return Colors.white;
    }
  }

  Color _getBorderColorForStatus(VerificationStatus status) {
    switch (status) {
      case VerificationStatus.pending:
        return AppColor.infoBlue.withOpacity(0.2);
      case VerificationStatus.verified:
        return AppColor.successGreen.withOpacity(0.2);
      case VerificationStatus.rejected:
        return AppColor.errorRed.withOpacity(0.2);
      case VerificationStatus.notSubmitted:
        return AppColor.textLightGray.withOpacity(0.2);
    }
  }
}

/// Displays a detail row with an icon, title, and value
class _DetailRow extends StatelessWidget {
  final IconData icon;
  final String title;
  final String value;
  final Color valueColor;

  const _DetailRow({
    required this.icon,
    required this.title,
    required this.value,
    this.valueColor = AppColor.textDarkBlue,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(
          icon,
          size: 20,
          color: AppColor.textMediumGray,
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),
              const SizedBox(height: 2),
              Text(
                value,
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                  color: valueColor,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

/// Displays the status indicator chip at the top of the screen
class _StatusIndicator extends StatelessWidget {
  final VerificationStatus status;

  const _StatusIndicator({required this.status});

  @override
  Widget build(BuildContext context) {
    final color = _getColorForStatus();
    final text = status.name;
    final icon = _getIconForStatus();

    return Container(
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(50),
      ),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            icon,
            size: 16,
            color: color,
          ),
          const SizedBox(width: 8),
          Text(
            text,
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.bold,
              color: color,
            ),
          ),
        ],
      ),
    );
  }

  Color _getColorForStatus() {
    switch (status) {
      case VerificationStatus.pending:
        return AppColor.infoBlue;
      case VerificationStatus.verified:
        return AppColor.successGreen;
      case VerificationStatus.rejected:
        return AppColor.errorRed;
      case VerificationStatus.notSubmitted:
        return AppColor.textMediumGray;
    }
  }

  IconData _getIconForStatus() {
    switch (status) {
      case VerificationStatus.pending:
        return Icons.hourglass_empty;
      case VerificationStatus.verified:
        return Icons.check_circle;
      case VerificationStatus.rejected:
        return Icons.error;
      case VerificationStatus.notSubmitted:
        return Icons.info;
    }
  }
}

/// Displays the progress tracker for the verification process
class _VerificationProgressTracker extends StatelessWidget {
  final double progress;
  final VerificationStatus status;

  const _VerificationProgressTracker({
    required this.progress,
    required this.status,
  });

  @override
  Widget build(BuildContext context) {
    final steps = ['Submitted', 'Reviewing', 'Verified'];

    return Column(
      children: [
        // Progress bar
        ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: LinearProgressIndicator(
            value: progress,
            backgroundColor: AppColor.backgroundGray.withOpacity(0.5),
            color: _getColorForStatus(),
            minHeight: 8,
          ),
        ),
        const SizedBox(height: 16),

        // Steps
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            for (int i = 0; i < steps.length; i++)
              _StepItem(
                step: steps[i],
                isCompleted: _isStepCompleted(i),
                isActive: _isStepActive(i),
                status: status,
              ),
          ],
        ),
      ],
    );
  }

  bool _isStepCompleted(int index) {
    switch (status) {
      case VerificationStatus.verified:
        return true;
      case VerificationStatus.rejected:
      case VerificationStatus.pending:
        return index < (status == VerificationStatus.pending ? 1 : 2);
      case VerificationStatus.notSubmitted:
        return false;
    }
  }

  bool _isStepActive(int index) {
    if (status == VerificationStatus.pending && index == 1) return true;
    if (status == VerificationStatus.notSubmitted && index == 0) return true;
    return false;
  }

  Color _getColorForStatus() {
    switch (status) {
      case VerificationStatus.pending:
        return AppColor.infoBlue;
      case VerificationStatus.verified:
        return AppColor.successGreen;
      case VerificationStatus.rejected:
        return AppColor.errorRed;
      case VerificationStatus.notSubmitted:
        return AppColor.textLightGray;
    }
  }
}

/// A single step item in the progress tracker
class _StepItem extends StatelessWidget {
  final String step;
  final bool isCompleted;
  final bool isActive;
  final VerificationStatus status;

  const _StepItem({
    required this.step,
    required this.isCompleted,
    required this.isActive,
    required this.status,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Circle indicator
        Container(
          width: 24,
          height: 24,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: _getBackgroundColor(),
          ),
          child: Center(
            child: _buildIcon(),
          ),
        ),
        const SizedBox(height: 4),

        // Step label
        Text(
          step,
          style: TextStyle(
            fontSize: 14,
            color: _getTextColor(),
            fontWeight:
                isCompleted || isActive ? FontWeight.w500 : FontWeight.normal,
          ),
        ),
      ],
    );
  }

  Widget? _buildIcon() {
    if (isCompleted) {
      return Icon(
        status == VerificationStatus.rejected && step == 'Verified'
            ? Icons.close
            : Icons.check,
        size: 16,
        color: Colors.white,
      );
    } else if (isActive && status == VerificationStatus.pending) {
      return Container(
        width: 8,
        height: 8,
        decoration: const BoxDecoration(
          shape: BoxShape.circle,
          color: AppColor.infoBlue,
        ),
      );
    }
    return null;
  }

  Color _getBackgroundColor() {
    if (isCompleted) {
      if (status == VerificationStatus.verified) {
        return AppColor.successGreen;
      } else if (status == VerificationStatus.rejected && step == 'Verified') {
        return AppColor.errorRed;
      } else {
        return AppColor.infoBlue;
      }
    } else if (isActive) {
      if (status == VerificationStatus.notSubmitted) {
        return AppColor.textLightGray.withOpacity(0.3);
      } else {
        return AppColor.infoBlue.withOpacity(0.1);
      }
    } else {
      return AppColor.backgroundGray;
    }
  }

  Color _getTextColor() {
    if (isCompleted &&
        status == VerificationStatus.rejected &&
        step == 'Verified') {
      return AppColor.errorRed;
    } else if (isCompleted || isActive) {
      return AppColor.textDarkBlue;
    } else {
      return AppColor.textMediumGray;
    }
  }
}
