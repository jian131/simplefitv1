package com.jian.simplefitv1.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.MuscleGroup;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;
import com.jian.simplefitv1.models.User;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.models.WorkoutSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp dịch vụ xử lý các hoạt động với cơ sở dữ liệu Firestore
 */
public class DatabaseService {

    private static final String TAG = "DatabaseService";

    // Các thể hiện Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Tham chiếu đến các collection
    private CollectionReference usersRef;
    private CollectionReference exercisesRef;
    private CollectionReference muscleGroupsRef;
    private CollectionReference routinesRef;
    private CollectionReference workoutsRef;

    /**
     * Constructor khởi tạo các thể hiện và tham chiếu Firebase
     */
    public DatabaseService() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        usersRef = db.collection("users");
        exercisesRef = db.collection("exercises");
        muscleGroupsRef = db.collection("muscleGroups");
        routinesRef = db.collection("routines");
        workoutsRef = db.collection("workouts");
    }

    /**
     * Lấy người dùng hiện đang xác thực
     * @return FirebaseUser hiện tại hoặc null nếu chưa xác thực
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Kiểm tra xem người dùng đã được xác thực chưa
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    public boolean isUserAuthenticated() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Lấy dữ liệu người dùng từ Firestore
     * @param userId ID người dùng
     * @param listener Callback khi hoàn thành
     */
    public void getUserData(String userId, OnUserDataListener listener) {
        usersRef.document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    listener.onUserDataLoaded(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy dữ liệu người dùng", e);
                    listener.onError(e);
                });
    }

    /**
     * Lưu dữ liệu người dùng vào Firestore
     * @param user Đối tượng người dùng cần lưu
     * @param listener Callback khi hoàn thành
     */
    public void saveUserData(User user, OnCompleteListener<Void> listener) {
        usersRef.document(user.getUserId()).set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Lấy tất cả bài tập từ Firestore
     * @param listener Callback với danh sách bài tập
     */
    public void getAllExercises(OnExercisesLoadedListener listener) {
        exercisesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exercise> exercises = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Exercise exercise = document.toObject(Exercise.class);
                        if (exercise != null) {
                            exercise.setId(document.getId());
                            exercises.add(exercise);
                        }
                    }
                    listener.onExercisesLoaded(exercises);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy danh sách bài tập", e);
                    listener.onError(e);
                });
    }

    /**
     * Lấy bài tập theo nhóm cơ
     * @param muscleGroupId ID của nhóm cơ
     * @param listener Callback với danh sách bài tập
     */
    public void getExercisesByMuscleGroup(String muscleGroupId, OnExercisesLoadedListener listener) {
        exercisesRef.whereArrayContains("muscleGroups", muscleGroupId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exercise> exercises = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Exercise exercise = document.toObject(Exercise.class);
                        if (exercise != null) {
                            exercise.setId(document.getId());
                            exercises.add(exercise);
                        }
                    }
                    listener.onExercisesLoaded(exercises);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy bài tập theo nhóm cơ", e);
                    listener.onError(e);
                });
    }

    /**
     * Lấy tất cả nhóm cơ từ Firestore
     * @param listener Callback với danh sách nhóm cơ
     */
    public void getAllMuscleGroups(OnMuscleGroupsLoadedListener listener) {
        muscleGroupsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MuscleGroup> muscleGroups = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        MuscleGroup muscleGroup = document.toObject(MuscleGroup.class);
                        if (muscleGroup != null) {
                            muscleGroup.setId(document.getId());
                            muscleGroups.add(muscleGroup);
                        }
                    }
                    listener.onMuscleGroupsLoaded(muscleGroups);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy nhóm cơ", e);
                    listener.onError(e);
                });
    }

    /**
     * Lưu một lịch trình vào Firestore
     * @param routine Lịch trình cần lưu
     * @param exercises Danh sách bài tập cho lịch trình này
     * @param listener Callback khi hoàn thành
     */
    public void saveRoutine(Routine routine, List<RoutineExercise> exercises, OnRoutineSavedListener listener) {
        // Kiểm tra xem người dùng đã xác thực chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("Người dùng chưa xác thực"));
            return;
        }

        // Đặt ID người dùng nếu chưa có
        if (routine.getUserId() == null || routine.getUserId().isEmpty()) {
            routine.setUserId(currentUser.getUid());
        }

        // Đặt thời gian nếu chưa có
        long now = System.currentTimeMillis();
        if (routine.getCreatedAt() == 0) {
            routine.setCreatedAt(now);
        }
        routine.setUpdatedAt(now);

        // Đặt số lượng bài tập
        routine.setExerciseCount(exercises.size());

        // Khởi tạo danh sách workoutIds nếu chưa có
        if (routine.getWorkoutIds() == null) {
            routine.setWorkoutIds(new ArrayList<>());
        }

        // Tạo hoặc cập nhật tài liệu lịch trình
        DocumentReference routineRef;
        if (routine.getId() != null && !routine.getId().isEmpty()) {
            routineRef = routinesRef.document(routine.getId());
        } else {
            routineRef = routinesRef.document();
            routine.setId(routineRef.getId());
        }

        // Lưu lịch trình vào Firestore
        routineRef.set(routine)
                .addOnSuccessListener(aVoid -> {
                    // Lưu các bài tập cho lịch trình này
                    saveRoutineExercises(routineRef, exercises, listener);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lưu lịch trình", e);
                    listener.onError(e);
                });
    }

    /**
     * Lưu các bài tập cho một lịch trình
     * @param routineRef Tham chiếu đến tài liệu lịch trình
     * @param exercises Danh sách bài tập cần lưu
     * @param listener Callback khi hoàn thành
     */
    private void saveRoutineExercises(DocumentReference routineRef,
                                      List<RoutineExercise> exercises,
                                      OnRoutineSavedListener listener) {
        CollectionReference exercisesRef = routineRef.collection("exercises");

        // Trước tiên xóa tất cả bài tập hiện có
        exercisesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Tạo batch để xóa bài tập cũ
            WriteBatch batch = db.batch();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                batch.delete(document.getReference());
            }

            // Thực thi batch
            batch.commit().addOnSuccessListener(aVoid -> {
                // Bây giờ thêm bài tập mới
                addRoutineExercises(exercisesRef, exercises, listener);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Lỗi khi xóa bài tập hiện có", e);
                listener.onError(e);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi khi lấy bài tập hiện có", e);
            listener.onError(e);
        });
    }

    /**
     * Thêm bài tập vào một lịch trình
     * @param exercisesRef Tham chiếu đến collection bài tập của lịch trình
     * @param exercises Danh sách bài tập cần thêm
     * @param listener Callback khi hoàn thành
     */
    private void addRoutineExercises(CollectionReference exercisesRef,
                                     List<RoutineExercise> exercises,
                                     OnRoutineSavedListener listener) {
        // Tạo batch để thêm bài tập
        WriteBatch batch = db.batch();

        // Thêm từng bài tập vào batch
        for (int i = 0; i < exercises.size(); i++) {
            RoutineExercise exercise = exercises.get(i);
            exercise.setOrder(i); // Đảm bảo thứ tự được đặt chính xác
            DocumentReference docRef = exercisesRef.document();
            batch.set(docRef, exercise);
        }

        // Thực thi batch
        batch.commit().addOnSuccessListener(aVoid -> {
            listener.onRoutineSaved();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi khi thêm bài tập mới", e);
            listener.onError(e);
        });
    }

    /**
     * Lấy tất cả lịch trình của người dùng hiện tại
     * @param listener Callback với danh sách lịch trình
     */
    public void getUserRoutines(OnRoutinesLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("Người dùng chưa xác thực"));
            return;
        }

        routinesRef.whereEqualTo("userId", currentUser.getUid())
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Routine> routines = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Routine routine = document.toObject(Routine.class);
                        if (routine != null) {
                            routine.setId(document.getId());
                            routines.add(routine);
                        }
                    }
                    listener.onRoutinesLoaded(routines);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy lịch trình của người dùng", e);
                    listener.onError(e);
                });
    }

    /**
     * Lưu một buổi tập vào Firestore
     * @param workout Buổi tập cần lưu
     * @param listener Callback khi hoàn thành
     */
    public void saveWorkout(Workout workout, OnWorkoutSavedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("Người dùng chưa xác thực"));
            return;
        }

        // Đảm bảo ID người dùng được đặt
        if (workout.getUserId() == null || workout.getUserId().isEmpty()) {
            workout.setUserId(currentUser.getUid());
        }

        // Make sure the workout is marked as completed
        workout.setCompleted(true);

        // Set end time if not already set
        if (workout.getEndTime() <= 0) {
            workout.setEndTime(System.currentTimeMillis());
        }

        // Calculate duration if not already set
        if (workout.getDuration() <= 0 && workout.getStartTime() > 0) {
            workout.setDuration(workout.getEndTime() - workout.getStartTime());
        }

        // Tạo hoặc cập nhật tài liệu buổi tập
        DocumentReference workoutRef;
        if (workout.getId() != null && !workout.getId().isEmpty()) {
            workoutRef = db.collection("workouts").document(workout.getId());
        } else {
            workoutRef = db.collection("workouts").document();
            workout.setId(workoutRef.getId());
        }

        // Lưu buổi tập vào Firestore
        workoutRef.set(workout)
                .addOnSuccessListener(aVoid -> {
                    listener.onWorkoutSaved(workout);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lưu buổi tập", e);
                    listener.onError(e);
                });
    }

    /**
     * Lưu một buổi tập vào một lịch trình
     * @param routineId ID của lịch trình
     * @param workout Buổi tập để lưu
     * @param listener Callback khi hoàn thành
     */
    public void addWorkoutToRoutine(String routineId, Workout workout, OnWorkoutSavedListener listener) {
        // Kiểm tra xem người dùng đã xác thực chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("Người dùng chưa xác thực"));
            return;
        }

        // Đặt ID người dùng nếu chưa đặt
        if (workout.getUserId() == null || workout.getUserId().isEmpty()) {
            workout.setUserId(currentUser.getUid());
        }

        // Trước tiên, lưu buổi tập
        DocumentReference workoutRef = workoutsRef.document();
        workout.setId(workoutRef.getId());

        workoutRef.set(workout)
                .addOnSuccessListener(aVoid -> {
                    // Bây giờ, cập nhật lịch trình để bao gồm buổi tập này
                    DocumentReference routineRef = routinesRef.document(routineId);
                    routineRef.get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Routine routine = documentSnapshot.toObject(Routine.class);
                                if (routine != null) {
                                    routine.addWorkoutId(workout.getId());
                                    // Cập nhật lịch trình
                                    routineRef.update("workoutIds", routine.getWorkoutIds(),
                                                    "workoutCount", routine.getWorkoutCount())
                                            .addOnSuccessListener(v -> listener.onWorkoutSaved(workout))
                                            .addOnFailureListener(listener::onError);
                                } else {
                                    listener.onError(new Exception("Không tìm thấy lịch trình"));
                                }
                            })
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy tất cả buổi tập cho một lịch trình
     * @param routineId ID của lịch trình
     * @param listener Callback với danh sách buổi tập
     */
    public void getWorkoutsForRoutine(String routineId, OnWorkoutsLoadedListener listener) {
        routinesRef.document(routineId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Routine routine = documentSnapshot.toObject(Routine.class);
                    if (routine != null && routine.getWorkoutIds() != null && !routine.getWorkoutIds().isEmpty()) {
                        // Truy vấn các buổi tập theo ID của chúng
                        workoutsRef
                                .whereIn(FieldPath.documentId(), routine.getWorkoutIds())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    List<Workout> workouts = new ArrayList<>();
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        Workout workout = snapshot.toObject(Workout.class);
                                        workout.setId(snapshot.getId());
                                        workouts.add(workout);
                                    }
                                    listener.onWorkoutsLoaded(workouts);
                                })
                                .addOnFailureListener(listener::onError);
                    } else {
                        // Không có buổi tập hoặc lịch trình không hợp lệ
                        listener.onWorkoutsLoaded(new ArrayList<>());
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy lịch sử tập luyện của người dùng
     * @param limit Số lượng buổi tập tối đa cần lấy (0 cho không giới hạn)
     * @param listener Callback với danh sách buổi tập
     */
    public void getUserWorkouts(int limit, OnWorkoutsLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("Người dùng chưa xác thực"));
            return;
        }

        // Tạo truy vấn
        Query query = workoutsRef
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("endTime", Query.Direction.DESCENDING);

        if (limit > 0) {
            query = query.limit(limit);
        }

        // Thực thi truy vấn
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Workout> workouts = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Workout workout = document.toObject(Workout.class);
                        if (workout != null) {
                            workout.setId(document.getId());
                            workouts.add(workout);
                        }
                    }
                    listener.onWorkoutsLoaded(workouts);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy buổi tập của người dùng", e);
                    listener.onError(e);
                });
    }

    /**
     * Xóa một lịch trình khỏi Firestore
     * @param routineId ID của lịch trình cần xóa
     * @param successListener Callback khi thành công
     * @param failureListener Callback khi thất bại
     */
    public void deleteRoutine(String routineId, OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {
        routinesRef.document(routineId).delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Cập nhật tóm tắt tập luyện của người dùng
     * @param userId ID người dùng
     * @param workout Buổi tập mới hoàn thành
     */
    public void updateWorkoutSummary(String userId, Workout workout) {
        DocumentReference summaryRef = db.collection("workout_summaries").document(userId);

        summaryRef.get().addOnSuccessListener(documentSnapshot -> {
            WorkoutSummary summary;
            if (documentSnapshot.exists()) {
                summary = documentSnapshot.toObject(WorkoutSummary.class);
            } else {
                summary = new WorkoutSummary(userId);
            }

            if (summary != null) {
                // Cập nhật tổng buổi tập
                summary.setTotalWorkouts(summary.getTotalWorkouts() + 1);

                // Cập nhật tổng thời gian (phút)
                long durationMinutes = workout.getDuration() / (60 * 1000);
                summary.setTotalTimeMinutes(summary.getTotalTimeMinutes() + durationMinutes);

                // Cập nhật số lượng bài tập
                summary.setTotalExercises(summary.getTotalExercises() + workout.getExerciseCount());

                // Lưu lại tóm tắt đã cập nhật
                summaryRef.set(summary);
            }
        });
    }

    /**
     * Interface cho callbacks dữ liệu người dùng
     */
    public interface OnUserDataListener {
        void onUserDataLoaded(User user);
        void onError(Exception e);
    }

    /**
     * Interface cho callbacks bài tập
     */
    public interface OnExercisesLoadedListener {
        void onExercisesLoaded(List<Exercise> exercises);
        void onError(Exception e);
    }

    /**
     * Interface cho callbacks nhóm cơ
     */
    public interface OnMuscleGroupsLoadedListener {
        void onMuscleGroupsLoaded(List<MuscleGroup> muscleGroups);
        void onError(Exception e);
    }

    /**
     * Interface cho callbacks lịch trình
     */
    public interface OnRoutinesLoadedListener {
        void onRoutinesLoaded(List<Routine> routines);
        void onError(Exception e);
    }

    /**
     * Interface cho callback lưu lịch trình
     */
    public interface OnRoutineSavedListener {
        void onRoutineSaved();
        void onError(Exception e);
    }

    /**
     * Interface cho callbacks nhiều buổi tập
     */
    public interface OnWorkoutsLoadedListener {
        void onWorkoutsLoaded(List<Workout> workouts);
        void onError(Exception e);
    }

    /**
     * Interface cho callback lưu buổi tập
     */
    public interface OnWorkoutSavedListener {
        void onWorkoutSaved(Workout workout);
        void onError(Exception e);
    }
}