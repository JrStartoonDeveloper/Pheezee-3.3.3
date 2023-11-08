package com.start.apps.pheezee.popup;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.start.apps.pheezee.activities.PatientsView.REQ_CAMERA;
import static com.start.apps.pheezee.activities.PatientsView.REQ_GALLERY;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.start.apps.pheezee.activities.GetPatinetData;
import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.activities.PremiumSubscriptionDataRec;
import com.start.apps.pheezee.activities.Record_affected_GetData;
import com.start.apps.pheezee.activities.SessionDisableDataRec;
import com.start.apps.pheezee.adapters.Affectedside_adapter;
//import com.start.apps.pheezee.adapters.RecommendationAdapter;
import com.start.apps.pheezee.pojos.PatientDetailsData;
//import com.start.apps.pheezee.pojos.ViewHealthySideData;
import com.start.apps.pheezee.pojos.PremiumSubscriptionUserData;
import com.start.apps.pheezee.pojos.ReportCountData;
import com.start.apps.pheezee.pojos.SessionDisabledata;
import com.start.apps.pheezee.pojos.ViewRecommandations;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.room.Entity.PhizioPatients;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class ViewPopUpWindow {
    Context context;
    String[] Data1={};
    String[] Data2={};
    String[] Data3={};
    String[] Data4 ={};
    String[] Data5 ={};
    String[] Data6 ={};
    String[] Data7={};
    String[] Data8={};
    String[] Data9={};
    String[] Data10 ={};
    String[] Data11 ={};
    String[] Data12 ={};
    static ArrayList<String> arrayList;
    static PhizioPatients patient;
    EditPopUpWindow.onClickListner listner;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Bitmap profile;
    String json_phizioemail, view_data,view_data_last,view_data_report, view_data_goal , gender, age;

    AlertDialog.Builder builder = null;
    boolean use_new_photo=false;

    boolean gallery_selected=false;
    boolean camera_selected=false;
    PopupWindow popup;


    GetDataService getDataService;

    String exercise,musclename, heldon;
    String HelExercise,HelMusclename,HelHeldon,bodypart;
    String pt_age ;
    HashMap<String,String> map=new HashMap<String,String>();
    HashMap<String,String> map1=new HashMap<String,String>();

    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };

    public ViewPopUpWindow(final Activity context, PhizioPatients patient, String json_phizioemail){
        Log.e("patient", String.valueOf(patient));
        this.context = context;
        this.patient = patient;
        this.json_phizioemail = json_phizioemail;
    }



    public ViewPopUpWindow(Context context,String json_phizioemail, Bitmap photo){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        this.profile = photo;
        use_new_photo = true;
    }

    public ViewPopUpWindow(Context applicationContext, String patients, String json_phizioemails) {
        Log.e("patient", String.valueOf(patients));
        this.context = applicationContext;
        this.patient = patient;
        this.json_phizioemail = json_phizioemails;
    }





    public void openViewPopUpWindow(){
        PopupWindow pw;
        final String[] case_description = {""};
        final String[] case_00 ={""};


        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        Log.e("abhbyscybdy",String.valueOf(config.smallestScreenWidthDp));
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.view_pop_2, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.view_pop, null);
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

        final TextView tv_patientId = layout.findViewById(R.id.tv_patient_id);
        final TextView tv_create_account = layout.findViewById(R.id.tv_create_account);
        final TextView patientName = layout.findViewById(R.id.patientName);
        final TextView tv_Medicalhistory_det = layout.findViewById(R.id.tv_Medicalhistory_det);
        final TextView tv_affected_side = layout.findViewById(R.id.tv_affected_side);
        final TextView tv_condition_det = layout.findViewById(R.id.tv_condition_det);
        final TextView tv_Speciality_det = layout.findViewById(R.id.tv_Speciality_det);
        final TextView patientPhone = layout.findViewById(R.id.patientphone);
        ImageView patient_profilepic = layout.findViewById(R.id.imageView4);
        ImageView patient_profilepic_image = layout.findViewById(R.id.profile_picture);
        final TextView patientEmail = layout.findViewById(R.id.patientemail);
        final ImageView backbutton = layout.findViewById(R.id.iv_back_app_info);
        final TextView text_data = layout.findViewById(R.id.text_data);
        final TextView text_data_01 = layout.findViewById(R.id.text_data_01);
        final TextView text_data_02 = layout.findViewById(R.id.text_data_02);
        final TextView text_data_03 = layout.findViewById(R.id.text_data_03);
        final ImageView edit_button_action = layout.findViewById(R.id.edit_button);
        final LinearLayout Start_Session_Outer_View = layout.findViewById(R.id.start_session_outer_view);
        final  TextView join_date = layout.findViewById(R.id.join);
        final  TextView session_number = layout.findViewById(R.id.session_count_number);
        final  TextView last_session_number = layout.findViewById(R.id.last_session);
        final  TextView start_doing_session_text = layout.findViewById(R.id.start_doing_session_text);
        final  TextView HealthSideTextOnly = layout.findViewById(R.id.HealthySide_Text_Only);
        final  TextView AffectedSideTextOnly = layout.findViewById(R.id.AffectedSide_Text_Only);
        final  TextView report_number = layout.findViewById(R.id.report_count);

        final ImageView injured_side = layout.findViewById(R.id.effected_side);
        final SemiCircleArcProgressBar semi_prog_bar = layout.findViewById(R.id.semi_prog);
        final SemiCircleArcProgressBar semi_prog_bar_phone = layout.findViewById(R.id.semi_prog_phone);
        final RelativeLayout SemiProgress_Phone_layout=layout.findViewById(R.id.semi_prog_phone_layout);
        final RelativeLayout SemiProgress_layout=layout.findViewById(R.id.semi_prog_layout);
        final TextView tv_goal_num_value = layout.findViewById(R.id.tv_goal_num);
        final TextView tv_goal_num_value_phone = layout.findViewById(R.id.tv_goal_num_phone);
        final CardView alertlayout=layout.findViewById(R.id.affected_alert);
        final Button StartSession =layout.findViewById(R.id.start_exercise);
        final Button start_session_btr = layout.findViewById(R.id.start_session);
        final Button start_session_Disable_btr = layout.findViewById(R.id.start_session_disable);
        ScrollView Page_ScrollView=layout.findViewById(R.id.scrollView);
        Toolbar Page_ToolBar=layout.findViewById(R.id.my_toolbar_bodypart);
        final TextView report_limit_kt = layout.findViewById(R.id.report_limit);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final  TextView alertText1 = layout.findViewById(R.id.alert_text1);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final  TextView alertText2 = layout.findViewById(R.id.alert_text2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout alertlayoutouter=layout.findViewById(R.id.alert_outer_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout alertsetvisible=layout.findViewById(R.id.alert_setvisible_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView=layout.findViewById(R.id.recc_recycle_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView Record_Recycle=layout.findViewById(R.id.record_affected_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView Record_Healthy_Recycle=layout.findViewById(R.id.record_Healthy_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout Recommendation_view=layout.findViewById(R.id.recommendation_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout AssessmentDoneView=layout.findViewById(R.id.exer_completed_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout AffectedSideView=layout.findViewById(R.id.AffectedSide_View);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout Start_Session=layout.findViewById(R.id.start_session_View);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final LinearLayout Record_View=layout.findViewById(R.id.record_view);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final TextView HealthySide_Text = layout.findViewById(R.id.HealthySide_Text);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final TextView AffectedSide_Text = layout.findViewById(R.id.AffectedSide_Text);
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final CardView recc_card=layout.findViewById(R.id.recc_card);
//        final RecyclerView recyclerView=layout.findViewById(R.id.view_recomm);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
                SharedPreferences view_data_s = PreferenceManager.getDefaultSharedPreferences(context);
                view_data = view_data_s.getString("view_data", "");
                SharedPreferences view_data_l = PreferenceManager.getDefaultSharedPreferences(context);
                view_data_last = view_data_l.getString("view_data_last", "");
                SharedPreferences view_data_r = PreferenceManager.getDefaultSharedPreferences(context);
                view_data_report = view_data_r.getString("view_data_report", "");
                SharedPreferences view_data_g = PreferenceManager.getDefaultSharedPreferences(context);
                view_data_goal = view_data_g.getString("view_data_goal", "");
                Log.e("view_data_goal",view_data_goal);
            }






            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onFinish() {
                try{
                    if(patient.getPatientinjured()!= null){
                        if(patient.getPatientinjured().equalsIgnoreCase("Left")){
                            injured_side.setImageResource(R.drawable.left_side_injured);
                            AffectedSide_Text.setText("("+patient.getPatientinjured()+" "+"Side"+")");
                            HealthySide_Text.setText("("+"Right Side"+")");

                        }else if (patient.getPatientinjured().equalsIgnoreCase("Right")){
                            injured_side.setImageResource(R.drawable.right_side_injured);
                            AffectedSide_Text.setText("("+patient.getPatientinjured()+" "+"Side"+")");
                            HealthySide_Text.setText("("+" Left Side"+")");


                        }else if(patient.getPatientinjured().equalsIgnoreCase("Bi-Lateral")){
                            injured_side.setImageResource(R.drawable.billateral_side_injured);

                        }
                    }
                    if(patient.getPatientinjured()== null){
                        injured_side.setVisibility(View.GONE);
                        alertsetvisible.setVisibility(View.VISIBLE);
                    }
                }catch (NumberFormatException e){
                }
                String start_date = patient.getDateofjoin();
                start_date = start_date.replace("/","-");
                String[] date_split = start_date.split("-");
                start_date = date_split[0]+"-"+date_split[1]+"-"+ date_split[2];
                String last_date = view_data_last;
                if(last_date != "-" ) {
                    last_date = last_date.replace("-", "-");
                    String[] date_split2 = last_date.split("-");
                    if (date_split2.length != 0) {
                        last_date = date_split2[2] + "-" + date_split2[1] + "-" + date_split2[0];
                    } else {
                        last_date = "-";
                    }
                }else{
                    last_date = "-";
                }

                join_date.setText(start_date);
                session_number.setText(view_data);
                last_session_number.setText(last_date);
                report_number.setText(view_data_report);
                gender = patient.getPatientgender();
                if(gender.equalsIgnoreCase("Male")){
                    gender = "M";
                }else if(gender.equalsIgnoreCase("Female")){
                    gender = "F";
                }
                age = patient.getPatientage();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    if(age.length() >= 3) {
                        date = formatter.parse(age);
                        Instant instant = date.toInstant();
                        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
                        LocalDate givenDate = zone.toLocalDate();
                        //Calculating the difference between given date to current date.
                        Period period = Period.between(givenDate, LocalDate.now());

                        pt_age = String.valueOf(period.getYears());
                    }else {
                        pt_age = age;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                //Converting obtained Date object to LocalDate object



                patientName.setText(patient.getPatientname()+","+" "+gender+"/"+pt_age);
                tv_patientId.setText("Patient ID: "+patient.getPatientid());
                patientPhone.setText(patient.getPatientphone());
                patientEmail.setText(patient.getPatientemail());
                tv_affected_side.setText(patient.getPatientinjured());
                /**
                 *
                 */

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("patient injured", patient.getPatientinjured());
                editor.apply();
                tv_condition_det.setText(patient.getPatientcasedes());
                tv_Speciality_det.setText(patient.getPatientcondition());
                tv_Medicalhistory_det.setText(patient.getPatienthistory());
                if(config.smallestScreenWidthDp<400){
                    SemiProgress_Phone_layout.setVisibility(View.VISIBLE);
                    SemiProgress_layout.setVisibility(View.GONE);
                    semi_prog_bar_phone.setVisibility(View.VISIBLE);
                    semi_prog_bar.setVisibility(View.GONE);
                }
                else{
                    SemiProgress_layout.setVisibility(View.VISIBLE);
                    SemiProgress_Phone_layout.setVisibility(View.GONE);
                    semi_prog_bar_phone.setVisibility(View.GONE);
                    semi_prog_bar.setVisibility(View.VISIBLE);
                }
                if(view_data_goal.equalsIgnoreCase("-")){
                    semi_prog_bar.setPercent(0);
                    semi_prog_bar_phone.setPercent(0);
                    start_doing_session_text.setVisibility(View.VISIBLE);
                    tv_goal_num_value.setText("0%");
                    tv_goal_num_value_phone.setText("0%");
                }else {
                    semi_prog_bar_phone.setPercent(Integer.parseInt(view_data_goal));
                    semi_prog_bar.setPercent(Integer.parseInt(view_data_goal));
                    tv_goal_num_value.setText(view_data_goal.concat("%"));
                    tv_goal_num_value_phone.setText(view_data_goal.concat("%"));
                }

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // Month value is 0-based, so adding 1
                int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                // Create a string representation of the date
                String currentDate = year + "-" + month + "-" + day1;
                ReportCountData reportdata = new ReportCountData(json_phizioemail, patient.getPatientid(),currentDate);
                Call<ReportCountDataRec> reportCountDataRecCall = getDataService.Report_count_Data(reportdata);
                reportCountDataRecCall.enqueue(new Callback<ReportCountDataRec>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(Call<ReportCountDataRec> call, Response<ReportCountDataRec> response) {
                        if(response.code()==200){
                            ReportCountDataRec res=response.body();

                            String report_limit_value = res.getSessionDetails();
                            int value=Integer.valueOf(report_limit_value);
                            if(value>=20){
                                report_limit_kt.setText(report_limit_value);
                                report_limit_kt.setTextColor(Color.parseColor("#CC2016"));
                            }
                            else{
                                report_limit_kt.setText(report_limit_value);
                                report_limit_kt.setTextColor(Color.parseColor("#002647"));}

                        }
                    }

                    @Override
                    public void onFailure(Call<ReportCountDataRec> call, Throwable t) {

                    }
                });




                try{
                    if(patient.getPatientinjured().equalsIgnoreCase("Bi-Lateral")){
                        HealthySide_Text.setVisibility(View.GONE);
                        AffectedSide_Text.setVisibility(View.GONE);
                        HealthSideTextOnly.setVisibility(View.GONE);
                        AffectedSideTextOnly.setVisibility(View.GONE);
                        ViewRecommandations data = new ViewRecommandations(json_phizioemail, patient.getPatientid(),patient.getPatientinjured());
                        Call<viewRecommandDataRec> view_recommandation_eft = getDataService.view_bilateral_Side(data);
                        view_recommandation_eft.enqueue(new Callback<viewRecommandDataRec>() {
                            @Override
                            public void onResponse(Call<viewRecommandDataRec> call, Response<viewRecommandDataRec> response) {
                                if (response.code() == 200) {
                                    viewRecommandDataRec res = response.body();
                                    exercise = res.getExercise();
                                    musclename = res.getMusclename();
                                    heldon = res.getHeldon();
                                    bodypart=res.getBodyPart();
                                    Data1=exercise.split(",");
                                    Data2=musclename.split(",");
                                    Data3=heldon.split(",");
                                    Data7=bodypart.split(",");
                                    ArrayList<GetPatinetData> RecordData=new ArrayList<>();
                                    ArrayList<Record_affected_GetData> AffectedSideData2=new ArrayList<>();

                                    for(int i=0;i< Data1.length;i++){
                                        AffectedSideData2.add(new Record_affected_GetData(Data1[i],Data2[i],Data3[i],Data7[i]));
                                    }
                                    Affectedside_adapter affectedside_adapter2=new Affectedside_adapter(context,AffectedSideData2);
                                    Record_Recycle.setAdapter(affectedside_adapter2);
                                    Record_Recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                                }
                            }
                            @Override
                            public void onFailure(Call<viewRecommandDataRec> call, Throwable t) {
                                Log.e("5555555555555","Fail");
                            }
                        });
                    }
                    else{
                        arrayList=new ArrayList<>();
                        ViewRecommandations data = new ViewRecommandations(json_phizioemail, patient.getPatientid(),patient.getPatientinjured());
                        Call<viewRecommandDataRec> view_recommandation_eft = getDataService.view_recommandation_effected(data);
                        view_recommandation_eft.enqueue(new Callback<viewRecommandDataRec>() {
                            @Override
                            public void onResponse(Call<viewRecommandDataRec> call, Response<viewRecommandDataRec> response) {
                                if (response.code() == 200) {

                                    viewRecommandDataRec res = response.body();
                                    exercise = res.getExercise();
                                    musclename = res.getMusclename();
                                    heldon = res.getHeldon();
                                    bodypart=res.getBodyPart();
                                    Data1=exercise.split(",");
                                    Data7= bodypart.split(",");
                                    Data2=musclename.split(",");
                                    Data3=heldon.split(",");

                                    ArrayList<GetPatinetData> RecordData=new ArrayList<>();
                                    ArrayList<Record_affected_GetData> AffectedSideData=new ArrayList<>();
                                    for(int i=0;i< Data1.length;i++){
                                        AffectedSideData.add(new Record_affected_GetData(Data1[i],Data2[i],Data3[i],Data7[i]));
                                    }
                                    Affectedside_adapter affectedside_adapter=new Affectedside_adapter(context,AffectedSideData);
                                    Record_Recycle.setAdapter(affectedside_adapter);
                                    Record_Recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    if(Data1.length!=0){

                                        for(int i=0;i<Data1.length;i++){
                                            String val=Data1[i]+Data2[i];
                                            if(map.containsKey(val)){
                                                break;
                                            }
                                            else{
                                                map.put(val,val);
                                            }
                                        }
                                    }
                                }
                            }


                            @Override
                            public void onFailure(Call<viewRecommandDataRec> call, Throwable t) {
                                Log.e("5555555555555","Fail");
                            }
                        });
                        ViewRecommandations data1 = new ViewRecommandations(json_phizioemail, patient.getPatientid(),patient.getPatientinjured());
                        Call<viewRecommandDataRec> view_recommandation_eft1 = getDataService.view_Healthy_side(data1);
                        view_recommandation_eft1.enqueue(new Callback<viewRecommandDataRec>() {
                            @Override
                            public void onResponse(Call<viewRecommandDataRec> call, Response<viewRecommandDataRec> response) {
                                if (response.code() == 200) {
                                    viewRecommandDataRec res = response.body();
                                    HelExercise = res.getExercise();
                                    HelMusclename = res.getMusclename();
                                    HelHeldon = res.getHeldon();
                                    bodypart=res.getBodyPart();
                                    Data4=HelExercise.split(",");
                                    Data5=HelMusclename.split(",");
                                    Data6=HelHeldon.split(",");
                                    Data8=bodypart.split(",");

                                    ArrayList<Record_affected_GetData> AffectedSideData1=new ArrayList<>();
                                    for(int i=0;i< Data4.length;i++){
                                        AffectedSideData1.add(new Record_affected_GetData(Data4[i],Data5[i],Data6[i],Data8[i]));
                                    }
                                    Affectedside_adapter affectedside_adapter1=new Affectedside_adapter(context,AffectedSideData1);
                                    Record_Healthy_Recycle.setAdapter(affectedside_adapter1);
                                    Record_Healthy_Recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                }
                            }
                            @Override
                            public void onFailure(Call<viewRecommandDataRec> call, Throwable t) {
                                Log.e("5555555555555","Fail");
                            }
                        });
                    }
                }catch (Exception e){
                    Log.e("abchedhdbd",String.valueOf(e));
                }

                ArrayList<GetPatinetData> RecordData=new ArrayList<>();
//                String[] arr={"Elbow Extension | Biceps","Elbow Extension | Pronator Quadratus","Elbow Extension | Biceps"};
//                String[] arr1={"Affected (Right) side","Affected (Right) side","Affected (Right) side"};
//                String[] arr2={"Done","Done","Done"};
//                for(int i=0;i< arr.length;i++){
//                    RecordData.add(new GetPatinetData(arr[i],arr1[i],arr2[i]));
//                }


                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        if(Data4.length==0){
                            HealthySide_Text.setVisibility(View.GONE);
                            HealthSideTextOnly.setVisibility(View.GONE);
                        } else if (Data1.length==0) {
                            AffectedSide_Text.setVisibility(View.GONE);
                            AffectedSideTextOnly.setVisibility(View.GONE);
                        }
//
                        if(Data1.length==0 && Data4.length==0){
                            Start_Session.setVisibility(View.VISIBLE);
//                            Recommendation_view.setVisibility(View.GONE);
//                            AssessmentDoneView.setVisibility(View.GONE);
                            Record_View.setVisibility(View.GONE);

                        }
                        else {
                            Start_Session.setVisibility(View.GONE);
                            Start_Session_Outer_View.setVisibility(View.GONE);
//                            Recommendation_view.setVisibility(View.VISIBLE);
//                            AssessmentDoneView.setVisibility(View.GONE);
                            Record_View.setVisibility(View.VISIBLE);
                        }
                        int startWidth = alertlayout.getWidth();
                        int outerView=alertlayoutouter.getWidth();
                        int startMargin = ((ViewGroup.MarginLayoutParams)alertlayout.getLayoutParams()).leftMargin;
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        int targetWidth = displayMetrics.widthPixels;
                        ValueAnimator animator = ValueAnimator.ofInt(startWidth, startWidth + (outerView-startWidth)); // increase width by 200px
                        animator.setDuration(1000);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = alertlayout.getLayoutParams();
                                layoutParams.width = value;
                                alertlayout.setLayoutParams(layoutParams);
                            }
                        });
                        animator.start();
                        new CountDownTimer(1000,1000){

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                alertText1.setVisibility(View.VISIBLE);
                                alertText2.setVisibility(View.VISIBLE);
                            }
                        }.start();
                    }
                }.start();



            }

        }.start();


        PremiumSubscriptionUserData premiumSubscriptionUserData=new PremiumSubscriptionUserData(json_phizioemail);
        Call<PremiumSubscriptionDataRec> premiumSubscriptionDataRecCall = getDataService.premium_subscription_user_data(premiumSubscriptionUserData);
        premiumSubscriptionDataRecCall.enqueue(new Callback<PremiumSubscriptionDataRec>() {
            @Override
            public void onResponse(Call<PremiumSubscriptionDataRec> call, Response<PremiumSubscriptionDataRec> response) {
                if(response.code()==200){
                    PremiumSubscriptionDataRec res = response.body();
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("CustomerType", res.getCustomerType());
                    editor.putString("StartDate", res.getStartDate());
                    editor.putString("EndDate", res.getEndDate());
                    editor.putString("ReportGenerated", res.getReportGenerated());
                    editor.putString("ReportAccessible", res.getReportAccessible());
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<PremiumSubscriptionDataRec> call, Throwable t) {

            }
        });

        edit_button_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupWindow(context);
                if(popup.isShowing()){
                    popup.dismiss();
                }
                View customView = LayoutInflater.from(context).inflate(R.layout.view_patinet_three_dots, null);
                popup.setContentView(customView);
                popup.setWidth(480);
                int location[] = new int[2];
                int x = location[0] + customView.getWidth();
                int y = location[1] + 50; // add 50 pixels from the top
                popup.showAtLocation(customView, Gravity.TOP | Gravity.RIGHT, 100, 250);
                LinearLayout delete_patient_btn = customView.findViewById(R.id.delete_patient);
                LinearLayout edit_patient_btn = customView.findViewById(R.id.edit_patient);
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);
                Page_ScrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            if (popup.isShowing()) {
                                popup.dismiss(); // Close the popup when clicking outside
                            }
                        }
                        return false;
                    }
                });
                Page_ToolBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            if (popup.isShowing()) {
                                popup.dismiss(); // Close the popup when clicking outside
                            }
                        }
                        return false;
                    }
                });


                delete_patient_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((PatientsView)context).deletePatient(patient);
                        popup.dismiss();
                    }
                });

                edit_patient_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((PatientsView)context).editThePatientDetails(patient);
                        popup.dismiss();
                    }
                });



//                ((PatientsView)context).editThePatientDetails(patient);
            }
        });

        StartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PatientsView)context).startSession(patient.getPatientid(),patient.getPatientname(), patient.getDateofjoin(),patient.getPatientinjured());
            }
        });
        start_session_btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PatientsView)context).startSession(patient.getPatientid(),patient.getPatientname(), patient.getDateofjoin(),patient.getPatientinjured());
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
                if ( popup !=null ) {
                    popup.dismiss(); // Close the popup when clicking outside
                }
            }
        });


        Calendar c=Calendar.getInstance();
        Integer month=c.get(Calendar.MONTH);
        Integer day=c.get(Calendar.DAY_OF_MONTH);
        Integer year=c.get(Calendar.YEAR);



        if(use_new_photo==false)
        {
            Glide.with(context)
                    .load("https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/" + json_phizioemail.replaceFirst("@", "%40") + "/patients/" + patient.getPatientid() + "/images/profilepic.png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.test_patient_add_1))
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


        tv_patientId.setVisibility(View.VISIBLE);
        tv_create_account.setText("Patient Profile");


        //Adapter for spinner
        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_description));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        String[] cases_list = context.getResources().getStringArray(R.array.case_description);
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList,cases_list);
        if(arrayList.contains(patient.getPatientcasedes())) {
        }else{

        }

        try{
            if(!patient.getPatientcondition().equals("")){
            }
        } catch (Exception e) {
            Log.e("your app", e.toString());
        }



















        Button addBtn = layout.findViewById(R.id.addBtn);

        addBtn.setText("Update");
        final Button cancelBtn = layout.findViewById(R.id.cancelBtn);









        if(patient.getPatientgender().equalsIgnoreCase("Male")) {
        }
        else{
        }






        case_description[0] = patient.getPatientcasedes();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(case_description[0].equalsIgnoreCase("Other")){

                }else {
                    try {

                    } catch (Exception e) {
                        Log.e("your app", e.toString());
                    }
                }



                String patientname = patientName.getText().toString();

                String patientphone = patientPhone.getText().toString();
                String patientemail = patientEmail.getText().toString();
                String patientcondition = text_data_02.getText().toString();







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

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientsView)context).openReportActivity(patient.getPatientid(),patient.getPatientname(), patient.getDateofjoin());
            }
        });
    }

    public void onBackPressed() {

        if(popup.isShowing()){
            popup.dismiss();
        }
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

    public void setOnClickListner(EditPopUpWindow.onClickListner listner){
        this.listner = listner;
    }

    public interface PopupDismissListener {
        void onPopupDismiss();
    }

}