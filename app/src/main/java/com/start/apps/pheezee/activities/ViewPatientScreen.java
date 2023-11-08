package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.RoundedCorner;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.start.apps.pheezee.pojos.ViewSessionReportDataSort;
import com.start.apps.pheezee.pojos.ViewSummaryDetails;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.utils.NetworkOperations;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class ViewPatientScreen extends AppCompatActivity {
    Button cancelBtn, deleteBtn;
    GetDataService getDataService;

    TextView patientName, tv_patient_id, tv_goal_num, patient_phone, patient_email, tv_affected_side, tv_condition_det, tv_Speciality_det, tv_Medicalhistory_det,join, last_session, session_count_number;

    SemiCircleArcProgressBar semi_prog;
    String patientid, patientname, dateofjoin, patientage, patientcasedes, patientcondition, patientgender, patientphone, patientemail, patientinjured, patienthistory, patientprofilepicurl, heldon, Goal_reached, age, pt_age,patient_id, email, session_count;

    URL imgurl;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        setContentView(R.layout.activity_view_patient_screen);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.delete_botton);
        patientName = findViewById(R.id.patientName);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        tv_goal_num = findViewById(R.id.tv_goal_num);
        patient_phone = findViewById(R.id.patientphone);
        patient_email = findViewById(R.id.patientemail);
        tv_affected_side = findViewById(R.id.tv_affected_side);
        tv_condition_det = findViewById(R.id.tv_condition_det);
        tv_Speciality_det = findViewById(R.id.tv_Speciality_det);
        tv_Medicalhistory_det = findViewById(R.id.tv_Medicalhistory_det);
        join = findViewById(R.id.join);
        last_session = findViewById(R.id.last_session);
        session_count_number = findViewById(R.id.session_count_number);
        semi_prog = findViewById(R.id.semi_prog);
        ImageView injured_side = findViewById(R.id.effected_side);
        ImageView imageView4 = findViewById(R.id.imageView4);
        ImageView iv_back_app_info = findViewById(R.id.iv_back_app_info);

        iv_back_app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Intent intent = new Intent();
        intent.putExtra("et_phizio_email", json_phizioemail);
        email = intent.getStringExtra("et_phizio_email");
        Intent intent1 = getIntent();
        patient_id = intent1.getStringExtra("patient_id");

        ViewSessionReportDataSort data = new ViewSessionReportDataSort(email, patient_id, "Year");
        Call<ViewSummaryDetails> view_report_data_history_data = getDataService.view_summary_report(data);


        view_report_data_history_data.enqueue(new Callback<ViewSummaryDetails>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<ViewSummaryDetails> call, @NonNull Response<ViewSummaryDetails> response) {
                if (response.code() == 200) {
                    ViewSummaryDetails res = response.body();
                    patientid = res.getpatientid();
                    patientname = res.getpatientname();
                    dateofjoin = res.getdateofjoin();
                    patientage = res.getpatientage();
                    patientcasedes = res.getpatientcasedes();
                    patientcondition = res.getpatientcondition();
                    patientgender = res.getpatientgender();
                    patientphone = res.getpatientphone();
                    patientemail = res.getpatientemail();
                    patientinjured = res.getpatientinjured();
                    heldon = res.getheldon();
                    patienthistory = res.getpatienthistory();
                    Goal_reached = res.getGoal_reached();
                    patientprofilepicurl = res.getpatientprofilepicurl();
                    session_count = res.getsession_count();
                    String temp = "https://s3.ap-south-1.amazonaws.com/pheezee/" + patientprofilepicurl;
                    if(patientprofilepicurl != "empty"){
                        Glide.with(getApplicationContext())
                                .load(temp)
                                .error(R.drawable.view_profi_icon)
                                .centerCrop()
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(150)))
                                .placeholder(R.drawable.view_profi_icon)
                                .into(imageView4);

                    }else{
                        imageView4.setImageResource(R.drawable.view_profi_icon);
                    }






                    String start_date = dateofjoin;
                    start_date = start_date.replace("/", "-");
                    String[] date_split = start_date.split("-");
                    start_date = date_split[0] + "-" + date_split[1] + "-" + date_split[2];

                    String last_date = heldon;
                    last_date = last_date.replace("-", "-");
                    String[] date_split2 = last_date.split("-");
                    if (date_split2.length != 0) {
                        last_date = date_split2[2] + "-" + date_split2[1] + "-" + date_split2[0];
                    } else {
                        last_date = "-";
                    }

                    try {
                        if (patientinjured != null) {
                            if (patientinjured.equalsIgnoreCase("Left")) {
                                injured_side.setImageResource(R.drawable.left_side_injured);
                            } else if (patientinjured.equalsIgnoreCase("Right")) {
                                injured_side.setImageResource(R.drawable.right_side_injured);
                            } else if (patientinjured.equalsIgnoreCase("Bi-Lateral")) {
                                injured_side.setImageResource(R.drawable.billateral_side_injured);
                            }

                        }
                    } catch (NumberFormatException e) {

                    }


                    if (patientgender.equalsIgnoreCase("Male")) {
                        patientgender = "M";
                    } else if (patientgender.equalsIgnoreCase("Female")) {
                        patientgender = "F";
                    }

                    age = patientage;


                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = null;
                    try {
                        if (age.length() >= 3) {
                            date = formatter.parse(age);
                            @SuppressLint({"NewApi", "LocalSuppress"}) Instant instant = date.toInstant();
                            ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
                            LocalDate givenDate = zone.toLocalDate();
                            //Calculating the difference between given date to current date.
                            Period period = Period.between(givenDate, LocalDate.now());
                            pt_age = String.valueOf(period.getYears());
                        } else {
                            pt_age = age;
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if (Goal_reached.isEmpty()) {
                        semi_prog.setPercent(0);
                    } else {
                        semi_prog.setPercent(Integer.parseInt(Goal_reached));
                    }


                    String patname = patientname + "," + " " + patientgender + "/" + age;

                    patientName.setText(patname);
                    tv_patient_id.setText("Patient ID: " + patientid);

                    patient_phone.setText(patientphone);
                    patient_email.setText(patientemail);
                    tv_affected_side.setText(patientinjured);
                    tv_condition_det.setText(patientcasedes);
                    tv_Speciality_det.setText(patientcondition);
                    tv_Medicalhistory_det.setText(patienthistory);
                    tv_goal_num.setText(Goal_reached.concat("%"));
                    join.setText(start_date);
                    last_session.setText(last_date);
                    session_count_number.setText(session_count);


                }
            }

            @Override
            public void onFailure(Call<ViewSummaryDetails> call, Throwable t) {

            }


        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReportActivity();


            }

            private void openReportActivity() {
                if (NetworkOperations.isNetworkAvailable(ViewPatientScreen.this)) {
                    Intent mmt_intent = new Intent(ViewPatientScreen.this, SessionReportActivity.class);
                    mmt_intent.putExtra("patientid", patient_id);
                    mmt_intent.putExtra("patientname", patientname);
                    mmt_intent.putExtra("dateofjoin", dateofjoin);
                    mmt_intent.putExtra("phizioemail", email);
                    startActivity(mmt_intent);
                } else {
                    NetworkOperations.networkError(ViewPatientScreen.this);
                }
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((PatientsView)context).deletePatient(patient);
            }
        });

    }

}