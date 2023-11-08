package com.start.apps.pheezee.popup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionSummeryableDataRec {
    @SerializedName("max_emg")
    @Expose
    private String max_emg;
    @SerializedName("max_rom")
    @Expose
    private String max_rom;

    public String getMax_emg() {
        return max_emg;
    }

    public void setMax_emg(String max_emg) {
        this.max_emg = max_emg;
    }

    public String getMax_rom() {
        return max_rom;
    }

    public void setMax_rom(String max_rom) {
        this.max_rom = max_rom;
    }
}
