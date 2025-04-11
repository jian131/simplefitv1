package com.jian.simplefitv1.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jian.simplefitv1.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

/**
 * Utility class for common Firebase operations
 */
public class FirebaseUtils {

    private static final String TAG = "FirebaseUtils";

    /**
     * Initialize Firestore settings
     */
    public static void initializeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * Get current Firebase user
     * @return FirebaseUser or null if not logged in
     */
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Update user profile with new display name
     * @param displayName New display name
     * @param listener Callback for completion
     */
    public static void updateUserDisplayName(String displayName, OnProfileUpdateListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            user.updateProfile(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating display name", e);
                    listener.onFailure(e);
                });
        } else {
            listener.onFailure(new Exception("User not logged in"));
        }
    }

    /**
     * Upload user profile image to Firebase Storage
     * @param imageUri Local URI of the image
     * @param listener Callback with download URL
     */
    public static void uploadProfileImage(Uri imageUri, OnImageUploadListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onFailure(new Exception("User not logged in"));
            return;
        }

        // Create storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageRef.child("profile_images/" + user.getUid() + "/" + UUID.randomUUID().toString());

        // Upload file
        UploadTask uploadTask = profileRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get download URL
            profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                listener.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL", e);
                listener.onFailure(e);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading image", e);
            listener.onFailure(e);
        });
    }

    /**
     * Update user profile with profile image URL
     * @param imageUrl URL of the image
     * @param listener Callback for completion
     */
    public static void updateUserProfileImage(String imageUrl, OnProfileUpdateListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build();

            user.updateProfile(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating profile image", e);
                    listener.onFailure(e);
                });
        } else {
            listener.onFailure(new Exception("User not logged in"));
        }
    }

    /**
     * Load profile image into ImageView
     * @param context Application context
     * @param imageView ImageView to load into
     */
    public static void loadProfileImage(Context context, ImageView imageView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.default_profile_image);
        }
    }

    /**
     * Delete user data when account is deleted
     * @param userId User ID
     * @param listener Callback for completion
     */
    public static void deleteUserData(String userId, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete user document
        db.collection("users").document(userId).delete()
            .addOnSuccessListener(aVoid -> {
                // Delete user workouts
                deleteUserWorkouts(userId, listener);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting user document", e);
                listener.onFailure(e);
            });
    }

    /**
     * Delete user workouts when account is deleted
     * @param userId User ID
     * @param listener Callback for completion
     */
    private static void deleteUserWorkouts(String userId, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get reference to user's workouts collection
        db.collection("users").document(userId).collection("workouts").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Create batch to delete workouts
                com.google.firebase.firestore.WriteBatch batch = db.batch();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    batch.delete(document.getReference());
                }

                // Execute batch
                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        // Delete user routines
                        deleteUserRoutines(userId, listener);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error deleting user workouts", e);
                        listener.onFailure(e);
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting user workouts", e);
                listener.onFailure(e);
            });
    }

    /**
     * Delete user routines when account is deleted
     * @param userId User ID
     * @param listener Callback for completion
     */
    private static void deleteUserRoutines(String userId, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get user routines
        db.collection("routines").whereEqualTo("userId", userId).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Create batch to delete routines
                com.google.firebase.firestore.WriteBatch batch = db.batch();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    batch.delete(document.getReference());
                }

                // Execute batch
                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        // Delete user profile images
                        deleteUserProfileImages(userId, listener);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error deleting user routines", e);
                        listener.onFailure(e);
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting user routines", e);
                listener.onFailure(e);
            });
    }

    /**
     * Delete user profile images when account is deleted
     * @param userId User ID
     * @param listener Callback for completion
     */
    private static void deleteUserProfileImages(String userId, OnCompleteListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profileRef = storage.getReference().child("profile_images/" + userId);

        // List all files in the directory
        profileRef.listAll()
            .addOnSuccessListener(listResult -> {
                // Delete each file
                for (StorageReference item : listResult.getItems()) {
                    item.delete();
                }

                // Notify completion
                listener.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error listing profile images", e);
                listener.onFailure(e);
            });
    }

    /**
     * Interface for profile update callbacks
     */
    public interface OnProfileUpdateListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    /**
     * Interface for image upload callbacks
     */
    public interface OnImageUploadListener {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }

    /**
     * Interface for completion callbacks
     */
    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
