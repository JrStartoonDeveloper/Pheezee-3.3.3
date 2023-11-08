package com.start.apps.pheezee.activities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report_count_status_data_req {
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
