package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

import android.app.Activity;
import android.content.Context;

import com.start.apps.pheezee.popup.ViewPopUpWindow;


public class MyClass {
    public void ViewPopUpWindows(Context context, String patient, String email) {
        ViewPopUpWindow popUpWindow = new ViewPopUpWindow(context, patient, email);
        popUpWindow.openViewPopUpWindow();
    }
}
