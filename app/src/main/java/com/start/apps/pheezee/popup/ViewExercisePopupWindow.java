package com.start.apps.pheezee.popup;


import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.activities.PremiumSubscriptionDataRec;
import com.start.apps.pheezee.activities.SessionReportActivity;
import com.start.apps.pheezee.adapters.SessionListArrayAdapter;
import com.start.apps.pheezee.classes.PatientActivitySingleton;
import com.start.apps.pheezee.classes.SessionListClass;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import start.apps.pheezee.R;
import com.start.apps.pheezee.activities.MonitorActivity;
import com.start.apps.pheezee.pojos.DeleteSessionData;
import com.start.apps.pheezee.pojos.MmtData;
import com.start.apps.pheezee.pojos.PatientStatusData;
import com.start.apps.pheezee.pojos.PremiumSubscriptionUserData;
import com.start.apps.pheezee.pojos.ResponseData;
import com.start.apps.pheezee.pojos.SessionData;
import com.start.apps.pheezee.pojos.SessionDetailsResult;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.room.Entity.MqttSync;
import com.start.apps.pheezee.room.PheezeeDatabase;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.ValueBasedColorOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.start.apps.pheezee.activities.MonitorActivity.IS_SCEDULED_SESSION;
import static com.start.apps.pheezee.activities.MonitorActivity.IS_SCEDULED_SESSIONS_COMPLETED;
import static com.start.apps.pheezee.activities.SessionReportActivity.phizioemail;

public class ViewExercisePopupWindow {
    private String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    private String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    private String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SessionListArrayAdapter sessionListArrayAdapter,sessionListArrayAdapter1,sessionListArrayAdapter2;
    ListView lv_sessionlist,lv_sessionlist1,lv_sessionlist2;
    TextView Upper_Extremity,Trunk,Lower_Extremity;

    LinearLayout Upper_Extremity_Layout,Trunk_Layout,Lower_Extremity_Layout;
    ArrayList<SessionListClass> mSessionListResults,mSessionListResults1,mSessionListResults2;
    private boolean session_inserted_in_server = false;
    JSONArray emgJsonArray, romJsonArray;
    int phizio_packagetype;
    private String dateString,heldon;
    private Context context;
    private PopupWindow report;
    private int maxEmgValue, maxAngle, minAngle, angleCorrection, exercise_selected_position, body_part_selected_position, repsselected,hold_angle_session;
    private String sessionNo, mmt_selected = "", therapist_name="",patient_status="", pain_status="",orientation, bodypart, phizioemail, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, body_orientation="", session_type="", dateofjoin, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected, max_emg_selected,comment_session;
    private String bodyOrientation="";
    private MqttSyncRepository repository;
    private MqttSyncRepository.OnSessionDataResponse response_data;
    private Long tsLong;
    GetDataService getDataService;
    ProgressDialog progress;


    public ViewExercisePopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                   String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                   String holdtime, String numofreps, int angleCorrection,
                                   String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                   int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                   String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected, int hold_angle_session, String mmt_selected,String therapist_name,String patient_status,String pain_status, String session_type, String comment_session){
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
        this.hold_angle_session = hold_angle_session;

        this.mmt_selected = mmt_selected;
        this.therapist_name = therapist_name;
        this.patient_status = pain_status;
        this.pain_status = pain_status;
        this.session_type = session_type;
        this.comment_session = comment_session;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);

    }

    public ViewExercisePopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                   String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                   String holdtime, String numofreps, int angleCorrection,
                                   String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                   int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                   String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected, String heldon){
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
        this.heldon = heldon;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);

    }

    private String calenderToYYYMMDD(Calendar date){
        Date date_cal = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date_cal);
        return strDate;
    }


    public void showWindow(){
        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;

        layout = ((Activity)context).getLayoutInflater().inflate(R.layout.view_exercise_popup, null);


        mSessionListResults = new ArrayList<SessionListClass>();
        mSessionListResults1 = new ArrayList<SessionListClass>();
        mSessionListResults2 = new ArrayList<SessionListClass>();




        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Calendar calendar = Calendar.getInstance();
        String date = calenderToYYYMMDD(calendar);

        if(heldon!=null)
        {
            date = heldon;
            TextView tv_delete_pateint_session = layout.findViewById(R.id.summary_tv_delete_session);
            TextView tv_back_session = layout.findViewById(R.id.tv_back_session);
            tv_delete_pateint_session.setVisibility(View.GONE);
            tv_back_session.setVisibility(View.VISIBLE);
        }
        PatientStatusData data = new PatientStatusData(phizioemail, patientid,date,date);

        progress = new ProgressDialog(context);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    finish();
                    dialog.dismiss();
                }
                return true;
            }
        });

        lv_sessionlist =layout.findViewById(R.id.lv_sessionList);
        lv_sessionlist1 =layout.findViewById(R.id.lv_sessionList1);
        lv_sessionlist2 =layout.findViewById(R.id.lv_sessionList2);
        Upper_Extremity=layout.findViewById(R.id.upper_extremity_text);
        Lower_Extremity=layout.findViewById(R.id.lower_extremity_text);
        Upper_Extremity_Layout=layout.findViewById(R.id.upper_extremity_layout);
        Lower_Extremity_Layout=layout.findViewById(R.id.lower_extremity_layout);
        Trunk_Layout=layout.findViewById(R.id.trunk_layout);
        Trunk=layout.findViewById(R.id.trunk_text);






        Call<List<SessionDetailsResult>> getSessiondetails_respone = getDataService.getSessiondetails(data);
        getSessiondetails_respone.enqueue(new Callback<List<SessionDetailsResult>>() {
            @Override
            public void onResponse(Call<List<SessionDetailsResult>> call, Response<List<SessionDetailsResult>> response) {

                if(response.isSuccessful()){
                    if (response.code() == 200) {


                        if(bodypart.length()>0) {
                            // Adding current exercise
                            SessionListClass first_exercise = new SessionListClass();
                            SessionListClass first_exercise1 = new SessionListClass();
                            SessionListClass first_exercise2 = new SessionListClass();
                            HashMap<String, Integer> hash=new HashMap<>();
                            String Key=bodypart+"/"+exercise_name+"/"+orientation+"/"+bodyOrientation;
                            Log.e("111111111111111111111111111111111111111",Key);
                            if(bodypart=="Sholder"||bodypart=="Elbow"||bodypart=="Forearm"||bodypart=="Wrist"){
                                first_exercise.setBodypart(bodypart);
                                first_exercise.setOrientation(bodyOrientation);
                                first_exercise.setPosition(orientation);
                                first_exercise.setExercise(exercise_name);
                                first_exercise.setMuscle_name(muscle_name);
                                first_exercise.setSession_time(sessiontime);
                                first_exercise.setHeldon(dateString);
                                first_exercise.setPatientemail(phizioemail);
                                first_exercise.setPatientid(patientid);
                                first_exercise.setPatientname(patientname);
                                if(!hash.containsKey(Key)){
                                    int emg=maxEmgValue;
                                    hash.put(Key,emg);
                                    first_exercise.setBestexercise("Yes");
                                }
                                else{
                                    int emg=hash.get(Key);
                                    int res_emg=maxEmgValue;
                                    if(emg<res_emg){
                                        hash.put(Key,res_emg);
                                        String[] str=Key.split("/");
                                        for(int k=0;k<str.length;k++){

                                        }
                                        for(int j=0;j<mSessionListResults.size();j++){

                                            try{
                                                Log.e("Helloonene",String.valueOf(str.length));
                                                if(str[0].equalsIgnoreCase(bodypart) && str[1].equalsIgnoreCase(exercise_name)
                                                        && str[2].equalsIgnoreCase(orientation) &&str[2].equalsIgnoreCase(bodyOrientation) && mSessionListResults.get(j).getBestexercise().equalsIgnoreCase("Yes")){
                                                    mSessionListResults.get(0).setBestexercise("No");
                                                }
                                            }catch (Exception exception){
                                                Log.e("Excepetion",String.valueOf(exception));
                                            }

                                        }

                                        first_exercise.setBestexercise("Yes");
                                    }
                                    else{
//                                            for(int j=0;j<mSessionListResults.size();j++){
//                                                Log.e("jnjcjdcfffycdtdc",String.valueOf());
//                                            }
                                        first_exercise.setBestexercise("No");
                                    }
                                }
                                mSessionListResults.add(first_exercise);
                            }
                            if(bodypart=="Hip"||bodypart=="Knee"||bodypart=="Ankle"){
                                first_exercise1.setBodypart(bodypart);
                                first_exercise1.setOrientation(bodyOrientation);
                                first_exercise1.setPosition(orientation);
                                first_exercise1.setExercise(exercise_name);
                                first_exercise1.setMuscle_name(muscle_name);
                                first_exercise1.setSession_time(sessiontime);
                                first_exercise1.setHeldon(dateString);
                                first_exercise1.setPatientemail(phizioemail);
                                first_exercise1.setPatientid(patientid);
                                first_exercise1.setPatientname(patientname);
                                if(!hash.containsKey(Key)){
                                    int emg=maxEmgValue;
                                    hash.put(Key,emg);
                                    first_exercise1.setBestexercise("Yes");

                                }
                                else{
                                    int emg=hash.get(Key);
                                    int res_emg=maxEmgValue;
                                    if(emg<res_emg){
                                        hash.put(Key,res_emg);
                                        String[] str=Key.split("/");
                                        for(int k=0;k<str.length;k++){

                                        }
                                        for(int j=0;j<mSessionListResults1.size();j++){

                                            try{
                                                Log.e("Helloonene",String.valueOf(str.length));
                                                if(str[0].equalsIgnoreCase(bodypart) && str[1].equalsIgnoreCase(exercise_name)
                                                        && str[2].equalsIgnoreCase(orientation) && mSessionListResults1.get(j).getBestexercise().equalsIgnoreCase("Yes")){
                                                    mSessionListResults1.get(0).setBestexercise("No");
                                                }
                                            }catch (Exception exception){
                                                Log.e("Excepetion",String.valueOf(exception));
                                            }

                                        }

                                        first_exercise1.setBestexercise("Yes");
                                    }
                                    else{
//                                            for(int j=0;j<mSessionListResults.size();j++){
//                                                Log.e("jnjcjdcfffycdtdc",String.valueOf());
//                                            }
                                        first_exercise1.setBestexercise("No");
                                    }
                                }

                                mSessionListResults1.add(first_exercise1);
                            }
                            else{
                                first_exercise2.setBodypart(bodypart);
                                first_exercise2.setOrientation(bodyOrientation);
                                first_exercise2.setPosition(orientation);
                                first_exercise2.setExercise(exercise_name);
                                first_exercise2.setMuscle_name(muscle_name);
                                first_exercise2.setSession_time(sessiontime);
                                first_exercise2.setHeldon(dateString);
                                first_exercise2.setPatientemail(phizioemail);
                                first_exercise2.setPatientid(patientid);
                                first_exercise2.setPatientname(patientname);
                                if(!hash.containsKey(Key)){
                                    int emg=maxEmgValue;
                                    hash.put(Key,emg);
                                    first_exercise2.setBestexercise("Yes");

                                }
                                else{
                                    int emg=hash.get(Key);
                                    int res_emg=maxEmgValue;
                                    if(emg<res_emg){
                                        hash.put(Key,res_emg);
                                        String[] str=Key.split("/");
                                        for(int k=0;k<str.length;k++){

                                        }
                                        for(int j=0;j<mSessionListResults2.size();j++){

                                            try{
                                                Log.e("Helloonene",String.valueOf(str.length));
                                                if(str[0].equalsIgnoreCase(bodypart) && str[1].equalsIgnoreCase(exercise_name)
                                                        && str[2].equalsIgnoreCase(orientation) && mSessionListResults2.get(j).getBestexercise().equalsIgnoreCase("Yes")){
                                                    mSessionListResults2.get(0).setBestexercise("No");
                                                }
                                            }catch (Exception exception){
                                                Log.e("Excepetion",String.valueOf(exception));
                                            }

                                        }

                                        first_exercise2.setBestexercise("Yes");
                                    }
                                    else{
//                                            for(int j=0;j<mSessionListResults.size();j++){
//                                                Log.e("jnjcjdcfffycdtdc",String.valueOf());
//                                            }
                                        first_exercise2.setBestexercise("No");
                                    }
                                }
                                mSessionListResults2.add(first_exercise2);
                            }


                        }
                        HashMap<String, Integer> hash=new HashMap<>();
                        for (int i = 0; i < response.body().size(); i++) {

                            SessionDetailsResult sesstionelement = response.body().get(i);
                            if(!dateString.equalsIgnoreCase(sesstionelement.getHeldon())) {
                                SessionListClass temp = new SessionListClass();
                                SessionListClass temp1 = new SessionListClass();
                                SessionListClass temp2 = new SessionListClass();
                                String Key=sesstionelement.getBodypart()+"/"+sesstionelement.getExercisename()+"/"+sesstionelement.getOrientation()+"/"+sesstionelement.getBodyorientation();
                                Log.e("111111111111111111111111111111111111111",Key);
                                if(sesstionelement.getBodypart().equalsIgnoreCase("Shoulder")||sesstionelement.getBodypart().equalsIgnoreCase("Elbow")||sesstionelement.getBodypart().equalsIgnoreCase("Forearm")||sesstionelement.getBodypart().equalsIgnoreCase("Wrist")){
                                    temp.setBodypart(sesstionelement.getBodypart());
                                    temp.setOrientation(sesstionelement.getBodyorientation());
                                    temp.setPosition(sesstionelement.getOrientation());
                                    temp.setExercise(sesstionelement.getExercisename());
                                    temp.setMuscle_name(sesstionelement.getMusclename());
                                    temp.setSession_time(sesstionelement.getSessiontime());
                                    temp.setHeldon(sesstionelement.getHeldon());
                                    temp.setPatientemail(phizioemail);
                                    temp.setPatientid(patientid);
                                    temp.setPatientname(patientname);
                                    if(!hash.containsKey(Key)){
                                        int emg=Integer.parseInt(sesstionelement.getMaxemg());
                                        hash.put(Key,emg);
                                        temp.setBestexercise("Yes");
                                    }
                                    else{
                                        int emg=hash.get(Key);
                                        int res_emg=Integer.parseInt(sesstionelement.getMaxemg());
                                        if(emg<res_emg){
                                            hash.put(Key,res_emg);
                                            String[] str=Key.split("/");
                                            for(int k=0;k<str.length;k++){

                                            }
                                            for(int j=0;j<mSessionListResults.size();j++){

                                                try{
                                                    Log.e("Helloonene",String.valueOf(str.length));
                                                    if(str[0].equalsIgnoreCase(sesstionelement.getBodypart()) && str[1].equalsIgnoreCase(sesstionelement.getExercisename())
                                                            && str[2].equalsIgnoreCase(sesstionelement.getOrientation()) &&str[2].equalsIgnoreCase(sesstionelement.getBodyorientation()) && mSessionListResults.get(j).getBestexercise().equalsIgnoreCase("Yes")){
                                                        mSessionListResults.get(0).setBestexercise("No");
                                                    }
                                                }catch (Exception exception){
                                                    Log.e("Excepetion",String.valueOf(exception));
                                                }

                                            }

                                            temp.setBestexercise("Yes");
                                        }
                                        else{
//                                            for(int j=0;j<mSessionListResults.size();j++){
//                                                Log.e("jnjcjdcfffycdtdc",String.valueOf());
//                                            }
                                            temp.setBestexercise("No");
                                        }
                                    }
                                    mSessionListResults.add(temp);
//                                    mSessionListResults2.add(temp);
                                }
                                if(sesstionelement.getBodypart().equalsIgnoreCase("Hip")||sesstionelement.getBodypart().equalsIgnoreCase("Knee")||sesstionelement.getBodypart().equalsIgnoreCase("Ankle")){
                                    temp2.setBodypart(sesstionelement.getBodypart());
                                    temp2.setOrientation(sesstionelement.getBodyorientation());
                                    temp2.setPosition(sesstionelement.getOrientation());
                                    temp2.setExercise(sesstionelement.getExercisename());
                                    temp2.setMuscle_name(sesstionelement.getMusclename());
                                    temp2.setSession_time(sesstionelement.getSessiontime());
                                    temp2.setHeldon(sesstionelement.getHeldon());
                                    temp2.setPatientemail(phizioemail);
                                    temp2.setPatientid(patientid);
                                    temp2.setPatientname(patientname);

                                    if(!hash.containsKey(Key)){
                                        int emg=Integer.parseInt(sesstionelement.getMaxemg());
                                        hash.put(Key,emg);
                                        temp2.setBestexercise("Yes");
                                    }
                                    else{
                                        int emg=hash.get(Key);
                                        int res_emg=Integer.parseInt(sesstionelement.getMaxemg());
                                        if(emg<res_emg){
                                            hash.put(Key,res_emg);
                                            String[] str=Key.split("/");
                                            for(int k=0;k<str.length;k++){

                                            }
                                            for(int j=0;j<mSessionListResults2.size();j++){

                                                try{
                                                    Log.e("Helloonene",String.valueOf(str.length));
                                                    if(str[0].equalsIgnoreCase(sesstionelement.getBodypart()) && str[1].equalsIgnoreCase(sesstionelement.getExercisename())
                                                            && str[2].equalsIgnoreCase(sesstionelement.getOrientation()) && mSessionListResults2.get(j).getBestexercise().equalsIgnoreCase("Yes")){
                                                        mSessionListResults2.get(0).setBestexercise("No");
                                                    }
                                                }catch (Exception exception){
                                                    Log.e("Excepetion",String.valueOf(exception));
                                                }

                                            }
                                            temp2.setBestexercise("Yes");
                                        }
                                        else{
                                            temp2.setBestexercise("No");
                                        }
                                    }
                                    mSessionListResults2.add(temp2);
                                }

                                if(sesstionelement.getBodypart().equalsIgnoreCase("Thoracic")|| sesstionelement.getBodypart().equalsIgnoreCase("Lumbar")||sesstionelement.getBodypart().equalsIgnoreCase("Abdomen")){
                                    temp1.setBodypart(sesstionelement.getBodypart());
                                    temp1.setOrientation(sesstionelement.getBodyorientation());
                                    temp1.setPosition(sesstionelement.getOrientation());
                                    temp1.setExercise(sesstionelement.getExercisename());
                                    temp1.setMuscle_name(sesstionelement.getMusclename());
                                    temp1.setSession_time(sesstionelement.getSessiontime());
                                    temp1.setHeldon(sesstionelement.getHeldon());
                                    temp1.setPatientemail(phizioemail);
                                    temp1.setPatientid(patientid);
                                    temp1.setPatientname(patientname);

                                    if(!hash.containsKey(Key)){
                                        int emg=Integer.parseInt(sesstionelement.getMaxemg());
                                        hash.put(Key,emg);
                                        temp1.setBestexercise("Yes");
                                    }
                                    else{
                                        int emg=hash.get(Key);
                                        int res_emg=Integer.parseInt(sesstionelement.getMaxemg());
                                        if(emg<res_emg){
                                            hash.put(Key,res_emg);
                                            String[] str=Key.split("/");
                                            for(int k=0;k<str.length;k++){

                                            }
                                            for(int j=0;j<mSessionListResults1.size();j++){

                                                try{
                                                    Log.e("Helloonene",String.valueOf(str.length));
                                                    if(str[0].equalsIgnoreCase(sesstionelement.getBodypart()) && str[1].equalsIgnoreCase(sesstionelement.getExercisename())
                                                            && str[2].equalsIgnoreCase(sesstionelement.getOrientation()) && mSessionListResults1.get(j).getBestexercise().equalsIgnoreCase("Yes")){
                                                        mSessionListResults1.get(0).setBestexercise("No");
                                                    }
                                                }catch (Exception exception){
                                                    Log.e("Excepetion",String.valueOf(exception));
                                                }

                                            }

                                            temp1.setBestexercise("Yes");
                                        }
                                        else{
//                                            for(int j=0;j<mSessionListResults.size();j++){
//                                                Log.e("jnjcjdcfffycdtdc",String.valueOf());
//                                            }
                                            temp1.setBestexercise("No");
                                        }
                                    }
                                    mSessionListResults1.add(temp1);
                                }

                            }

                        }
                        if(!(mSessionListResults.size() >0)){
                            Upper_Extremity.setVisibility(View.GONE);
                            Upper_Extremity_Layout.setVisibility(View.GONE);

                        }
                        if(!(mSessionListResults1.size() >0)){
                            Trunk.setVisibility(View.GONE);
                            Trunk_Layout.setVisibility(View.GONE);

                        }
                        if(!(mSessionListResults2.size() >0)){
                            Lower_Extremity.setVisibility(View.GONE);
                            Lower_Extremity_Layout.setVisibility(View.GONE);

                        }
//                        Log.e("111222333334444",String.valueOf(mSessionListResults2.size()>0));
                        if(mSessionListResults!=null){
                            sessionListArrayAdapter = new SessionListArrayAdapter(context, mSessionListResults);
                            lv_sessionlist.setAdapter(sessionListArrayAdapter);
//                        TextView Session_heading = layout.findViewById(R.id.Session_heading);
//                        Session_heading.setText("Session"+" "+sessionNo);
                        }
                        if(mSessionListResults1!=null){

                            sessionListArrayAdapter1 = new SessionListArrayAdapter(context, mSessionListResults1);
                            lv_sessionlist1.setAdapter(sessionListArrayAdapter1);
                        }
                        if(mSessionListResults2!=null){

                            sessionListArrayAdapter2 = new SessionListArrayAdapter(context, mSessionListResults2);
                            lv_sessionlist2.setAdapter(sessionListArrayAdapter2);
                        }
//                        TextView Session_heading = layout.findViewById(R.id.Session_heading);
//                        Session_heading.setText("Session"+" "+sessionNo);
                        setDynamicHeight(lv_sessionlist);
                        setDynamicHeight(lv_sessionlist1);
                        setDynamicHeight(lv_sessionlist2);


                        progress.dismiss();

                    }
                }
            }
            public void setDynamicHeight(ListView listView) {
                ListAdapter adapter = listView.getAdapter();
                //check adapter if null
                if (adapter == null) {
                    return;
                }
                int height = 0;
                int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
                for (int i = 0; i < adapter.getCount(); i++) {
                    View listItem = adapter.getView(i, null, listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    height += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
                layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
                listView.setLayoutParams(layoutParams);
                listView.requestLayout();
            }
            @Override
            public void onFailure(@NonNull Call<List<SessionDetailsResult>> call, @NonNull Throwable t) {
                try{
                    // Adding current exercise
                    SessionListClass first_exercise = new SessionListClass();
                    first_exercise.setBodypart(bodypart);
                    first_exercise.setOrientation(bodyOrientation);

                    first_exercise.setPosition(orientation);
                    first_exercise.setExercise(exercise_name);
                    first_exercise.setMuscle_name(muscle_name);
                    first_exercise.setSession_time(sessiontime);


                    first_exercise.setHeldon(dateString);

                    first_exercise.setPatientemail(phizioemail);
                    first_exercise.setPatientid(patientid);
                    first_exercise.setPatientname(patientname);
                    mSessionListResults.add(first_exercise);

                    sessionListArrayAdapter = new SessionListArrayAdapter(context, mSessionListResults);

                    lv_sessionlist.setAdapter(sessionListArrayAdapter);
                    TextView Session_heading = layout.findViewById(R.id.Session_heading);
                    Session_heading.setText("Session"+" "+sessionNo);

                    progress.dismiss();
                }
                catch (Exception e)
                {

                }

            }
        });

        lv_sessionlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SessionListClass temp = (SessionListClass) adapterView.getItemAtPosition(i);

            }
        });






        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        report.setWindowLayoutMode(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(true);
        report.showAtLocation(layout, Gravity.CENTER, 0, 0);

        if(NetworkOperations.isNetworkAvailable(context)){
        }
        else {
            progress.dismiss();
            networkError_popup(context,report);
        }


        TextView tv_delete_pateint_session = layout.findViewById(R.id.summary_tv_delete_session);
        TextView tv_back_session = layout.findViewById(R.id.tv_back_session);

//        final LinearLayout ll_click_to_view_report = layout.findViewById(R.id.ll_click_to_view_report);
        final LinearLayout ll_click_to_next = layout.findViewById(R.id.ll_click_to_next);


        //Share and cancel image view
        ImageView summary_go_back = layout.findViewById(R.id.summary_go_back);
        ImageView summary_share =  layout.findViewById(R.id.summary_share);
        ImageView body_potion = layout.findViewById(R.id.injured_side_image);
        SharedPreferences preferences_injured = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String patient_injured = preferences_injured.getString("patient injured", "");
        if(patient_injured.equalsIgnoreCase("Left")){
            body_potion.setImageResource(R.drawable.left_side_injured);
        }
        if(patient_injured.equalsIgnoreCase("Right")){
            body_potion.setImageResource(R.drawable.right_side_injured);
        }
//        if(patient_injured.equalsIgnoreCase("Left") && orientation.equalsIgnoreCase("Right")){
//            body_potion.setImageResource(R.drawable.ref_right_side_injured);
//        }
//        if(patient_injured.equalsIgnoreCase("Right") && orientation.equalsIgnoreCase("Left")){
//            body_potion.setImageResource(R.drawable.ref_right_side_injured);
//        }
        if(patient_injured.equalsIgnoreCase("Bi-Lateral")){
            body_potion.setImageResource(R.drawable.billateral_side_injured);
        }




//        if(IS_SCEDULED_SESSION && !IS_SCEDULED_SESSIONS_COMPLETED){
//            ll_click_to_view_report.setVisibility(View.GONE);
//        }else {
//            ll_click_to_view_report.setVisibility(View.VISIBLE);
//        }



        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }



        ll_click_to_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start
                ArrayList<SessionListClass> session_list = sessionListArrayAdapter.mSessionArrayList;

                //Check for the condition if anything is not checked
                boolean item_unchecked=false;
                for(int i=0;i<session_list.size();i++){
                    SessionListClass session_list_element_alert = session_list.get(i);
                    if(!session_list_element_alert.isSelected()){
                        item_unchecked=true;

                    }
                }

                if(item_unchecked){

                    //Alert Dialog
                    // Custom notification added by Haaris
                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.notification_dialog_box);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    TextView notification_title = dialog.findViewById(R.id.notification_box_title);
                    TextView notification_message = dialog.findViewById(R.id.notification_box_message);

                    Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                    Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                    Notification_Button_ok.setText("Confirm");
                    Notification_Button_cancel.setText("Cancel");

                    // Setting up the notification dialog
                    notification_title.setText("Deleting a session");
                    notification_message.setText("Are you sure you want to delete the \n session from the list. Please Confirm");

                    // On click on Cancel
                    Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    // On click Continue
                    Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            // Setting shared preference for deciding to download the report or not
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            editor = sharedPreferences.edit();

                            //Setting date in yyyy/mm/dd format
                            SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String sharedpref_date = date_formatter.format(new Date(tsLong));
                            editor.putBoolean(patientid+heldon, true);
                            editor.apply();
                            Log.d("sharedpreff",patientid+sharedpref_date);

                            for(int i=0;i<session_list.size();i++){
                                SessionListClass session_list_element = session_list.get(i);
                                if(!session_list_element.isSelected()){

                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("phizioemail", phizioemail);
                                        object.put("patientid", patientid);
                                        object.put("heldon", session_list_element.getHeldon());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    DeleteSessionData test = new DeleteSessionData(phizioemail,patientid,session_list_element.getHeldon(),String.valueOf(i));
                                    Call<ResponseData> dataCall = getDataService.deletePatientSession(test);
                                    dataCall.enqueue(new Callback<ResponseData>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseData> call, @NonNull Response<ResponseData> response) {
                                            if (response.code() == 200) {
                                                ResponseData res = response.body();
                                                if (res != null) {
                                                    if (res.getResponse().equalsIgnoreCase("deleted")) {
//                                            deleteParticular(res.getId());Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                                                        if (onSessionDataResponse != null)
                                                            onSessionDataResponse.onSessionDeleted(true, "Exercise removed");
                                                    } else {
                                                        if (onSessionDataResponse != null)
                                                            onSessionDataResponse.onSessionDeleted(false, "");
                                                    }
                                                } else {
                                                    if (onSessionDataResponse != null)
                                                        onSessionDataResponse.onSessionDeleted(false, "");
                                                }
                                            } else {
                                                if (onSessionDataResponse != null)
                                                    onSessionDataResponse.onSessionDeleted(false, "");
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseData> call, @NonNull Throwable t) {
                                            if (onSessionDataResponse != null)
                                                onSessionDataResponse.onSessionDeleted(false, "");
                                        }
                                    });

                                }
                                else{

                                }
                            }


                            // End



                            Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                            ll_click_to_next.setAnimation(aniFade);
                            if(NetworkOperations.isNetworkAvailable(context)){
                                Intent mmt_intent = new Intent(context, SessionReportActivity.class);
                                mmt_intent.putExtra("patientid", patientid);
                                mmt_intent.putExtra("patientname", patientname);
                                mmt_intent.putExtra("phizioemail", phizioemail);
                                mmt_intent.putExtra("dateofjoin",dateofjoin);
                                ((Activity)context).startActivity(mmt_intent);
//                                report.dismiss();
                            }
                            else {
                                networkError_popup(context,report);
                            }
                            dialog.dismiss();

                        }
                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);

                    // End
                }else{

                    Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                    ll_click_to_next.setAnimation(aniFade);
                    if(NetworkOperations.isNetworkAvailable(context)){
                        Intent mmt_intent = new Intent(context, SessionReportActivity.class);
                        mmt_intent.putExtra("patientid", patientid);
                        mmt_intent.putExtra("patientname", patientname);
                        mmt_intent.putExtra("phizioemail", phizioemail);
                        mmt_intent.putExtra("dateofjoin",dateofjoin);
                        ((Activity)context).startActivity(mmt_intent);
//                        report.dismiss();
                    }
                    else {
                        networkError_popup(context,report);
                    }

                }

            }
        });

        tv_delete_pateint_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                report.dismiss();
                if(IS_SCEDULED_SESSION){
                    if(IS_SCEDULED_SESSIONS_COMPLETED){
                        Intent i = new Intent(context, PatientsView.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(i);
                    }
                }
                ((Activity)context).finish();



            }


        });

        tv_back_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
            }
        });




        summary_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
            }
        });


        //for held on date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
        dateString = formatter.format(new Date(tsLong));
        String dateString_date = formatter_date.format(new Date(tsLong));



        report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(IS_SCEDULED_SESSIONS_COMPLETED) {
                    if(context!=null)
                        ((MonitorActivity) context).sceduledSessionsHasBeenCompletedDialog();
                }
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
                    object.put("commentsession",comment_session);
                    object.put("symptoms","");
                    object.put("activetime",actiontime);
                    object.put("orientation", orientation);
                    object.put("mmtgrade",mmt_selected);
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

    public static void networkError_popup(Context context, PopupWindow report){

        // Custom notification added by Haaris
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog_box_single_button);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(lp);

        TextView notification_title = dialog.findViewById(R.id.notification_box_title);
        TextView notification_message = dialog.findViewById(R.id.notification_box_message);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);

        Notification_Button_ok.setText("OK");

        // Setting up the notification dialog
        notification_title.setText("Network Error");
        notification_message.setText("Please connect to internet to view the exercise list");


        // On click on Continue
        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
                ((Activity)context).finish();
                dialog.dismiss();



            }
        });

        dialog.show();

        // End
    }


    public void setOnSessionDataResponse(MqttSyncRepository.OnSessionDataResponse response){
        this.response_data = response;
    }
}


