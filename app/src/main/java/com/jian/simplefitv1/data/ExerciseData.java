package com.jian.simplefitv1.data;

import com.jian.simplefitv1.models.Exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseData {

    private static final Map<String, Exercise> exerciseMap = new HashMap<>();
    private static boolean isInitialized = false;

    // Initialize sample exercise data
    private static void initializeData() {
        if (isInitialized) {
            return;
        }

        // Exercise 1: Push-up
        Exercise pushup = new Exercise(
                "pushup",
                "Hít đất (Push-up)",
                "Bài tập cơ bản giúp phát triển cơ ngực, vai và tay.",
                "exercise_pushup",
                Arrays.asList("chest", "shoulders", "triceps"),
                "Cơ bản",
                Arrays.asList(
                        "Bắt đầu ở tư thế plank cao với hai tay rộng hơn vai một chút.",
                        "Giữ cơ thể thẳng từ đầu đến gót chân.",
                        "Hạ ngực xuống sàn bằng cách gập khuỷu tay.",
                        "Đẩy người lên trở về tư thế ban đầu.",
                        "Giữ cốt lõi căng suốt động tác."
                ),
                "https://www.youtube.com/watch?v=IODxDxX7oi4"
        );

        // Exercise 2: Pull-up
        Exercise pullup = new Exercise(
                "pullup",
                "Kéo xà (Pull-up)",
                "Bài tập phát triển cơ lưng, cơ tay và cơ vai.",
                "exercise_pullup",
                Arrays.asList("back", "biceps", "arms"),
                "Trung bình",
                Arrays.asList(
                        "Nắm thanh xà ngang với hai tay rộng hơn vai, lòng bàn tay hướng ra trước.",
                        "Treo người với tay duỗi thẳng và chân không chạm đất.",
                        "Kéo người lên cho đến khi cằm vượt qua thanh xà.",
                        "Từ từ hạ người xuống với kiểm soát.",
                        "Lặp lại mà không chạm đất giữa các lần thực hiện."
                ),
                "https://www.youtube.com/watch?v=eGo4IYlbE5g"
        );

        // Exercise 3: Dips
        Exercise dips = new Exercise(
                "dips",
                "Chống đẩy vai (Dips)",
                "Bài tập phát triển cơ tam đầu, cơ ngực và cơ vai.",
                "exercise_dips",
                Arrays.asList("triceps", "chest", "shoulders"),
                "Trung bình",
                Arrays.asList(
                        "Sử dụng hai thanh song song hoặc ghế, nắm chặt bằng hai tay.",
                        "Duỗi thẳng tay và nâng cơ thể lên.",
                        "Hạ người xuống bằng cách gập khuỷu tay cho đến khi cánh tay song song với mặt đất.",
                        "Đẩy người lên trở về tư thế ban đầu.",
                        "Giữ khuỷu tay gần thân để tập trung vào cơ tam đầu."
                ),
                "https://www.youtube.com/watch?v=2z8JmcrW-As"
        );

        // Exercise 4: Squats
        Exercise squats = new Exercise(
                "squats",
                "Ngồi xổm (Squats)",
                "Bài tập phát triển cơ đùi, cơ mông và cơ lõi.",
                "exercise_squats",
                Arrays.asList("legs"),
                "Cơ bản",
                Arrays.asList(
                        "Đứng thẳng, hai chân rộng bằng vai.",
                        "Giữ ngực thẳng và lưng thẳng.",
                        "Hạ người xuống bằng cách gập đầu gối và đẩy hông về phía sau.",
                        "Hạ xuống cho đến khi đùi song song với mặt đất hoặc thấp hơn.",
                        "Đẩy gót chân xuống sàn để trở về tư thế ban đầu."
                ),
                "https://www.youtube.com/watch?v=aclHkVaku9U"
        );

        // Exercise 5: Lunges
        Exercise lunges = new Exercise(
                "lunges",
                "Lunge",
                "Bài tập phát triển cơ đùi, cơ mông và cải thiện sự cân bằng.",
                "exercise_lunges",
                Arrays.asList("legs"),
                "Cơ bản",
                Arrays.asList(
                        "Đứng thẳng, hai chân rộng bằng hông.",
                        "Bước một chân về phía trước.",
                        "Hạ người xuống cho đến khi cả hai đầu gối gập 90 độ.",
                        "Đẩy gót chân trước xuống sàn để trở về tư thế ban đầu.",
                        "Lặp lại với chân còn lại."
                ),
                "https://www.youtube.com/watch?v=QOVaHwm-Q6U"
        );

        // Exercise 6: Plank
        Exercise plank = new Exercise(
                "plank",
                "Plank",
                "Bài tập isometric củng cố cơ lõi, bụng và lưng dưới.",
                "exercise_plank",
                Arrays.asList("core", "abs", "lower_back"),
                "Cơ bản",
                Arrays.asList(
                        "Bắt đầu với tư thế chống đẩy, hai khuỷu tay chống xuống sàn dưới vai.",
                        "Duỗi thẳng chân, chống mũi bàn chân xuống sàn.",
                        "Giữ cơ thể thẳng từ đầu đến gót chân.",
                        "Căng cơ bụng và giữ tư thế trong thời gian mong muốn.",
                        "Thở đều trong suốt quá trình thực hiện."
                ),
                "https://www.youtube.com/watch?v=pSHjTRCQxIw"
        );

        // Exercise 7: Mountain Climbers
        Exercise mountainClimbers = new Exercise(
                "mountain_climbers",
                "Leo núi (Mountain Climbers)",
                "Bài tập cardio phát triển cơ lõi và tăng nhịp tim.",
                "exercise_mountain_climbers",
                Arrays.asList("core", "abs", "cardio"),
                "Cơ bản",
                Arrays.asList(
                        "Bắt đầu ở tư thế plank cao, hai tay thẳng.",
                        "Kéo một đầu gối về phía ngực.",
                        "Đưa chân đó trở lại tư thế ban đầu trong khi kéo chân còn lại về phía trước.",
                        "Tiếp tục luân phiên hai chân với nhịp độ nhanh.",
                        "Giữ cốt lõi căng và lưng thẳng trong suốt quá trình."
                ),
                "https://www.youtube.com/watch?v=nmwgirgXLYM"
        );

        // Exercise 8: Burpees
        Exercise burpees = new Exercise(
                "burpees",
                "Burpees",
                "Bài tập toàn thân kết hợp chống đẩy, ngồi xổm và nhảy.",
                "exercise_burpees",
                Arrays.asList("full_body", "cardio"),
                "Nâng cao",
                Arrays.asList(
                        "Đứng thẳng, hai chân rộng bằng vai.",
                        "Ngồi xổm, đặt tay xuống sàn trước mặt.",
                        "Đá chân về phía sau vào tư thế plank.",
                        "Thực hiện một lần chống đẩy (tùy chọn).",
                        "Nhảy chân về trước vào tư thế ngồi xổm.",
                        "Nhảy lên cao, duỗi thẳng người và vung tay lên trên đầu."
                ),
                "https://www.youtube.com/watch?v=TU8QYVW0gDU"
        );

        // Exercise 9: Jumping Jacks
        Exercise jumpingJacks = new Exercise(
                "jumping_jacks",
                "Nhảy jack (Jumping Jacks)",
                "Bài tập cardio đơn giản tăng nhịp tim và làm nóng cơ thể.",
                "exercise_jumping_jacks",
                Arrays.asList("cardio", "full_body"),
                "Cơ bản",
                Arrays.asList(
                        "Đứng thẳng, hai chân khép, hai tay dọc theo thân.",
                        "Nhảy để mở rộng chân ra hai bên đồng thời đưa tay lên trên đầu.",
                        "Nhảy trở lại tư thế ban đầu.",
                        "Tiếp tục lặp lại với nhịp độ nhanh.",
                        "Giữ nhịp thở đều đặn suốt bài tập."
                ),
                "https://www.youtube.com/watch?v=c4DAnQ6DtF8"
        );

        // Exercise 10: Crunches
        Exercise crunches = new Exercise(
                "crunches",
                "Gập bụng (Crunches)",
                "Bài tập tập trung vào cơ bụng trên.",
                "exercise_crunches",
                Arrays.asList("abs"),
                "Cơ bản",
                Arrays.asList(
                        "Nằm ngửa, hai gối gập, bàn chân đặt trên sàn.",
                        "Đặt hai tay sau đầu hoặc khoanh trước ngực.",
                        "Nâng vai và đầu lên khỏi sàn bằng cách co cơ bụng.",
                        "Hạ từ từ trở lại sàn.",
                        "Giữ cằm cách xa ngực để tránh căng cổ."
                ),
                "https://www.youtube.com/watch?v=Xyd_fa5zoEU"
        );

        // Exercise 11: Leg Raises
        Exercise legRaises = new Exercise(
                "leg_raises",
                "Nâng chân (Leg Raises)",
                "Bài tập tập trung vào cơ bụng dưới.",
                "exercise_leg_raises",
                Arrays.asList("abs", "core"),
                "Trung bình",
                Arrays.asList(
                        "Nằm ngửa, hai tay đặt dọc theo thân hoặc dưới mông.",
                        "Giữ chân thẳng và bắt đầu với chân nằm trên sàn.",
                        "Nâng từ từ hai chân lên cho đến khi vuông góc với sàn.",
                        "Hạ từ từ chân xuống nhưng không để chạm sàn trước khi nâng lại.",
                        "Giữ lưng dưới ép sát sàn trong suốt động tác."
                ),
                "https://www.youtube.com/watch?v=l4kQd9eWclE"
        );

        // Exercise 12: Australian Pull-ups
        Exercise australianPullups = new Exercise(
                "australian_pullups",
                "Kéo xà úc (Australian Pull-ups)",
                "Bài tập kéo xà ngang phát triển cơ lưng và cơ tay phù hợp cho người mới.",
                "exercise_australian_pullups",
                Arrays.asList("back", "biceps"),
                "Cơ bản",
                Arrays.asList(
                        "Đặt một thanh ngang ở độ cao thấp hoặc sử dụng bàn.",
                        "Nằm dưới thanh, nắm thanh bằng hai tay với lòng bàn tay hướng về phía bạn.",
                        "Giữ cơ thể thẳng, gót chân chạm sàn.",
                        "Kéo ngực về phía thanh xà.",
                        "Hạ người xuống với kiểm soát và lặp lại."
                ),
                "https://www.youtube.com/watch?v=FnWrvWZpI9c"
        );

        // Exercise 13: Pike Push-ups
        Exercise pikePushups = new Exercise(
                "pike_pushups",
                "Chống đẩy pike (Pike Push-ups)",
                "Bài tập phát triển cơ vai và chuẩn bị cho handstand push-up.",
                "exercise_pike_pushups",
                Arrays.asList("shoulders", "triceps"),
                "Trung bình",
                Arrays.asList(
                        "Bắt đầu ở tư thế chống đẩy.",
                        "Nâng hông lên cao để tạo hình chữ V ngược với sàn.",
                        "Đầu hướng về phía sàn, hai tay rộng bằng vai.",
                        "Gập khuỷu tay, hạ đầu gần sàn.",
                        "Đẩy người trở lại tư thế ban đầu bằng cách duỗi thẳng tay."
                ),
                "https://www.youtube.com/watch?v=x7_I5SUAd00"
        );

        // Exercise 14: Diamond Push-ups
        Exercise diamondPushups = new Exercise(
                "diamond_pushups",
                "Chống đẩy kim cương (Diamond Push-ups)",
                "Biến thể của chống đẩy tập trung vào cơ tam đầu.",
                "exercise_diamond_pushups",
                Arrays.asList("triceps", "chest"),
                "Trung bình",
                Arrays.asList(
                        "Bắt đầu ở tư thế plank cao.",
                        "Đặt hai tay sát vào nhau dưới ngực, ngón cái và ngón trỏ tạo hình kim cương.",
                        "Giữ cơ thể thẳng từ đầu đến gót chân.",
                        "Hạ ngực xuống gần tay.",
                        "Đẩy người lên trở về tư thế ban đầu."
                ),
                "https://www.youtube.com/watch?v=J0DnG1_S92I"
        );

        // Exercise 15: Superman
        Exercise superman = new Exercise(
                "superman",
                "Superman",
                "Bài tập tăng cường cơ lưng dưới và cơ mông.",
                "exercise_superman",
                Arrays.asList("lower_back"),
                "Cơ bản",
                Arrays.asList(
                        "Nằm sấp, tay duỗi thẳng phía trước, chân thẳng.",
                        "Nâng đồng thời tay, ngực, và chân lên khỏi sàn.",
                        "Giữ tư thế này trong 2-3 giây.",
                        "Từ từ hạ người xuống sàn.",
                        "Lặp lại động tác."
                ),
                "https://www.youtube.com/watch?v=cc6UVRS7PW4"
        );

        // Exercise 16: Handstand
        Exercise handstand = new Exercise(
                "handstand",
                "Chống đẩy thẳng đứng (Handstand)",
                "Bài tập nâng cao phát triển sức mạnh vai, cân bằng và cơ lõi.",
                "exercise_handstand",
                Arrays.asList("shoulders", "core"),
                "Nâng cao",
                Arrays.asList(
                        "Bắt đầu bằng cách đặt tay xuống sàn cách tường khoảng 15-20cm.",
                        "Đá chân lên tường để hỗ trợ cân bằng.",
                        "Giữ cơ thể thẳng, tay duỗi thẳng.",
                        "Tập trung vào việc giữ cân bằng và hít thở đều.",
                        "Khi đã quen, có thể tập không cần tường."
                ),
                "https://www.youtube.com/watch?v=ctunmnwbbSI"
        );

        // Exercise 17: L-sits
        Exercise lSits = new Exercise(
                "l_sits",
                "L-sits",
                "Bài tập isometric phát triển cơ lõi, vai và cơ tứ đầu đùi.",
                "exercise_l_sits",
                Arrays.asList("core", "abs", "shoulders"),
                "Nâng cao",
                Arrays.asList(
                        "Ngồi trên sàn, đặt lòng bàn tay xuống sàn ở hai bên hông.",
                        "Đẩy cơ thể lên, nâng mông khỏi sàn.",
                        "Duỗi thẳng chân về phía trước, tạo hình chữ L.",
                        "Giữ tư thế này càng lâu càng tốt.",
                        "Bắt đầu với đầu gối gập nếu quá khó."
                ),
                "https://www.youtube.com/watch?v=IUZJoSP66HI"
        );

        // Exercise 18: Hollow Body Hold
        Exercise hollowBodyHold = new Exercise(
                "hollow_body_hold",
                "Hollow Body Hold",
                "Bài tập isometric phát triển cơ bụng sâu và cơ lõi.",
                "exercise_hollow_body_hold",
                Arrays.asList("abs", "core"),
                "Trung bình",
                Arrays.asList(
                        "Nằm ngửa, ép lưng dưới sát sàn.",
                        "Nâng vai và chân lên khỏi sàn đồng thời.",
                        "Duỗi tay về phía sau đầu.",
                        "Tạo hình cong như một chiếc thuyền chuối.",
                        "Giữ tư thế này từ 20-60 giây."
                ),
                "https://www.youtube.com/watch?v=4xRpKpHpyYw"
        );

        // Exercise 19: Jump Squats
        Exercise jumpSquats = new Exercise(
                "jump_squats",
                "Squat nhảy (Jump Squats)",
                "Bài tập plyometric phát triển sức mạnh và sức bền cho chân.",
                "exercise_jump_squats",
                Arrays.asList("legs", "cardio"),
                "Trung bình",
                Arrays.asList(
                        "Đứng thẳng, hai chân rộng bằng vai.",
                        "Hạ người xuống tư thế ngồi xổm.",
                        "Từ tư thế ngồi xổm, bật mạnh lên cao.",
                        "Đáp xuống nhẹ nhàng, ngay lập tức đi vào tư thế ngồi xổm tiếp theo.",
                        "Tiếp tục lặp lại động tác."
                ),
                "https://www.youtube.com/watch?v=CVaEhXotL7M"
        );

        // Exercise 20: Flutter Kicks
        Exercise flutterKicks = new Exercise(
                "flutter_kicks",
                "Đạp chân (Flutter Kicks)",
                "Bài tập tăng cường cơ bụng dưới.",
                "exercise_flutter_kicks",
                Arrays.asList("abs", "core"),
                "Cơ bản",
                Arrays.asList(
                        "Nằm ngửa, hai tay đặt dưới mông hoặc bên cạnh thân.",
                        "Nâng đầu và vai lên khỏi sàn (tùy chọn).",
                        "Nâng chân lên khoảng 15cm khỏi sàn.",
                        "Đạp hai chân lên xuống luân phiên, giống như đang bơi.",
                        "Giữ lưng dưới ép sát sàn trong suốt quá trình."
                ),
                "https://www.youtube.com/watch?v=ANVdMDaYRts"
        );

        // Add all exercises to the map
        exerciseMap.put(pushup.getId(), pushup);
        exerciseMap.put(pullup.getId(), pullup);
        exerciseMap.put(dips.getId(), dips);
        exerciseMap.put(squats.getId(), squats);
        exerciseMap.put(lunges.getId(), lunges);
        exerciseMap.put(plank.getId(), plank);
        exerciseMap.put(mountainClimbers.getId(), mountainClimbers);
        exerciseMap.put(burpees.getId(), burpees);
        exerciseMap.put(jumpingJacks.getId(), jumpingJacks);
        exerciseMap.put(crunches.getId(), crunches);
        exerciseMap.put(legRaises.getId(), legRaises);
        exerciseMap.put(australianPullups.getId(), australianPullups);
        exerciseMap.put(pikePushups.getId(), pikePushups);
        exerciseMap.put(diamondPushups.getId(), diamondPushups);
        exerciseMap.put(superman.getId(), superman);
        exerciseMap.put(handstand.getId(), handstand);
        exerciseMap.put(lSits.getId(), lSits);
        exerciseMap.put(hollowBodyHold.getId(), hollowBodyHold);
        exerciseMap.put(jumpSquats.getId(), jumpSquats);
        exerciseMap.put(flutterKicks.getId(), flutterKicks);

        isInitialized = true;
    }

    /**
     * Get all exercises in the database
     * @return List of all exercises
     */
    public static List<Exercise> getAllExercises() {
        initializeData();
        return new ArrayList<>(exerciseMap.values());
    }

    /**
     * Get an exercise by its ID
     * @param id The exercise ID
     * @return The Exercise object, or null if not found
     */
    public static Exercise getExerciseById(String id) {
        initializeData();
        return exerciseMap.get(id);
    }

    /**
     * Filter exercises by muscle group
     * @param muscleGroupId The muscle group ID
     * @return List of exercises targeting the specified muscle group
     */
    public static List<Exercise> getExercisesByMuscleGroup(String muscleGroupId) {
        initializeData();

        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : exerciseMap.values()) {
            if (exercise.getMuscleGroups().contains(muscleGroupId)) {
                filteredExercises.add(exercise);
            }
        }

        return filteredExercises;
    }

    /**
     * Filter exercises by difficulty level
     * @param difficulty The difficulty level (Cơ bản, Trung bình, or Nâng cao)
     * @return List of exercises with the specified difficulty
     */
    public static List<Exercise> getExercisesByDifficulty(String difficulty) {
        initializeData();

        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : exerciseMap.values()) {
            if (exercise.getDifficulty().equalsIgnoreCase(difficulty)) {
                filteredExercises.add(exercise);
            }
        }

        return filteredExercises;
    }
}
