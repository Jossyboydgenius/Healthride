import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A custom loading animation with bouncing dots
class LoadingAnimation extends StatefulWidget {
  /// Size of each circle
  final double circleSize;

  /// Color of the circles
  final Color circleColor;

  /// Space between circles
  final double spaceBetween;

  /// Distance the circles travel vertically
  final double travelDistance;

  /// Creates a loading animation
  const LoadingAnimation({
    super.key,
    this.circleSize = 25.0,
    this.circleColor = AppColor.primaryBlue,
    this.spaceBetween = 10.0,
    this.travelDistance = 20.0,
  });

  @override
  State<LoadingAnimation> createState() => _LoadingAnimationState();
}

class _LoadingAnimationState extends State<LoadingAnimation>
    with TickerProviderStateMixin {
  late final List<AnimationController> _controllers;
  late final List<Animation<double>> _animations;

  @override
  void initState() {
    super.initState();
    _controllers = List.generate(
      3,
      (index) => AnimationController(
        vsync: this,
        duration: const Duration(milliseconds: 1200),
      ),
    );

    _animations = _controllers.map((controller) {
      return Tween<double>(begin: 0.0, end: 1.0).animate(CurvedAnimation(
        parent: controller,
        curve: const Interval(0.0, 0.5, curve: Curves.easeOutCubic),
        reverseCurve: const Interval(0.5, 1.0, curve: Curves.easeInCubic),
      ));
    }).toList();

    // Start animations with delays
    for (var i = 0; i < _controllers.length; i++) {
      Future.delayed(Duration(milliseconds: i * 100), () {
        if (mounted) {
          _controllers[i].repeat();
        }
      });
    }
  }

  @override
  void dispose() {
    for (var controller in _controllers) {
      controller.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(
        _controllers.length,
        (index) {
          return Padding(
            padding: EdgeInsets.symmetric(horizontal: widget.spaceBetween / 2),
            child: AnimatedBuilder(
              animation: _animations[index],
              builder: (context, child) {
                return Transform.translate(
                  offset: Offset(
                      0, -widget.travelDistance * _animations[index].value),
                  child: child,
                );
              },
              child: Container(
                width: widget.circleSize,
                height: widget.circleSize,
                decoration: const BoxDecoration(
                  shape: BoxShape.circle,
                  gradient: LinearGradient(
                    colors: [AppColor.primaryBlue, AppColor.primaryPurple],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

/// A centered loading indicator with three bouncing dots
class LoadingIndicator extends StatelessWidget {
  /// Size of the container
  final double size;

  /// Color of the dots
  final Color color;

  /// Creates a loading indicator
  const LoadingIndicator({
    super.key,
    this.size = 100.0,
    this.color = AppColor.primaryBlue,
  });

  @override
  Widget build(BuildContext context) {
    final dotSize = size / 6;

    return SizedBox(
      width: size,
      height: size,
      child: Center(
        child: LoadingAnimation(
          circleSize: dotSize,
          circleColor: color,
          spaceBetween: dotSize / 2,
          travelDistance: size / 12,
        ),
      ),
    );
  }
}
