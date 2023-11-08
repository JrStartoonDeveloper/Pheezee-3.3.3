package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewSummaryDetails {
    @SerializedName("patientid")
    @Expose
    private String patientid;
    @SerializedName("patientname")
    @Expose
    private String patientname;
    @SerializedName("dateofjoin")
    @Expose
    private String dateofjoin;

    @SerializedName("patientage")
    @Expose
    private String patientage;

    @SerializedName("patientcasedes")
    @Expose
    private String patientcasedes;

    @SerializedName("patientcondition")
    @Expose
    private String patientcondition;

    @SerializedName("patientgender")
    @Expose
    private String patientgender;

    @SerializedName("patientphone")
    @Expose
    private String patientphone;

    @SerializedName("patientemail")
    @Expose
    private String patientemail;

    @SerializedName("patientinjured")
    @Expose
    private String patientinjured;

    @SerializedName("patienthistory")
    @Expose
    private String patienthistory;

    @SerializedName("patientprofilepicurl")
    @Expose
    private String patientprofilepicurl;



    @SerializedName("heldon")
    @Expose
    private String heldon;

    @SerializedName("Goal_reached")
    @Expose
    private String Goal_reached;

    @SerializedName("session_count")
    @Expose
    private String session_count;







    public String getpatientid() {
        return patientid;
    }

    public void setpatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getpatientname() {
        return patientname;
    }

    public void setpatientname(String patientname) {
        this.patientname = patientname;
    }


    public String getdateofjoin() {
        return dateofjoin;
    }

    public void setdateofjoin(String dateofjoin) {
        this.dateofjoin = dateofjoin;
    }



    public String getpatientage() {
        return patientage;
    }

    public void setpatientage(String patientage) {
        this.patientage = patientage;
    }

    public String getpatientcasedes() {
        return patientcasedes;
    }

    public void setpatientcasedes(String patientcasedes) {
        this.patientcasedes = patientcasedes;
    }


    public String getpatientcondition() {
        return patientcondition;
    }

    public void setpatientcondition(String patientcondition) {
        this.patientcondition = patientcondition;
    }




    public String getpatientgender() {
        return patientgender;
    }

    public void setpatientgender(String patientgender) {
        this.patientgender = patientgender;
    }

    public String getpatientphone() {
        return patientphone;
    }

    public void setpatientphone(String patientphone) {
        this.patientphone = patientphone;
    }


    public String getpatientemail() {
        return patientemail;
    }

    public void setpatientemail(String patientemail) {
        this.patientemail = patientemail;
    }

    public String getpatientinjured() {
        return patientinjured;
    }

    public void setpatientinjured(String patientinjured) {
        this.patientinjured = patientinjured;
    }



    public String getpatienthistory() {
        return patienthistory;
    }

    public void setpatienthistory(String patienthistory) {
        this.patienthistory = patienthistory;
    }


    public String getpatientprofilepicurl() {
        return patientprofilepicurl;
    }

    public void setpatientprofilepicurl(String patientprofilepicurl) {
        this.patientprofilepicurl = patientprofilepicurl;
    }

    public String getheldon() {
        return heldon;
    }

    public void setheldon(String heldon) {
        this.heldon = heldon;
    }

    public String getGoal_reached() {
        return Goal_reached;
    }

    public void setGoal_reached(String Goal_reached) {
        this.Goal_reached = Goal_reached;
    }

    public String getsession_count() {
        return session_count;
    }

    public void setsession_count(String session_count) {
        this.session_count = session_count;
    }

}
