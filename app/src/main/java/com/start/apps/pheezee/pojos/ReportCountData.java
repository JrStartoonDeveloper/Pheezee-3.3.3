package com.start.apps.pheezee.pojos;

public class ReportCountData {
    String phizioemail, patientid, heldon;

    public ReportCountData(String phizioemail,String patientid, String heldon){
        this.phizioemail= phizioemail;
        this.patientid= patientid;
        this.heldon = heldon;
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

    public void setHeldon(String heldon) {
        this.heldon = heldon;
    }

    public String getHeldon() {
        return heldon;
    }


}
