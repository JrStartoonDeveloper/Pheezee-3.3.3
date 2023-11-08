package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;
import static com.start.apps.pheezee.services.PheezeeBleService.hardware_version_characteristic_uuid;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.start.apps.pheezee.Usermanual;
import com.start.apps.pheezee.pojos.PatientStatusData;
import com.start.apps.pheezee.pojos.PhizioDetailsData;
import com.start.apps.pheezee.pojos.PhizioSessionReportData;
import com.start.apps.pheezee.activities.DeviceInfoActivity;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.services.PicassoCircleTransformation;
import com.start.apps.pheezee.utils.BitmapOperations;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.RegexOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

import static com.start.apps.pheezee.services.PheezeeBleService.device_state;
import static com.start.apps.pheezee.services.PheezeeBleService.jobid_device_details_update;
import static com.start.apps.pheezee.services.PheezeeBleService.jobid_device_status;


public class MyAccountPannel extends AppCompatActivity {



        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_pannel);




        //Back Button
        ImageView button = findViewById(R.id.iv_back_phizio_profile);
        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        finish();
        }
        });
        //Change Password button

        LinearLayout app_layer = (LinearLayout) findViewById (R.id.lchange_password_btr);
        app_layer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
        startActivity(intent);
        }
        });
        //Delete Account Button
                LinearLayout app_layer2 = (LinearLayout) findViewById (R.id.ldelete_btr);
                app_layer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), DeleteAccountOne.class);
                                startActivity(intent);
                        }
                });



        // App_Info
                LinearLayout app_layer3 = (LinearLayout) findViewById (R.id.lapp_info);
                app_layer3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), AppInfo.class);
                                startActivity(intent);
                        }
                });

        //Term and Conditions
//        ImageView button3 = findViewById(iv_go3);
//        button3.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//        Intent intent = new Intent(getApplicationContext(), TeamsAndConditions.class);
//        startActivity(intent);
//        }
//        });
//       Rate Us Button
//        ImageView button3 = findViewById(iv_go3);
//        button3.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//        Intent intent = new Intent(getApplicationContext(), TeamsAndConditions.class);
//        startActivity(intent);
//        }
//        });
        //Warrenty Details
//        ImageView button4 = findViewById(iv_go4);
//        button4.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//        });

        }
        }

