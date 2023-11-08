package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewSessionReportData {
    @SerializedName("session")
    @Expose
    private String session;
    @SerializedName("report")
    @Expose
    private String report;
    @SerializedName("data")
    @Expose
    private String data;

    @SerializedName("heldon_dates")
    @Expose
    private String heldon_dates;

    @SerializedName("patient")
    @Expose
    private String patient;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
    public String getReportCollection() {
        return data;
    }

    public void setReportCollection(String data) {
        this.data = data;
    }

    public String getHeldonDates() {
        return heldon_dates;
    }

    public void setHeldonDates(String heldon_dates) {
        this.heldon_dates = heldon_dates;
    }

    public String getPatientData() {
        return patient;
    }

    public void setPatientData(String patient) {
        this.patient = patient;
    }

}
