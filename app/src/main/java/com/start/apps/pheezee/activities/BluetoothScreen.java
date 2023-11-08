package com.start.apps.pheezee.activities;


import static start.apps.pheezee.R.id.iv_back_app_info;
import static start.apps.pheezee.R.id.scanning_printer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.clans.fab.FloatingActionButton;
import com.start.apps.pheezee.pojos.BluetoothCommunication;
import com.start.apps.pheezee.pojos.PremiumPopupData;
import com.start.apps.pheezee.pojos.ReportStatusData;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

public class BluetoothScreen extends AppCompatActivity implements OpenBluetoothSocketTask.SocketConnectionListener {

    private static final int PERMISSION_BLUETOOTH = 25;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 26;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 27;
    private static final int PERMISSION_BLUETOOTH_SCAN = 28;

    private static final int  REQUEST_ENABLE_BT = 29;

//    ProgressDialog report_dialog;

    Button print_thermal;
    File pdfFile;

    TextView print_button, print_button_dim;
    PDFView pdfView;
    Bitmap[] bitmaps;

    Bitmap resizedBitmap;

    String path;

    PopupWindow popup;

    Bitmap decodedByte;

    private int progress = 5;


    FloatingActionButton floatingActionButton  ;

    Button printer_icon_ct,printer_thermal_icon_ct_disconnect,printer_thermal_share_ct,printer_thermal_icon_ct_connect,printer_thermal_icon_ct_reprint;

    LinearLayout A_Four_Printer_Layout;
    SwipeRefreshLayout swipeRefreshLayout;

    androidx.constraintlayout.widget.ConstraintLayout layout_status;


    PdfRenderer renderer = null;

    Runnable runnable;
    Handler handler;

    String pt_email, pt_name , pt_number;

    GetDataService getDataService;

    EscPosPrinter printer_th;

    boolean loopCompleted = false;
    boolean bluetooth_status = false;

    boolean connection_status = false;
    LinearLayout linear_layout_1_ct, linear_layout_2_ct;

    Button detailed_btn_1_ct, summary_btn_1_ct,detailed_btn_2_ct, summary_btn_2_ct;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp<=750) {
            setContentView(R.layout.activity_bluetooth_screen_tablet);
        } else if(config.smallestScreenWidthDp>750) {
            setContentView(R.layout.activity_bluetooth_screen_tablet_large);
        }
        else{
            setContentView(R.layout.activity_bluetooth_screen);
        }
        linear_layout_1_ct = findViewById(R.id.linear_layout_1);
        linear_layout_2_ct = findViewById(R.id.linear_layout_2);
        detailed_btn_1_ct = findViewById(R.id.detailed_btn_1);
        detailed_btn_2_ct = findViewById(R.id.detailed_btn_2);
        summary_btn_1_ct = findViewById(R.id.summary_btn_1);
        summary_btn_2_ct = findViewById(R.id.summary_btn_2);
        print_button = findViewById(R.id.button_bluetooth);
        print_button_dim = findViewById(R.id.button_bluetooth_disable);
        pdfView = findViewById(R.id.pdfView);
        ImageView button1 = findViewById(iv_back_app_info);
        print_thermal = findViewById(R.id.print_thermal);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        floatingActionButton = findViewById(R.id.menu_delete);
        printer_icon_ct = findViewById(R.id.printer_icon);
        A_Four_Printer_Layout=findViewById(R.id.afourprinter_layout);
        printer_thermal_icon_ct_connect = findViewById(R.id.printer_thermal_icon_connected);
        printer_thermal_icon_ct_disconnect = findViewById(R.id.printer_thermal_icon_disconnected);
        printer_thermal_share_ct = findViewById(R.id.printer_thermal_share);
        printer_thermal_icon_ct_reprint=findViewById(R.id.printer_thermal_icon_reprint);
        layout_status = findViewById(R.id.layout_id);
        layout_status.setVisibility(View.GONE);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        new_pheezee_printer();
        printer_thermal_icon_ct_connect.setVisibility(View.GONE);




        button1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent patientId = getIntent();
        String patientid = patientId.getStringExtra("patientId");
        Intent date = getIntent();
        String date_value =  date.getStringExtra("date");
        Intent phizioemail = getIntent();
        pt_email = phizioemail.getStringExtra("phizioemail");
        Intent status_value = getIntent();
        String count_status = status_value.getStringExtra("count_status");
        Log.e("111111111111111111111111111111111111",count_status);
        Log.e("111111111111111111111111111111111111",pt_email);
        if(count_status.equalsIgnoreCase("true")){
            ReportStatusData data = new ReportStatusData(pt_email);
            Call<Report_count_status_data_req> report_count_status_data_reqCall = getDataService.Report_Status(data);
            report_count_status_data_reqCall.enqueue(new Callback<Report_count_status_data_req>() {
                @Override
                public void onResponse(Call<Report_count_status_data_req> call, Response<Report_count_status_data_req> response) {

                }

                @Override
                public void onFailure(Call<Report_count_status_data_req> call, Throwable t) {

                }
            });

        }




        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.progress_bar_notification);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        ProgressBar mCustomProgressBar  = dialog.findViewById(R.id.CustomProgressBar);
        TextView fact_text = dialog.findViewById(R.id.fact_text);
        ImageView cross_button = dialog.findViewById(R.id.cross_button);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                String[] myStringArray = {"Checking joint ROM helps assess flexibility and find ways to improve.", "EMG can identify muscle weakness and imbalances in patients.", "Joint ROM exercises can improve mobility, flexibility, and prevent problems.", "EMG can show patients how to activate muscles correctly during exercises.", "Joint ROM exercises can help prevent injuries and improve sports performance."};
                Random random = new Random();
                int randomIndex = random.nextInt(myStringArray.length);
                String randomValue = myStringArray[randomIndex];
                fact_text.setText(randomValue.toString());
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(r, 1000);

        handler = new Handler();

        final Runnable bt_check = new Runnable() {
            public void run() {
                if(connection_status == true) {
//                    pheezee_printer();
//                    BluetoothPrintersConnections.selectFirstPaired();
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(bt_check, 1000);









        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getApplicationContext().getExternalFilesDir(null)+"/Pheezee", patientid+date_value+".pdf");
                Uri fileUri = FileProvider.getUriForFile(BluetoothScreen.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(shareIntent, "Share file"));
            }
        });

        printer_icon_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf");
                Uri fileUri = FileProvider.getUriForFile(BluetoothScreen.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(shareIntent, "Printer"));
            }
        });

        printer_thermal_share_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf");
                Uri fileUri = FileProvider.getUriForFile(BluetoothScreen.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(shareIntent, "Printer"));
            }
        });

        detailed_btn_1_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_1_ct.setVisibility(View.VISIBLE);
                linear_layout_2_ct.setVisibility(View.GONE);
                printer_icon_ct.setVisibility(View.VISIBLE);
                A_Four_Printer_Layout.setVisibility(View.VISIBLE);
                printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                printer_thermal_share_ct.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);

                pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf"))
                        .load();
            }
        });

        summary_btn_1_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_1_ct.setVisibility(View.GONE);
                linear_layout_2_ct.setVisibility(View.VISIBLE);
                printer_icon_ct.setVisibility(View.GONE);
                A_Four_Printer_Layout.setVisibility(View.GONE);
                printer_thermal_share_ct.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
                if (bluetooth_status == true){
                    if (printer_thermal_icon_ct_reprint.getVisibility() == View.VISIBLE){
                        printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                        printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                    }else{
                        printer_thermal_icon_ct_connect.setVisibility(View.VISIBLE);
                        printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                    }
                }else{
                    printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                    printer_thermal_icon_ct_disconnect.setVisibility(View.VISIBLE);
                }

                pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"))
                        .load();
            }
        });

        detailed_btn_2_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_1_ct.setVisibility(View.VISIBLE);
                linear_layout_2_ct.setVisibility(View.GONE);
                printer_icon_ct.setVisibility(View.VISIBLE);
                A_Four_Printer_Layout.setVisibility(View.VISIBLE);
                printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                printer_thermal_share_ct.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
                pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf"))
                        .load();
                if (popup != null) {
                    popup.dismiss();
                }
            }
        });

        summary_btn_2_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printer_thermal_share_ct.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
                linear_layout_1_ct.setVisibility(View.GONE);
                linear_layout_2_ct.setVisibility(View.VISIBLE);
                printer_icon_ct.setVisibility(View.GONE);
                A_Four_Printer_Layout.setVisibility(View.GONE);
                if (bluetooth_status == true){
                    if (printer_thermal_icon_ct_reprint.getVisibility() == View.VISIBLE){
                        printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                        printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                    }else{
                        printer_thermal_icon_ct_connect.setVisibility(View.VISIBLE);
                        printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                    }

                }else{
                    printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                    printer_thermal_icon_ct_disconnect.setVisibility(View.VISIBLE);
                }
                pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"))
                        .load();
//                openPlayStoreLink();
               
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bluetooth_status = false;
//                new_pheezee_printer();
//                print_button.setVisibility(View.VISIBLE);
//                print_button_dim.setVisibility(View.GONE);
                File file = new File(getApplicationContext().getExternalFilesDir(null)+"/Pheezee", patientid+date_value+".pdf");
                if(file.exists()){
                    pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf"))
                            .load();
                    linear_layout_1_ct.setVisibility(View.VISIBLE);
                    linear_layout_2_ct.setVisibility(View.GONE);
                    printer_icon_ct.setVisibility(View.VISIBLE);
                    A_Four_Printer_Layout.setVisibility(View.VISIBLE);
                    printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                    printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                    printer_thermal_share_ct.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                }else{
                    Toast.makeText(BluetoothScreen.this, "Please refresh the screen", Toast.LENGTH_LONG).show();
                }
                if(popup != null) {
                    popup.dismiss();
                }
//                Toast.makeText(BluetoothScreen.this, "Working Refresh", Toast.LENGTH_LONG).show();
                // This line is important as it explicitly
                // refreshes only once
                // If "true" it implicitly refreshes forever
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        cross_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });











        new CountDownTimer(35000, 1000) {
            @Override
            public void onTick(long l) {
                BluetoothCommunication data = new BluetoothCommunication(pt_email, pt_name, pt_number);

                Call<BluetoothCommunication> bluetoothCommunicationCall = getDataService.bluetooth_data_fetch(data);
                bluetoothCommunicationCall.enqueue(new Callback<BluetoothCommunication>() {
                    @Override
                    public void onResponse(Call<BluetoothCommunication> call, Response<BluetoothCommunication> response) {
                        if (response.code() == 200) {
                            BluetoothCommunication res = response.body();
                            pt_email = res.getPhizioemail();
                            pt_name = res.getName();
                            pt_number =  res.getPhone();
                        }

                    }


                    @Override
                    public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                    }
                });



                progress++;
                mCustomProgressBar.setProgress((int) progress * 100 / (35000 / 1000));
                Drawable draw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_progressbar);
                mCustomProgressBar.setProgressDrawable(draw);


            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFinish() {
                layout_status.setVisibility(View.VISIBLE);
                dialog.dismiss();
//                    pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf"))
//                            .load();
                File file = new File(getApplicationContext().getExternalFilesDir(null)+"/Pheezee", patientid+date_value+".pdf");
                if(file.exists()){
                    pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf"))
                            .load();
                }else{
                    Toast.makeText(BluetoothScreen.this, "Warning! Low Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        }.start();






//        }
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print_button.setVisibility(View.VISIBLE);
                print_button_dim.setVisibility(View.GONE);
                if(popup != null) {
                    popup.dismiss();
                }
            }
        });

        printer_thermal_icon_ct_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the PDF file
                final Dialog dialog_dp = new Dialog(BluetoothScreen.this);
                dialog_dp.setContentView(R.layout.dialog_progress);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog_dp.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog_dp.getWindow().setAttributes(lp);
                dialog_dp.setCanceledOnTouchOutside(false);
                dialog_dp.show();
                loopCompleted = false;


                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                        pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"))
                                .load();
                        print_button.setVisibility(View.GONE);
                        print_button_dim.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onFinish() {


                        try {
                            renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"), ParcelFileDescriptor.MODE_READ_ONLY));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // Create an array to hold the bitmaps
                        Bitmap[] bitmaps = new Bitmap[renderer.getPageCount()];
                        int i ;
                        // Render each page into a bitmap
                        for (i = 0; i < renderer.getPageCount(); i++) {
                            PdfRenderer.Page page = renderer.openPage(i);
                            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                            Paint paint = new Paint();
                            paint.setColorFilter(filter);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawBitmap(bitmap, 0, 0, paint);
                            bitmaps[i] = bitmap;
                            decodedByte = bitmap;

                            try {
                                printer_th = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 70f, 48);
                                int width = decodedByte.getWidth(), height = decodedByte.getHeight();
                                StringBuilder textToPrint = new StringBuilder();
                                for (int y = 0; y < height; y += 256) {
                                    resizedBitmap = Bitmap.createBitmap(decodedByte, 0, y, width, (y + 256 >= height) ? height - y : 256);
                                    textToPrint.append("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer_th, resizedBitmap) + "</img>\n");
                                }
                                printer_th.printFormattedTextAndCut(textToPrint.toString());
                                decodedByte.recycle();
                                page.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        loopCompleted = true;
                        // Clean up resources
                        renderer.close();



                    }
                }.start();

                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if(loopCompleted){
                            print_button.setVisibility(View.VISIBLE);
                            print_button_dim.setVisibility(View.GONE);
                            dialog_dp.dismiss();
                            printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                            printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                            printer_thermal_icon_ct_reprint.setVisibility(View.VISIBLE);
                            printer_th.disconnectPrinter();
                        }

                        handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(r, 1000);

            }
        });

        printer_thermal_icon_ct_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the PDF file
                final Dialog dialog_dp = new Dialog(BluetoothScreen.this);
                dialog_dp.setContentView(R.layout.dialog_progress);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog_dp.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog_dp.getWindow().setAttributes(lp);
                dialog_dp.setCanceledOnTouchOutside(false);
                dialog_dp.show();
                loopCompleted = false;


                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                        pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"))
                                .load();
                        print_button.setVisibility(View.GONE);
                        print_button_dim.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onFinish() {


                        try {
                            renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"), ParcelFileDescriptor.MODE_READ_ONLY));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // Create an array to hold the bitmaps
                        Bitmap[] bitmaps = new Bitmap[renderer.getPageCount()];
                        int i ;
                        // Render each page into a bitmap
                        for (i = 0; i < renderer.getPageCount(); i++) {
                            PdfRenderer.Page page = renderer.openPage(i);
                            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                            Paint paint = new Paint();
                            paint.setColorFilter(filter);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawBitmap(bitmap, 0, 0, paint);
                            bitmaps[i] = bitmap;
                            decodedByte = bitmap;

                            try {
                                printer_th = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 70f, 48);
                                int width = decodedByte.getWidth(), height = decodedByte.getHeight();
                                StringBuilder textToPrint = new StringBuilder();
                                for (int y = 0; y < height; y += 256) {
                                    resizedBitmap = Bitmap.createBitmap(decodedByte, 0, y, width, (y + 256 >= height) ? height - y : 256);
                                    textToPrint.append("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer_th, resizedBitmap) + "</img>\n");
                                }
                                printer_th.printFormattedTextAndCut(textToPrint.toString());
                                decodedByte.recycle();
                                page.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        loopCompleted = true;
                        // Clean up resources
                        renderer.close();



                    }
                }.start();

                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if(loopCompleted){
                            print_button.setVisibility(View.VISIBLE);
                            print_button_dim.setVisibility(View.GONE);
                            dialog_dp.dismiss();
                            printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                            printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                            printer_thermal_icon_ct_reprint.setVisibility(View.VISIBLE);
                            printer_th.disconnectPrinter();
                        }

                        handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(r, 1000);

            }
        });





        printer_thermal_icon_ct_disconnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
//                print_button.setVisibility(View.GONE);
//                print_button_dim.setVisibility(View.VISIBLE);
                if (adapter.isEnabled()) {
                    if (popup != null) {
                        popup.dismiss();
                    }


                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(BluetoothScreen.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BluetoothScreen.this, new String[]{Manifest.permission.BLUETOOTH}, BluetoothScreen.PERMISSION_BLUETOOTH);
                    } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(BluetoothScreen.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BluetoothScreen.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BluetoothScreen.PERMISSION_BLUETOOTH_ADMIN);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(BluetoothScreen.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BluetoothScreen.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BluetoothScreen.PERMISSION_BLUETOOTH_CONNECT);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(BluetoothScreen.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BluetoothScreen.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BluetoothScreen.PERMISSION_BLUETOOTH_SCAN);
                    }
                    else {
                        if (bluetooth_status == true){

//                            if (BluetoothPrintersConnections.selectFirstPaired() != null) {
                            popup = new PopupWindow(BluetoothScreen.this);
                            View customView = LayoutInflater.from(BluetoothScreen.this).inflate(R.layout.printerlayour, null);
                            popup.setContentView(customView);
                            int location[] = new int[2];
                            int x = location[0] + customView.getWidth();
                            int y = location[1] + 50; // add 50 pixels from the top
                            popup.showAtLocation(customView, Gravity.TOP | Gravity.RIGHT, x, 250);

                            LinearLayout thermal_printer = customView.findViewById(R.id.thermal_printer);

                            LinearLayout printer = customView.findViewById(R.id.other_printer);
                            printer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    print_button.setVisibility(View.VISIBLE);
                                    print_button_dim.setVisibility(View.GONE);
                                    new CountDownTimer(2000, 1000) {
                                        @Override
                                        public void onTick(long l) {
                                            pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf"))
                                                    .load();
                                        }

                                        @Override
                                        public void onFinish() {
                                            popup.dismiss();
                                            File file = new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf");
                                            Uri fileUri = FileProvider.getUriForFile(BluetoothScreen.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file);
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setType("application/pdf");
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                            startActivity(Intent.createChooser(shareIntent, "Share file"));
                                        }
                                    }.start();

                                }
                            });
                            thermal_printer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Open the PDF file
                                    final Dialog dialog_dp = new Dialog(BluetoothScreen.this);
                                    dialog_dp.setContentView(R.layout.dialog_progress);
                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog_dp.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    dialog_dp.getWindow().setAttributes(lp);
                                    dialog_dp.setCanceledOnTouchOutside(false);
                                    dialog_dp.show();
                                    loopCompleted = false;


                                    new CountDownTimer(2000, 1000) {
                                        @Override
                                        public void onTick(long l) {

                                            pdfView.fromFile(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"))
                                                    .load();
                                            print_button.setVisibility(View.GONE);
                                            print_button_dim.setVisibility(View.VISIBLE);

                                        }

                                        @Override
                                        public void onFinish() {

                                            popup.dismiss();
                                            try {
                                                renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"), ParcelFileDescriptor.MODE_READ_ONLY));
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            // Create an array to hold the bitmaps
                                            Bitmap[] bitmaps = new Bitmap[renderer.getPageCount()];
                                            int i ;
                                            // Render each page into a bitmap
                                            for (i = 0; i < renderer.getPageCount(); i++) {
                                                PdfRenderer.Page page = renderer.openPage(i);
                                                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                                                ColorMatrix matrix = new ColorMatrix();
                                                matrix.setSaturation(0);
                                                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                                                Paint paint = new Paint();
                                                paint.setColorFilter(filter);
                                                Canvas canvas = new Canvas(bitmap);
                                                canvas.drawBitmap(bitmap, 0, 0, paint);
                                                bitmaps[i] = bitmap;
                                                decodedByte = bitmap;

                                                try {
                                                    printer_th = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 70f, 48);
                                                    int width = decodedByte.getWidth(), height = decodedByte.getHeight();
                                                    StringBuilder textToPrint = new StringBuilder();
                                                    for (int y = 0; y < height; y += 256) {
                                                        resizedBitmap = Bitmap.createBitmap(decodedByte, 0, y, width, (y + 256 >= height) ? height - y : 256);
                                                        textToPrint.append("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer_th, resizedBitmap) + "</img>\n");
                                                    }
                                                    printer_th.printFormattedTextAndCut(textToPrint.toString());
                                                    decodedByte.recycle();
                                                    page.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            loopCompleted = true;
                                            // Clean up resources
                                            renderer.close();



                                        }
                                    }.start();

                                    handler = new Handler();

                                    final Runnable r = new Runnable() {
                                        public void run() {
                                            if(loopCompleted){
                                                print_button.setVisibility(View.VISIBLE);
                                                print_button_dim.setVisibility(View.GONE);
                                                dialog_dp.dismiss();
                                                printer_th.disconnectPrinter();
                                            }

                                            handler.postDelayed(this, 1000);
                                        }
                                    };

                                    handler.postDelayed(r, 1000);

                                }
                            });

//                            }
                        }
                        else if (bluetooth_status == false) {
                            popup = new PopupWindow(BluetoothScreen.this);
                            View customView = LayoutInflater.from(BluetoothScreen.this).inflate(R.layout.buyprinter, null);
                            popup.setContentView(customView);
                            int location[] = new int[2];
                            int x = location[0] + customView.getWidth();

                            int y = location[1] + 50; // add 50 pixels from the top
                            popup.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER, x, 500);
                            LinearLayout scan_layout_test = customView.findViewById(R.id.scan_layout_test);
                            LinearLayout scanning_layout_test = customView.findViewById(R.id.scanning_printer);
                            ImageView progressBar_testing_1 = customView.findViewById(R.id.progressBar_testing_1);
                            ProgressBar progressBar_testing_2 = customView.findViewById(R.id.progressBar_testing_2);
                            TextView scan_th_device = customView.findViewById(R.id.scan_th_device);
                            TextView buy_now_ptr = customView.findViewById(R.id.buy_now_ptr);
                            LinearLayout other_printer = customView.findViewById(R.id.other_printer);
                            scan_layout_test.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new_pheezee_printer();
                                    scan_layout_test.setVisibility(View.GONE);
                                    scanning_layout_test.setVisibility(View.VISIBLE);
                                    scan_th_device.setText("Scanning");
                                    new CountDownTimer(10000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            if (bluetooth_status == true){
                                                printer_thermal_icon_ct_connect.setVisibility(View.VISIBLE);
                                                printer_thermal_icon_ct_disconnect.setVisibility(View.GONE);
                                                if (popup != null) {
                                                    popup.dismiss();
                                                }
                                            }else{
                                                printer_thermal_icon_ct_connect.setVisibility(View.GONE);
                                                printer_thermal_icon_ct_disconnect.setVisibility(View.VISIBLE);
                                                scan_layout_test.setVisibility(View.VISIBLE);
                                                scanning_layout_test.setVisibility(View.GONE);
                                                scan_th_device.setText("Scan");
                                            }
                                        }
                                    }.start();

                                }
                            });

                            buy_now_ptr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    print_button.setVisibility(View.VISIBLE);
                                    print_button_dim.setVisibility(View.GONE);
                                    popup.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), BuyPrinterActivity.class);
                                    intent.putExtra("pt_name", pt_name);
                                    intent.putExtra("pt_number", pt_number);
                                    intent.putExtra("pt_email", pt_email);
                                    startActivity(intent);


                                }
                            });
                            other_printer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popup.dismiss();

                                    print_button.setVisibility(View.VISIBLE);
                                    print_button_dim.setVisibility(View.GONE);
                                    File file = new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + ".pdf");
                                    Uri fileUri = FileProvider.getUriForFile(BluetoothScreen.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file);
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("application/pdf");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                    startActivity(Intent.createChooser(shareIntent, "Print File"));
                                }
                            });
                        }
                    }

                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }


            }

        });





    }

    private void openPlayStoreLink() {
        // Example Play Store link
        String playStoreLink = "https://play.google.com/store/apps/details?id=com.example.app";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));

        // Check if the Play Store app is installed
        PackageManager packageManager = getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
        }
    }

    @SuppressLint("MissingPermission")
    private void new_pheezee_printer(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {

        }
        BluetoothTask bluetoothTask = new BluetoothTask();
        bluetoothTask.execute();

    }







    @SuppressLint("MissingPermission")
    private void pheezee_printer() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            // Handle the error or notify the user accordingly
        } else {
            // Bluetooth is supported
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        BluetoothDevice targetPrinter = null;
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("RSTRADING") || device.getName().equals("MPT-III") || device.getName().equals("Pheezee Printer")) {
                targetPrinter = device;
                break;
            }
        }




        if (targetPrinter != null) {

            String deviceAddress = targetPrinter.getAddress();
            UUID uuid = targetPrinter.getUuids()[0].getUuid();
            connectToBluetoothPrinter(deviceAddress,uuid);
            // Bluetooth printer is available
            // Proceed with the connection using connections.selectFirstPaired() method
        } else {

            // Bluetooth printer is not available
            // Handle the error or notify the user accordingly
        }


    }







    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void connectToBluetoothPrinter(String deviceAddressinfo,UUID uuiddata) {
        String deviceAddress = deviceAddressinfo; // Replace with the actual device address
        UUID uuid = uuiddata; // Replace with the actual UUID

        OpenBluetoothSocketTask task = new OpenBluetoothSocketTask(deviceAddress, uuid, this);
        task.execute();
    }


    @Override
    public void onSocketConnectionResult(boolean isConnected, BluetoothSocket socket) {
        if (isConnected) {
            bluetooth_status = true;
            connection_status = true;

        } else {
            bluetooth_status = false;
        }
    }


    private class BluetoothTask extends AsyncTask<Void, Void, BluetoothConnection> {

        @Override
        protected BluetoothConnection doInBackground(Void... params) {
            BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
            BluetoothConnection[] bluetoothPrinters = printers.getList();

            if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
                for (BluetoothConnection printer : bluetoothPrinters) {
                    try {
                        return printer.connect();
                    } catch (EscPosConnectionException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }





//            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//                @SuppressLint("MissingPermission")
//                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//                if (pairedDevices.size() > 0) {
//                    try {
//                        BluetoothConnection printer = BluetoothPrintersConnections.selectFirstPaired();
//                        // Proceed with printing or other operations
//                        if(printer.equals("Unable to connect to bluetooth device.")){
//                            Toast.makeText(BluetoothScreen.this, "222222222222222222222", Toast.LENGTH_LONG).show();
//                        }
//                        Toast.makeText(BluetoothScreen.this, "111111111111111111111111111", Toast.LENGTH_LONG).show();
////                        return 0;
//                    } catch (Exception e) {
//                        // Handle the exception (e.g., display an error message)
//                        return null;
//                    }
//
//                }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(BluetoothConnection device) {
            if (device != null) {
                // Do something with the Bluetooth device
                bluetooth_status = true;

            } else {
                bluetooth_status = false;
                // Handle case when no paired devices are available
            }
        }
    }

}