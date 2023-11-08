package com.start.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import start.apps.pheezee.R;

import com.start.apps.pheezee.classes.PatientActivitySingleton;
import com.start.apps.pheezee.pojos.DeleteSessionData;
import com.start.apps.pheezee.pojos.MmtData;
import com.start.apps.pheezee.pojos.SessionData;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.room.Entity.MqttSync;
import com.start.apps.pheezee.room.PheezeeDatabase;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.ValueBasedColorOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.start.apps.pheezee.utils.PackageTypes.STANDARD_PACKAGE;

public class PhysiofeedbackPopupWindow {
    private String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    private String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    private String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    private boolean session_inserted_in_server = false;
    private String dateString;
    private Context context;
    private PopupWindow report;
    private int maxEmgValue, maxAngle, minAngle, angleCorrection, exercise_selected_position, body_part_selected_position, repsselected,hold_angle_session;
    private String sessionNo, mmt_selected = "", pain_status="", orientation, bodypart, phizioemail, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, body_orientation="",patient_status="",therapist_name="", session_type="", dateofjoin, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected, max_emg_selected;
    private String bodyOrientation="";
    private MqttSyncRepository repository;
    private MqttSyncRepository.OnSessionDataResponse response_data;
    private Long tsLong;
    private boolean mmt_selected_flag = false;
    private boolean pain_status_flag = false;
    private View layout_d;
    JSONArray emgJsonArray, romJsonArray;
    int phizio_packagetype;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public PhysiofeedbackPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps, int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected,View layout_d,JSONArray emgJsonArray, JSONArray romJsonArray,int phizio_packagetype,int hold_angle_session){
        this.context = context;
        this.maxEmgValue = maxEmgValue;
        this.sessionNo = sessionNo;
        this.maxAngle = maxAngle;
        this.minAngle = minAngle;
        this.orientation = orientation;
        this.bodypart = bodypart;
        this.phizioemail = phizioemail;
        this.sessiontime = sessiontime;
        this.actiontime = actiontime;
        this.holdtime = holdtime;
        this.numofreps = numofreps;
        this.angleCorrection = angleCorrection;
        this.patientid = patientid;
        this.patientname = patientname;
        this.tsLong = tsLong;
        this.bodyOrientation = bodyOrientation;
        this.dateofjoin = dateOfJoin;
        this.exercise_selected_position = exercise_selected_position;
        this.body_part_selected_position = body_part_selected_position;
        this.exercise_name = exercise_name;
        this.muscle_name = muscle_name;
        this.min_angle_selected = min_angle_selected;
        this.max_angle_selected = max_angle_selected;
        this.max_emg_selected = max_emg_selected;
        this.repsselected = repsselected;
        this.layout_d = layout_d;
        this.emgJsonArray = emgJsonArray;
        this.romJsonArray = romJsonArray;
        this.phizio_packagetype=phizio_packagetype;
        this.hold_angle_session=hold_angle_session;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }

    public void showWindow(){
        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.physiofeedback_popup_tablet, null);


        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.physiofeedback_popup, null);

        }

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        report = new PopupWindow(layout, width-0, ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        report.setWindowLayoutMode(width-0,ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(false);


        report.showAtLocation(layout, Gravity.CENTER, 0, 0);


        final LinearLayout ll_mmt_confirm = layout.findViewById(R.id.bp_model_mmt_confirm);

        LinearLayout ll_mmt_container = layout.findViewById(R.id.ll_mmt_grading);
        LinearLayout ll_paint_scale = layout.findViewById(R.id.paint_scale);

//        final RadioGroup rg_session_type = layout.findViewById(R.id.rg_session_type);
        final TextView ll_click_to_view_report = layout.findViewById(R.id.ll_click_to_view_report);

        EditText et_remarks = layout.findViewById(R.id.et_remarks);
        EditText phizio_name = layout.findViewById(R.id.phizio_name);
        TextView tv_confirm = layout.findViewById(R.id.tv_confirm_ll_overall_summary);
        Spinner sp_case_des = layout.findViewById(R.id.spinner1);
        Spinner sp_case_des_01 = layout.findViewById(R.id.spinner2);
        TextView text_box_data = layout.findViewById(R.id.text_box_data);
        TextView text_box_data_01= layout.findViewById(R.id.text_box_data_01);
//        ImageView image_exercise = layout.findViewById(R.id.image_exercise);

        SharedPreferences phiziname = PreferenceManager.getDefaultSharedPreferences(context);
        String phizioname_r = phiziname.getString("phizioname", "");

        phizio_name.setText(phizioname_r);

        // Spinner Data
        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.patient_status));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_case_des.setAdapter(array_exercise_names);

        sp_case_des.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        }) ;

        // Spinner 01 Data
        ArrayAdapter<String> array_exercise_names_01 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.session_type));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_case_des_01.setAdapter(array_exercise_names_01);

        sp_case_des_01.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        }) ;




        // Setting the proper image


        String feedback_image = orientation+"_"+bodypart+"_"+exercise_name;
        feedback_image = "ic_fb_"+feedback_image;
        feedback_image = feedback_image.replace(" - ","_");
        feedback_image = feedback_image.replace(" ","_");
        feedback_image = feedback_image.replace(")","");
        feedback_image = feedback_image.replace("(","");
        feedback_image = feedback_image.toLowerCase();

        int res = context.getResources().getIdentifier(feedback_image, "drawable",context.getPackageName());

//        if(res !=0) {
//            image_exercise.setImageResource(res);
//        }



//        rg_session_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
////                tv_confirm.setText("Confirm Session");
//            }
//        });


        for (int i=0;i<ll_mmt_container.getChildCount();i++){
            View view_nested = ll_mmt_container.getChildAt(i);
            view_nested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mmt_selected_flag = true;
//                    tv_confirm.setText("Confirm Session");
                    LinearLayout ll_container = ((LinearLayout)v);
                    LinearLayout parent = (LinearLayout) ll_container.getParent();
                    for (int i=0;i<parent.getChildCount();i++){
                        LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                        TextView tv_childs = (TextView) ll_child.getChildAt(0);
                        tv_childs.setBackgroundResource(R.drawable.grey_circle_feedback);
                        tv_childs.setTextColor(ContextCompat.getColor(context,R.color.pitch_black));
                    }
                    TextView tv_selected = (TextView) ll_container.getChildAt(0);
                    tv_selected.setBackgroundColor(Color.YELLOW);
                    mmt_selected=tv_selected.getText().toString();
                    tv_selected.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
                    tv_selected.setTextColor(ContextCompat.getColor(context,R.color.white));
                }
            });
        }
        for (int i=0;i<ll_paint_scale.getChildCount();i++){
            View view_nested = ll_paint_scale.getChildAt(i);
            view_nested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pain_status_flag = true;
//                    tv_confirm.setText("Confirm Session");
                    LinearLayout ll_container = ((LinearLayout)v);
                    LinearLayout parent = (LinearLayout) ll_container.getParent();
                    for (int i=0;i<parent.getChildCount();i++){
                        LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                        TextView tv_childs = (TextView) ll_child.getChildAt(0);
                        tv_childs.setBackgroundResource(R.drawable.grey_circle_feedback);
                        tv_childs.setTextColor(ContextCompat.getColor(context,R.color.pitch_black));
                    }
                    TextView tv_selected = (TextView) ll_container.getChildAt(0);
                    tv_selected.setBackgroundColor(Color.YELLOW);
                    pain_status=tv_selected.getText().toString();
                    tv_selected.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
                    tv_selected.setTextColor(ContextCompat.getColor(context,R.color.white));
                }
            });
        }


        //for held on date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateString = formatter.format(new Date(tsLong));

        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }

        ll_click_to_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
//                if(IS_SCEDULED_SESSION){
//                    if(IS_SCEDULED_SESSIONS_COMPLETED){
//                        Intent i = new Intent(context, PatientsView.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        context.startActivity(i);
//                    }
//                }
//                ((Activity)context).finish();
//                if(NetworkOperations.isNetworkAvailable(context)){
//                    Intent mmt_intent = new Intent(context, SessionReportActivity.class);
//                    mmt_intent.putExtra("patientid", patientid);
//                    mmt_intent.putExtra("patientname", patientname);
//                    mmt_intent.putExtra("phizioemail", phizioemail);
//                    mmt_intent.putExtra("dateofjoin",dateofjoin);
//                    ((Activity)context).startActivity(mmt_intent);
//                    report.dismiss();
//                }
//                else {
//                    NetworkOperations.networkError(context);
//                }
            }
        });

        ll_mmt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                ll_mmt_confirm.setAnimation(aniFade);
                String type = tv_confirm.getText().toString();
                if(type.equalsIgnoreCase("Next")) {

                    if (sp_case_des.getSelectedItem().toString().trim().equals("Patient Status")) {
                        text_box_data.setText("False");

                    }else{
                        text_box_data.setText("True");
                    }


                    if (sp_case_des_01.getSelectedItem().toString().trim().equals("Session Type")) {
                        text_box_data_01.setText("False");

                    }else{
                        text_box_data_01.setText("True");
                    }

                    if(text_box_data_01.getText().toString().equals("False")){
                        showToast("Please select the session type");
                    }
                    if(text_box_data.getText().toString().equals("False")){
                        showToast("Please select the patient Status");
                    }
                    if(mmt_selected_flag == false){
                        showToast("Please select the mmt grade");
                    }
                    if(pain_status_flag == false){
                        showToast("Please select the pain Scale");
                    }
                    if(therapist_name.equals(null)){
                        showToast("Please enter the therapist name");
                    }

                    if((!therapist_name.equals(""))){
                        showToast("Please enter the therapist name");
                    }
                    Log.e("Status_Kranthi", text_box_data.getText().toString());
                    Log.e("Status_Kranthi", text_box_data_01.getText().toString());


//                    RadioButton rb_session_type = layout.findViewById(rg_session_type.getCheckedRadioButtonId());
                    if (sp_case_des != null && mmt_selected_flag == true && pain_status_flag == true && text_box_data.getText().toString().equals("True") && text_box_data_01.getText().toString().equals("True") ) {
                        session_type = sp_case_des_01.getSelectedItem().toString();
                    }
                    String check = mmt_selected.concat(session_type);
                    String patient_status = sp_case_des.getSelectedItem().toString();
                    String comment_session = et_remarks.getText().toString();
                    String therapist_name = phizio_name.getText().toString();

                    if(therapist_name.equalsIgnoreCase(phizioname_r)){
                        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("phizioname", phizio_name.getText().toString());
                        editor.apply();
                    }else{
                        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("phizioname", phizio_name.getText().toString());
                        editor.apply();
                    }







                    if (sp_case_des != null && mmt_selected_flag == true && pain_status_flag == true && text_box_data.getText().toString().equals("True") && text_box_data_01.getText().toString().equals("True") && (!therapist_name.equals("")) ) {
                        if (!check.equalsIgnoreCase("Next")) {
//                        tv_confirm.setText("Next Session");
                            Log.e("33333333333333333333333333333333333333333",pain_status);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("phizioemail", phizioemail);
                                object.put("patientid", patientid);
                                object.put("heldon", dateString);
                                object.put("mmtgrade", mmt_selected);
                                object.put("painstatus",pain_status);
                                object.put("bodyorientation", bodyOrientation);
                                object.put("sessiontype", session_type);
                                object.put("commentsession", comment_session);
                                object.put("therapistname",therapist_name);
                                object.put("patientstatus",patient_status);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MqttSync mqttSync = new MqttSync(mqtt_publish_update_patient_mmt_grade, object.toString());
                            new StoreLocalDataAsync(mqttSync).execute();

                            //Else
                            ll_click_to_view_report.setAnimation(aniFade);
                            SessionSummaryPopupWindow feedback = new SessionSummaryPopupWindow(context,maxEmgValue, sessionNo, maxAngle, minAngle, orientation, bodypart,
                                    phizioemail, sessiontime, actiontime, holdtime, numofreps,
                                    angleCorrection, patientid, patientname, tsLong, bodyOrientation, dateofjoin, exercise_selected_position,body_part_selected_position,
                                    muscle_name,exercise_name,min_angle_selected,max_angle_selected,max_emg_selected,repsselected,hold_angle_session,mmt_selected,therapist_name,patient_status,pain_status,session_type,comment_session);
                            feedback.showWindow();
                            feedback.storeLocalSessionDetails(emgJsonArray,romJsonArray);

                            // Setting shared preference for deciding to download the report or not
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            editor = sharedPreferences.edit();

                            //Setting date in yyyy/mm/dd format
                            SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String sharedpref_date = date_formatter.format(new Date(tsLong));
                            editor.putBoolean(patientid+sharedpref_date, true);
                            editor.apply();
                            Log.d("sharedpreff",patientid+sharedpref_date);

                            if(phizio_packagetype!=STANDARD_PACKAGE)
                                repository.getPatientSessionNo(patientid);
                            feedback.setOnSessionDataResponse(new MqttSyncRepository.OnSessionDataResponse() {
                                @Override
                                public void onInsertSessionData(Boolean response, String message) {
                                    if (response)
                                        showToast(message.substring(0,1).toUpperCase()+ message.substring(1).toLowerCase());

                                }

                                @Override
                                public void onSessionDeleted(Boolean response, String message) {
                                    showToast(message.substring(0,1).toUpperCase()+ message.substring(1).toLowerCase());
                                }

                                @Override
                                public void onMmtValuesUpdated(Boolean response, String message) {
                                    showToast(message.substring(0,1).toUpperCase()+ message.substring(1).toLowerCase());
                                }

                                @Override
                                public void onCommentSessionUpdated(Boolean response) {
                                }
                            });

//                            report.dismiss();
//                            if (IS_SCEDULED_SESSION) {
//                                if (IS_SCEDULED_SESSIONS_COMPLETED) {
//                                    Intent i = new Intent(context, PatientsView.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    context.startActivity(i);
//                                }
//                            }
//                            ((Activity) context).finish();

                        }
                    }else {
//                        showToast("Please select MMT & Session type");
                    }
                }


            }
        });










        report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                View layout_dismiss = ((Activity) context).getLayoutInflater().inflate(R.layout.fragment_standard_gold_teach, null);
                ConstraintLayout monitorLayout = ((Activity)context).getWindow().findViewById(R.id.monitorLayout);
                monitorLayout.setAlpha(1.0f);
            }
        });
    }

    private void showToast(String nothing_selected) {
        Toast.makeText(context, nothing_selected, Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout ll_container = ((LinearLayout)v);
            LinearLayout parent = (LinearLayout) ll_container.getParent();
            for (int i=0;i<parent.getChildCount();i++){
                LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                TextView tv_childs = (TextView) ll_child.getChildAt(0);
                tv_childs.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
                tv_childs.setTextColor(ContextCompat.getColor(context,R.color.pitch_black));
            }
            TextView tv_selected = (TextView) ll_container.getChildAt(0);
            tv_selected.setBackgroundColor(Color.YELLOW);
            mmt_selected=tv_selected.getText().toString();
            tv_selected.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
            tv_selected.setTextColor(ContextCompat.getColor(context,R.color.white));
        }
    };




    /**
     * Sending data to the server and storing locally
     */
    public class StoreLocalDataAsync extends AsyncTask<Void,Void,Long> {
        private MqttSync mqttSync;
        public StoreLocalDataAsync(MqttSync mqttSync){
            this.mqttSync = mqttSync;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return PheezeeDatabase.getInstance(context).mqttSyncDao().insert(mqttSync);
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            new SendDataToServerAsync(mqttSync,id).execute();
        }
    }

    /**
     * Sending data to the server and storing locally
     */
    public class SendDataToServerAsync extends AsyncTask<Void, Void, Void> {
        private MqttSync mqttSync;
        private Long id;
        public SendDataToServerAsync(MqttSync mqttSync, Long id){
            this.mqttSync = mqttSync;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject object = new JSONObject(mqttSync.getMessage());
                object.put("id",id);
                if(NetworkOperations.isNetworkAvailable(context)){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if(mqttSync.getTopic()==mqtt_publish_update_patient_mmt_grade){
                        if(session_inserted_in_server){
                            MmtData data = gson.fromJson(object.toString(),MmtData.class);
                            repository.updateMmtData(data);
                        }
                        else {

                        }
                    } else  if(mqttSync.getTopic()==mqtt_delete_pateint_session){
                        if(session_inserted_in_server){
                            DeleteSessionData data = gson.fromJson(object.toString(),DeleteSessionData.class);
                            repository.deleteSessionData(data);
                        }
                        else {

                        }
                    }
                    else {
                        SessionData data = gson.fromJson(object.toString(),SessionData.class);
                        repository.insertSessionData(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * collects all the data of the session and sends to async task to send the data to the server and also to store locally.
     * @param emgJsonArray
     * @param romJsonArray
     */

    public void storeLocalSessionDetails( JSONArray emgJsonArray, JSONArray romJsonArray) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("Testing","Working");
                try {
                    JSONObject object = new JSONObject();
                    object.put("heldon",dateString);
                    object.put("maxangle",maxAngle);
                    object.put("minangle",minAngle);
                    object.put("anglecorrected",angleCorrection);
                    object.put("maxemg",maxEmgValue);
                    object.put("holdtime",holdtime);
                    object.put("holdangle",hold_angle_session);
                    object.put("bodypart",bodypart);
                    object.put("sessiontime",sessiontime);
                    object.put("numofreps",numofreps);
                    object.put("numofsessions",sessionNo);
                    object.put("phizioemail",phizioemail);
                    object.put("patientid",patientid);
                    object.put("painscale","");
                    object.put("muscletone","");
                    object.put("exercisename",exercise_name);
                    object.put("commentsession","");
                    object.put("symptoms","");
                    object.put("activetime",actiontime);
                    object.put("orientation", orientation);
                    object.put("mmtgrade",mmt_selected);
                    object.put("therapistname",therapist_name);
                    object.put("painstatus",pain_status);
                    object.put("patientstatus",patient_status);
                    object.put("bodyorientation",bodyOrientation);
                    object.put("sessiontype",session_type);
                    object.put("repsselected",repsselected);
                    object.put("musclename", muscle_name);
                    object.put("maxangleselected",max_angle_selected);
                    object.put("minangleselected",min_angle_selected);
                    object.put("maxemgselected",max_emg_selected);
                    object.put("sessioncolor",ValueBasedColorOperations.getCOlorBasedOnTheBodyPartExercise(bodypart,exercise_selected_position,maxAngle,minAngle,context));
                    Gson gson = new GsonBuilder().create();
                    Lock lock = new ReentrantLock();
                    lock.lock();
                    SessionData data = gson.fromJson(object.toString(),SessionData.class);
                    data.setEmgdata(emgJsonArray);
                    data.setRomdata(romJsonArray);
                    data.setActivityList(PatientActivitySingleton.getInstance().getactivitylist());
                    object = new JSONObject(gson.toJson(data));
                    Log.e("11111111111111111111111111111111111111111111111", String.valueOf(object));
                    MqttSync sync = new MqttSync(mqtt_publish_add_patient_session_emg_data,object.toString());
                    lock.unlock();
                    new StoreLocalDataAsync(sync).execute();
                    int numofsessions = Integer.parseInt(sessionNo);
                    repository.setPatientSessionNumber(String.valueOf(numofsessions),patientid);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    MqttSyncRepository.OnSessionDataResponse onSessionDataResponse = new MqttSyncRepository.OnSessionDataResponse() {
        @Override
        public void onInsertSessionData(Boolean response, String message) {
            if(response_data!=null){
                if(response){
                    session_inserted_in_server = true;
                }
                response_data.onInsertSessionData(response,message);
            }
        }

        @Override
        public void onSessionDeleted(Boolean response, String message) {
            if(response_data!=null){
                response_data.onSessionDeleted(response,message);
            }
        }

        @Override
        public void onMmtValuesUpdated(Boolean response, String message) {
            if(response_data!=null){
                response_data.onMmtValuesUpdated(response,message);
            }
        }

        @Override
        public void onCommentSessionUpdated(Boolean response) {
            if(response_data!=null){
                response_data.onCommentSessionUpdated(response);
            }
        }
    };



    public void setOnSessionDataResponse(MqttSyncRepository.OnSessionDataResponse response){
        this.response_data = response;
    }
}


