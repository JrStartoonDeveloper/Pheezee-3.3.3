package com.start.apps.pheezee.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ParseException;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import start.apps.pheezee.R;

import com.start.apps.pheezee.adapters.ReportAdapter;
import com.start.apps.pheezee.adapters.SessionReportListArrayAdapter;
import com.start.apps.pheezee.adapters.OverallReportListArrayAdapter;
import com.start.apps.pheezee.classes.SessionListClass;
import com.start.apps.pheezee.fragments.ReportMonth;
import com.start.apps.pheezee.fragments.ReportWeek;
import com.start.apps.pheezee.pojos.BluetoothCommunication;
import com.start.apps.pheezee.pojos.GetReportDataResponse;
import com.start.apps.pheezee.pojos.Overalldetail;
import com.start.apps.pheezee.pojos.Overallresponse;
import com.start.apps.pheezee.pojos.PatientStatusData;
import com.start.apps.pheezee.pojos.PremiumSubscriptionUserData;
import com.start.apps.pheezee.pojos.SessionList;
import com.start.apps.pheezee.pojos.SessionResult;
import com.start.apps.pheezee.pojos.Sessiondetail;
import com.start.apps.pheezee.popup.ViewExercisePopupWindow;
import com.start.apps.pheezee.popup.ViewPopUpWindow;
import com.start.apps.pheezee.repository.MqttSyncRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.File;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.services.PheezeeBleService;
import com.start.apps.pheezee.utils.DateOperations;
import com.start.apps.pheezee.utils.TimeOperations;

public class SessionReportActivity extends AppCompatActivity implements MqttSyncRepository.OnReportDataResponseListner {
    String pt_email, pt_name , pt_number;
    GetReportDataResponse session_arry;
    boolean inside_report_activity = true;
    boolean overall_selected = false;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SessionReportListArrayAdapter sesssionreport_adapter;
    private ReportAdapter reportAdapter;
    OverallReportListArrayAdapter overallreport_adapter;
    ProgressDialog progress;
    ListView lv_sessionlist;
    RecyclerView recyclerView;
    ArrayList<String> dates_sessions;
    Iterator iterator;

    TextView tv_day, tv_week, tv_month, tv_overall_summary, tv_overall,tv_session_duration;
    LinearLayout ll_session_duration;
    MqttSyncRepository repository;
    ImageView iv_go_back;
    ArrayList<SessionListClass> mSessionListResults,mOverallListResults;
    GetDataService getDataService;

    JSONObject json_phizio;
    SharedPreferences sharedPref;
    boolean isBound = false;
    PheezeeBleService mService;

    Button BuyNowPremium,PaidPremiumBuyNow,TimePeriodBuyNow;
    String StartDate,Enddate,ReportGenerated,ReportAccessible;

    TextView PaidPremiumReportGeneretedText,PaidPremiumReportAccessibleText,PaidPremiumReportRemainingText,PaidPremiumReportRemainingText1;

    TextView TimePeriodEndDate, TimePerioddaysLeftText,GracePeriodText,TimePerioddaysLeftLass;

    LinearLayout PaidPremium, RegularPremium,TimePeriod,GracePeriod,PaidPremiumRemainingLayout,PaidPremiumReportGenerated,TimePeriodDaysLeftLayout,TimePeriodEndDateLayout;

    LinearLayout OldUserlayout;
    Button GracePeriodBuyNow,OldUserBuyButton;

    public static String patientId="", phizioemail="", patientName="", dateofjoin="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_report);
        fragmentManager = getSupportFragmentManager();
        repository = new MqttSyncRepository(getApplication());
        repository.setOnReportDataResponseListener(this);
        declareView();


        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        patientId = getIntent().getStringExtra("patientid");
        phizioemail = getIntent().getStringExtra("phizioemail");
        patientName = getIntent().getStringExtra("patientname");
        dateofjoin = getIntent().getStringExtra("dateofjoin");
        PremiumSubscription();
        progress = new ProgressDialog(this);
        progress.setMessage("Generating report");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialog.dismiss();
                }
                return true;
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, PheezeeBleService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);

        checkPermissionsRequired();
        repository.getReportData(phizioemail,patientId);

    }

    private void PremiumSubscription() {

        PaidPremium=findViewById(R.id.paid_premium_layout);
        RegularPremium=findViewById(R.id.regular_Premium_layout);
        GracePeriod=findViewById(R.id.grace_period_layout);
        TimePeriod=findViewById(R.id.time_period_layout);
        PaidPremiumRemainingLayout=findViewById(R.id.paid_premium_download_remainig_layout);
        PaidPremiumReportGenerated=findViewById(R.id.paid_premium_report_genereted_layout);
        PaidPremiumBuyNow=findViewById(R.id.paid_premium_buy_now_button);
        PaidPremiumReportGeneretedText=findViewById(R.id.paid_premium_report_genereted);
        PaidPremiumReportAccessibleText=findViewById(R.id.paid_premium_report_accessible);
        PaidPremiumReportRemainingText=findViewById(R.id.paid_premium_report_remaining);
        PaidPremiumReportRemainingText1=findViewById(R.id.paid_premium_report_accessible_remaining);
        TimePerioddaysLeftText=findViewById(R.id.time_period_days_Left);
        TimePeriodDaysLeftLayout=findViewById(R.id.time_period_days_Left_layout);
        TimePeriodEndDateLayout=findViewById(R.id.time_period_end_date_layout);
        TimePeriodBuyNow=findViewById(R.id.time_period_buy_now_button);
        TimePeriodEndDate=findViewById(R.id.time_period_end_date);
        GracePeriodText=findViewById(R.id.grace_period_text);
        TimePerioddaysLeftLass=findViewById(R.id.time_period_days_Left_less);
        GracePeriodBuyNow=findViewById(R.id.grace_period_buy_button);
        OldUserlayout=findViewById(R.id.old_Premium_layout);
        OldUserBuyButton=findViewById(R.id.buy_now_button_old);
        try {
            String Email = getIntent().getStringExtra("phizioemail");
            Log.e("11111111111111111222222222222222222222223",phizioemail);
            Log.e("11111111111111111222222222222222222222223",Email);
            PremiumSubscriptionUserData premiumSubscriptionUserData=new PremiumSubscriptionUserData(phizioemail);
            Call<PremiumSubscriptionDataRec> premiumSubscriptionDataRecCall = getDataService.premium_subscription_user_data(premiumSubscriptionUserData);
            premiumSubscriptionDataRecCall.enqueue(new Callback<PremiumSubscriptionDataRec>() {
                @Override
                public void onResponse(Call<PremiumSubscriptionDataRec> call, Response<PremiumSubscriptionDataRec> response) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        if(response.code()==200){
                            PremiumSubscriptionDataRec res = response.body();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            // Get the current date
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH) + 1; // Month value is 0-based, so adding 1
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            // Create a string representation of the date
                            String currentDate1 = day + "-" + month + "-" + year;
                            String currentDateStr = currentDate1;
                            String lastDateStr = res.getEndDate();
                            String firstDateStr=res.getStartDate();
                            Log.e("1111111111111111111111111111111111111111111111111",lastDateStr);
                            if (currentDateStr != null && !currentDateStr.equalsIgnoreCase("null") && !currentDateStr.equalsIgnoreCase("") &&
                                    lastDateStr != null && !lastDateStr.equalsIgnoreCase("null") && !lastDateStr.equalsIgnoreCase("")) {
                                LocalDate currentDate = LocalDate.parse(currentDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                                LocalDate lastDate = LocalDate.parse(lastDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));

                                // Calculate the number of days between the dates
                                long daysBetween = ChronoUnit.DAYS.between(currentDate, lastDate);

                                int comparison = currentDate.compareTo(lastDate);
                                Log.e("1111111222222222223333333333444", String.valueOf(comparison));

                                // Print the current date
//                            System.out.println(currentDate);
//                            LocalDate date1 = LocalDate.parse(currentDate1, formatter);
//                            LocalDate date2 = LocalDate.parse(res.getEndDate(), formatter);
//                            = ChronoUnit.DAYS.between(date1, date2);
                                long difference_In_Days =0;

                                Log.e("1111111222222222223333333333444", String.valueOf(difference_In_Days));
                                StartDate=res.getStartDate();
                                Enddate=res.getEndDate();
                                ReportGenerated=res.getReportGenerated();
                                ReportAccessible=res.getReportAccessible();
                                if(res.getCustomerType().equalsIgnoreCase("null")||res.getCustomerType().equalsIgnoreCase("") ){
                                    RegularPremium.setVisibility(View.VISIBLE);
                                    PaidPremium.setVisibility(View.GONE);
                                    TimePeriod.setVisibility(View.GONE);
                                    OldUserlayout.setVisibility(View.GONE);
                                }
                                if(res.getCustomerType().equalsIgnoreCase("old_user")){
                                    OldUserlayout.setVisibility(View.VISIBLE);
                                    RegularPremium.setVisibility(View.GONE);
                                    PaidPremium.setVisibility(View.GONE);
                                    TimePeriod.setVisibility(View.GONE);
                                }
                                if(res.getCustomerType().equalsIgnoreCase("time_period_mode") || res.getCustomerType().equalsIgnoreCase("rental_mode")){
                                    RegularPremium.setVisibility(View.GONE);
                                    PaidPremium.setVisibility(View.GONE);
                                    TimePeriod.setVisibility(View.VISIBLE);
                                    OldUserlayout.setVisibility(View.GONE);
                                    TimePeriodEndDate.setText(Enddate);


                                    if(comparison>0){
                                        if (res.getCustomerType().equalsIgnoreCase("time_period_mode")){
                                            GracePeriod.setVisibility(View.GONE);
                                            RegularPremium.setVisibility(View.VISIBLE);
                                            PaidPremium.setVisibility(View.GONE);
                                            TimePeriod.setVisibility(View.GONE);
                                            TimePeriodEndDate.setText(Enddate);
                                        }
                                        else{
                                            GracePeriod.setVisibility(View.VISIBLE);
                                            RegularPremium.setVisibility(View.GONE);
                                            PaidPremium.setVisibility(View.GONE);
                                            TimePeriod.setVisibility(View.GONE);
                                            TimePeriodEndDate.setText(Enddate);
                                        }

                                    }

                                    if(daysBetween<25){
                                        if(daysBetween<=5){
                                            TimePerioddaysLeftLass.setText(String.valueOf(daysBetween).concat(" Days"));
                                            TimePerioddaysLeftText.setVisibility(View.GONE);
                                            TimePerioddaysLeftLass.setVisibility(View.VISIBLE);
                                        }
                                        TimePeriodEndDateLayout.setVisibility(View.GONE);
                                        TimePeriodDaysLeftLayout.setVisibility(View.VISIBLE);
                                        TimePeriodBuyNow.setVisibility(View.VISIBLE);
                                        TimePerioddaysLeftText.setText(String.valueOf(daysBetween).concat(" Days"));


                                    }
                                    else{
                                        TimePeriodEndDateLayout.setVisibility(View.VISIBLE);
                                        TimePeriodDaysLeftLayout.setVisibility(View.GONE);
                                        TimePeriodBuyNow.setVisibility(View.GONE);
                                    }
                                }
                                if(res.getCustomerType().equalsIgnoreCase("subscription_mode")){
                                    RegularPremium.setVisibility(View.GONE);
                                    PaidPremium.setVisibility(View.VISIBLE);
                                    TimePeriod.setVisibility(View.GONE);
                                    OldUserlayout.setVisibility(View.GONE);
                                    int Repoprts_remaining=Integer.valueOf(ReportAccessible)-Integer.valueOf(ReportGenerated);
                                    if(Repoprts_remaining>20){
                                        PaidPremiumReportGenerated.setVisibility(View.VISIBLE);
                                        PaidPremiumRemainingLayout.setVisibility(View.GONE);
                                        PaidPremiumBuyNow.setVisibility(View.GONE);
                                        PaidPremiumReportGeneretedText.setText(ReportGenerated);
                                        PaidPremiumReportAccessibleText.setText(ReportAccessible);
                                    }else{
                                        if(Repoprts_remaining<0){
                                            RegularPremium.setVisibility(View.VISIBLE);
                                            PaidPremium.setVisibility(View.GONE);
                                            TimePeriod.setVisibility(View.GONE);
                                        }
                                        else{
                                            PaidPremiumReportGenerated.setVisibility(View.GONE);
                                            PaidPremiumRemainingLayout.setVisibility(View.VISIBLE);
                                            PaidPremiumBuyNow.setVisibility(View.VISIBLE);
                                            PaidPremiumReportRemainingText.setText(String.valueOf(Repoprts_remaining));
                                            PaidPremiumReportRemainingText1.setText(ReportAccessible);
                                        }

                                    }
                                }
                                if(res.getCustomerType().equalsIgnoreCase("force_stop")){
                                    GracePeriod.setVisibility(View.VISIBLE);
                                    GracePeriodText.setText("Your Payment is pending");
                                }
                            }
                            else{
                                RegularPremium.setVisibility(View.VISIBLE);
                                PaidPremium.setVisibility(View.GONE);
                                TimePeriod.setVisibility(View.GONE);
                            }


                        }
                    }
                    catch (Exception e){
                        Log.e("11111111111111", String.valueOf(e));
                    }

                }

                @Override
                public void onFailure(Call<PremiumSubscriptionDataRec> call, Throwable t) {

                }
            });
        }catch (Exception e){

        }


        TimePeriodBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothCommunication data = new BluetoothCommunication(phizioemail, pt_name, pt_number);
                Call<BluetoothCommunication> bluetoothCommunicationCall = getDataService.bluetooth_data_fetch(data);
                bluetoothCommunicationCall.enqueue(new Callback<BluetoothCommunication>() {
                    @Override
                    public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                        if (response.code() == 200) {
                            BluetoothCommunication res = response.body();
                            pt_email = res.getPhizioemail();
                            pt_name = res.getName();
                            pt_number =  res.getPhone();
                            Intent intent = new Intent(SessionReportActivity.this, SubscriptionActivity.class);
                            intent.putExtra("pt_name", pt_name);
                            intent.putExtra("pt_number",pt_number);
                            intent.putExtra("pt_email",pt_email);
                            startActivity(intent);
                        }

                    }


                    @Override
                    public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                    }
                });
            }
        });

        GracePeriodBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothCommunication data = new BluetoothCommunication(phizioemail, pt_name, pt_number);
                Call<BluetoothCommunication> bluetoothCommunicationCall = getDataService.bluetooth_data_fetch(data);
                bluetoothCommunicationCall.enqueue(new Callback<BluetoothCommunication>() {
                    @Override
                    public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                        if (response.code() == 200) {
                            BluetoothCommunication res = response.body();
                            pt_email = res.getPhizioemail();
                            pt_name = res.getName();
                            pt_number =  res.getPhone();
                            Intent intent = new Intent(SessionReportActivity.this, SubscriptionActivity.class);
                            intent.putExtra("pt_name", pt_name);
                            intent.putExtra("pt_number",pt_number);
                            intent.putExtra("pt_email",pt_email);
                            startActivity(intent);
                        }

                    }


                    @Override
                    public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                    }
                });
            }
        });
        OldUserBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothCommunication data = new BluetoothCommunication(phizioemail, pt_name, pt_number);
                Call<BluetoothCommunication> bluetoothCommunicationCall = getDataService.bluetooth_data_fetch(data);
                bluetoothCommunicationCall.enqueue(new Callback<BluetoothCommunication>() {
                    @Override
                    public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                        if (response.code() == 200) {
                            BluetoothCommunication res = response.body();
                            pt_email = res.getPhizioemail();
                            pt_name = res.getName();
                            pt_number =  res.getPhone();
                            Intent intent = new Intent(SessionReportActivity.this, SubscriptionActivity.class);
                            intent.putExtra("pt_name", pt_name);
                            intent.putExtra("pt_number",pt_number);
                            intent.putExtra("pt_email",pt_email);
                            startActivity(intent);
                        }

                    }


                    @Override
                    public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                    }
                });
            }
        });
        PaidPremiumBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothCommunication data = new BluetoothCommunication(phizioemail, pt_name, pt_number);
                Call<BluetoothCommunication> bluetoothCommunicationCall = getDataService.bluetooth_data_fetch(data);
                bluetoothCommunicationCall.enqueue(new Callback<BluetoothCommunication>() {
                    @Override
                    public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                        if (response.code() == 200) {
                            BluetoothCommunication res = response.body();
                            pt_email = res.getPhizioemail();
                            pt_name = res.getName();
                            pt_number =  res.getPhone();
                            Intent intent = new Intent(SessionReportActivity.this, SubscriptionActivity.class);
                            intent.putExtra("pt_name", pt_name);
                            intent.putExtra("pt_number",pt_number);
                            intent.putExtra("pt_email",pt_email);
                            startActivity(intent);
                        }

                    }


                    @Override
                    public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                    }
                });
            }
        });
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            PheezeeBleService.LocalBinder mLocalBinder = (PheezeeBleService.LocalBinder)service;
            mService = mLocalBinder.getServiceInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            mService = null;
        }
    };

    private void declareView() {

        tv_day = findViewById(R.id.tv_session_report_day);
        tv_month = findViewById(R.id.tv_session_report_month);
        tv_week = findViewById(R.id.tv_session_report_week);
        tv_overall_summary = findViewById(R.id.tv_session_report_overall_report);
        tv_session_duration = findViewById(R.id.tv_session_duration);
        tv_overall = findViewById(R.id.tv_session_report_overall);
        iv_go_back = findViewById(R.id.iv_back_session_report_new);
        lv_sessionlist =findViewById(R.id.report_listview);
        recyclerView = findViewById(R.id.recyclerView);
        ll_session_duration =findViewById(R.id.ll_session_duration);

        BuyNowPremium=findViewById(R.id.buy_now_button);
        mSessionListResults = new ArrayList<SessionListClass>();
        mOverallListResults = new ArrayList<SessionListClass>();

        BuyNowPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothCommunication data = new BluetoothCommunication(phizioemail, pt_name, pt_number);
                Call<BluetoothCommunication> bluetoothCommunicationCall = getDataService.bluetooth_data_fetch(data);
                bluetoothCommunicationCall.enqueue(new Callback<BluetoothCommunication>() {
                    @Override
                    public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                        if (response.code() == 200) {
                            BluetoothCommunication res = response.body();
                            pt_email = res.getPhizioemail();
                            pt_name = res.getName();
                            pt_number =  res.getPhone();
                                Intent intent = new Intent(SessionReportActivity.this, SubscriptionActivity.class);
                                intent.putExtra("pt_name", pt_name);
                                intent.putExtra("pt_number",pt_number);
                                intent.putExtra("pt_email",pt_email);
                                startActivity(intent);
                        }

                    }


                    @Override
                    public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                    }
                });
            }
        });












        iv_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.e("yyyyyyyyyyyyyyyyyyyyyyyyyyyy","Working");
//                Intent i = new Intent(SessionReportActivity.this, ViewPopUpWindow.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(i);
            }
        });

        lv_sessionlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SessionListClass temp = (SessionListClass) adapterView.getItemAtPosition(i);

                ViewExercisePopupWindow feedback = new ViewExercisePopupWindow(SessionReportActivity.this,0, Integer.toString(mSessionListResults.size()-i), 0, 0, "", "",
                        phizioemail, "", "", "", "",
                        0, temp.getPatientid(), "", 0L, "", dateofjoin, 0,0,
                        "","","","","",0,temp.getHeldon());
                feedback.showWindow();
            }
        });


        tv_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewOfDayMonthWeek();
                tv_day.setTypeface(null, Typeface.BOLD);
                tv_day.setAlpha(1);
                String htmlString="<b><u>Session</u></b>";
                tv_day.setText(Html.fromHtml(htmlString));
                ll_session_duration.setVisibility(View.GONE);
                openDayFragment();
                overall_selected=false;

            }
        });



        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewOfDayMonthWeek();
                tv_month.setTypeface(null, Typeface.BOLD);
                tv_month.setAlpha(1);

                openMonthFragment();
            }
        });

        tv_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewOfDayMonthWeek();
                tv_week.setTypeface(null, Typeface.BOLD);
                tv_week.setAlpha(1);
                openWeekFragment();
            }
        });

        tv_overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewOfDayMonthWeek();
                tv_overall.setTypeface(null, Typeface.BOLD);
                tv_overall.setAlpha(1);
                String htmlString="<b><u>Overall</u></b>";
                tv_overall.setText(Html.fromHtml(htmlString));
                ll_session_duration.setVisibility(View.VISIBLE);
                overall_selected = true;
                openOverallFragment();
            }
        });
    }



    public void changeViewOfDayMonthWeek(){
        tv_month.setTypeface(null, Typeface.NORMAL);
        tv_week.setTypeface(null, Typeface.NORMAL);
        tv_day.setTypeface(null, Typeface.NORMAL);
        tv_overall.setTypeface(null, Typeface.NORMAL);
        tv_day.setAlpha(0.5f);
        tv_week.setAlpha(0.5f);
        tv_month.setAlpha(0.5f);
        tv_overall.setAlpha(0.5f);

        String htmlString="Overall";
        tv_overall.setText(Html.fromHtml(htmlString));

        htmlString="Session";
        tv_day.setText(Html.fromHtml(htmlString));
    }

    public void openDayFragment() {

            if (session_arry != null) {


                HashSet<String> hashSet = new HashSet<>();

                List<SessionList> res = session_arry.getSessionList();
                List<SessionResult> session_result_array = session_arry.getSessionResult();
                List<Sessiondetail> download_date_array = null;
                if(session_result_array.size()>0) {
                    download_date_array = session_result_array.get(0).getSessiondetails();
                }

                JSONArray array=null;

                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(res);
                try {
                    array = new JSONArray(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(array.length()>0) {
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject object = array.getJSONObject(i);
                            hashSet.add(object.getString("heldon").substring(0,10));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                iterator = hashSet.iterator();
                dates_sessions = new ArrayList<>();
                while (iterator.hasNext()){
                    dates_sessions.add(iterator.next()+"");

                }

                Collections.sort(dates_sessions,new Comparator<String>() {
                    @Override
                    public int compare(String arg0, String arg1) {
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd");
                        int compareResult = 0;
                        try {
                            Date arg0Date = format.parse(arg0);
                            Date arg1Date = format.parse(arg1);
                            compareResult = arg0Date.compareTo(arg1Date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            compareResult = arg0.compareTo(arg1);
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        return compareResult;
                    }
                });

                // Adding Session Duration
                if(dates_sessions.size()>=1) {

                    //Date
                    String from_date = dates_sessions.get(0);
                    from_date=from_date.replace("-","/");
                    String[] from_date_split = from_date.split("/");
                    from_date = from_date_split[2]+"/"+from_date_split[1]+"/"+from_date_split[0];
                    from_date = DateOperations.getDateInMonthAndDate(from_date);
                    String[] from_date_split_format = from_date.split(",");
                    from_date = from_date_split_format[0];
                    from_date = from_date + " " +from_date_split[0];
                    //Date
                    String to_date = dates_sessions.get(dates_sessions.size() - 1);
                    to_date=to_date.replace("-","/");
                    String[] to_date_split = to_date.split("/");
                    to_date = to_date_split[2]+"/"+to_date_split[1]+"/"+to_date_split[0];
                    to_date = DateOperations.getDateInMonthAndDate(to_date);
                    String[] to_date_split_format = to_date.split(",");
                    to_date = to_date_split_format[0];
                    to_date = to_date +" "+ to_date_split[0];


                    if(!from_date.equalsIgnoreCase(to_date)) {
                        tv_session_duration.setText(from_date + " to " + to_date);
                    }
                }
                Collections.sort(dates_sessions,Collections.reverseOrder());

                mSessionListResults.clear();
                for (int i = 0; i < dates_sessions.size(); i++) {


                        int counter=0;
                        SessionListClass temp= new SessionListClass();
                        temp.setHeldon(dates_sessions.get(i));
                        temp.setPatientid(patientId);
                        temp.setPatientemail(phizioemail);

                        //Adding download date
                    if(download_date_array != null) {
                        for (int k = 0; k < download_date_array.size(); k++) {

                            if(download_date_array.get(k).getHeldon() != null) {
                                if ((dates_sessions.get(i)).equals(download_date_array.get(k).getHeldon().toString())) {
                                    // Storing the download date in musclename.
                                    temp.setMuscle_name(download_date_array.get(k).getDate().toString());

                                }
                            }

                        }
                    }
                         if(array.length()>0) {
                        for (int j = 0; j < array.length(); j++) {
                            try {
                                JSONObject object = array.getJSONObject(j);

                                if(object.getString("heldon").substring(0,10).equals(dates_sessions.get(i))){
                                    counter=counter+1;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
//                         Log.d("session","hashe here");
//                         Log.d("session",hashSet)
//                        temp.setSession_time(String.valueOf(Collections.frequency(hashSet,dates_sessions.get(i))));
                        temp.setSession_time(String.valueOf(counter));
                        mSessionListResults.add(temp);




                }


                sesssionreport_adapter = new SessionReportListArrayAdapter(this, mSessionListResults,this.getApplication());
                lv_sessionlist.setAdapter(sesssionreport_adapter);

                reportAdapter = new ReportAdapter(mSessionListResults);
                recyclerView.setAdapter(reportAdapter);
            } else {
                showToast("Fetching report data, please wait..");
            }
    }

    public void openWeekFragment() {
            if (session_arry != null) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new ReportWeek();
                fragmentTransaction.replace(R.id.fragment_report_container, fragment);
                fragmentTransaction.commit();
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            } else {
                showToast("Fetching report data, please wait..");
            }
    }

    public void openMonthFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new ReportMonth();
        fragmentTransaction.replace(R.id.fragment_report_container,fragment);
        fragmentTransaction.commit();
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void openOverallFragment(){
        if(session_arry!=null) {


                    // START
            Calendar calendar = Calendar.getInstance();
            String date = calenderToYYYMMDD(calendar);
            PatientStatusData data = new PatientStatusData(phizioemail, patientId,date,date);
            Call<Overallresponse> getOverall_list_respone = getDataService.getOverall_list(data);

            getOverall_list_respone.enqueue(new  Callback<Overallresponse>() {
                @Override
                public void onResponse(Call<Overallresponse> call, Response<Overallresponse> response) {

                    if(response.isSuccessful()){
                        mOverallListResults.clear();

                        if (response.code() == 200) {


                            List<SessionResult> session_result_array = session_arry.getSessionResult();
                            List<Overalldetail> download_date_array = null;
                            if(session_result_array.size()>0) {
                                download_date_array = session_result_array.get(0).getOveralldetails();
                            }


//                            String response_s = response.body();

                            Overallresponse obj = response.body();

                            if(obj.getElbow()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Elbow");
                                temp.setSession_time(String.valueOf(obj.getElbow()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("elbow").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getKnee()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Knee");
                                temp.setSession_time(String.valueOf(obj.getKnee()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("knee").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getAnkle()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Ankle");
                                temp.setSession_time(String.valueOf(obj.getAnkle()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("ankle").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getHip()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Hip");
                                temp.setSession_time(String.valueOf(obj.getHip()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("hip").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getWrist()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Wrist");
                                temp.setSession_time(String.valueOf(obj.getWrist()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("wrist").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getShoulder()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Shoulder");
                                temp.setSession_time(String.valueOf(obj.getShoulder()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("shoulder").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getForearm()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Forearm");
                                temp.setSession_time(String.valueOf(obj.getForearm()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("forearm").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            if(obj.getSpine()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Spine");
                                temp.setSession_time(String.valueOf(obj.getSpine()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("spine").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }
                            try{
                            if(obj.getAbdomen()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Abdomen");
                                temp.setSession_time(String.valueOf(obj.getAbdomen()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("abdomen").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }

                                if(obj.getCervical()>0)
                                {
                                    SessionListClass temp= new SessionListClass();
                                    temp.setBodypart("Cervical");
                                    temp.setSession_time(String.valueOf(obj.getCervical()));
                                    temp.setPatientid(patientId);
                                    temp.setPatientemail(phizioemail);
                                    //Adding download date
                                    if(download_date_array != null) {
                                        for (int k = 0; k < download_date_array.size(); k++) {
                                            if(download_date_array.get(k).getBodypart() != null) {
                                                if (("cervical").equals(download_date_array.get(k).getBodypart())) {
                                                    // Storing the download date in musclename.
                                                    temp.setMuscle_name(download_date_array.get(k).getDate());
                                                    temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                                }
                                            }
                                        }
                                    }
                                    mOverallListResults.add(temp);

                                }


                                if(obj.getThoracic()>0)
                                {
                                    SessionListClass temp= new SessionListClass();
                                    temp.setBodypart("Thoracic");
                                    temp.setSession_time(String.valueOf(obj.getThoracic()));
                                    temp.setPatientid(patientId);
                                    temp.setPatientemail(phizioemail);
                                    //Adding download date
                                    if(download_date_array != null) {
                                        for (int k = 0; k < download_date_array.size(); k++) {
                                            if(download_date_array.get(k).getBodypart() != null) {
                                                if (("thoracic").equals(download_date_array.get(k).getBodypart())) {
                                                    // Storing the download date in musclename.
                                                    temp.setMuscle_name(download_date_array.get(k).getDate());
                                                    temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                                }
                                            }
                                        }
                                    }
                                    mOverallListResults.add(temp);

                                }

                                if(obj.getLumbar()>0)
                                {
                                    SessionListClass temp= new SessionListClass();
                                    temp.setBodypart("Lumbar");
                                    temp.setSession_time(String.valueOf(obj.getLumbar()));
                                    temp.setPatientid(patientId);
                                    temp.setPatientemail(phizioemail);
                                    //Adding download date
                                    if(download_date_array != null) {
                                        for (int k = 0; k < download_date_array.size(); k++) {
                                            if(download_date_array.get(k).getBodypart() != null) {
                                                if (("lumbar").equals(download_date_array.get(k).getBodypart())) {
                                                    // Storing the download date in musclename.
                                                    temp.setMuscle_name(download_date_array.get(k).getDate());
                                                    temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                                }
                                            }
                                        }
                                    }
                                    mOverallListResults.add(temp);

                                }


                            }catch( Exception err)
                            {}
                            if(obj.getOthers()>0)
                            {
                                SessionListClass temp= new SessionListClass();
                                temp.setBodypart("Others");
                                temp.setSession_time(String.valueOf(obj.getOthers()));
                                temp.setPatientid(patientId);
                                temp.setPatientemail(phizioemail);
                                //Adding download date
                                if(download_date_array != null) {
                                    for (int k = 0; k < download_date_array.size(); k++) {
                                        if(download_date_array.get(k).getBodypart() != null) {
                                            if (("others").equals(download_date_array.get(k).getBodypart())) {
                                                // Storing the download date in musclename.
                                                temp.setMuscle_name(download_date_array.get(k).getDate());
                                                temp.setDownload_status(download_date_array.get(k).getDownload_status());
                                            }
                                        }
                                    }
                                }
                                mOverallListResults.add(temp);

                            }

                            SessionListClass temp= new SessionListClass();
                            temp.setBodypart("-");
                            overallreport_adapter.add(temp);
                            overallreport_adapter.remove(temp);

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Overallresponse> call, @NonNull Throwable t) {

                }
            });

                    // END


            overallreport_adapter = new OverallReportListArrayAdapter(this, mOverallListResults,this.getApplication());
            lv_sessionlist.setAdapter(overallreport_adapter);
        }
        else {
            showToast("Fetching report data, please wait..");
        }
    }

    private String calenderToYYYMMDD(Calendar date){
        Date date_cal = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date_cal);
        return strDate;
    }


    @Override
    protected void onPause() {
        super.onPause();
        inside_report_activity = false;
    }

    @Override
    protected void onDestroy() {
        repository.disableReportDataListner();
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, PatientsView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    public GetReportDataResponse getSessions(){
        return session_arry;
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReportDataReceived(GetReportDataResponse array, boolean response) {
        progress.dismiss();
        if (response){
            session_arry = array;
            changeViewOfDayMonthWeek();

            if(overall_selected==false) {
                tv_day.setTypeface(null, Typeface.BOLD);
                tv_day.setAlpha(1);
                String htmlString="<b><u>Session</u></b>";
                tv_day.setText(Html.fromHtml(htmlString));
                ll_session_duration.setVisibility(View.GONE);
                openDayFragment();
            }else
            {
                tv_overall.setTypeface(null, Typeface.BOLD);
                tv_overall.setAlpha(1);
                String htmlString="<b><u>Overall</u></b>";
                tv_overall.setText(Html.fromHtml(htmlString));
                ll_session_duration.setVisibility(View.VISIBLE);
                openOverallFragment();
            }
        }
        else {
            networkError_popup();

        }
    }

    public void networkError_popup(){

        // Custom notification added by Haaris
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.notification_dialog_box_single_button);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);

        TextView notification_title = dialog.findViewById(R.id.notification_box_title);
        TextView notification_message = dialog.findViewById(R.id.notification_box_message);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);

        Notification_Button_ok.setText("OK");

        // Setting up the notification dialog
        notification_title.setText("Network Error");
        notification_message.setText("Please connect to internet to view the reports");


        // On click on Continue
        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();



            }
        });

        dialog.show();

        // End
    }

    @Override
    public void onDayReportReceived(File file, String message, Boolean response) {
    }

    private void checkPermissionsRequired() {
        //external storage permission
//        if (ContextCompat.checkSelfPermission(SessionReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(SessionReportActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
    }






    @Override
    protected void onResume() {

        super.onResume();
        if(inside_report_activity==false)
        {
//            startActivity(getIntent());
            repository.getReportData(phizioemail,patientId);
            progress.setMessage("Generating report");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }
        inside_report_activity = true;

    }

}
