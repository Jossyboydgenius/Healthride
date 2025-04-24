import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A modern list item with leading and trailing icons
class ModernListItem extends StatelessWidget {
  /// Item title
  final String title;

  /// Optional item subtitle
  final String? subtitle;

  /// Optional leading icon
  final IconData? leadingIcon;

  /// Trailing icon
  final IconData trailingIcon;

  /// Function called when the item is tapped
  final VoidCallback? onTap;

  /// Padding around the item
  final EdgeInsetsGeometry padding;

  /// Creates a modern list item
  const ModernListItem({
    super.key,
    required this.title,
    this.subtitle,
    this.leadingIcon,
    this.trailingIcon = Icons.chevron_right,
    this.onTap,
    this.padding = const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: padding,
        child: Row(
          children: [
            if (leadingIcon != null) ...[
              Icon(
                leadingIcon,
                color: AppColor.primaryBlue,
                size: 24.0,
              ),
              const SizedBox(width: 16.0),
            ],
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleSmall?.copyWith(
                          color: AppColor.textDarkBlue,
                          fontWeight: FontWeight.w500,
                        ),
                  ),
                  if (subtitle != null)
                    Text(
                      subtitle!,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppColor.textMediumGray,
                          ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                ],
              ),
            ),
            Icon(
              trailingIcon,
              color: AppColor.textLightGray,
              size: 20.0,
            ),
          ],
        ),
      ),
    );
  }
}
