package com.start.apps.pheezee.pojos;

public class ViewRecommandations {
    private String phizioemail;
    private String patientid;
    private String injured_side;


    public ViewRecommandations(String phizioemail, String patientid, String injured_side) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.injured_side = injured_side;
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

    public String getInjured_side() {
        return injured_side;
    }

    public void setInjured_side(String injured_side) {
        this.injured_side = injured_side;
    }
}
