import 'package:flutter/material.dart';
import '../../constants/app_color.dart';

/// A modern search bar for addresses with expandable behavior
class AddressSearchBar extends StatefulWidget {
  /// Controller for the text field
  final TextEditingController controller;

  /// Controller for the destination field (optional)
  final TextEditingController? destinationController;

  /// Placeholder text
  final String placeholder;

  /// Leading icon
  final IconData icon;

  /// Color for the icon
  final Color iconColor;

  /// Callback when tapped
  final VoidCallback? onTap;

  /// Whether the search bar is focused
  final bool isFocused;

  /// Callback when text changes
  final Function(String)? onChanged;

  /// Callback when the field is collapsed/submitted
  final VoidCallback? onFieldSubmitted;

  /// Callback when a suggestion is selected
  final Function(String)? onSuggestionSelected;

  /// Creates an address search bar
  const AddressSearchBar({
    Key? key,
    required this.controller,
    this.destinationController,
    this.placeholder = 'Search',
    this.icon = Icons.search,
    this.iconColor = Colors.grey,
    this.onTap,
    this.isFocused = false,
    this.onChanged,
    this.onFieldSubmitted,
    this.onSuggestionSelected,
  }) : super(key: key);

  @override
  State<AddressSearchBar> createState() => _AddressSearchBarState();
}

class _AddressSearchBarState extends State<AddressSearchBar>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _heightAnimation;
  late TextEditingController _destinationController;
  final FocusNode _fromFocusNode = FocusNode();
  final FocusNode _toFocusNode = FocusNode();
  bool _isExpanded = false;

  // Example suggestions (to be replaced with real data in production)
  final List<String> _suggestions = [
    'Memorial Hospital',
    'City Health Clinic',
    'Downtown Medical Center',
    'Memorial pm m Medical Center',
    'Memorial pm m Hospital',
    'Memorial pm m Clinic',
    'Memorial pm m Healthcare'
  ];
  bool _showSuggestions = false;
  String _searchQuery = '';

  // Define colors for icons
  final Color _originIconBg = const Color(0xFFE6EFFF);
  final Color _destinationIconBg = const Color(0xFFF2EAFF);
  final Color _dividerColor = const Color(0xFFEEEEEE);

  @override
  void initState() {
    super.initState();
    _destinationController =
        widget.destinationController ?? TextEditingController();

    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 250), // Slightly faster animation
    );

    _heightAnimation = Tween<double>(
      begin: 70.0, // Smaller starting height
      end: 162.0, // Increased height to prevent overflow and add more padding
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeOutCubic, // Smoother animation curve
    ));

    _fromFocusNode.addListener(_onFocusChange);
    _toFocusNode.addListener(_onFocusChange);
  }

  @override
  void dispose() {
    _animationController.dispose();
    // Only dispose if we created the controller
    if (widget.destinationController == null) {
      _destinationController.dispose();
    }
    _fromFocusNode.dispose();
    _toFocusNode.dispose();
    super.dispose();
  }

  void _onFocusChange() {
    final bool hasFocus = _fromFocusNode.hasFocus || _toFocusNode.hasFocus;
    if (hasFocus && !_isExpanded) {
      _expand();
    } else if (!hasFocus && _isExpanded && !_showSuggestions) {
      _collapse();
    }
  }

  void _expand() {
    setState(() {
      _isExpanded = true;
    });
    _animationController.forward();

    // Set focus to destination field immediately after expanding
    // Remove the delay to make it more responsive
    FocusScope.of(context).requestFocus(_toFocusNode);

    if (widget.onTap != null) {
      widget.onTap!();
    }
  }

  void _collapse() {
    setState(() {
      _isExpanded = false;
      _showSuggestions = false;
    });
    _animationController.reverse();

    // Notify the parent when the field is collapsed
    if (widget.onFieldSubmitted != null) {
      widget.onFieldSubmitted!();
    }
  }

  void _handleTextChange(String value) {
    setState(() {
      _searchQuery = value;
      _showSuggestions = value.isNotEmpty;
    });

    if (widget.onChanged != null) {
      widget.onChanged!(value);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // Main search container
        AnimatedBuilder(
          animation: _animationController,
          builder: (context, child) {
            return Container(
              height: _heightAnimation.value,
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(30),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 10,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: _isExpanded ? _buildExpandedView() : _buildCollapsedView(),
            );
          },
        ),

        // Location suggestions
        if (_showSuggestions && _isExpanded)
          Container(
            margin: const EdgeInsets.only(top: 8),
            constraints: const BoxConstraints(
              maxHeight: 250,
            ),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(
                  24), // Increased border radius to match screenshot
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 10,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            child: ListView.separated(
              shrinkWrap: true,
              physics: const ClampingScrollPhysics(),
              itemCount: _suggestions.length,
              separatorBuilder: (context, index) => Divider(
                height: 1,
                indent: 72,
                endIndent: 16, // Added endIndent so divider doesn't touch edge
                color: Colors.grey.withOpacity(
                    0.2), // Lighter divider color to match rest of UI
              ),
              itemBuilder: (context, index) {
                final suggestion = _suggestions[index];
                return ListTile(
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 16.0,
                    vertical: 8.0,
                  ),
                  leading: Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      color: _destinationIconBg,
                      shape: BoxShape.circle,
                      border: Border.all(color: Colors.transparent, width: 0),
                    ),
                    child: const Icon(
                      Icons.place,
                      color: AppColor.primaryPurple,
                      size: 20,
                    ),
                  ),
                  title: Text(
                    suggestion,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                      color: AppColor.textDarkBlue,
                    ),
                  ),
                  trailing: const Icon(
                    Icons.chevron_right,
                    color: AppColor.textMediumGray,
                  ),
                  onTap: () {
                    final focusedController = _fromFocusNode.hasFocus
                        ? widget.controller
                        : _destinationController;
                    focusedController.text = suggestion;

                    // Notify parent when a suggestion is selected
                    if (widget.onSuggestionSelected != null) {
                      widget.onSuggestionSelected!(suggestion);
                    }

                    setState(() {
                      _showSuggestions = false;
                    });

                    // Unfocus and collapse the search bar after selecting a suggestion
                    FocusScope.of(context).unfocus();
                    _collapse();
                  },
                );
              },
            ),
          ),
      ],
    );
  }

  // Build the collapsed (single-row) view
  Widget _buildCollapsedView() {
    return GestureDetector(
      onTap: _expand, // Directly call expand on tap
      behavior: HitTestBehavior.opaque, // Make entire area tappable
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: _originIconBg,
                shape: BoxShape.circle,
                border: Border.all(color: Colors.transparent, width: 0),
              ),
              child: const Icon(
                Icons.search,
                color: AppColor.primaryBlue,
                size: 18,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                widget.placeholder,
                style: TextStyle(
                  fontSize: 16,
                  color: AppColor.textMediumGray.withOpacity(0.7),
                  fontFamily: 'SF Pro Display',
                ),
              ),
            ),
            // Add the close button for the collapsed state as well
            if (widget.controller.text.isNotEmpty)
              GestureDetector(
                onTap: () {
                  // Prevent the event from bubbling up to parent
                  widget.controller.clear();
                  _handleTextChange('');
                },
                child: Icon(
                  Icons.close,
                  color: Colors.grey[500],
                  size: 24,
                ),
              ),
          ],
        ),
      ),
    );
  }

  // Build the expanded (two-row) view
  Widget _buildExpandedView() {
    return Column(
      children: [
        // From row
        Padding(
          padding: const EdgeInsets.only(
              left: 16.0,
              right: 16.0,
              top: 20.0,
              bottom: 12.0), // Increased vertical padding
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: _originIconBg,
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.transparent, width: 0),
                ),
                child: const Icon(
                  Icons.my_location,
                  color: AppColor.primaryBlue,
                  size: 18,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: TextField(
                  controller: widget.controller,
                  focusNode: _fromFocusNode,
                  onChanged: _handleTextChange,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w500,
                    color: AppColor.textDarkBlue,
                  ),
                  decoration: InputDecoration(
                    hintText: "Current location",
                    hintStyle: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.normal,
                      color: AppColor.textMediumGray.withOpacity(0.7),
                    ),
                    border: InputBorder.none,
                    enabledBorder: InputBorder.none,
                    focusedBorder: InputBorder.none,
                    errorBorder: InputBorder.none,
                    disabledBorder: InputBorder.none,
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: 8.0), // Added padding
                  ),
                  cursorColor: widget.iconColor,
                  textAlignVertical: TextAlignVertical.center,
                ),
              ),
              if (widget.controller.text.isNotEmpty)
                GestureDetector(
                  onTap: () {
                    widget.controller.clear();
                    _handleTextChange('');
                  },
                  child: Icon(
                    Icons.close,
                    color: Colors.grey[500],
                    size: 24,
                  ),
                ),
            ],
          ),
        ),

        // Divider
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          child: Divider(
            height: 1,
            thickness: 1,
            color: Colors.grey.withOpacity(0.2),
          ),
        ),

        // To row
        Padding(
          padding: const EdgeInsets.only(
              left: 16.0,
              right: 16.0,
              top: 12.0,
              bottom: 20.0), // Increased vertical padding
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: _destinationIconBg,
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.transparent, width: 0),
                ),
                child: const Icon(
                  Icons.place,
                  color: AppColor.primaryPurple,
                  size: 18,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: TextField(
                  controller: _destinationController,
                  focusNode: _toFocusNode,
                  onChanged: _handleTextChange,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w500,
                    color: AppColor.textDarkBlue,
                  ),
                  decoration: InputDecoration(
                    hintText: 'Where to?',
                    hintStyle: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.normal,
                      color: AppColor.textMediumGray.withOpacity(0.7),
                    ),
                    border: InputBorder.none,
                    enabledBorder: InputBorder.none,
                    focusedBorder: InputBorder.none,
                    errorBorder: InputBorder.none,
                    disabledBorder: InputBorder.none,
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: 8.0), // Added padding
                  ),
                  cursorColor: AppColor.primaryPurple,
                  textAlignVertical: TextAlignVertical.center,
                ),
              ),
              if (_destinationController.text.isNotEmpty)
                GestureDetector(
                  onTap: () {
                    _destinationController.clear();
                    _handleTextChange('');
                  },
                  child: Icon(
                    Icons.close,
                    color: Colors.grey[500],
                    size: 24,
                  ),
                ),
            ],
          ),
        ),
      ],
    );
  }
}
