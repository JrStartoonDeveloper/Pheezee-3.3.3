package com.start.apps.pheezee.pojos;

public class SessionSummeryOppositesideTableData {
    String patientid,phizioemail,injured_side,bodypart,bodyorientation,exercisename,musclename;

    public SessionSummeryOppositesideTableData(String patientId, String phizioemail, String injured_side, String bodypart, String bodyorientation, String exercisename, String musclename) {
        this.patientid = patientId;
        this.phizioemail = phizioemail;
        this.injured_side = injured_side;
        this.bodypart = bodypart;
        this.bodyorientation = bodyorientation;
        this.exercisename = exercisename;
        this.musclename = musclename;
    }
    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getPatientId() {
        return patientid;
    }

    public String getInjurede_side() {
        return injured_side;
    }

    public String getBodypart() {
        return bodypart;
    }

    public String getBodyorientation() {
        return bodyorientation;
    }

    public String getExercisename() {
        return exercisename;
    }

    public String getMusclename() {
        return musclename;
    }

    public void setPatientId(String patientId) {
        this.patientid = patientId;
    }

    public void setInjurede_side(String injurede_side) {
        injured_side = injurede_side;
    }

    public void setBodypart(String bodypart) {
        this.bodypart = bodypart;
    }

    public void setBodyorientation(String bodyorientation) {
        this.bodyorientation = bodyorientation;
    }

    public void setExercisename(String exercisename) {
        this.exercisename = exercisename;
    }

    public void setMusclename(String musclename) {
        this.musclename = musclename;
    }
}
