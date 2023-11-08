package com.start.apps.pheezee.popup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportCountDataRec {
    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("sessiondetails")
    @Expose
    private String sessionDetails;

    public void setId(String id) {
        this.id = id;
    }

    public void setSessionDetails(String sessionDetails) {
        this.sessionDetails = sessionDetails;
    }

    public String getId() {
        return id;
    }

    public String getSessionDetails() {
        return sessionDetails;
    }
}
