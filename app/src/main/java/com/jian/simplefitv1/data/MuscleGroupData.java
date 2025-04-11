package com.jian.simplefitv1.data;

import com.jian.simplefitv1.models.MuscleGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuscleGroupData {

    private static final Map<String, MuscleGroup> muscleGroupMap = new HashMap<>();
    private static boolean isInitialized = false;

    // Initialize sample muscle group data
    private static void initializeData() {
        if (isInitialized) {
            return;
        }

        // Muscle Group 1: Chest
        MuscleGroup chest = new MuscleGroup(
                "chest",
                "Ngực",
                "Cơ ngực lớn và cơ ngực nhỏ nằm ở phần trước của cơ thể.",
                "muscle_chest"
        );

        // Muscle Group 2: Back
        MuscleGroup back = new MuscleGroup(
                "back",
                "Lưng",
                "Cơ lưng rộng, cơ thang và cơ trám nằm ở lưng trên và giữa.",
                "muscle_back"
        );

        // Muscle Group 3: Shoulders
        MuscleGroup shoulders = new MuscleGroup(
                "shoulders",
                "Vai",
                "Cơ delta bao phủ khớp vai, gồm ba phần: trước, giữa và sau.",
                "muscle_shoulders"
        );

        // Muscle Group 4: Arms
        MuscleGroup arms = new MuscleGroup(
                "arms",
                "Tay",
                "Bao gồm cơ biceps ở mặt trước cánh tay và cơ triceps ở mặt sau cánh tay.",
                "muscle_arms"
        );

        // Muscle Group 5: Triceps
        MuscleGroup triceps = new MuscleGroup(
                "triceps",
                "Cơ tam đầu",
                "Cơ ba đầu nằm ở mặt sau cánh tay, có chức năng duỗi khuỷu tay.",
                "muscle_triceps"
        );

        // Muscle Group 6: Biceps
        MuscleGroup biceps = new MuscleGroup(
                "biceps",
                "Cơ hai đầu",
                "Cơ hai đầu nằm ở mặt trước cánh tay, có chức năng gấp khuỷu tay.",
                "muscle_biceps"
        );

        // Muscle Group 7: Core
        MuscleGroup core = new MuscleGroup(
                "core",
                "Cốt lõi",
                "Cơ bụng sâu bao gồm cơ bụng thẳng, cơ chéo và cơ bụng ngang.",
                "muscle_core"
        );

        // Muscle Group 8: Legs
        MuscleGroup legs = new MuscleGroup(
                "legs",
                "Chân",
                "Bao gồm cơ đùi trước, cơ đùi sau, cơ bắp chân và cơ mông hỗ trợ cơ thể phần dưới.",
                "muscle_legs"
        );

        // Muscle Group 9: Abs
        MuscleGroup abs = new MuscleGroup(
                "abs",
                "Bụng",
                "Cơ bụng là nhóm cơ nằm ở phần trước thân, giữa ngực và xương chậu.",
                "muscle_abs"
        );

        // Muscle Group 10: Lower Back
        MuscleGroup lowerBack = new MuscleGroup(
                "lower_back",
                "Lưng dưới",
                "Cơ lưng dưới giúp hỗ trợ và ổn định cột sống thắt lưng.",
                "muscle_lower_back"
        );

        // Muscle Group 11: Cardio
        MuscleGroup cardio = new MuscleGroup(
                "cardio",
                "Tim mạch",
                "Tập luyện tim mạch giúp tăng cường sức khỏe tim và hệ hô hấp.",
                "muscle_cardio"
        );

        // Muscle Group 12: Full Body
        MuscleGroup fullBody = new MuscleGroup(
                "full_body",
                "Toàn thân",
                "Bài tập kết hợp nhiều nhóm cơ lớn trên toàn cơ thể.",
                "muscle_full_body"
        );

        // Add all muscle groups to the map
        muscleGroupMap.put(chest.getId(), chest);
        muscleGroupMap.put(back.getId(), back);
        muscleGroupMap.put(shoulders.getId(), shoulders);
        muscleGroupMap.put(arms.getId(), arms);
        muscleGroupMap.put(triceps.getId(), triceps);
        muscleGroupMap.put(biceps.getId(), biceps);
        muscleGroupMap.put(core.getId(), core);
        muscleGroupMap.put(legs.getId(), legs);
        muscleGroupMap.put(abs.getId(), abs);
        muscleGroupMap.put(lowerBack.getId(), lowerBack);
        muscleGroupMap.put(cardio.getId(), cardio);
        muscleGroupMap.put(fullBody.getId(), fullBody);

        isInitialized = true;
    }

    /**
     * Get all muscle groups in the database
     * @return List of all muscle groups
     */
    public static List<MuscleGroup> getAllMuscleGroups() {
        initializeData();
        return new ArrayList<>(muscleGroupMap.values());
    }

    /**
     * Get a muscle group by its ID
     * @param id The muscle group ID
     * @return The MuscleGroup object, or null if not found
     */
    public static MuscleGroup getMuscleGroupById(String id) {
        initializeData();
        return muscleGroupMap.get(id);
    }
}
