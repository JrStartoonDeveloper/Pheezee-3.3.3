package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SesssionPrintSummary {
    @SerializedName("bodypart")
    @Expose
    private String bodypart;
    @SerializedName("exercisename")
    @Expose
    private String exercisename;

    @SerializedName("musclename")
    @Expose
    private String musclename;

    @SerializedName("maxemg")
    @Expose
    private String maxemg;

    @SerializedName("maxangle")
    @Expose
    private String maxangle;

    @SerializedName("minangle")
    @Expose
    private String minangle;

    @SerializedName("maxangleselected")
    @Expose
    private String maxangleselected;

    @SerializedName("minangleselected")
    @Expose
    private String minangleselected;



    public String getbodypart() {
        return bodypart;
    }

    public void setbodypart(String bodypart) {
        this.bodypart = bodypart;
    }


    public String getexercisename() {
        return exercisename;
    }

    public void setexercisename(String exercisename) {
        this.exercisename = exercisename;
    }

    public String getmusclename() {
        return musclename;
    }

    public void setmusclename(String musclename) {
        this.musclename = musclename;
    }

    public String getmaxemg() {
        return maxemg;
    }

    public void setmaxemg(String maxemg) {
        this.maxemg = maxemg;
    }


    public String getmaxangle() {
        return maxangle;
    }

    public void setmaxangle(String maxangle) {
        this.maxangle = maxangle;
    }

    public String getminangle() {
        return minangle;
    }

    public void setminangle(String minangle) {
        this.minangle = minangle;
    }

    public String getmaxangleselected() {
        return maxangleselected;
    }

    public void setmaxangleselected(String maxangleselected) {
        this.maxangleselected = maxangleselected;
    }

    public String getminangleselected() {
        return minangleselected;
    }

    public void setminangleselected(String minangleselected) {
        this.minangleselected = minangleselected;
    }


}
