package com.start.apps.pheezee.activities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PremiumSubscriptionDataRec {
    @SerializedName("customer_type")
    @Expose
    private String CustomerType;
    @SerializedName("start_date")
    @Expose
    private String StartDate;
    @SerializedName("end_date")
    @Expose
    private String EndDate;
    @SerializedName("report_generated")
    @Expose
    private String ReportGenerated;
    @SerializedName("number_of_accessable_generate")
    @Expose
    private String ReportAccessible;
    @SerializedName("amount")
    @Expose
    private String Amount;

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customer_type) {
        this.CustomerType = customer_type;
    }
    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String start_date) {
        this.StartDate = start_date;
    }
    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        this.EndDate = endDate;
    }
    public String getReportGenerated() {
        return ReportGenerated;
    }

    public void setReportGenerated(String reportGenerated) {
        this.ReportGenerated = reportGenerated;
    }
    public String getReportAccessible() {
        return ReportAccessible;
    }

    public void setReportAccessible(String reportAccessible) {
        this.ReportAccessible = reportAccessible;
    }
    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        this.Amount = amount;
    }
}
