package com.start.apps.pheezee.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ImageView;
import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import static com.start.apps.pheezee.activities.PatientsView.REQ_CAMERA;
import static com.start.apps.pheezee.activities.PatientsView.REQ_GALLERY;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import start.apps.pheezee.R;

import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.pojos.DeletePatientData;
import com.start.apps.pheezee.pojos.PatientDetailsData;
import com.start.apps.pheezee.room.Entity.PhizioPatients;
import com.start.apps.pheezee.utils.DateOperations;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.TimeOperations;

import static com.facebook.FacebookSdk.getApplicationContext;

import static start.apps.pheezee.R.id.checkbox;
import static start.apps.pheezee.R.id.injured_postion;
import static start.apps.pheezee.R.id.iv_back_app_info;
import static start.apps.pheezee.R.id.text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPatientPopUpWindow{
    onClickListner listner;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Context context;
    String json_phizioemail;
    Bitmap profile;

    String Patinet_Name,Patient_Phone,Patient_Email;
    boolean gallery_selected=false;
    boolean camera_selected=false;
    private DatePickerDialog datePickerDialog;

    AlertDialog.Builder builder = null;
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };


    public AddPatientPopUpWindow(Context context, String json_phizioemail){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
    }

    public AddPatientPopUpWindow(Context context, String json_phizioemail, Bitmap photo){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        this.profile = photo;
    }

    public void openAddPatientPopUpWindow(){
        final String[] case_description = {""};
        PopupWindow pw;
        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        Log.e("abhbyscybdy",String.valueOf(config.smallestScreenWidthDp));
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.popup_tablet, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.popup, null);
        }

        pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pw.setElevation(10);
        }
        pw.setTouchable(true);
        pw.setOutsideTouchable(true);
        pw.setContentView(layout);
        pw.setFocusable(true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

        final EditText patientName = layout.findViewById(R.id.patientName);
        final TextView patientAge = layout.findViewById(R.id.patientAge);
        final EditText patientPhone = layout.findViewById(R.id.patientphone);
        final EditText patientEmail = layout.findViewById(R.id.patientemail);
//        final EditText caseDescription = layout.findViewById(R.id.contentDescription);
        final RadioGroup radioGroup = layout.findViewById(R.id.patientGender);
        final Spinner sp_case_des = layout.findViewById(R.id.sp_case_des);
        final Spinner sp_case_des_01 = layout.findViewById(R.id.sp_case_des_01);
        final TextView patientHistory = layout.findViewById(R.id.textbox);
        final ImageView backbutton = layout.findViewById(R.id.iv_back_app_info);
        final CheckBox checkbox_left = layout.findViewById(R.id.check_left);
        final CheckBox checkbox_right = layout.findViewById(R.id.check_right);
        final TextView check_box = layout.findViewById(R.id.checkbox_text);
//        final TextView spinnper_temp =layout.findViewById(R.id.spinner_temp);
        final TextView text_data = layout.findViewById(R.id.text_data);
        final TextView text_data_01 = layout.findViewById(R.id.text_data_01);
        final TextView text_data_02 = layout.findViewById(R.id.text_data_02);
        final TextView other_text_box = layout.findViewById(R.id.other_text_box);
        final EditText other_text_box_2 = layout.findViewById(R.id.other_text_box_2);

        // Multi Check Box



        patientHistory.setMovementMethod(new ScrollingMovementMethod());
        patientHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.notification_dialog_box_check_box);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);

                TextView notification_title = dialog.findViewById(R.id.notification_box_title);
//                CheckBox checkBox = dialog.findViewById(R.id.)
//                TextView notification_message = dialog.findViewById(R.id.notification_box_message);
                CheckBox checkBox_01 = dialog.findViewById(R.id.check_01);
                CheckBox checkBox_02 = dialog.findViewById(R.id.check_02);
                CheckBox checkBox_03 = dialog.findViewById(R.id.check_03);
                CheckBox checkBox_04 = dialog.findViewById(R.id.check_04);
                CheckBox checkBox_05 = dialog.findViewById(R.id.check_05);
//                CheckBox checkBox_06 = dialog.findViewById(R.id.check_06);

                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                Notification_Button_ok.setText("Yes");
                Notification_Button_cancel.setText("No");

                // Setting up the notification dialog
                notification_title.setText("Selected History");


                // On click on Continue
                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && !checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM");
                        }
                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && !checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension");
                        }
                        if(!checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypothyroidism");
                        }
                        if(!checkBox_01.isChecked() && !checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Presence of Implant");
                        }
                        if(!checkBox_01.isChecked() && !checkBox_02.isChecked() && !checkBox_03.isChecked() && !checkBox_04.isChecked() && checkBox_05.isChecked()){
                            patientHistory.setText("Others");
                        }
                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && !checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
//                            patientHistory.setText(getApplicationContext().getString(R.string.dmhypertension));

                            patientHistory.setText("DM, Hypertension");

                        }
                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypothyroidism");
                        }
                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Presence of Implant");
                        }
                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypertension, Hypothyroidism");
                        }
                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypothyroidism, Presence of Implant");
                        }

                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypertension, Presence of Implant");

                        }
                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypertension, Hypothyroidism, Presence of Implant");
                        }
                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension, Hypothyroidism");
                        }
                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension, Presence of Implant");
                        }

                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension,  Hypothyroidism, Presence of Implant");
                        }

                        if(!checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypothyroidism, Presence of Implant");
                        }
                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && checkBox_05.isChecked()){
                            patientHistory.setText("Null");
                        }
                        dialog.dismiss();
                    }
                });
                // On click Cancel
                Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                dialog.show();

                // End

            }


        });

        ArrayList<Integer> arrayList_history = new ArrayList<>();
        String[] array_history  = {"DM","Hypertension", "Hypothyroidism", "Hyperthyroidism", "Presence of implant", "others"};






//     // suggestion Box
//
//        ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.check_box));
//        patientHistory.setAdapter(arrayAdapter);
//        patientHistory.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());



        ImageView patient_profilepic = layout.findViewById(R.id.imageView4);
        ImageView patient_profilepic_image = layout.findViewById(R.id.profile_picture);


        // Age Calander
        Calendar c=Calendar.getInstance();
        Integer month=c.get(Calendar.MONTH);
        Integer day=c.get(Calendar.DAY_OF_MONTH);
        Integer year=c.get(Calendar.YEAR);



        patientAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DatePickerDialog  datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker view, int year, int month, int day) {
                       month = month + 1;
                       String date = day + "/" + month + "/" + year;

                       String age = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault()).format(new Date());

                       patientAge.setText(date);
                   }
               },year,month,day);
               datePickerDialog.show();
            }
        });

        //Back Button

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
            }
        });






        //Adapter for spinner

        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_description));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        sp_case_des.setAdapter(array_exercise_names);
        final String todaysDate = DateOperations.dateInMmDdYyyy();
        sp_case_des.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        }) ;

        sp_case_des_01.setVisibility(View.INVISIBLE);
//        spinnper_temp.setVisibility(View.VISIBLE);


        Button addBtn = layout.findViewById(R.id.addBtn);
        Button cancelBtn = layout.findViewById(R.id.cancelBtn);
        if(this.profile!=null){
            patient_profilepic_image.setImageBitmap(this.profile);
            patient_profilepic_image.setVisibility(View.VISIBLE);
            patientName.setText(this.Patinet_Name);
            patientPhone.setText(this.Patient_Phone);
            patientEmail.setText(this.Patient_Email);
//            patient_profilepic.setVisibility(View.VISIBLE);
        }


        sp_case_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                case_description[0] = sp_case_des.getSelectedItem().toString();
                if (id == 0) {
                    other_text_box.setVisibility(View.INVISIBLE);
                    sp_case_des_01.setVisibility(View.VISIBLE);
                    other_text_box_2.setVisibility(View.INVISIBLE);
                    ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_00));
                    array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp_case_des_01.setAdapter(array_sub0);
                    sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return false;
                        }
                    });
                } else if (id == 1) {
                    other_text_box.setVisibility(View.INVISIBLE);
                    sp_case_des_01.setVisibility(View.VISIBLE);
                    other_text_box_2.setVisibility(View.INVISIBLE);
                    ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_01));
                    array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp_case_des_01.setAdapter(array_sub0);
                    sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return false;
                        }
                    });
                } else if (id == 2) {

                    other_text_box.setVisibility(View.INVISIBLE);
                    sp_case_des_01.setVisibility(View.VISIBLE);
                    other_text_box_2.setVisibility(View.INVISIBLE);
                    ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_02));
                    array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp_case_des_01.setAdapter(array_sub0);
                    sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return false;
                        }
                    });
                } else if (id == 3) {

                    other_text_box.setVisibility(View.INVISIBLE);
                    sp_case_des_01.setVisibility(View.VISIBLE);
                    other_text_box_2.setVisibility(View.INVISIBLE);
                    ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_03));
                    array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp_case_des_01.setAdapter(array_sub0);
                    sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return false;
                        }
                    });
                } else if (id == 4) {
                    other_text_box.setVisibility(View.INVISIBLE);
                    sp_case_des_01.setVisibility(View.INVISIBLE);
                    other_text_box_2.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 0;

                RadioButton btn = layout.findViewById(radioGroup.getCheckedRadioButtonId());
                if(sharedPref.getInt("maxid",-1)!=-1){
                    id = sharedPref.getInt("maxid",0);
                    id+=1;
                }
                // Check Box Logic

                if(checkbox_left.isChecked() && !checkbox_right.isChecked()){
                    check_box.setText("Left");
                } else if(!checkbox_left.isChecked() && checkbox_right.isChecked()){
                    check_box.setText("Right");
                }
                if(checkbox_left.isChecked() && checkbox_right.isChecked()){
                    check_box.setText("Bi-Lateral");
                }

                if(case_description[0].equalsIgnoreCase("Other")){
                    String patientcondition_data = other_text_box_2.getText().toString();
                    text_data_02.setText(patientcondition_data);
                }else {
                    String patientcondition_data = sp_case_des_01.getSelectedItem().toString();
                    text_data_02.setText(patientcondition_data);
                }


                String dateString = TimeOperations.getUTCdatetimeAsString();
                String patientid =  String.valueOf(id).concat(" ").concat(dateString);
                String patientname = patientName.getText().toString();
                String patientphone = patientPhone.getText().toString();
                String patientage = patientAge.getText().toString();
                String patientemail = patientEmail.getText().toString();
                String patientcondition = text_data_02.getText().toString();
                Log.e("testing_data_patientcondition",patientcondition);
                String patienthistory = patientHistory.getText().toString();
                String patientinjured = check_box.getText().toString();
                Log.e("Kranthi_Burra",patientinjured);




                if(case_description[0].equals("Specialities")){
                    text_data.setText("False");
                }else{
                    text_data.setText("True");
                }
                Log.e("Kranthi_text_data", text_data.getText().toString());
                if(patientcondition.equals(null)){
                    text_data_01.setText("False");
                }else{
                    text_data_01.setText("True");
                }


             


                if ((!patientname.equals("")) && (!patientid.equals("")) && (!patientage.equals(""))&& (text_data.getText().toString().equals("True")) && (text_data_01.getText().toString().equals("True")) && btn!=null  &&  (!patientphone.equals("")) && (!patientemail.equals("")) && (!patientinjured.equals("")) && (!patienthistory.equals(""))) {
                    PhizioPatients patient = new PhizioPatients(patientid,patientname,"0",todaysDate,patientage,btn.getText().toString(),
                            case_description[0],"active",patientinjured,patientphone,patientemail,patientcondition,patienthistory,"empty", false);

                    Log.e("kranthi_testing_patient", String.valueOf(patient));

                    PatientDetailsData data = new PatientDetailsData(json_phizioemail,patientid, patientname, "0",
                            todaysDate,patientage, btn.getText().toString(),case_description[0],"active",patientinjured, patientphone,patientemail,patientcondition,patienthistory, "empty");

                    if(sharedPref.getInt("maxid",-1)!=-1) {
                        editor.putInt("maxid", id);
                        editor.apply();
                    }
                    listner.onAddPatientClickListner(patient,data,true,profile);
                    pw.dismiss();
                }
                else {
                    listner.onAddPatientClickListner(null,null,false,null);
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });


        patient_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Custom notification added by Haaris
                // custom dialog
                Log.e("1111111111111111111111111111111111111111111122222222222222","Hello1");
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.set_profile_photo_layout);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);

                ImageView take_photo_asset =  dialog.findViewById(R.id.take_photo_asset);
                ImageView gallery_asset =  dialog.findViewById(R.id.gallery_asset);

                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);



                // On click on Continue
                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(camera_selected==true)
                        {
                            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                pw.dismiss();
                                ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 5);
                                cameraIntent();
                            }
                            else {
                                pw.dismiss();
                                cameraIntent();
                            }

                            dialog.dismiss();

                        }else if(gallery_selected==true)
                        {
                            galleryIntent();
                            pw.dismiss();
                            dialog.dismiss();
                        }else
                        {
                            Toast.makeText(context, "Please select any one option.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                // On click on Continue
                take_photo_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=false;
                        camera_selected=true;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_unselected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_selected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));




                    }
                });

                // On click on Continue
                gallery_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=true;
                        camera_selected=false;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_selected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_unselected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));


                    }
                });

                dialog.show();

                // End
            }
        });

        patient_profilepic_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Custom notification added by Haaris
                // custom dialog
                Log.e("1111111111111111111111111111111111111111111122222222222222","Hello");

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.set_profile_photo_layout);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                Log.e("1111111111111111111111111111111111111111111122222222222222",patientName.getText().toString());
                Log.e("1111111111111111111111111111111111111111111122222222222222",patientEmail.getText().toString());
                Log.e("1111111111111111111111111111111111111111111122222222222222",patientPhone.getText().toString());
                Patinet_Name= patientName.getText().toString();
                Patient_Email= patientEmail.getText().toString();
                Patient_Phone= patientPhone.getText().toString();
                dialog.getWindow().setAttributes(lp);

                ImageView take_photo_asset =  dialog.findViewById(R.id.take_photo_asset);
                ImageView gallery_asset =  dialog.findViewById(R.id.gallery_asset);

                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);



                // On click on Continue
                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(camera_selected==true)
                        {
                            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                pw.dismiss();
                                ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 5);
                                cameraIntent();
                            }
                            else {
                                pw.dismiss();
                                cameraIntent();
                            }

                            dialog.dismiss();

                        }else if(gallery_selected==true)
                        {
                            galleryIntent();
                            pw.dismiss();
                            dialog.dismiss();
                        }else
                        {
                            Toast.makeText(context, "Please select any one option.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                // On click on Continue
                take_photo_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=false;
                        camera_selected=true;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_unselected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_selected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));




                    }
                });

                // On click on Continue
                gallery_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=true;
                        camera_selected=false;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_selected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_unselected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));


                    }
                });

                dialog.show();

                // End
            }
        });
    }
    private void cameraIntent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ((Activity) context).startActivityForResult(takePicture, 21);
        }else {
            ActivityCompat.requestPermissions(((Activity) context), new String[] {Manifest.permission.CAMERA}, REQ_CAMERA);
        }
    }
    private void galleryIntent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//        pickPhoto.putExtra("patientid",1);
            ((Activity) context).startActivityForResult(pickPhoto, 22);
        }else {
            ActivityCompat.requestPermissions(((Activity) context), new String[] {Manifest.permission.CAMERA}, REQ_GALLERY);
        }
    }



    public interface onClickListner{
        void onAddPatientClickListner(PhizioPatients patient, PatientDetailsData data, boolean isvalid,Bitmap photo);
    }

    public void setOnClickListner(onClickListner listner){
        this.listner = listner;
    }
}
