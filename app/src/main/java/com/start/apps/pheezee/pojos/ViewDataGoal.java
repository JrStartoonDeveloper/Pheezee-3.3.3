package com.start.apps.pheezee.pojos;

public class ViewDataGoal {
    String phizioemail;
    String patientid;
    String patientdoj;
    public ViewDataGoal(String phizioemail,String patientid, String patientdoj ){
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.patientdoj = patientdoj;
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

    public String getPatientdoj() {
        return patientdoj;
    }

    public void setPatientdoj(String patientdoj) {
        this.patientdoj = patientdoj;
    }
}
