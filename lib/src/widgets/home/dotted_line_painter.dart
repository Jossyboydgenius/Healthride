import 'package:flutter/material.dart';

/// A custom painter that draws a vertical dotted line
class DottedLinePainter extends CustomPainter {
  /// The color of the dotted line
  final Color color;

  /// The dash height
  final double dashHeight;

  /// The space between dashes
  final double dashSpace;

  /// The stroke width of the line
  final double strokeWidth;

  /// Creates a dotted line painter with customizable properties
  DottedLinePainter({
    this.color = const Color(0xFFBDBDBD),
    this.dashHeight = 4.0,
    this.dashSpace = 4.0,
    this.strokeWidth = 2.0,
  });

  @override
  void paint(Canvas canvas, Size size) {
    Paint paint = Paint()
      ..color = color.withOpacity(0.5)
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    double startY = 0;

    while (startY < size.height) {
      canvas.drawLine(Offset(0, startY), Offset(0, startY + dashHeight), paint);
      startY += dashHeight + dashSpace;
    }
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => false;
}
