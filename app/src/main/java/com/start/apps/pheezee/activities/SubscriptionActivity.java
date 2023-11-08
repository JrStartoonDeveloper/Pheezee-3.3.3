package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.SortHistoryDate.context;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.start.apps.pheezee.pojos.BluetoothCommunication;
import com.start.apps.pheezee.pojos.ReportCommunicationData;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class SubscriptionActivity extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    LinearLayout SubscriptionFistView,First500ReportSelect,First100ReportSelect;
    LinearLayout SubscriptionSecondView,Second500ReportSelect,Second100ReportSelect,PremiumLayout;
    private FrameLayout overlayLayout;
    EditText PatientName,PatinetPhone,PatientEmail;
    GetDataService getDataService;
    private ImageView svgImageView,svgImageViewsmall;
    Button BuyNow;
    String pt_email, pt_name, pt_number,pt_amount;

    ImageView iv_back_app_info_kt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp<750) {
            setContentView(R.layout.activity_subscription_tablet);
        } else if(config.smallestScreenWidthDp>=750) {
            setContentView(R.layout.activity_subscription_tablet_large);
        }else{
            setContentView(R.layout.activity_subscription);
        }


        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        SubscriptionFistView = findViewById(R.id.subscriptionfirstview);
        PremiumLayout=findViewById(R.id.premium_layout);
        SubscriptionSecondView = findViewById(R.id.subscriptionsecondview);
        First500ReportSelect=findViewById(R.id.first_500_report_select);
        First100ReportSelect=findViewById(R.id.first_100_report_select);
        Second500ReportSelect=findViewById(R.id.second_500_report_select);
        Second100ReportSelect=findViewById(R.id.second_100_report_select);
        PatientName=findViewById(R.id.patient_name);
        PatientEmail=findViewById(R.id.Patient_Email);
        PatinetPhone=findViewById(R.id.Patient_Phone);
        BuyNow=findViewById(R.id.buy_now_button);
        overlayLayout = findViewById(R.id.overlay_layout);
        iv_back_app_info_kt = findViewById(R.id.iv_back_app_info);
        svgImageView = findViewById(R.id.svgImageView);
//        svgImageViewsmall = findViewById(R.id.svgImageViewsmall);
        iv_back_app_info_kt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pt_amount="20000";


        First500ReportSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               SubscriptionFistView.setVisibility(View.VISIBLE);
               SubscriptionSecondView.setVisibility(View.GONE);
               pt_amount="20000";
            }
        });

        First100ReportSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubscriptionFistView.setVisibility(View.GONE);
                SubscriptionSecondView.setVisibility(View.VISIBLE);
                pt_amount="4000";
            }
        });
        Second500ReportSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SubscriptionFistView.setVisibility(View.VISIBLE);
                SubscriptionSecondView.setVisibility(View.GONE);
                pt_amount="20000";
            }
        });

        Second100ReportSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubscriptionFistView.setVisibility(View.GONE);
                SubscriptionSecondView.setVisibility(View.VISIBLE);
                pt_amount="4000";
            }
        });


        Intent intent = getIntent();
        PatientName.setText(intent.getStringExtra("pt_name"));
        PatientEmail.setText(intent.getStringExtra("pt_email"));
        PatinetPhone.setText( intent.getStringExtra("pt_number"));

        BuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pt_email = PatientEmail.getText().toString();
                pt_name = PatientName.getText().toString();
                pt_number = PatinetPhone.getText().toString();
                ReportCommunication data = new ReportCommunication(pt_email, pt_name, pt_number,
                        pt_amount);
                Call<ReportCommunicationData> reportCommunicationDataCall = getDataService.report_email_con(data);
                reportCommunicationDataCall.enqueue(new Callback<ReportCommunicationData>() {
                    @Override
                    public void onResponse(Call<ReportCommunicationData> call, Response<ReportCommunicationData> response) {
                        if (response.code() == 200) {
                            ReportCommunicationData res = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReportCommunicationData> call, Throwable t) {

                    }
                });
//

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("status", "true");
                editor.apply();
                PremiumLayout.setVisibility(View.GONE);
                overlayLayout.setVisibility(View.VISIBLE);
                Animation fadeIn = AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.fade_in);
                overlayLayout.startAnimation(fadeIn);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeInImage();
                    }
                }, 500);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Apply fade-out animation
                        Animation fadeOut = AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.fade_out);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                overlayLayout.setVisibility(View.GONE);
                                finish();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        overlayLayout.startAnimation(fadeOut);
                    }
                }, 3000);
//                Toast.makeText(SubscriptionActivity.this, "Request sent.", Toast.LENGTH_LONG).show();

            }
        });
    }
    private void fadeInImage() {
        // Fade-in animation for the image


        // Fade-in animation for the image
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000); // 500 milliseconds
        fadeIn.setFillAfter(true);

        // Start the fade-in animation
        svgImageView.startAnimation(fadeIn);
        svgImageView.setVisibility(View.VISIBLE);
    }
}