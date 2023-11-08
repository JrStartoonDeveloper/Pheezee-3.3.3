package com.start.apps.pheezee.activities;

public class Record_affected_GetData {
    String ExerciseType;
    String MuscleName;
    String HeldOn;
    String BodyPart;

    public Record_affected_GetData(String exerciseType, String muscleName, String heldOn,String bodyPart) {
        ExerciseType = exerciseType;
        MuscleName = muscleName;
        HeldOn = heldOn;
        BodyPart=bodyPart;
    }

    public String getExerciseType() {
        return ExerciseType;
    }

    public String getMuscleName() {
        return MuscleName;
    }

    public String getHeldOnDate() {
        return HeldOn;
    }
    public String getBodyPart() {
        return BodyPart;
    }
}
