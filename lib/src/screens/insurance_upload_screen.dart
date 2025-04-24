import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../constants/app_color.dart';
import '../routes/app_routes.dart';

/// Screen for uploading insurance and identification documents
class InsuranceUploadScreen extends StatefulWidget {
  /// Creates an insurance upload screen
  const InsuranceUploadScreen({super.key});

  @override
  State<InsuranceUploadScreen> createState() => _InsuranceUploadScreenState();
}

class _InsuranceUploadScreenState extends State<InsuranceUploadScreen> {
  // Mock service for demo purposes
  String? _insuranceCardUrl;
  String? _idCardUrl;
  bool _isLoading = false;
  String? _error;
  String? _currentUploadTarget;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  Future<void> _loadUserData() async {
    // Mock implementation
    // In a real app, you would fetch this from a service
    setState(() {
      _insuranceCardUrl = null;
      _idCardUrl = null;
    });
  }

  Future<void> _takePicture(String documentType) async {
    setState(() {
      _currentUploadTarget = documentType;
      _isLoading = true;
      _error = null;
    });

    try {
      final ImagePicker picker = ImagePicker();
      final XFile? image = await picker.pickImage(source: ImageSource.camera);

      if (image != null) {
        final File imageFile = File(image.path);
        await _uploadImage(imageFile, documentType);
      }
    } catch (e) {
      setState(() {
        _error = 'Failed to take picture: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _uploadFromGallery(String documentType) async {
    setState(() {
      _currentUploadTarget = documentType;
      _isLoading = true;
      _error = null;
    });

    try {
      final ImagePicker picker = ImagePicker();
      final XFile? image = await picker.pickImage(source: ImageSource.gallery);

      if (image != null) {
        final File imageFile = File(image.path);
        await _uploadImage(imageFile, documentType);
      }
    } catch (e) {
      setState(() {
        _error = 'Failed to upload from gallery: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _uploadImage(File imageFile, String documentType) async {
    // Mock implementation
    await Future.delayed(const Duration(seconds: 2));

    setState(() {
      if (documentType == 'insurance') {
        _insuranceCardUrl = 'https://example.com/mock-insurance-card.jpg';
      } else if (documentType == 'id') {
        _idCardUrl = 'https://example.com/mock-id-card.jpg';
      }
    });
  }

  Future<void> _submitForVerification() async {
    setState(() {
      _currentUploadTarget = null;
      _isLoading = true;
      _error = null;
    });

    try {
      // Mock submission
      await Future.delayed(const Duration(seconds: 2));

      if (mounted) {
        Navigator.of(context)
            .pushReplacementNamed(AppRoutes.verificationStatus);
      }
    } catch (e) {
      setState(() {
        _error = 'Failed to submit for verification: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Insurance Verification'),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const Text(
                'Upload Your Documents',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: AppColor.textDarkBlue,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                'We need clear photos of your Insurance Card and a valid ID.',
                style: TextStyle(
                  color: AppColor.textMediumGray,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),

              // Error message
              if (_error != null)
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(12),
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: AppColor.errorRedLight,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    _error!,
                    style: const TextStyle(
                      color: AppColor.errorRed,
                      fontSize: 14,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),

              // Insurance Card Upload
              _DocumentUploadCard(
                title: 'Insurance Card',
                imageUrl: _insuranceCardUrl,
                isLoading: _isLoading && _currentUploadTarget == 'insurance',
                onTakePhotoClick: () => _takePicture('insurance'),
                onUploadClick: () => _uploadFromGallery('insurance'),
              ),

              const SizedBox(height: 16),

              // ID Card Upload
              _DocumentUploadCard(
                title: 'Identification Card',
                description: "(Driver's License or Government ID)",
                imageUrl: _idCardUrl,
                isLoading: _isLoading && _currentUploadTarget == 'id',
                onTakePhotoClick: () => _takePicture('id'),
                onUploadClick: () => _uploadFromGallery('id'),
              ),

              const SizedBox(height: 24),

              // Submit button
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  onPressed: (_insuranceCardUrl != null &&
                          _idCardUrl != null &&
                          !_isLoading)
                      ? _submitForVerification
                      : null,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColor.primaryBlue,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(28),
                    ),
                    elevation: 0,
                  ),
                  child: _isLoading && _currentUploadTarget == null
                      ? const CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        )
                      : const Text(
                          'Submit for Verification',
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
  }
}

/// A card for document uploads
class _DocumentUploadCard extends StatelessWidget {
  final String title;
  final String? description;
  final String? imageUrl;
  final bool isLoading;
  final VoidCallback onTakePhotoClick;
  final VoidCallback onUploadClick;

  const _DocumentUploadCard({
    required this.title,
    this.description,
    this.imageUrl,
    required this.isLoading,
    required this.onTakePhotoClick,
    required this.onUploadClick,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            // Title and description
            Text(
              title,
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppColor.textDarkBlue,
              ),
            ),
            if (description != null)
              Padding(
                padding: const EdgeInsets.only(top: 4),
                child: Text(
                  description!,
                  style: const TextStyle(
                    fontSize: 14,
                    color: AppColor.textMediumGray,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            const SizedBox(height: 16),

            // Image display area
            Container(
              width: double.infinity,
              height: 200,
              decoration: BoxDecoration(
                color: AppColor.backgroundGray,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(
                  color: AppColor.textLightGray,
                  width: 1,
                ),
              ),
              child: isLoading
                  ? const Center(
                      child: CircularProgressIndicator(
                        color: AppColor.primaryBlue,
                      ),
                    )
                  : imageUrl != null
                      ? ClipRRect(
                          borderRadius: BorderRadius.circular(8),
                          child: Image.network(
                            imageUrl!,
                            fit: BoxFit.contain,
                            errorBuilder: (context, error, stackTrace) {
                              return const Center(
                                child: Text(
                                  'Error loading image',
                                  style: TextStyle(color: AppColor.errorRed),
                                ),
                              );
                            },
                          ),
                        )
                      : Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: const [
                            Icon(
                              Icons.camera_alt,
                              size: 48,
                              color: AppColor.textLightGray,
                            ),
                            SizedBox(height: 8),
                            Text(
                              'Take photo or upload',
                              style: TextStyle(
                                color: AppColor.textMediumGray,
                              ),
                            ),
                          ],
                        ),
            ),
            const SizedBox(height: 16),

            // Buttons
            Row(
              children: [
                // Take Photo Button
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: isLoading ? null : onTakePhotoClick,
                    icon: const Icon(
                      Icons.camera_alt,
                      size: 18,
                    ),
                    label: Text(imageUrl != null ? 'Retake' : 'Take Photo'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: imageUrl != null
                          ? Colors.white
                          : AppColor.primaryBlue,
                      foregroundColor: imageUrl != null
                          ? AppColor.primaryBlue
                          : Colors.white,
                      side: imageUrl != null
                          ? const BorderSide(color: AppColor.primaryBlue)
                          : null,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                // Upload Button
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: isLoading ? null : onUploadClick,
                    icon: const Icon(
                      Icons.file_upload,
                      size: 18,
                    ),
                    label: Text(imageUrl != null ? 'Upload New' : 'Upload'),
                    style: OutlinedButton.styleFrom(
                      side: const BorderSide(color: AppColor.primaryBlue),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
