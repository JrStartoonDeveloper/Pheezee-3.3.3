package com.start.apps.pheezee.popup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class viewRecommandDataRec {
    @SerializedName("exercisename")
    @Expose
    private String exercisename;
    @SerializedName("musclename")
    @Expose
    private String musclename;
    @SerializedName("heldon")
    @Expose
    private String heldon;
    @SerializedName("bodypart")
    @Expose
    private String bodypart;

    public String getExercise() {
        return exercisename;
    }

    public void setExercise(String exercisename) {
        this.exercisename = exercisename;
    }

    public String getMusclename() {
        return musclename;
    }

    public void setMusclename(String musclename) {
        this.musclename = musclename;
    }
    public String getHeldon() {
        return heldon;
    }

    public void setHeldon(String heldon) {
        this.heldon = heldon;
    }

    public void set(String bodypart) {
        this.bodypart = bodypart;
    }
    public String getBodyPart() {
        return bodypart;
    }
}
