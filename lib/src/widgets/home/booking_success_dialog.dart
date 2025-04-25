import 'dart:math' as math;
import 'package:flutter/material.dart';
import '../../constants/app_color.dart';
import 'dart:async';

/// Dialog to show when booking is successful
class BookingSuccessDialog extends StatefulWidget {
  /// Callback when dialog is dismissed
  final VoidCallback onDismiss;

  /// Creates a booking success dialog
  const BookingSuccessDialog({
    Key? key,
    required this.onDismiss,
  }) : super(key: key);

  @override
  State<BookingSuccessDialog> createState() => _BookingSuccessDialogState();
}

class _BookingSuccessDialogState extends State<BookingSuccessDialog>
    with TickerProviderStateMixin {
  late AnimationController _scaleController;
  late Animation<double> _scaleAnimation;

  late AnimationController _confettiController;
  late Animation<double> _confettiAnimation;

  // For auto-closing
  Timer? _autoCloseTimer;

  @override
  void initState() {
    super.initState();

    // Scale animation for the card entry
    _scaleController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _scaleAnimation = CurvedAnimation(
      parent: _scaleController,
      // Use spring curve to match the Kotlin implementation
      curve: const ElasticOutCurve(0.6),
    );

    // Confetti animation with delay
    _confettiController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );

    _confettiAnimation = CurvedAnimation(
      parent: _confettiController,
      // Use spring curve to match the Kotlin implementation
      curve: const ElasticOutCurve(0.6),
    );

    // Start the scale animation immediately
    _scaleController.forward();

    // Start the confetti animation after a delay
    Future.delayed(const Duration(milliseconds: 400), () {
      _confettiController.forward();
    });

    // Auto-close after 5 seconds
    _autoCloseTimer = Timer(const Duration(seconds: 5), () {
      widget.onDismiss();
    });
  }

  @override
  void dispose() {
    _scaleController.dispose();
    _confettiController.dispose();
    _autoCloseTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      elevation: 0,
      child: AnimatedBuilder(
        animation: _scaleController,
        builder: (context, child) {
          return Transform.scale(
            scale: _scaleAnimation.value,
            child: Opacity(
              opacity: _scaleAnimation.value.clamp(0.0, 1.0),
              child: Container(
                width: MediaQuery.of(context).size.width * 0.9,
                padding: const EdgeInsets.all(32),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(28),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.1),
                      spreadRadius: 1,
                      blurRadius: 10,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Success animation container with fallback for safety
                    AnimatedBuilder(
                      animation: _confettiController,
                      builder: (context, child) {
                        return Container(
                          width: 100,
                          height: 100,
                          margin: const EdgeInsets.only(bottom: 24),
                          child: Stack(
                            alignment: Alignment.center,
                            children: [
                              // Success circle background
                              Container(
                                width: 100,
                                height: 100,
                                decoration: BoxDecoration(
                                  shape: BoxShape.circle,
                                  gradient: RadialGradient(
                                    colors: [
                                      const Color(0xFF10B981).withOpacity(0.1),
                                      const Color(0xFF10B981).withOpacity(0.05),
                                    ],
                                  ),
                                ),
                              ),

                              // Success icon with animation
                              Transform.scale(
                                scale: _confettiAnimation.value,
                                child: const Icon(
                                  Icons.check_circle,
                                  color: Color(0xFF10B981),
                                  size: 64,
                                ),
                              ),

                              // Confetti particles
                              if (_confettiAnimation.value > 0)
                                CustomPaint(
                                  painter: ConfettiPainter(
                                    progress: _confettiAnimation.value,
                                  ),
                                  size: const Size(100, 100),
                                ),
                            ],
                          ),
                        );
                      },
                    ),

                    const Text(
                      "Ride Booked!",
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: AppColor.textDarkBlue,
                      ),
                    ),

                    const SizedBox(height: 16),

                    const Text(
                      "Your ride has been scheduled successfully. We'll send you a reminder before your pickup time.",
                      style: TextStyle(
                        fontSize: 16,
                        color: AppColor.textMediumGray,
                      ),
                      textAlign: TextAlign.center,
                    ),

                    const SizedBox(height: 32),

                    // Button
                    SizedBox(
                      width: double.infinity,
                      height: 56,
                      child: ElevatedButton(
                        onPressed: widget.onDismiss,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppColor.primaryBlue,
                          foregroundColor: Colors.white,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(24),
                          ),
                        ),
                        child: const Text(
                          "Great!",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

/// Custom painter for the confetti particles
class ConfettiPainter extends CustomPainter {
  final double progress;

  ConfettiPainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    if (progress < 0.1) return;

    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;
    final particles = 12;
    final particleRadius = 4.0;

    for (int i = 0; i < particles; i++) {
      final angle = (i * 360 / particles) * (math.pi / 180);
      final x = center.dx + radius * 0.8 * math.cos(angle);
      final y = center.dy + radius * 0.8 * math.sin(angle);

      final paint = Paint();

      // Alternate colors for particles
      if (i % 3 == 0) {
        paint.color = AppColor.primaryBlue;
      } else if (i % 3 == 1) {
        paint.color = AppColor.primaryPurple;
      } else {
        paint.color = Colors.teal; // Represents AccentTeal in Kotlin
      }

      // Draw the particle with size scaled by progress
      canvas.drawCircle(
        Offset(x, y),
        particleRadius * progress,
        paint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant ConfettiPainter oldDelegate) {
    return oldDelegate.progress != progress;
  }
}
