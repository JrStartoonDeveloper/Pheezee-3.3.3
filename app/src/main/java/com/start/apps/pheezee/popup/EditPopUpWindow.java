package com.start.apps.pheezee.popup;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import start.apps.pheezee.R;

import com.start.apps.pheezee.pojos.PatientDetailsData;
import com.start.apps.pheezee.room.Entity.PhizioPatients;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.start.apps.pheezee.activities.PatientsView.REQ_CAMERA;
import static com.start.apps.pheezee.activities.PatientsView.REQ_GALLERY;

public class EditPopUpWindow {
    Context context;
    static PhizioPatients patient;
    onClickListner listner;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Bitmap profile;
    String json_phizioemail;
    AlertDialog.Builder builder = null;
    boolean use_new_photo=false;

    boolean gallery_selected=false;
    boolean camera_selected=false;

    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };

    public EditPopUpWindow(final Activity context, PhizioPatients patient, String json_phizioemail){
        this.context = context;
        this.patient = patient;
        this.json_phizioemail = json_phizioemail;
    }

    public EditPopUpWindow(Context context,String json_phizioemail, Bitmap photo){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        this.profile = photo;
        use_new_photo = true;
    }

    public void openEditPopUpWindow(){
        PopupWindow pw;
        final String[] case_description = {""};
        final String[] case_00 ={""};
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();display.getSize(size);int width = size.x;int height = size.y;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View layout = inflater.inflate(R.layout.popup, null);

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

        final TextView tv_patientId = layout.findViewById(R.id.tv_patient_id);
        final TextView tv_create_account = layout.findViewById(R.id.tv_create_account);
        final EditText patientName = layout.findViewById(R.id.patientName);
        final TextView patientAge = layout.findViewById(R.id.patientAge);
        final EditText patientPhone = layout.findViewById(R.id.patientphone);
        final RadioGroup radioGroup = layout.findViewById(R.id.patientGender);
        RadioButton btn_male = layout.findViewById(R.id.radioBtn_male);
        RadioButton btn_female = layout.findViewById(R.id.radioBtn_female);
        final Spinner sp_case_des = layout.findViewById(R.id.sp_case_des);
        ImageView patient_profilepic = layout.findViewById(R.id.imageView4);
        ImageView patient_profilepic_image = layout.findViewById(R.id.profile_picture);
        final Spinner sp_case_des_01 = layout.findViewById(R.id.sp_case_des_01);
        final TextView patientHistory = layout.findViewById(R.id.textbox);
        final EditText patientEmail = layout.findViewById(R.id.patientemail);
        final CheckBox checkbox_left = layout.findViewById(R.id.check_left);
        final CheckBox checkbox_right = layout.findViewById(R.id.check_right);
        final TextView check_box = layout.findViewById(R.id.checkbox_text);
        final ImageView backbutton = layout.findViewById(R.id.iv_back_app_info);
        final TextView other_text_box = layout.findViewById(R.id.other_text_box);
        final EditText other_text_box_2 = layout.findViewById(R.id.other_text_box_2);
        final TextView text_data = layout.findViewById(R.id.text_data);
        final TextView text_data_01 = layout.findViewById(R.id.text_data_01);
        final TextView text_data_02 = layout.findViewById(R.id.text_data_02);
        final TextView text_data_03 = layout.findViewById(R.id.text_data_03);
        final RelativeLayout touch_update = layout.findViewById(R.id.touch_update);
        final LinearLayout touch_update_l = layout.findViewById(R.id.touch_update_l);
//        final TextView temp = layout.findViewById(R.id.spinner_temp_01);

        // Multi Check Box

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
                notification_title.setText("Seleted History");


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
                            patientHistory.setText("DM , Hypertension");
                        }
                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypothyroidism");
                        }
                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Presence of Implant");
                        }
                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypertension, Hypothyroidism ");
                        }
                        if(checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM,Hypothyroidism,Presence of Implant");
                        }

                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypertension, Presence of Implant");
                        }
                        if(checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("DM, Hypertension,Hypothyroidism, Presence of Implant");
                        }
                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && !checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension,Hypothyroidism");
                        }
                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && !checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension, Presence of Implant");
                        }

                        if(!checkBox_01.isChecked() && checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypertension,Hypothyroidism,Presence of Implant");
                        }

                        if(!checkBox_01.isChecked() && !checkBox_02.isChecked() && checkBox_03.isChecked() && checkBox_04.isChecked() && !checkBox_05.isChecked()){
                            patientHistory.setText("Hypothyroidism,Presence of Implant");
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

        // check Box


        //  back botton
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
            }
        });


        Calendar c=Calendar.getInstance();
        Integer month=c.get(Calendar.MONTH);
        Integer day=c.get(Calendar.DAY_OF_MONTH);
        Integer year=c.get(Calendar.YEAR);

        patientAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        patientAge.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        if(use_new_photo==false)
        {
            Glide.with(context)
                    .load("https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/" + json_phizioemail.replaceFirst("@", "%40") + "/patients/" + patient.getPatientid() + "/images/profilepic.png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.default_profile))

                    .listener(new RequestListener<Drawable>() {
                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                      patient_profilepic_image.setVisibility(View.VISIBLE);
                                      patient_profilepic.setVisibility(View.GONE);
                                      return false;
                                  }
                              }
                    )
                    .into(patient_profilepic_image);

        }else {
            if(this.profile!=null){
                patient_profilepic_image.setImageBitmap(this.profile);
                patient_profilepic_image.setVisibility(View.VISIBLE);
                patient_profilepic.setVisibility(View.GONE);
            }

        }

        tv_patientId.setText("Patient ID: "+patient.getPatientid());
        tv_patientId.setVisibility(View.VISIBLE);
        tv_create_account.setText("Edit Patient");


        //Adapter for spinner
        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_description));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_case_des.setAdapter(array_exercise_names);
        String[] cases_list = context.getResources().getStringArray(R.array.case_description);
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList,cases_list);
        if(arrayList.contains(patient.getPatientcasedes())) {
            sp_case_des.setSelection(arrayList.indexOf(patient.getPatientcasedes()));
        }else{

        }

        try{
        if(!patient.getPatientcondition().equals("")){
            other_text_box.setVisibility(View.VISIBLE);
            other_text_box.setText(patient.getPatientcondition());
            sp_case_des_01.setVisibility(View.INVISIBLE);
        }
        } catch (Exception e) {
            Log.e("your app", e.toString());
        }

        try {
            String patientinjured_side = patient.getPatientinjured();
            if(!checkbox_left.isChecked() && !checkbox_right.isChecked()){
                Log.e("Kranthi_data",patientinjured_side);
                if(patientinjured_side.equalsIgnoreCase("Left")){
                    checkbox_left.setChecked(true);
                }
                if(patientinjured_side.equalsIgnoreCase("Right")){
                    checkbox_right.setChecked(true);
                }
                if(patientinjured_side.equalsIgnoreCase("Bi-Lateral")){
                    checkbox_left.setChecked(true);
                    checkbox_right.setChecked(true);
                }
            }
        } catch (Exception e) {
            Log.e("your app", e.toString());
        }





        sp_case_des.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Log.e("case_desc", String.valueOf(event.getAction()));
                if(event.getAction() == MotionEvent.ACTION_UP){
                    other_text_box.setVisibility(View.INVISIBLE);
                    sp_case_des.setSelection(0);
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
                    Log.e("Status","Working");
                }else{
                    text_data_03.setText("False");
                }

                return false;
            }


        }) ;









//        sp_case_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                case_description[0] = sp_case_des.getSelectedItem().toString();
//
//                Log.e("case_desc_text",text_data_03.getText().toString());
//                if (text_data_03.getText().toString().equalsIgnoreCase("True")) {
//                    if (id == 0) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_00));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 1) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_01));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 2) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_02));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 3) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_03));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 4) {
//                        other_text_box.setVisibility(View.VISIBLE);
//                        sp_case_des_01.setVisibility(View.INVISIBLE);
//
//                    }
//                }else{
//                    if (id == 0) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_00));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 1) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_01));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 2) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_02));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 3) {
//                        other_text_box.setVisibility(View.INVISIBLE);
//                        sp_case_des_01.setVisibility(View.VISIBLE);
//                        ArrayAdapter<String> array_sub0 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_03));
//                        array_sub0.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        sp_case_des_01.setAdapter(array_sub0);
//                        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                return false;
//                            }
//                        });
//                    } else if (id == 4) {
//                        other_text_box.setVisibility(View.VISIBLE);
//                        sp_case_des_01.setVisibility(View.INVISIBLE);
//
//                    }
//                }
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });






        Button addBtn = layout.findViewById(R.id.addBtn);

        addBtn.setText("Update");
        final Button cancelBtn = layout.findViewById(R.id.cancelBtn);

        patientName.setText(patient.getPatientname());
        patientAge.setText(patient.getPatientage());
        patientPhone.setText(patient.getPatientphone());
        patientEmail.setText(patient.getPatientemail());
        patientHistory.setText(patient.getPatienthistory());

        if(patient.getPatientgender().equalsIgnoreCase("Male")) {
            radioGroup.check(btn_male.getId());
        }
        else{
            radioGroup.check(btn_female.getId());
        }






        case_description[0] = patient.getPatientcasedes();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(case_description[0].equalsIgnoreCase("Other")){
                    String patientcondition_data = other_text_box_2.getText().toString();
                    text_data_02.setText(patientcondition_data);
                }else {
                    try {
                        String patientcondition_data = sp_case_des_01.getSelectedItem().toString();
                        text_data_02.setText(patientcondition_data);
                    } catch (Exception e) {
                        Log.e("your app", e.toString());
                    }
                }


                RadioButton btn = layout.findViewById(radioGroup.getCheckedRadioButtonId());
                String patientname = patientName.getText().toString();
                String patientage = patientAge.getText().toString();
                String patientphone = patientPhone.getText().toString();
                String patientemail = patientEmail.getText().toString();
                String patientcondition = text_data_02.getText().toString();
//                String patientcondition = sp_case_des_01.getSelectedItem().toString();
                String patienthistory = patientHistory.getText().toString();




                if(checkbox_left.isChecked()){
                    check_box.setText("Left");
                    Log.e("Status","Working Left");
                }else if(checkbox_right.isChecked()){
                    check_box.setText("Right");
                    Log.e("Status","Working Right");
                }
                if(checkbox_left.isChecked() && checkbox_right.isChecked()){
                    check_box.setText("Bi-Lateral");
                    Log.e("Status","Working Bi-Lateral");
                }
                Log.e("kranthi", String.valueOf(check_box));
                String patientinjured = check_box.getText().toString();
                Log.e("Patient_Injured",patientinjured);

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

                if ((!patientname.equals("")) && (!patientage.equals(""))&& (text_data.getText().toString().equals("True")) && (text_data_01.getText().toString().equals("True")) && btn!=null  &&  (!patientphone.equals("")) && (!patientemail.equals("")) && (!patientinjured.equals("")) && (!patienthistory.equals(""))) {
                    PhizioPatients patients = new PhizioPatients(patient.getPatientid(),patientname,patient.getNumofsessions(),patient.getDateofjoin()
                            ,patientage,btn.getText().toString(),case_description[0],patient.getStatus(),patientinjured,patient.getPatientphone(),patient.getPatientemail(),patient.getPatientcondition(),patient.getPatienthistory(),patient.getPatientprofilepicurl(), patient.isSceduled());
                    patient.setPatientname(patientname);
                    patient.setPatientage(patientage);
                    patient.setPatientphone(patientphone);
                    patient.setPatientinjured(patientinjured);
                    patient.setPatientcasedes(case_description[0]);
                    patient.setPatientemail(patientemail);
                    patient.setPatientcondition(patientcondition);
                    patient.setPatienthistory(patienthistory);
                    patient.setPatientgender(btn.getText().toString());
                    PatientDetailsData data = new PatientDetailsData(json_phizioemail, patient.getPatientid(),
                            patient.getPatientname(),patient.getNumofsessions(), patient.getDateofjoin(), patient.getPatientage(),
                            patient.getPatientgender(), patient.getPatientcasedes(), patient.getStatus(),patientinjured,patient.getPatientphone(), patient.getPatientemail(), patient.getPatientcondition(),patient.getPatienthistory(),patient.getPatientprofilepicurl());
                    listner.onAddClickListner(patient,data,true,profile);
                    pw.dismiss();
                }
                else {
                    listner.onAddClickListner(null,null,false,profile);
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
                            if(ContextCompat.checkSelfPermission(context,CAMERA) == PackageManager.PERMISSION_DENIED) {

                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.notification_dialog_box);

                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                dialog.getWindow().setAttributes(lp);

                                TextView notification_title = dialog.findViewById(R.id.notification_box_title);
                                TextView notification_message = dialog.findViewById(R.id.notification_box_message);

                                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                                Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                                Notification_Button_ok.setText("Yes");
                                Notification_Button_cancel.setText("No");

                                // Setting up the notification dialog
                                notification_title.setText("Camera permission request");
                                notification_message.setText("Pheezee app needs camera permission \n to access camera");

                                // On click on Continue
                                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_DENIED) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                            intent.setData(uri);
                                            context.startActivity(intent);
                                            dialog.dismiss();

                                        }
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
                            } else if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                pw.dismiss();
//                                ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 5);
                                cameraIntent();
                            }
                            else {
                                pw.dismiss();
                                cameraIntent();
                            }

                            dialog.dismiss();

                        }else if(gallery_selected==true)
                        {
                            if(ContextCompat.checkSelfPermission(context,READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                                Log.i("Location_status:","working");
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.notification_dialog_box);

                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                dialog.getWindow().setAttributes(lp);

                                TextView notification_title = dialog.findViewById(R.id.notification_box_title);
                                TextView notification_message = dialog.findViewById(R.id.notification_box_message);

                                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                                Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                                Notification_Button_ok.setText("Yes");
                                Notification_Button_cancel.setText("No");

                                // Setting up the notification dialog
                                notification_title.setText("Storage permission request");
                                notification_message.setText("Pheezee app needs storage permission \n to access your gallery");

                                // On click on Continue
                                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                            intent.setData(uri);
                                            context.startActivity(intent);
                                            dialog.dismiss();

                                        }
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
                            } else if(ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                galleryIntent();
                                pw.dismiss();
                                dialog.dismiss();
                            }
                            else {
                                pw.dismiss();
                                galleryIntent();
                            }

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
    }



    private void cameraIntent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ((Activity) context).startActivityForResult(takePicture, 41);
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
            ((Activity) context).startActivityForResult(pickPhoto, 42);
        }else {
            ActivityCompat.requestPermissions(((Activity) context), new String[] {Manifest.permission.CAMERA}, REQ_GALLERY);
        }
    }


    public interface onClickListner{
        void onAddClickListner(PhizioPatients patients, PatientDetailsData data, boolean isvalid,Bitmap photo);
    }

    public void setOnClickListner(onClickListner listner){
        this.listner = listner;
    }
}
