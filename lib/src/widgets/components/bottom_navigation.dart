import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// Bottom navigation bar for the app
class HealthRideBottomNavigation extends StatelessWidget {
  /// Current route/tab
  final String currentRoute;

  /// Callback when a tab is tapped
  final Function(String) onNavigate;

  /// Creates a bottom navigation bar
  const HealthRideBottomNavigation({
    super.key,
    required this.currentRoute,
    required this.onNavigate,
  });

  @override
  Widget build(BuildContext context) {
    final items = [
      {
        'route': 'home',
        'label': 'Book',
        'icon': Icons.directions_car_outlined,
        'activeIcon': Icons.directions_car
      },
      {
        'route': 'rides',
        'label': 'Rides',
        'icon': Icons.history_outlined,
        'activeIcon': Icons.history
      },
      {
        'route': 'notifications',
        'label': 'Alerts',
        'icon': Icons.notifications_outlined,
        'activeIcon': Icons.notifications
      },
      {
        'route': 'profile',
        'label': 'Profile',
        'icon': Icons.person_outline,
        'activeIcon': Icons.person
      },
    ];

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(24),
          topRight: Radius.circular(24),
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      height: 80,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: items.map((item) {
          final isSelected = currentRoute == item['route'];
          return Expanded(
            child: InkWell(
              onTap: () => onNavigate(item['route'] as String),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    isSelected
                        ? item['activeIcon'] as IconData
                        : item['icon'] as IconData,
                    color: isSelected
                        ? AppColor.primaryBlue
                        : AppColor.textMediumGray,
                    size: 26,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    item['label'] as String,
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight:
                          isSelected ? FontWeight.bold : FontWeight.normal,
                      color: isSelected
                          ? AppColor.primaryBlue
                          : AppColor.textMediumGray,
                      fontFamily: 'Poppins',
                    ),
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}
