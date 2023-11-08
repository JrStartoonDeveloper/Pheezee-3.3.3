package com.start.apps.pheezee.pojos;

import android.util.Log;

public class AddPatientData {
    long id;
    String phizioemail;
    String patientid;
    String patientname;
    String numofsessions;
    String dateofjoin;
    String patientage;
    String patientgender;
    String patientcasedes;
    String status;
    String patientinjured;
    String patientphone;
    String patientemail;
    String patientcondition;
    String patienthistory;
    String patientprofilepicurl;


    public AddPatientData(long id, String phizioemail, String patientid, String patientname, String numofsessions,
                              String dateofjoin, String patientage, String patientgender, String patientcasedes,
                              String status,String patientinjured, String patientphone, String patientemail, String patientcondition, String patienthistory,String patientprofilepicurl) {
        this.id = id;
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.patientname = patientname;
        this.numofsessions = numofsessions;
        this.dateofjoin = dateofjoin;
        this.patientage = patientage;
        this.patientgender = patientgender;
        this.patientcasedes = patientcasedes;
        this.status = status;
        this.patientinjured = patientinjured;
        this.patientphone = patientphone;
        this.patientemail = patientemail;
        this.patientcondition = patientcondition;
        this.patienthistory = patienthistory;
        this.patientprofilepicurl = patientprofilepicurl;
    }


    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getNumofsessions() {
        return numofsessions;
    }

    public void setNumofsessions(String numofsessions) {
        this.numofsessions = numofsessions;
    }

    public String getDateofjoin() {
        return dateofjoin;
    }

    public void setDateofjoin(String dateofjoin) {
        this.dateofjoin = dateofjoin;
    }

    public String getPatientage() {
        return patientage;
    }

    public void setPatientage(String patientage) {
        this.patientage = patientage;
    }

    public String getPatientgender() {
        return patientgender;
    }

    public void setPatientgender(String patientgender) {
        this.patientgender = patientgender;
    }

    public String getPatientcasedes() {
        return patientcasedes;
    }

    public void setPatientcasedes(String patientcasedes) {
        this.patientcasedes = patientcasedes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientinjured() {
        return patientinjured;
    }

    public void setPatientinjured(String status) {
        this.patientinjured = patientinjured;
    }



    public String getPatientphone() {
        return patientphone;
    }

    public void setPatientphone(String patientphone) {
        this.patientphone = patientphone;
    }

    public String getPatientemail() {
        return patientemail;
    }

    public void setPatientemail(String patientemail) {
        this.patientemail = patientemail;
    }

    public String getPatientcondition() {
        return patientcondition;
    }

    public void setPatientcondition(String patientcondition) {
        this.patientcondition = patientcondition;
    }

    public String getPatienthistory() {
        return patienthistory;
    }

    public void setPatienthistory(String patienthistory) {
        this.patienthistory = patienthistory;
    }


    public String getPatientprofilepicurl() {
        return patientprofilepicurl;
    }

    public void setPatientprofilepicurl(String patientprofilepicurl) {
        this.patientprofilepicurl = patientprofilepicurl;
    }
}
