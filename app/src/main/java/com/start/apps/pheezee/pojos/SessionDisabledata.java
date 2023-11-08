package com.start.apps.pheezee.pojos;

public class SessionDisabledata {
    String PhizioEmail,PatientId;

    public SessionDisabledata(String phizioemail,String PatientId){
        this.PhizioEmail=phizioemail;
        this.PatientId=PatientId;
    }
    public String getPhizioemail() {
        return PhizioEmail;
    }

    public void setPhizioemail(String phizioemail) {
        this.PhizioEmail = phizioemail;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }
}
