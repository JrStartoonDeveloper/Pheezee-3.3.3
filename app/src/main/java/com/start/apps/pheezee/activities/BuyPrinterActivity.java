package com.start.apps.pheezee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.start.apps.pheezee.pojos.BluetoothCommunication;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class BuyPrinterActivity extends AppCompatActivity {

    EditText pt_name_bx, pt_phone_bx,pt_email_bx;
    ImageView back_btr;

    Button buy_later, yes_btr;

    String pt_email, pt_name, pt_number;

    GetDataService getDataService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp<800) {
            setContentView(R.layout.buy_printer_pop_tablet);
        } else if (config.smallestScreenWidthDp>=800) {
            setContentView(R.layout.buy_printer_large_tablet);
        }else{
            setContentView(R.layout.activity_buy_printer);
        }


        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        pt_name_bx = findViewById(R.id.pt_name_e);
        pt_phone_bx = findViewById(R.id.pt_phone_e);
        pt_email_bx = findViewById(R.id.pt_email_e);
        back_btr = findViewById(R.id.iv_back_app_info);
        buy_later = findViewById(R.id.notification_ButtonCancel);
        yes_btr = findViewById(R.id.notification_ButtonOK);

        Intent intent = getIntent();
        pt_name_bx.setText(intent.getStringExtra("pt_name"));
        pt_phone_bx.setText(intent.getStringExtra("pt_number"));
        pt_email_bx.setText( intent.getStringExtra("pt_email"));
        back_btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buy_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        yes_btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pt_email = pt_email_bx.getText().toString();
                pt_name = pt_name_bx.getText().toString();
                pt_number = pt_phone_bx.getText().toString();
                BluetoothCommunication data = new BluetoothCommunication(pt_email, pt_name, pt_number);
                Call<BluetoothCommunication> bluetoothCommunicationcall2 = getDataService.bluetooth_email_con(data);
                bluetoothCommunicationcall2.enqueue(new Callback<BluetoothCommunication>() {
                                            @Override
                                            public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                                                if (response.code() == 200) {
                                                    BluetoothCommunication res = response.body();

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<BluetoothCommunication> call, Throwable t) {

                                            }
                                        });
                finish();
                Toast.makeText(BuyPrinterActivity.this, "Request sent.", Toast.LENGTH_LONG).show();
            }
        });



    }
}