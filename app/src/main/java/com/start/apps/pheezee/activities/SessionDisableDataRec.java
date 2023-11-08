package com.start.apps.pheezee.activities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionDisableDataRec {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("count_status")
    @Expose
    private String count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
