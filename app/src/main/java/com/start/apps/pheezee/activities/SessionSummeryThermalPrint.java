package com.start.apps.pheezee.activities;

import static com.facebook.FacebookSdk.getApplicationContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import start.apps.pheezee.R;

public class SessionSummeryThermalPrint extends AppCompatActivity {
    PdfRenderer renderer = null;
    Bitmap decodedByte;
    EscPosPrinter printer_th;
    Bitmap resizedBitmap;
    PopupWindow popup;
    private static final int PERMISSION_BLUETOOTH = 25;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 26;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 27;
    private static final int PERMISSION_BLUETOOTH_SCAN = 28;

    private static final int REQUEST_ENABLE_BT = 1;

    boolean bluetooth_status=false;
    boolean loopCompleted = false;
    FloatingActionButton floatingActionButtonConnected,floatingActionButtonPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summery_thermal_print);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        floatingActionButtonConnected = findViewById(R.id.thermal_printer_conected);
        floatingActionButtonPrint = findViewById(R.id.thermal_printer_not_connected);


        floatingActionButtonConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog_dp = new Dialog(SessionSummeryThermalPrint.this);
                dialog_dp.setContentView(R.layout.dialog_progress);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog_dp.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog_dp.getWindow().setAttributes(lp);
                dialog_dp.setCanceledOnTouchOutside(false);
                dialog_dp.show();
                File file = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
                Uri fileUri = FileProvider.getUriForFile(SessionSummeryThermalPrint.this, SessionSummeryThermalPrint.this.getPackageName() + ".my.package.name.provider", file);


                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        try {
                            ParcelFileDescriptor parcelFileDescriptor = null;
                            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                            renderer = new PdfRenderer(parcelFileDescriptor);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // Create an array to hold the bitmaps
                        Bitmap[] bitmaps = new Bitmap[renderer.getPageCount()];
                        int i;
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

                Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (loopCompleted) {
                            dialog_dp.dismiss();
                            printer_th.disconnectPrinter();
                            finish();
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(r, 1000);

            }
        });

        floatingActionButtonPrint.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
//                print_button.setVisibility(View.GONE);
//                print_button_dim.setVisibility(View.VISIBLE);
                if (adapter.isEnabled()) {
                    if (popup != null) {
                        popup.dismiss();
                    }


                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(SessionSummeryThermalPrint.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SessionSummeryThermalPrint.this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                    } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(SessionSummeryThermalPrint.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SessionSummeryThermalPrint.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(SessionSummeryThermalPrint.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SessionSummeryThermalPrint.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(SessionSummeryThermalPrint.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SessionSummeryThermalPrint.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
                    } else {
                        if (bluetooth_status == true) {
                            floatingActionButtonConnected.setVisibility(View.VISIBLE);
                            floatingActionButtonPrint.setVisibility(View.GONE);

                        } else if (bluetooth_status == false) {
                            popup = new PopupWindow(SessionSummeryThermalPrint.this);
                            View customView = LayoutInflater.from(SessionSummeryThermalPrint.this).inflate(R.layout.buyprinter, null);
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
                                            if (bluetooth_status == true) {
                                                floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                                floatingActionButtonPrint.setVisibility(View.GONE);
                                                if (popup != null) {
                                                    popup.dismiss();
                                                }
                                            } else {
                                                floatingActionButtonConnected.setVisibility(View.GONE);
                                                floatingActionButtonPrint.setVisibility(View.VISIBLE);
                                                scan_layout_test.setVisibility(View.VISIBLE);
                                                scanning_layout_test.setVisibility(View.GONE);
                                                scan_th_device.setText("Scan");
                                            }
                                        }
                                    }.start();

                                }
                            });

//                            buy_now_ptr.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    popup.dismiss();
//                                    Intent intent = new Intent(getApplicationContext(), BuyPrinterActivity.class);
//                                    intent.putExtra("pt_name", pt_name);
//                                    intent.putExtra("pt_number", pt_number);
//                                    intent.putExtra("pt_email", pt_email);
//                                    startActivity(intent);
//
//
//                                }
//                            });
                            other_printer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popup.dismiss();
                                    File file = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
                                    Uri fileUri = FileProvider.getUriForFile(SessionSummeryThermalPrint.this, SessionSummeryThermalPrint.this.getPackageName() + ".my.package.name.provider", file);
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("application/pdf");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                    SessionSummeryThermalPrint.this.startActivity(Intent.createChooser(shareIntent, "Share file"));
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
    private void new_pheezee_printer(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {

        }
        SessionSummeryThermalPrint.BluetoothTask bluetoothTask = new SessionSummeryThermalPrint.BluetoothTask();
        bluetoothTask.execute();

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