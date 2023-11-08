package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.start.apps.pheezee.adapters.MyAdapter;
import com.start.apps.pheezee.adapters.MyAdapterNew;
import com.start.apps.pheezee.pojos.ViewSessionReportData;
import com.start.apps.pheezee.pojos.ViewSessionReportDataSort;
import com.start.apps.pheezee.pojos.ViewStatusSessionHistory;
import com.start.apps.pheezee.popup.ViewPopUpWindow;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.room.Entity.PhizioPatients;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class SortHistoryDate extends AppCompatActivity {

    String pt_name_arr[], pt_des_arr[];
    String car_name_arr[], car_des_arr[], car_id_arr[],car_col_arr[];
    RecyclerView recyclerView;
    LinearLayout linearLayout;

    Button start_btn;

    public static Context context;

    List<String> titles,titles2,titles3,titles4;
    ArrayList<String> Imgurl;
    ImageView imageView, iv_back_app_info;

    Button btn_history, btn_today;

    GetDataService getDataService;
    String Session_count="-";
    String Report_count="-";
    String Report_collection="-";

    String Patient_ID = "-";

    String Heldon_dates = "-";


    String[] strArray = null;

    String[] strArray_two = null;

    String[] strArray_three = null;

    String[] strArray_five = null;

    String[] strArray_four = null;



    public static void viewPopUpWindow(PhizioPatients patient) {


    }



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        context = getApplicationContext();


        new CountDownTimer(1000, 1000){
            public void onTick(long millisUntilFinished) {
                Intent intent = new Intent();
                intent.putExtra("et_phizio_email", json_phizioemail);
                String email = intent.getStringExtra("et_phizio_email");
                Intent intent1 = getIntent();
                String month_and_year = intent1.getStringExtra("month_and_year");
                ViewSessionReportDataSort data = new ViewSessionReportDataSort(email, month_and_year, "Year");
                Call<ViewSessionReportData> view_report_data_history_data = getDataService.view_report_data_history_report(data);
                view_report_data_history_data.enqueue(new Callback<ViewSessionReportData>() {
                    @Override
                    public void onResponse(@NonNull Call<ViewSessionReportData> call, @NonNull Response<ViewSessionReportData> response) {
                        if (response.code() == 200) {
                            ViewSessionReportData res = response.body();
                            Session_count = res.getSession();
                            Report_count = res.getReport();
                            Report_collection = res.getReportCollection();
                            Heldon_dates = res.getHeldonDates();
                            Patient_ID = res.getPatientData();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ViewSessionReportData> call, @NonNull Throwable t) {
                        Log.e("Kranthi_Status","Not_Working");
                    }
                });

            }
            public void onFinish() {
                setContentView(R.layout.activity_sort_history_date);
                iv_back_app_info = findViewById(R.id.iv_back_app_info);
                btn_history = findViewById(R.id.btn_history);
                btn_today = findViewById(R.id.btn_today);

                start_btn = findViewById(R.id.start_btn);

                start_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent start_btn = new Intent(SortHistoryDate.this, PatientsView.class);
                        startActivity(start_btn);
                    }
                });


                btn_today.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent today = new Intent(SortHistoryDate.this, SortHistoryDate.class);
                        startActivity(today);
                    }
                });
                btn_history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent history = new Intent(SortHistoryDate.this, SortHistoryYear.class);
                        startActivity(history);
                    }
                });
                iv_back_app_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent back_press = new Intent(SortHistoryDate.this, PhizioProfile.class);
                        startActivity(back_press);
                    }
                });


                Imgurl = new ArrayList<>();
                titles = new ArrayList<>();
                titles2 = new ArrayList<>();
                titles3 = new ArrayList<>();
                titles4 = new ArrayList<>();

                imageView = findViewById(R.id.imageView);
                recyclerView = findViewById(R.id.RecyclerView);
                linearLayout = findViewById(R.id.no_session_view);



                if(Report_count.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                }



                strArray = Report_count.split(",");
                for (int i = 0; i< strArray.length; i++){
                    titles.add(strArray[i]);
                }

                strArray_two = Report_collection.split(",");
                for (int i = 0; i< strArray_two.length; i++){
                    titles2.add(strArray_two[i]);
                }

                strArray_four = Heldon_dates.split(",");
                for (int i = 0; i< strArray_four.length; i++){
                    titles3.add(strArray_four[i]);
                }

                strArray_five = Patient_ID.split(",");
                for (int i = 0; i< strArray_five.length; i++){
                    titles4.add(strArray_five[i]);
                }

                strArray_three = Session_count.split(",");
                for (int i = 0; i< strArray_three.length; i++){

                    if(strArray_three[i].equalsIgnoreCase("empty")){
                        Imgurl.add("https://drive.google.com/file/d/1udABunZs-amdbnRnss3RjCLC70jzm1K6/view?usp=share_link");
                    }else{
                        String temp = strArray_three[i];
                        temp = "https://s3.ap-south-1.amazonaws.com/pheezee/" + temp;
                        Imgurl.add(temp);
                    }
                }


                car_name_arr = strArray;
                car_des_arr = strArray_two;
                car_col_arr = strArray_four;
                car_id_arr = strArray_five;

//                MyAdapter myAdapter = new MyAdapter(SortHistoryDate.this,car_name_arr,car_des_arr,car_id_arr,Imgurl);
                MyAdapterNew myAdapter = new MyAdapterNew(SortHistoryDate.this,car_name_arr,car_des_arr,car_col_arr,car_id_arr,Imgurl);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }

        }.start();



    }
    @Override
    public void onBackPressed() {
       Intent back_press = new Intent(SortHistoryDate.this, PhizioProfile.class);
       startActivity(back_press);

    }
}