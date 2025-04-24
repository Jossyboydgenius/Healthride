import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../constants/app_color.dart';
import '../widgets/components/gradient_button.dart';
import 'dart:math' as math;

/// Notification model
class NotificationItem {
  /// Unique identifier
  final String id;

  /// Notification title
  final String title;

  /// Notification message content
  final String message;

  /// Time the notification was received
  final DateTime time;

  /// Type of notification
  final NotificationType type;

  /// Whether the notification has been read
  final bool isRead;

  /// Creates a notification
  const NotificationItem({
    required this.id,
    required this.title,
    required this.message,
    required this.time,
    required this.type,
    required this.isRead,
  });
}

/// Types of notifications
enum NotificationType {
  /// Ride booking confirmed
  rideConfirmed,

  /// Driver has been assigned to the ride
  driverAssigned,

  /// Reminder about upcoming ride
  rideReminder,

  /// Ride has been completed
  rideCompleted,

  /// Special promotion or offer
  promotion,
}

/// Screen that displays user notifications
class NotificationsScreen extends StatefulWidget {
  /// Creates a notifications screen
  const NotificationsScreen({super.key});

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen>
    with SingleTickerProviderStateMixin {
  bool _showContent = false;
  late List<NotificationItem> notifications;

  @override
  void initState() {
    super.initState();
    _loadNotifications();

    // Start animations
    Future.delayed(const Duration(milliseconds: 100), () {
      if (mounted) {
        setState(() {
          _showContent = true;
        });
      }
    });
  }

  void _loadNotifications() {
    // Sample notifications data
    notifications = [
      NotificationItem(
        id: "n1",
        title: "Driver Assigned",
        message:
            "Michael Johnson will be your driver for your upcoming ride to Medical Center.",
        time: DateTime.now().subtract(const Duration(hours: 1)),
        type: NotificationType.driverAssigned,
        isRead: false,
      ),
      NotificationItem(
        id: "n2",
        title: "Ride Reminder",
        message:
            "Reminder: You have a scheduled ride tomorrow at 9:30 AM to City Hospital.",
        time: DateTime.now().subtract(const Duration(hours: 5)),
        type: NotificationType.rideReminder,
        isRead: false,
      ),
      NotificationItem(
        id: "n3",
        title: "Ride Completed",
        message:
            "Your ride to Medical Center has been completed. Thank you for using HealthRide!",
        time: DateTime.now().subtract(const Duration(days: 1)),
        type: NotificationType.rideCompleted,
        isRead: true,
      ),
      NotificationItem(
        id: "n4",
        title: "Special Offer",
        message:
            "Get 20% off on your next 3 rides! Offer valid for the next 7 days.",
        time: DateTime.now().subtract(const Duration(days: 2)),
        type: NotificationType.promotion,
        isRead: true,
      ),
      NotificationItem(
        id: "n5",
        title: "Ride Confirmed",
        message:
            "Your ride to Wellness Clinic has been confirmed for next Monday.",
        time: DateTime.now().subtract(const Duration(days: 3)),
        type: NotificationType.rideConfirmed,
        isRead: true,
      ),
    ];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        title: const Text(
          'Notifications',
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: AppColor.textDarkBlue,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          color: AppColor.textDarkBlue,
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.done_all_outlined),
            color: AppColor.primaryBlue,
            onPressed: () {
              // Mark all as read
            },
          ),
        ],
      ),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              Color(0xFFF0F4FF), // Light blue
              Color(0xFFF5F0FF), // Light purple
              Color(0xFFF8FBFF), // White-ish
            ],
          ),
        ),
        child: Stack(
          children: [
            // Decorative shape top-left
            Positioned(
              top: 0,
              left: 0,
              child: Container(
                width: MediaQuery.of(context).size.width * 0.8,
                height: MediaQuery.of(context).size.width * 0.8,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  gradient: RadialGradient(
                    colors: [
                      AppColor.primaryBlue.withOpacity(0.1),
                      Colors.transparent,
                    ],
                  ),
                ),
              ),
            ),

            // Decorative shape bottom-right
            Positioned(
              bottom: 0,
              right: 0,
              child: Container(
                width: MediaQuery.of(context).size.width * 0.9,
                height: MediaQuery.of(context).size.width * 0.9,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  gradient: RadialGradient(
                    colors: [
                      AppColor.primaryPurple.withOpacity(0.08),
                      Colors.transparent,
                    ],
                  ),
                ),
              ),
            ),

            SafeArea(
              child: AnimatedOpacity(
                opacity: _showContent ? 1.0 : 0.0,
                duration: const Duration(milliseconds: 500),
                curve: Curves.easeOut,
                child: AnimatedSlide(
                  offset: _showContent ? Offset.zero : const Offset(0, 0.1),
                  duration: const Duration(milliseconds: 500),
                  curve: Curves.easeOutCubic,
                  child: notifications.isEmpty
                      ? _buildEmptyState()
                      : _buildNotificationsList(),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNotificationsList() {
    final unreadCount = notifications.where((n) => !n.isRead).length;

    // Group notifications by day
    final todayDate = DateTime(
      DateTime.now().year,
      DateTime.now().month,
      DateTime.now().day,
    );
    final yesterdayDate = todayDate.subtract(const Duration(days: 1));

    final groupedNotifications = <String, List<NotificationItem>>{};

    for (final notification in notifications) {
      final notificationDate = DateTime(
        notification.time.year,
        notification.time.month,
        notification.time.day,
      );

      String group;
      if (notificationDate == todayDate) {
        group = 'Today';
      } else if (notificationDate == yesterdayDate) {
        group = 'Yesterday';
      } else {
        group = 'Earlier';
      }

      if (!groupedNotifications.containsKey(group)) {
        groupedNotifications[group] = [];
      }
      groupedNotifications[group]!.add(notification);
    }

    return Column(
      children: [
        // Unread count banner
        if (unreadCount > 0) _buildUnreadBanner(unreadCount),

        // Notifications list
        Expanded(
          child: ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: groupedNotifications.keys.length,
            itemBuilder: (context, index) {
              final dateGroup = groupedNotifications.keys.elementAt(index);
              final notificationsForGroup = groupedNotifications[dateGroup]!;

              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Date header
                  Row(
                    children: [
                      Text(
                        dateGroup,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: AppColor.textDarkBlue,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Divider(
                          color: AppColor.textLightGray.withOpacity(0.5),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),

                  // Notifications for this group
                  ...notificationsForGroup
                      .map((notification) => Padding(
                            padding: const EdgeInsets.only(bottom: 12),
                            child: TweenAnimationBuilder<double>(
                              tween: Tween(begin: 0.0, end: 1.0),
                              duration: Duration(
                                  milliseconds: 300 +
                                      (notificationsForGroup
                                              .indexOf(notification) *
                                          100)),
                              builder: (context, value, child) {
                                return Opacity(
                                  opacity: value,
                                  child: Transform.translate(
                                    offset: Offset(0, 20 * (1 - value)),
                                    child: child,
                                  ),
                                );
                              },
                              child: _buildNotificationCard(notification),
                            ),
                          ))
                      .toList(),

                  const SizedBox(height: 16),
                ],
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildUnreadBanner(int unreadCount) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        gradient: const LinearGradient(
          colors: [
            AppColor.primaryBlue,
            AppColor.primaryPurple,
          ],
        ),
        boxShadow: [
          BoxShadow(
            color: AppColor.primaryBlue.withOpacity(0.3),
            blurRadius: 8,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      child: Row(
        children: [
          // Pulsating counter with animation
          TweenAnimationBuilder<double>(
            tween: Tween(begin: 0.9, end: 1.1),
            duration: const Duration(milliseconds: 1000),
            curve: Curves.easeInOut,
            builder: (context, value, child) {
              return Transform.scale(
                scale: value,
                child: child,
              );
            },
            child: Container(
              width: 32,
              height: 32,
              decoration: const BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
              ),
              alignment: Alignment.center,
              child: Text(
                unreadCount.toString(),
                style: const TextStyle(
                  color: AppColor.primaryBlue,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          const SizedBox(width: 16),
          Text(
            "You have $unreadCount unread notification${unreadCount > 1 ? 's' : ''}",
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNotificationCard(NotificationItem notification) {
    // Define colors based on notification type
    final Color primaryColor;
    final Color secondaryColor;
    final IconData icon;

    switch (notification.type) {
      case NotificationType.rideConfirmed:
        primaryColor = AppColor.successGreen;
        secondaryColor = const Color(0xFF34D399);
        icon = Icons.check_circle_rounded;
        break;
      case NotificationType.driverAssigned:
        primaryColor = AppColor.primaryBlue;
        secondaryColor = const Color(0xFF4CC2FF);
        icon = Icons.person_rounded;
        break;
      case NotificationType.rideReminder:
        primaryColor = AppColor.warningYellow;
        secondaryColor = const Color(0xFFFCD34D);
        icon = Icons.alarm_rounded;
        break;
      case NotificationType.rideCompleted:
        primaryColor = AppColor.successGreen;
        secondaryColor = const Color(0xFF34D399);
        icon = Icons.done_all_rounded;
        break;
      case NotificationType.promotion:
        primaryColor = AppColor.primaryPurple;
        secondaryColor = const Color(0xFF9F6EFF);
        icon = Icons.local_offer_rounded;
        break;
    }

    // Format time
    final timeFormat = DateFormat('h:mm a');
    final formattedTime = timeFormat.format(notification.time);

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: notification.isRead
                ? Colors.grey.withOpacity(0.1)
                : primaryColor.withOpacity(0.2),
            blurRadius: notification.isRead ? 4 : 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Opacity(
        opacity: notification.isRead ? 0.85 : 1.0,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Icon with gradient background
              Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  gradient: LinearGradient(
                    colors: [primaryColor, secondaryColor],
                  ),
                ),
                child: Icon(
                  icon,
                  color: Colors.white,
                  size: 24,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          notification.title,
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: notification.isRead
                                ? FontWeight.w500
                                : FontWeight.bold,
                            color: AppColor.textDarkBlue,
                          ),
                        ),
                        if (!notification.isRead)
                          Container(
                            width: 8,
                            height: 8,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              gradient: LinearGradient(
                                colors: [primaryColor, secondaryColor],
                              ),
                            ),
                          ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      notification.message,
                      style: TextStyle(
                        fontSize: 14,
                        color: notification.isRead
                            ? AppColor.textMediumGray
                            : AppColor.textDarkBlue,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 8),
                    Align(
                      alignment: Alignment.centerRight,
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 10,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: const Color(0xFFF0F4FF),
                          borderRadius: BorderRadius.circular(50),
                        ),
                        child: Text(
                          formattedTime,
                          style: const TextStyle(
                            fontSize: 12,
                            color: AppColor.textMediumGray,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Animated floating icon
          TweenAnimationBuilder<double>(
            tween: Tween(begin: 0, end: 1),
            duration: const Duration(milliseconds: 1000),
            builder: (context, value, child) {
              return Transform.translate(
                offset: Offset(0, math.sin(value * math.pi * 2) * 10),
                child: Container(
                  width: 160,
                  height: 160,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    gradient: const LinearGradient(
                      colors: [
                        AppColor.primaryBlue,
                        AppColor.primaryPurple,
                      ],
                    ),
                    boxShadow: [
                      BoxShadow(
                        color: AppColor.primaryBlue.withOpacity(0.3),
                        blurRadius: 16,
                        offset: const Offset(0, 8),
                      ),
                    ],
                  ),
                  child: const Icon(
                    Icons.notifications_outlined,
                    size: 80,
                    color: Colors.white,
                  ),
                ),
              );
            },
          ),
          const SizedBox(height: 32),
          const Text(
            "No Notifications Yet",
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: AppColor.textDarkBlue,
            ),
          ),
          const SizedBox(height: 16),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 32),
            child: Text(
              "We'll notify you when you have new messages, ride updates, or special offers",
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 16,
                color: AppColor.textMediumGray,
              ),
            ),
          ),
          const SizedBox(height: 32),
          GradientButton(
            text: 'Book a Ride',
            onPressed: () {
              // Handle booking
            },
            width: 200,
          ),
        ],
      ),
    );
  }
}
