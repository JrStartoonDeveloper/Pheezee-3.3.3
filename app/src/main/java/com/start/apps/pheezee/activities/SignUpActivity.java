package com.start.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import start.apps.pheezee.R;

import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;
import com.start.apps.pheezee.pojos.SignUpData;
import com.start.apps.pheezee.popup.OtpBuilder;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.utils.RegexOperations;

import java.util.ArrayList;


public class SignUpActivity extends AppCompatActivity implements MqttSyncRepository.OnSignUpResponse ,AdapterView.OnItemSelectedListener {





    //    Spinner spinner;
    EditText et_signup_name, et_signup_password, et_signup_email, et_signup_phone;

    //String to save edittexts
    String str_signup_name, str_signup_password,str_signup_email,str_signup_phone;
    String country_code="91";
    CountryCodePicker ccp;
    Button btn_signup_create;
    TextView tv_signup_cancel;
    //    TextView et_sognup_conty;
    boolean dialogStatus = false;
    MqttSyncRepository repository;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        spinner=findViewById(R.id.phoneSpinner);
//        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Countries, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(R.layout.custom_green_spinner);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        repository = new MqttSyncRepository(getApplication());
        repository.setOnSignUpResponse(this);
        //defining all the view elements
        dialogStatus = false;
        //EDIT TEXTS
        et_signup_name = findViewById(R.id.et_signup_name);
        et_signup_password = findViewById(R.id.et_signup_password);
        et_signup_email = findViewById(R.id.et_signup_email);
        et_signup_phone = findViewById(R.id.et_signup_phone);
//        et_sognup_conty = findViewById(R.id.number);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        progressDialog = new ProgressDialog(this,R.style.greenprogress);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        //Buttons
        btn_signup_create  = findViewById(R.id.btn_signup_create);
        //TextViews
        tv_signup_cancel = findViewById(R.id.login);

//        View.OnClickListener listener=new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new CountDownTimer(4000,100){
//
//                    @Override
//                    public void onTick(long l) {
//                        ViewGroup.LayoutParams params=text.getLayoutParams();
//
//                            params.width/=1.1;
//                            params.height*=1.1;
//
//                        text.setLayoutParams(params);
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                    }
//                }.start();
//            }
//        };
//        grow.setOnClickListener(listener);
        /**
         * create account
         */

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                country_code = selectedCountry.getPhoneCode();
//                Toast.makeText(getApplicationContext(),country_code, Toast.LENGTH_SHORT).show();
            }
        });
        btn_signup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_signup_name = et_signup_name.getText().toString();
                str_signup_password = et_signup_password.getText().toString();
                str_signup_email = et_signup_email.getText().toString();
                str_signup_phone = et_signup_phone.getText().toString();
                if(RegexOperations.isSignupValid(str_signup_name,str_signup_email,str_signup_password,str_signup_phone)){
                    progressDialog.show();
                    repository.confirmEmail(str_signup_email);
                }
                else {
                    showToast(RegexOperations.getNonValidMessageSignup(str_signup_name,str_signup_email,str_signup_password,str_signup_phone));
                }
            }
        });
        tv_signup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_redirectlogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent_redirectlogin);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfirmEmail(boolean response, String message) {
        progressDialog.dismiss();
        if(response){
            OtpBuilder builder = new OtpBuilder(this,message);
            builder.showDialog();
            builder.setOnOtpResponseListner(new OtpBuilder.OtpResponseListner() {
                @Override
                public void onResendClick() {
                    builder.dismiss();
                    progressDialog.show();
                    repository.confirmEmail(str_signup_email);
                }

                @Override
                public void onPinEntery(boolean pin) {
                    if(pin){
                        progressDialog.show();
                        if(country_code==null)
                            country_code="91";
//                        Log.e("country_code",country_code);
                        String number="+"+country_code+"-"+str_signup_phone;
                        SignUpData data = new SignUpData(str_signup_name,str_signup_email,str_signup_password,
                                number,"empty", new ArrayList<>());
                        repository.signUp(data);
                    }
                    else {
                        showToast("Invalid OTP!");
                    }
                }
            });
        }
        else {
            showToast(message);
        }
    }

    @Override
    public void onSignUp(boolean response) {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
        if(!response){
            showToast("Error, try again later");
        }
        else {
            Intent i = new Intent(SignUpActivity.this, PatientsView.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//       String text=adapterView.getItemAtPosition(i).toString();
//       String[] data= text.split(" ",2);
//       int pos=data.length;
//        et_sognup_conty.setText(data[pos-1]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}

