package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;

import com.start.apps.pheezee.adapters.MyAdapter;
import com.start.apps.pheezee.pojos.ViewSessionReportData;
import com.start.apps.pheezee.pojos.ViewStatusSessionHistory;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class FinalHistoryPatient extends AppCompatActivity {

    String pt_name_arr[], pt_des_arr[];
    String car_name_arr[], car_des_arr[];
    RecyclerView recyclerView;
    List<String> titles,titles2;
    ArrayList<String> Imgurl;
    ImageView imageView;

    GetDataService getDataService;
    String Session_count="-";
    String Report_count="-";
    String Report_collection="-";

    String[] strArray = null;

    String[] strArray_two = null;

    String[] strArray_three = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        new CountDownTimer(1000, 1000){
            public void onTick(long millisUntilFinished) {
                Intent intent = new Intent();
                intent.putExtra("et_phizio_email", json_phizioemail);
                String str1 = intent.getStringExtra("et_phizio_email");
                ViewStatusSessionHistory data = new ViewStatusSessionHistory(str1, "Month", "Year");
                Call<ViewSessionReportData> view_report_data_history_data = getDataService.view_report_data_history(data);
                view_report_data_history_data.enqueue(new Callback<ViewSessionReportData>() {
                    @Override
                    public void onResponse(@NonNull Call<ViewSessionReportData> call, @NonNull Response<ViewSessionReportData> response) {
                        if (response.code() == 200) {
                            ViewSessionReportData res = response.body();
                            Session_count = res.getSession();
                            Report_count = res.getReport();
                            Report_collection = res.getReportCollection();
                            Log.e("Kranthi_Status","Working");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ViewSessionReportData> call, @NonNull Throwable t) {
                        Log.e("Kranthi_Status","Not_Working");
                    }
                });

            }
            public void onFinish() {
                setContentView(R.layout.activity_final_history_patient);
                Imgurl = new ArrayList<>();
                titles = new ArrayList<>();
                titles2 = new ArrayList<>();

                imageView = findViewById(R.id.imageView);
                recyclerView = findViewById(R.id.RecyclerView);

                strArray = Report_count.split(",");
                for (int i = 0; i< strArray.length; i++){
                    titles.add(strArray[i]);
                }

                strArray_two = Report_collection.split(",");
                for (int i = 0; i< strArray_two.length; i++){
                    titles2.add(strArray_two[i]);
                }

                strArray_three = Session_count.split(",");
                for (int i = 0; i< strArray_three.length; i++){

                    if(strArray_three[i].equalsIgnoreCase("empty")){
                        Imgurl.add("https://images.pexels.com/photos/8463778/pexels-photo-8463778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
                    }else{
                        String temp = strArray_three[i];
                        temp = "https://s3.ap-south-1.amazonaws.com/pheezee/" + temp;
                        Imgurl.add(temp);
                    }
                }

                car_name_arr = strArray;
                car_des_arr = strArray_two;
                Log.e("strArray", String.valueOf(strArray));
                Log.e("car_name_arr", String.valueOf(car_name_arr));
//                MyAdapter myAdapter = new MyAdapter(FinalHistoryPatient.this,car_name_arr,car_des_arr, car_id_arr, Imgurl);
//                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }

        }.start();

    }
}