package com.start.apps.pheezee.activities;

public class ReportCommunication {

    private String pt_email;
    private String pt_name;
    private String pt_phone;

    private String pt_amount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public ReportCommunication(String pt_email, String pt_name, String pt_phone, String pt_amount) {
        this.pt_email = pt_email;
        this.pt_name = pt_name;
        this.pt_phone = pt_phone;
        this.pt_amount=pt_amount;
    }


    public String getPhizioemail() {
        return pt_email;
    }

    public void setPhizioemail(String pt_email) {
        this.pt_email = pt_email;
    }

    public String getName() {
        return pt_name;
    }

    public void setName(String pt_name) {
        this.pt_name = pt_name;
    }

    public String getPhone() {
        return pt_phone;
    }

    public void setPhone(String pt_phone) {
        this.pt_phone = pt_phone;
    }
    public String getAmount() {
        return pt_amount;
    }

    public void setAmount(String pt_amount) {
        this.pt_amount = pt_amount;
    }

}
