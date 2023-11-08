package com.start.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

import com.start.apps.pheezee.pojos.ReportStatusData;
import com.start.apps.pheezee.popup.ForgotPasswordDialog;
import com.start.apps.pheezee.popup.OtpBuilder;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.RegexOperations;
import com.trncic.library.DottedProgressBar;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements MqttSyncRepository.OnLoginResponse, View.OnFocusChangeListener {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    DottedProgressBar dottedProgressBar;
    String str_login_email, str_login_password;
    ProgressDialog progressDialog;
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 123;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    TextView tv_login,tv_welcome_message,tv_login_welcome_user,tv_signup_screen,tv_forgot_password,error_text;
    LinearLayout ll_login,ll_signin_section,ll_signup_section,ll_welcome;
    ImageView error_icon;

    LinearLayout error_layout;
    Button btn_login;
    RelativeLayout rl_login_section;
    EditText et_mail,et_password;
    GetDataService getDataService;
    MqttSyncRepository repository;
    private String storedEmail, storedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_continue);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        storedEmail = preferences.getString(KEY_EMAIL, null);
        storedPassword = preferences.getString(KEY_PASSWORD, null);

        if(storedEmail!= null && storedPassword!= null){

        }

        initializeView();
        repository = new MqttSyncRepository(getApplication());
        repository.setOnLoginResponse(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                initializeView();
                // Authentication successful, proceed to the main screen
//                startActivity(new Intent(this, MainActivity.class));
                  // Optional: Finish the login activity to prevent going back
            } else {
                // Authentication failed or was canceled
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    /**
     * disables the welcome view
     */
    private void disableWelcomeView() {
        ll_welcome.setVisibility(View.INVISIBLE);
        dottedProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Enables the previous view with edit texts etc
     */
    private void enablePreviousView() {
        ll_signup_section.setVisibility(View.VISIBLE);
        ll_signin_section.setVisibility(View.VISIBLE);
        rl_login_section.setVisibility(View.VISIBLE);
    }

    @SuppressLint("WrongViewCast")
    private void initializeView() {
        final Animation animation_up = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_up_dialog);
//        tv_login = findViewById(R.id.btn_login_login);
//        rl_login_section = findViewById(R.id.rl_login_section);
//        ll_signin_section = findViewById(R.id.layout_signin);
        btn_login = findViewById(R.id.btn_login);
        tv_forgot_password = findViewById(R.id.btn_forgot_password);
//        dottedProgressBar = findViewById(R.id.dot_progress_bar);
        et_mail = findViewById(R.id.login_et_email);
        et_password = findViewById(R.id.login_et_password);
        ((EditText)findViewById(R.id.login_et_email)).setOnFocusChangeListener(this);
        ((EditText)findViewById(R.id.login_et_password)).setOnFocusChangeListener(this);
//        ll_signup_section = findViewById(R.id.ll_login_btn);
//        ll_welcome = findViewById(R.id.ll_welcome_section);
//        tv_welcome_message = findViewById(R.id.tv_welcome_message);
//        tv_login_welcome_user = findViewById(R.id.login_tv_welcome_user);
        tv_signup_screen = findViewById(R.id.login_tv_signup);
        progressDialog = new ProgressDialog(this,R.style.greenprogress);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        error_icon=findViewById(R.id.lg_error_icon);
        error_text=findViewById(R.id.lg_error_text);
        error_layout=findViewById(R.id.ig_error_layout);

        // Skipping the login flow - Haaris 10/7/2020
        setTheme(R.style.AppTheme_NoActionBarLogin);
//        rl_login_section.startAnimation(animation_up);
//        ll_signin_section.setVisibility(View.VISIBLE);
//        ll_signin_section.startAnimation(animation_up);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        // Create an editor to modify the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserStatus", "Pass");
        // Apply the changes
        editor.apply();
        et_mail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==66)
                    et_password.requestFocus();
                return false;
            }
        });



        tv_signup_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(LoginActivity.this,SignUpActivity.class)); }
        });

//        tv_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTheme(R.style.AppTheme_NoActionBarLogin);
//                rl_login_section.startAnimation(animation_up);
//                ll_signin_section.setVisibility(View.VISIBLE);
//                ll_signin_section.startAnimation(animation_up);
//                ll_login.setVisibility(View.GONE);
//            }
//        });




        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkOperations.isNetworkAvailable(LoginActivity.this)) {
                    str_login_email = et_mail.getText().toString();
                    str_login_password = et_password.getText().toString();

                    // Put the user's email into SharedPreferences

                    if(RegexOperations.isLoginValid(str_login_email,str_login_password)) {
                        Pheezee_app_version_send();
                        repository.loginUser(str_login_email, str_login_password);
                        ReportStatusData data = new ReportStatusData(str_login_email);
                        Call<Report_count_status_data_req> report_count_status_data_reqCall = getDataService.Force_Update(data);
                        report_count_status_data_reqCall.enqueue(new Callback<Report_count_status_data_req>() {
                            @Override
                            public void onResponse(Call<Report_count_status_data_req> call, Response<Report_count_status_data_req> response) {
                                if (response.code() == 200) {
                                    Report_count_status_data_req res = response.body();
                                    String status = res.getStatus();

                                    if ("Fail".equalsIgnoreCase(status)) {
                                        editor.putString("UserStatus", "Fail");
                                        // Apply the changes
                                        editor.apply();
                                        // Handle failure
                                    } else if ("Pass".equalsIgnoreCase(status)) {
                                        editor.putString("UserStatus", "Pass");
                                        // Apply the changes
                                        editor.apply();

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Report_count_status_data_req> call, Throwable t) {
                                // Handle network call failure if needed
                            }
                        });

//                        disablePreviousView();
//                        enableWelcomeView();
                        setWelcomeText("Logging in..");
//                        dottedProgressBar.startProgress();
                    }
                    else {
//                        Toast.makeText(getApplicationContext(),"Hello World",Toast.LENGTH_LONG).show();
                        error_icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_fillcred_icon));
                        error_text.setText(RegexOperations.getNonValidMessageLogin(str_login_email,str_login_password));
                        error_layout.setVisibility(View.VISIBLE);
//                        showToast();
                    }
                }
                else {
                    error_icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_nointernert_icon));
                    error_text.setText("Please connect to internet and try again");
                    error_layout.setVisibility(View.VISIBLE);
//                    NetworkOperations.networkError(LoginActivity.this);
                }

            }
        });


        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_login_email = et_mail.getText().toString();
                if (NetworkOperations.isNetworkAvailable(LoginActivity.this)) {
                    if (!str_login_email.equalsIgnoreCase("")) {
                        setTheme(R.style.AppTheme_NoActionBar);
                        progressDialog.show();
                        repository.forgotPassword(str_login_email);
                    } else {
//                        error_text.setText("Please enter the email address!");
//                        error_layout.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Please enter the email address!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    error_icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_nointernert_icon));
                    error_text.setText("Please connect to internet and try again");
                    error_layout.setVisibility(View.VISIBLE);
//                    NetworkOperations.networkError(LoginActivity.this);
                }
            }
        });

    }

    private void setWelcomeText(String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                tv_welcome_message.setText(str);
            }
        });
    }

//    private void enableWelcomeView() {
//        ll_welcome.setVisibility(View.VISIBLE);
//        dottedProgressBar.setVisibility(View.VISIBLE);
//    }

    private void disablePreviousView() {
//        ll_signup_section.setVisibility(View.INVISIBLE);
//        ll_signin_section.setVisibility(View.INVISIBLE);
        rl_login_section.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("ResourceType")
    public void showToast(String message){

//        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }
    private void GetUpdateData(String str_login_email, String str_login_password){
        if(str_login_email.equalsIgnoreCase("") || str_login_password.equalsIgnoreCase("")){
            if(RegexOperations.isLoginValid(str_login_email,str_login_password)) {
                repository.loginUser(str_login_email, str_login_password);
//                        disablePreviousView();
//                        enableWelcomeView();
                setWelcomeText("Logging in..");
//                        dottedProgressBar.startProgress();
            }
            else {
//                        Toast.makeText(getApplicationContext(),"Hello World",Toast.LENGTH_LONG).show();
                error_icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_fillcred_icon));
                error_text.setText(RegexOperations.getNonValidMessageLogin(str_login_email,str_login_password));
                error_layout.setVisibility(View.VISIBLE);
//                        showToast();
            }
        }
        else{
            ReportStatusData data = new ReportStatusData(str_login_email);
            Call<Report_count_status_data_req> report_count_status_data_reqCall = getDataService.Force_Update(data);
            report_count_status_data_reqCall.enqueue(new Callback<Report_count_status_data_req>() {
                @Override
                public void onResponse(Call<Report_count_status_data_req> call, Response<Report_count_status_data_req> response) {
                    if (response.code() == 200) {
                        Report_count_status_data_req res=response.body();
                        Log.e("1111111111111111111111111111111111111111111111111111111111",res.getStatus());
                        String Status=res.getStatus();


                        if(Status.equalsIgnoreCase("Fail")){
                            kranthifunction();
                        }
                        if(Status.equalsIgnoreCase("Pass")){

                        }
                    }
                }

                @Override
                public void onFailure(Call<Report_count_status_data_req> call, Throwable t) {

                }
            });
        }
    }

    private void Pheezee_app_version_send() {
        if (NetworkOperations.isNetworkAvailable(LoginActivity.this)) {
            repository.updateApp_version(str_login_email,  "3.3.3");
        }
    }

    private void kranthifunction(){
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.force_update_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LinearLayout UpdateButton= dialog.findViewById(R.id.update_button);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playStoreUrl = "https://play.google.com/store/apps/details?id=com.start.apps.pheezee";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl));
                startActivity(intent);

                // Dismiss the dialog after starting the intent
                dialog.dismiss();
            }
        });
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    @Override
    public void onDetachedFromWindow() { super.onDetachedFromWindow(); }
    @Override
    public void onLoginResponse(boolean response, String message) {
        if(response){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ReportStatusData data = new ReportStatusData(str_login_email);
                    Call<Report_count_status_data_req> report_count_status_data_reqCall = getDataService.Force_Update(data);
                    report_count_status_data_reqCall.enqueue(new Callback<Report_count_status_data_req>() {
                        @Override
                        public void onResponse(Call<Report_count_status_data_req> call, Response<Report_count_status_data_req> response) {
                            if (response.code() == 200) {
                                Report_count_status_data_req res = response.body();
                                String status = res.getStatus();
                                SharedPreferences view_data_s = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                String CustomerType = view_data_s.getString("UserStatus", "");
                                Log.e("111111111111111111111111111111222222222222222222222333333333333",CustomerType);
                                Log.e("111111111111111111111111111111222222222222222222222333333333333",status);
                                if ("Fail".equalsIgnoreCase(status)) {
                                    kranthifunction();  // Handle failure
                                } else if ("Pass".equalsIgnoreCase(status)) {
//                        handleLoginSuccess( message);
                                    error_icon.setVisibility(View.GONE);
                                    error_text.setText("Welcome " + "Dr." + message);
                                    error_text.setTextColor(Color.parseColor("#012E57"));
                                    error_layout.setVisibility(View.VISIBLE);
                                    error_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_docname_view));
                                    setWelcomeText(message);
                                    setWelcomeText("Welcome");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(LoginActivity.this, PatientsView.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }, 500);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Report_count_status_data_req> call, Throwable t) {
                            // Handle network call failure if needed
                        }
                    });
                }
            },1000);
//            if(IsVarsionCorrect()){
//                error_icon.setVisibility(View.GONE);
//                error_text.setText("Welcome "+"Dr."+message);
//                error_text.setTextColor(Color.parseColor("#012E57"));
//                error_layout.setVisibility(View.VISIBLE);
//                error_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_docname_view));
//                setWelcomeText(message);
//                setWelcomeText("Welcome");
//
////                          tv_login_welcome_user.setText("Dr. "+ message);
////                          dottedProgressBar.startProgress();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent i = new Intent(LoginActivity.this, PatientsView.class);
//                        startActivity(i);
//                        finish();
////                                  dottedProgressBar.stopProgress();
//                    }
//                },500);
//            }
//            else{
//                kranthifunction();
//            }


        }
        else {
            setWelcomeText(message);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    error_icon.setVisibility(View.VISIBLE);
                    error_text.setText(message);
                    error_text.setTextColor(Color.parseColor("#CC2016"));
                    error_layout.setVisibility(View.VISIBLE);
                    error_icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_fillcred_icon));
                    error_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_error_border));
//                    disableWelcomeView();
//                    enablePreviousView();
                }
            },1000);
        }
    }

    public void checkVersionAndUpdate( String message) {
        final String[] StatusValue = new String[1];

        ReportStatusData data = new ReportStatusData(str_login_email);
        Call<Report_count_status_data_req> report_count_status_data_reqCall = getDataService.Force_Update(data);

        report_count_status_data_reqCall.enqueue(new Callback<Report_count_status_data_req>() {
            @Override
            public void onResponse(Call<Report_count_status_data_req> call, Response<Report_count_status_data_req> response) {
                if (response.code() == 200) {
                    Report_count_status_data_req res = response.body();
                    String status = res.getStatus();
                    Log.e("111111111111111111111111111111222222222222222222222333333333333",status);
                    if ("Fail".equalsIgnoreCase(status)) {
                        kranthifunction();  // Handle failure
                    } else if ("Pass".equalsIgnoreCase(status)) {
//                        handleLoginSuccess( message);
                        error_icon.setVisibility(View.GONE);
                        error_text.setText("Welcome " + "Dr." + message);
                        error_text.setTextColor(Color.parseColor("#012E57"));
                        error_layout.setVisibility(View.VISIBLE);
                        error_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.lg_docname_view));
                        setWelcomeText(message);
                        setWelcomeText("Welcome");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(LoginActivity.this, PatientsView.class);
                                startActivity(i);
                                finish();
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onFailure(Call<Report_count_status_data_req> call, Throwable t) {
                // Handle network call failure if needed
            }
        });
    }

    private void handleLoginSuccess(String message) {

    }

    @Override
    public void onForgotPasswordResponse(boolean response, String message) {
        progressDialog.dismiss();
        if(response){
            OtpBuilder builder = new OtpBuilder(this,message);
            builder.showDialog();
            builder.setOnOtpResponseListner(new OtpBuilder.OtpResponseListner() {

                @Override
                public void onResendClick() {
                    builder.dismiss();
                    progressDialog.show();
                    repository.forgotPassword(str_login_email);
                }

                @Override
                public void onPinEntery(boolean pin) {
                    if(pin){
                        builder.dismiss();
                        ForgotPasswordDialog dialog = new ForgotPasswordDialog(LoginActivity.this);
                        dialog.showDialog();
                        dialog.setOnForgotPasswordListner(new ForgotPasswordDialog.OnForgotPasswordListner() {
                            @Override
                            public void onUpdateClicked(boolean flag, String message) {
                                if(flag){
                                    dialog.dismiss();
                                    progressDialog.show();
                                    repository.updatePassword(str_login_email,message);
                                }
                                else { showToast(message); }
                            }
                        });
                    }
                    else { showToast("Invalid OTP"); }
                }
            });
        }
        else { showToast(message); }
    }

    @Override
    public void onPasswordUpdated( String message) {
        progressDialog.dismiss();
        showToast(message);
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            switch (view.getId()) {
                case R.id.login_et_email:
                    error_layout.setVisibility(View.INVISIBLE);
                    break;
                case R.id.login_et_password:
                    error_layout.setVisibility(View.INVISIBLE);
                    break;
                default:
                    error_layout.setVisibility(View.INVISIBLE);
            }

        }
        else{
            error_layout.setVisibility(View.INVISIBLE);
        }
    }
}