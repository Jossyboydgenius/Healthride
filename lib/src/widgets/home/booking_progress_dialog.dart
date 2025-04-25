import 'dart:math' as math;
import 'package:flutter/material.dart';
import '../../constants/app_color.dart';
import 'booking_success_dialog.dart';

/// Dialog to show while booking is in progress
class BookingProgressDialog extends StatefulWidget {
  /// Callback when booking is complete
  final VoidCallback onBookingComplete;

  /// Creates a booking progress dialog
  const BookingProgressDialog({
    Key? key,
    required this.onBookingComplete,
  }) : super(key: key);

  @override
  State<BookingProgressDialog> createState() => _BookingProgressDialogState();
}

class _BookingProgressDialogState extends State<BookingProgressDialog>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _rotationAnimation;
  late Animation<double> _pulseAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..repeat();

    _rotationAnimation = Tween<double>(
      begin: 0.0,
      end: 2 * math.pi,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.linear,
    ));

    _pulseAnimation = TweenSequence<double>([
      TweenSequenceItem(tween: Tween<double>(begin: 1.0, end: 1.1), weight: 1),
      TweenSequenceItem(tween: Tween<double>(begin: 1.1, end: 1.0), weight: 1),
    ]).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeInOut,
    ));

    // Simulate a network request then show success dialog directly
    Future.delayed(const Duration(seconds: 2), () {
      // Show success dialog in place of current dialog
      Navigator.of(context).pop(); // Remove the progress dialog

      // Show success dialog immediately
      WidgetsBinding.instance.addPostFrameCallback((_) {
        showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return BookingSuccessDialog(
              onDismiss: widget.onBookingComplete,
            );
          },
        );
      });
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      elevation: 0,
      child: Container(
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(24),
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
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Animated progress ring
            AnimatedBuilder(
              animation: _controller,
              builder: (context, child) {
                return Container(
                  width: 100,
                  height: 100,
                  margin: const EdgeInsets.only(bottom: 24),
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      // Animated ring with rotation and scaling
                      Transform(
                        transform: Matrix4.identity()
                          ..rotateZ(_rotationAnimation.value)
                          ..scale(_pulseAnimation.value),
                        alignment: Alignment.center,
                        child: CustomPaint(
                          painter:
                              ProgressRingPainter(progress: _controller.value),
                          size: const Size(100, 100),
                        ),
                      ),

                      // Center car icon
                      const Icon(
                        Icons.directions_car_outlined,
                        color: AppColor.primaryBlue,
                        size: 28,
                      ),
                    ],
                  ),
                );
              },
            ),

            const Text(
              "Booking Your Ride",
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.w600,
                color: AppColor.textDarkBlue,
                letterSpacing: 0,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Custom painter for drawing the animated progress ring
class ProgressRingPainter extends CustomPainter {
  final double progress;

  ProgressRingPainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;
    final strokeWidth = 8.0;
    final rect =
        Rect.fromCircle(center: center, radius: radius - strokeWidth / 2);

    // Create the gradient from primary blue to primary purple
    final gradient = SweepGradient(
      startAngle: -math.pi / 2, // Start at the top
      endAngle: 3 * math.pi / 2, // End at the top (full circle)
      colors: [
        AppColor.primaryBlue,
        AppColor.primaryBlue,
        AppColor.primaryPurple,
        AppColor.primaryPurple,
      ],
      stops: [0.0, 0.25, 0.75, 1.0],
      transform:
          GradientRotation(-math.pi / 2), // Rotate gradient to start at top
    );

    // Create the paint for the progress arc
    final paint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..shader = gradient.createShader(rect);

    // Background track - light gray circle
    final backgroundPaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..color = Colors.grey.withOpacity(0.1);

    // Draw the background track
    canvas.drawCircle(center, radius - strokeWidth / 2, backgroundPaint);

    // Calculate the sweep angle based on progress and animate it
    final sweepAngle = 2 * math.pi * progress;

    // Draw the progress arc
    canvas.drawArc(
      rect,
      -math.pi / 2, // Start at the top
      sweepAngle,
      false,
      paint,
    );

    // Draw the dot at the end of the progress arc if progress > 0
    if (progress > 0) {
      final dotAngle = -math.pi / 2 + sweepAngle;
      final dotX = center.dx + (radius - strokeWidth / 2) * math.cos(dotAngle);
      final dotY = center.dy + (radius - strokeWidth / 2) * math.sin(dotAngle);

      // Determine dot color based on progress
      Color dotColor;
      if (progress <= 0.25) {
        dotColor = AppColor.primaryBlue;
      } else if (progress >= 0.75) {
        dotColor = AppColor.primaryPurple;
      } else {
        // Linearly interpolate between blue and purple
        final t = (progress - 0.25) / 0.5; // normalize to 0-1 range
        dotColor = Color.lerp(AppColor.primaryBlue, AppColor.primaryPurple, t)!;
      }

      final dotPaint = Paint()
        ..style = PaintingStyle.fill
        ..color = dotColor;

      canvas.drawCircle(Offset(dotX, dotY), strokeWidth / 1.5, dotPaint);
    }
  }

  @override
  bool shouldRepaint(ProgressRingPainter oldDelegate) {
    return oldDelegate.progress != progress;
  }
}
