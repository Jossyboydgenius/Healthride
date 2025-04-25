import 'dart:math';
import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// Dialog shown when booking is successfully completed
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
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );

    _scaleAnimation = Tween<double>(
      begin: 0.7,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.elasticOut,
    ));

    _controller.forward();

    // Auto-close dialog after 3 seconds
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        _dismissDialog();
      }
    });
  }

  void _dismissDialog() {
    Navigator.of(context).pop();
    widget.onDismiss();
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
      child: AnimatedBuilder(
        animation: _controller,
        builder: (context, child) {
          return Transform.scale(
            scale: _scaleAnimation.value,
            child: child,
          );
        },
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
              const SizedBox(height: 8),

              // Success checkmark with confetti
              Stack(
                alignment: Alignment.center,
                children: [
                  // Rotating Confetti
                  SizedBox(
                    width: 100,
                    height: 100,
                    child: RotatingConfetti(),
                  ),

                  // Success checkmark
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      color: AppColor.primaryPurple.withOpacity(0.1),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.check_rounded,
                      color: AppColor.primaryPurple,
                      size: 40,
                    ),
                  ),
                ],
              ),

              const SizedBox(height: 24),

              const Text(
                "Booking Successful!",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                  color: AppColor.textDarkBlue,
                  letterSpacing: 0,
                ),
              ),

              const SizedBox(height: 8),

              const Text(
                "Your ride has been scheduled successfully.",
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 14,
                  color: AppColor.textMediumGray,
                ),
              ),

              const SizedBox(height: 24),

              ElevatedButton(
                onPressed: _dismissDialog,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColor.primaryPurple,
                  foregroundColor: Colors.white,
                  minimumSize: const Size(150, 45),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: const Text(
                  "Great!",
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 16,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Widget that displays rotating confetti around the success mark
class RotatingConfetti extends StatefulWidget {
  const RotatingConfetti({Key? key}) : super(key: key);

  @override
  State<RotatingConfetti> createState() => _RotatingConfettiState();
}

class _RotatingConfettiState extends State<RotatingConfetti>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late List<ConfettiPiece> _confettiPieces;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 4000),
    )..repeat();

    // Create confetti pieces
    final random = Random();
    _confettiPieces = List.generate(12, (index) {
      return ConfettiPiece(
        angle: index * (pi / 6), // Distribute evenly in a circle
        distance: 40 + random.nextDouble() * 10, // Vary distance slightly
        color: index % 3 == 0
            ? AppColor.primaryBlue
            : index % 3 == 1
                ? AppColor.primaryPurple
                : Colors.amber,
        size: 5 + random.nextDouble() * 5,
        rotationSpeed: 0.2 + random.nextDouble() * 0.4,
      );
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return CustomPaint(
          size: const Size(100, 100),
          painter: ConfettiPainter(
            confettiPieces: _confettiPieces,
            progress: _controller.value,
          ),
        );
      },
    );
  }
}

/// Data class for a confetti piece
class ConfettiPiece {
  /// Initial angle from center
  final double angle;

  /// Distance from center
  final double distance;

  /// Color of the confetti piece
  final Color color;

  /// Size of the confetti piece
  final double size;

  /// Rotation speed
  final double rotationSpeed;

  /// Creates a confetti piece
  ConfettiPiece({
    required this.angle,
    required this.distance,
    required this.color,
    required this.size,
    required this.rotationSpeed,
  });
}

/// Custom painter for the rotating confetti
class ConfettiPainter extends CustomPainter {
  final List<ConfettiPiece> confettiPieces;
  final double progress;

  ConfettiPainter({
    required this.confettiPieces,
    required this.progress,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);

    for (final piece in confettiPieces) {
      // Calculate rotation angle based on progress and speed
      final rotationAngle =
          piece.angle + (progress * 2 * pi * piece.rotationSpeed);

      // Calculate position
      final x = center.dx + cos(rotationAngle) * piece.distance;
      final y = center.dy + sin(rotationAngle) * piece.distance;

      // Draw confetti piece
      final paint = Paint()
        ..color = piece.color
        ..style = PaintingStyle.fill;

      // Create a star shape
      canvas.save();
      canvas.translate(x, y);
      canvas.rotate(
          rotationAngle * 2); // Additional rotation for the piece itself

      // Draw a simple star/confetti piece
      final path = Path();
      if (piece.color == Colors.amber) {
        // Star shape for gold confetti
        for (int i = 0; i < 5; i++) {
          final starAngle = i * 2 * pi / 5;
          final x1 = cos(starAngle) * piece.size;
          final y1 = sin(starAngle) * piece.size;
          final x2 = cos(starAngle + pi / 5) * (piece.size / 2);
          final y2 = sin(starAngle + pi / 5) * (piece.size / 2);

          if (i == 0) {
            path.moveTo(x1, y1);
          } else {
            path.lineTo(x1, y1);
          }
          path.lineTo(x2, y2);
        }
        path.close();
      } else {
        // Circle for colored confetti
        path.addOval(
            Rect.fromCircle(center: Offset.zero, radius: piece.size / 2));
      }

      canvas.drawPath(path, paint);
      canvas.restore();
    }
  }

  @override
  bool shouldRepaint(ConfettiPainter oldDelegate) {
    return oldDelegate.progress != progress;
  }
}
