package com.start.apps.pheezee.activities;

public class GetPatinetData {
    String ExerciseType;
    String ExerciseSide;
    String remainImage;

    public GetPatinetData(String exerciseType, String exerciseSide, String remainImage) {
        ExerciseType = exerciseType;
        ExerciseSide = exerciseSide;
        this.remainImage = remainImage;
    }

    public String getExerciseType() {
        return ExerciseType;
    }

    public String getExerciseSide() {
        return ExerciseSide;
    }

    public String getRemainImage() {
        return remainImage;
    }
}
