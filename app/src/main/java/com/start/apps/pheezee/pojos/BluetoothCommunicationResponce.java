package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BluetoothCommunicationResponce {
    @SerializedName("pt_email")
    @Expose
    private String pt_email;
    @SerializedName("pt_name")
    @Expose
    private String pt_name;
    @SerializedName("pt_number")
    @Expose
    private String pt_number;



    public String getEmail() {
        return pt_email;
    }

    public void setEmail(String pt_email) {
        this.pt_email = pt_email;
    }

    public String getName() {
        return pt_name;
    }

    public void setName(String pt_name) {
        this.pt_name = pt_name;
    }



    public String getNumber() {
        return pt_number;
    }

    public void setNumber(String pt_number) {
        this.pt_number = pt_number;
    }




}
