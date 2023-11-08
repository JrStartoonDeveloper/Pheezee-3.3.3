package com.start.apps.pheezee.popup;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;
import start.apps.pheezee.R;

import com.start.apps.pheezee.activities.BluetoothScreen;
import com.start.apps.pheezee.activities.BuyPrinterActivity;
import com.start.apps.pheezee.activities.MonitorActivity;
import com.start.apps.pheezee.activities.OpenBluetoothSocketTask;
import com.start.apps.pheezee.activities.PremiumSubscriptionDataRec;
import com.start.apps.pheezee.activities.SessionReportActivity;
//import com.start.apps.pheezee.activities.SessionSummeryPrint;
//import com.start.apps.pheezee.activities.SessionSummeryThermalPrint;
import com.start.apps.pheezee.classes.PatientActivitySingleton;
import com.start.apps.pheezee.pojos.BluetoothCommunication;
import com.start.apps.pheezee.pojos.DeleteSessionData;
import com.start.apps.pheezee.pojos.MmtData;
import com.start.apps.pheezee.pojos.PatientStatusData;
import com.start.apps.pheezee.pojos.PhizioSessionReportData;
import com.start.apps.pheezee.pojos.PremiumSubscriptionUserData;
import com.start.apps.pheezee.pojos.SessionData;
import com.start.apps.pheezee.pojos.SessionSummeryOppositesideTableData;
import com.start.apps.pheezee.pojos.SessionSummeryTableData;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.room.Entity.MqttSync;
import com.start.apps.pheezee.room.PheezeeDatabase;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.TakeScreenShot;
import com.start.apps.pheezee.utils.ValueBasedColorOperations;
import com.start.apps.pheezee.views.ArcViewInside;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.start.apps.pheezee.activities.MonitorActivity.IS_SCEDULED_SESSIONS_COMPLETED;
import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

public class SessionSummaryPopupWindow{
    private String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    private String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    private String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    private static final int PERMISSION_BLUETOOTH = 25;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 26;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 27;
    private static final int PERMISSION_BLUETOOTH_SCAN = 28;

    private static final int REQUEST_ENABLE_BT = 1;
    String oppositeside;
    String pt_email, pt_name , pt_number;
    String Healthyside;
    LinearLayout RegularDegreeRemaining, PremiumDegreeRemaining1, PremiunDegreeRemaining2;
    GetDataService getDataService;
    Handler handler;
    boolean bluetooth_status = false;
    boolean loopCompleted = false;
    boolean HaveEMGNormatives = true;
    Bitmap decodedByte;
    EscPosPrinter printer_th;
    Bitmap resizedBitmap;
    PdfRenderer renderer = null;
    private boolean session_inserted_in_server = false;
    JSONArray emgJsonArray, romJsonArray;
    int phizio_packagetype;
    private String dateString;
    private Context context;
    private PopupWindow report;

    PopupWindow popup;

    private int maxEmgValue, maxAngle, minAngle, angleCorrection, exercise_selected_position, body_part_selected_position, repsselected, hold_angle_session;
    private String sessionNo, mmt_selected = "", orientation, bodypart, phizioemail, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, body_orientation = "", session_type = "", dateofjoin, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected, max_emg_selected, therapist_name = "", patient_status = "", pain_status = "", comment_session = "";
    private String bodyOrientation = "";
    private MqttSyncRepository repository;
    private MqttSyncRepository.OnSessionDataResponse response_data;
    private Long tsLong;
    ;
    String Healthy_Side = "", BodyPart;
    String[] Data4 = {};
    String[] Data5 = {};

    public SessionSummaryPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps, int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected, int hold_angle_session, String mmt_selected, String therapist_name, String patient_status, String pain_status, String session_type, String comment_session) {
        this.context = context;
        this.maxEmgValue = maxEmgValue;
        this.sessionNo = sessionNo;
        this.maxAngle = maxAngle;
        this.minAngle = minAngle;
        this.orientation = orientation;
        this.bodypart = bodypart;
        this.phizioemail = phizioemail;
        this.sessiontime = sessiontime;
        this.actiontime = actiontime;
        this.holdtime = holdtime;
        this.numofreps = numofreps;
        this.angleCorrection = angleCorrection;
        this.patientid = patientid;
        this.patientname = patientname;
        this.tsLong = tsLong;
        this.bodyOrientation = bodyOrientation;
        this.dateofjoin = dateOfJoin;
        this.exercise_selected_position = exercise_selected_position;
        this.body_part_selected_position = body_part_selected_position;
        this.exercise_name = exercise_name;
        this.muscle_name = muscle_name;
        this.min_angle_selected = min_angle_selected;
        this.max_angle_selected = max_angle_selected;
        this.max_emg_selected = max_emg_selected;
        this.repsselected = repsselected;
        this.hold_angle_session = hold_angle_session;

        this.mmt_selected = mmt_selected;
        this.therapist_name = therapist_name;
        this.patient_status = patient_status;
        this.pain_status = pain_status;
        this.session_type = session_type;
        this.comment_session = comment_session;
        repository = new MqttSyncRepository(((Activity) context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }

    public SessionSummaryPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps, int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected, JSONArray emgJsonArray, JSONArray romJsonArray, int phizio_packagetype) {
        this.context = context;
        this.maxEmgValue = maxEmgValue;
        this.sessionNo = sessionNo;
        this.maxAngle = maxAngle;
        this.minAngle = minAngle;
        this.orientation = orientation;
        this.bodypart = bodypart;
        this.phizioemail = phizioemail;
        this.sessiontime = sessiontime;
        this.actiontime = actiontime;
        this.holdtime = holdtime;
        this.numofreps = numofreps;
        this.angleCorrection = angleCorrection;
        this.patientid = patientid;
        this.patientname = patientname;
        this.tsLong = tsLong;
        this.bodyOrientation = bodyOrientation;
        this.dateofjoin = dateOfJoin;
        this.exercise_selected_position = exercise_selected_position;
        this.body_part_selected_position = body_part_selected_position;
        this.exercise_name = exercise_name;
        this.muscle_name = muscle_name;
        this.min_angle_selected = min_angle_selected;
        this.max_angle_selected = max_angle_selected;
        this.max_emg_selected = max_emg_selected;
        this.repsselected = repsselected;
        this.emgJsonArray = emgJsonArray;
        this.romJsonArray = romJsonArray;
        this.phizio_packagetype = phizio_packagetype;
        repository = new MqttSyncRepository(((Activity) context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }


    public void showWindow() {

        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ActivityName", "sessionSummeryScreen");
        editor.apply();
        Configuration config = ((Activity) context).getResources().getConfiguration();
        final View layout;
        if (config.smallestScreenWidthDp >= 600) {
            layout = ((Activity) context).getLayoutInflater().inflate(R.layout.session_testing_tablet, null);
        } else {
            layout = ((Activity) context).getLayoutInflater().inflate(R.layout.session_testing, null);
        }

        FrameLayout layout_MainMenu = (FrameLayout) layout.findViewById(R.id.session_summary_frame);
        layout_MainMenu.getForeground().setAlpha(0);

        int color = ValueBasedColorOperations.getCOlorBasedOnTheBodyPart(bodypart,
                exercise_selected_position, maxAngle, minAngle, context);

        int emg_color = ValueBasedColorOperations.getEmgColor(400, maxEmgValue, context);
        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true);
        report.setWindowLayoutMode(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(true);
        report.showAtLocation(layout, Gravity.CENTER, 0, 0);

        LinearLayout ll_min_max_arc = layout.findViewById(R.id.ll_min_max_arc);
        final TextView tv_patient_name = layout.findViewById(R.id.tv_summary_patient_name);
        final TextView tv_patient_id = layout.findViewById(R.id.tv_summary_patient_id);
        TextView tv_held_on = layout.findViewById(R.id.session_held_on);
        TextView tv_min_angle = layout.findViewById(R.id.tv_min_angle);
        TextView tv_min_angle_Normal = layout.findViewById(R.id.tv_min_angle_normal);
        TextView tv_max_angle = layout.findViewById(R.id.tv_max_angle);
        TextView tv_max_angle_Normal = layout.findViewById(R.id.tv_max_angle_normal);
        TextView tv_total_time = layout.findViewById(R.id.tv_total_time);
        TextView tv_action_time_summary = layout.findViewById(R.id.tv_action_time_normal);
        TextView tv_action_time_summary_Normal = layout.findViewById(R.id.tv_action_time_normal);
        TextView tv_active_time_summary_Premium = layout.findViewById(R.id.tv_active_time_premium);
        TextView tv_active_time_summary_Premium_Print = layout.findViewById(R.id.tv_active_time_premium_print);
        TextView tv_hold_time_summary_Premium = layout.findViewById(R.id.tv_hold_time_premium);
        TextView tv_hold_time_summary_Premium_print = layout.findViewById(R.id.tv_hold_time_premium_print);
        TextView tv_hold_time = layout.findViewById(R.id.tv_hold_time_normal);
        TextView tv_hold_time_normal = layout.findViewById(R.id.tv_hold_time_normal);
        TextView tv_num_of_reps = layout.findViewById(R.id.tv_num_of_reps);
        TextView tv_num_of_reps_Normal = layout.findViewById(R.id.tv_num_of_reps_normal);
        TextView tv_max_emg = layout.findViewById(R.id.tv_max_emg);
        TextView tv_session_num = layout.findViewById(R.id.tv_session_no);
        TextView tv_session_num_pervious = layout.findViewById(R.id.tv_session_no_pervious);
        TextView tv_session_num_current = layout.findViewById(R.id.tv_session_no_current);
        TextView tv_orientation_and_bodypart = layout.findViewById(R.id.tv_orientation_and_bodypart);
        TextView tv_pervious_emg = layout.findViewById(R.id.pervious_emg);
        TextView tv_pervious_rom = layout.findViewById(R.id.pervious_rom);
        TextView tv_current_emg = layout.findViewById(R.id.current_emg);
        TextView tv_current_rom = layout.findViewById(R.id.current_rom);
        TextView Repititons_count = layout.findViewById(R.id.repititions);
        TextView Repititons_count_Print = layout.findViewById(R.id.repititions_print);
        SemiCircleArcProgressBar semiCircleArcProgressBar = layout.findViewById(R.id.semi_prog);
        TextView tv_healthy_emg = layout.findViewById(R.id.HealthySide_Text);
//        LinearLayout AlertOuterView=layout.findViewById(R.id.alert_outer_view);
//        CardView AlertView=layout.findViewById(R.id.session_affected_alert);
        TextView tv_emg_percentage = layout.findViewById(R.id.emg_percentage);
        TextView tv_rom_perc = layout.findViewById(R.id.session_rom_perc);
        TextView tv_rom_perc1 = layout.findViewById(R.id.session_rom_perc1);
//        TextView AlertText=layout.findViewById(R.id.session_alert_text1);
        TextView tv_goal_reacted = layout.findViewById(R.id.goal_reached_text);
        TextView progress_bar_data = layout.findViewById(R.id.progress_bar_data);
//        SemiCircleArcProgressBar Progress_Bar=layout.findViewById(R.id.Progress_Bar);
        ImageView status_web = layout.findViewById(R.id.status_web);
        ImageView status_web_rom = layout.findViewById(R.id.status_web_rom);
        ImageView body_potion = layout.findViewById(R.id.injured_side_image);
        TextView tv_musclename = layout.findViewById(R.id.tv_muscle_name);
        TextView tv_range = layout.findViewById(R.id.tv_range_min_max);
        TextView tv_delete_pateint_session = layout.findViewById(R.id.summary_tv_delete_session);
        TextView tv_target_emg = layout.findViewById(R.id.tv_target_emg_show_temp);
        TextView tv_target_emg_value = layout.findViewById(R.id.tv_target_emg_show);
        final LinearLayout alertlayoutouter = layout.findViewById(R.id.alert_outer_view);
        LinearLayout alertsetvisible = layout.findViewById(R.id.alert_setvisible_view);
        final CardView alertlayout = layout.findViewById(R.id.affected_alert);
        TextView alertText2 = layout.findViewById(R.id.alert_text2);
        TextView CurrentAffectedSide = layout.findViewById(R.id.current_exercise_side);
        TextView ExerciseOtherSide = layout.findViewById(R.id.exercise_otherside);
        TextView SessionStatements = layout.findViewById(R.id.session_statements);
        CardView PremiumTable = layout.findViewById(R.id.premium_table);
        CardView RegularTable = layout.findViewById(R.id.regular_table);
        TextView RegularROM = layout.findViewById(R.id.regular_current_rom);
        TextView RegularROM_Print = layout.findViewById(R.id.regular_current_rom_print);
        TextView RegularEMG = layout.findViewById(R.id.regular_current_emg);
        TextView RegularEMG_Print = layout.findViewById(R.id.regular_current_emg_print);
        LinearLayout StatementOuterView = layout.findViewById(R.id.statement_outer_view);
        LinearLayout SessionSummeryLayout = layout.findViewById(R.id.session_summary_layout);
        LinearLayout ActiveTimeView = layout.findViewById(R.id.active_time_view);
        LinearLayout ActiveTimeViewThermal = layout.findViewById(R.id.active_time_view_thermal);
        LinearLayout HoldTimeView = layout.findViewById(R.id.hold_time_view);
        LinearLayout HoldTimeViewThermal = layout.findViewById(R.id.hold_time_view_thermal);
        FloatingActionButton floatingActionButtonConnected = layout.findViewById(R.id.menu_print_connected);
        FloatingActionButton floatingActionButtonPrint = layout.findViewById(R.id.menu_print);
        FloatingActionButton floatingActionButtonShare = layout.findViewById(R.id.menu_share);
        double EMG_Normatives = 0.0;
//        final LinearLayout ll_click_to_view_report = layout.findViewById(R.id.ll_click_to_view_report);
        final LinearLayout ll_click_to_next = layout.findViewById(R.id.ll_click_to_next);
        TextView tv_musle_movement = layout.findViewById(R.id.musle_movement);
        TextView tv_musle_movement_Print = layout.findViewById(R.id.musle_movement_print);
        LinearLayout ProgressBar_Outer_Layout = layout.findViewById(R.id.bar_cal);
        LinearLayout Rom_Table_Outerview = layout.findViewById(R.id.rom_table_outerview);
        CardView ROM_Layout = layout.findViewById(R.id.rom_layout);
        CardView ROM_Layout_Normal = layout.findViewById(R.id.rom_layout_Normal);
        LinearLayout PremiumDegreeRemaining1 = layout.findViewById(R.id.PremiumDegreeremaining1);
        LinearLayout PremiumDegreeRemaining2 = layout.findViewById(R.id.PremiumDegreeremaining2);
        LinearLayout RegularDegreeRemaining = layout.findViewById(R.id.regularDegreeremaining);
        CardView ROMIsoetricView = layout.findViewById(R.id.rom_layout_isometric);
        //Share and cancel image view
        ImageView summary_go_back = layout.findViewById(R.id.summary_go_back);
        ImageView summary_share = layout.findViewById(R.id.summary_share);
        int ROM_Normatives = 0;
        new_pheezee_printer();
        if(bluetooth_status==true){
            floatingActionButtonConnected.setVisibility(View.VISIBLE);
            floatingActionButtonPrint.setVisibility(View.GONE);

        }
        //Emg Progress Bar
        ProgressBar pb_max_emg = layout.findViewById(R.id.progress_max_emg);


//        if(IS_SCEDULED_SESSION && !IS_SCEDULED_SESSIONS_COMPLETED){
//            ll_click_to_view_report.setVisibility(View.GONE);
//        }else {
//            ll_click_to_view_report.setVisibility(View.VISIBLE);
//        }


        try {
            //for held on date
            SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
            String dateString_sessionnumber = formatter_date.format(new Date(tsLong));
            // Haaris
            PatientStatusData data = new PatientStatusData(phizioemail, patientid, dateString_sessionnumber, dateString_sessionnumber);
            Call<PhizioSessionReportData> getsession_report_count_response = getDataService.getsession_number_count(data);
            getsession_report_count_response.enqueue(new Callback<PhizioSessionReportData>() {
                @Override
                public void onResponse(@NonNull Call<PhizioSessionReportData> call, @NonNull Response<PhizioSessionReportData> response) {
                    if (response.code() == 200) {
                        PhizioSessionReportData res = response.body();
                        sessionNo = res.getSession().toString();
                        tv_session_num.setText(sessionNo);
                        int session_pervious = Integer.parseInt(sessionNo) - 1;
                        Log.e("session_pervious", Integer.toString(session_pervious));
                        tv_session_num_pervious.setText(Integer.toString(session_pervious));
                        tv_session_num_current.setText(sessionNo);


                    }
                }

                @Override
                public void onFailure(@NonNull Call<PhizioSessionReportData> call, @NonNull Throwable t) {

                }


            });



        } catch (Exception e) {
            e.printStackTrace();
        }


        tv_orientation_and_bodypart.setText(orientation + "-" + bodypart + "-" + exercise_name);
        tv_musclename.setText(muscle_name);
        tv_musle_movement.setText(orientation + "-" + bodypart + "-" + exercise_name + "-" + muscle_name);
        tv_musle_movement_Print.setText(orientation + "-" + bodypart + "-" + exercise_name + "-" + muscle_name);

        if (exercise_name.equalsIgnoreCase("Isometric")) {
            maxAngle = 0;
            minAngle = 0;
        }


        try {
            // Get the AssetManager
            AssetManager assetManager = context.getAssets();

            // Open the JSON file
            InputStream inputStream = assetManager.open("ROMNormatives.json");

            // Read the JSON file into a string
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            // Parse the JSON string
            JSONObject jsonData = new JSONObject(json);
            boolean hasFlexionInShoulder = jsonData.getJSONObject(bodypart).has(exercise_name);
            if (hasFlexionInShoulder) {
                ROM_Normatives = jsonData.getJSONObject(bodypart).getInt(exercise_name);

            } else {

                Log.e("Normative data log", "Normative Data Not Found");
            }
            // Access the data


        } catch (Exception e) {
            Log.e("555555555555555555556666677770", String.valueOf(e));
            e.printStackTrace();
        }
        try {
            // Get the AssetManager
            AssetManager assetManager = context.getAssets();

            // Open the JSON file
            InputStream inputStream = assetManager.open("EMGData.json");

            // Read the JSON file into a string
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            // Parse the JSON string
            JSONObject jsonData = new JSONObject(json);

            // Access the data
            JSONArray BodyPartData = jsonData.getJSONArray(bodypart);

            if (BodyPartData != null && BodyPartData.length() > 0) {
                JSONObject Object = BodyPartData.getJSONObject(0);
                JSONArray MovementData = Object.getJSONArray(exercise_name);
                Log.e("1223232434343545446566754534542322", String.valueOf(MovementData));
                Log.e("1223232434343545446566754534542322", exercise_name);
                if (MovementData != null && MovementData.length() > 0) {
                    JSONObject MovementObject = MovementData.getJSONObject(0);
                    Log.e("555555555555555555556666677770", String.valueOf(muscle_name));
                    if (MovementObject.has(muscle_name)) {
                        EMG_Normatives = MovementObject.getInt(muscle_name);
                    } else {
                        HaveEMGNormatives = false;
                    }
                } else {
                    HaveEMGNormatives = false;
                }

            }


        } catch (Exception e) {
            Log.e("555555555555555555556666677770", String.valueOf(e));
            e.printStackTrace();
        }


        ll_click_to_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                (SessionSummaryPopupWindow.this).openReportActivity(patientid, patientname, dateofjoin);
            }
        });

//        ll_click_to_next.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("Range")
//            @Override
//            public void onClick(View v) {
//                PhysiofeedbackPopupWindow feedback = new PhysiofeedbackPopupWindow(context,maxEmgValue, sessionNo, maxAngle, minAngle, orientation, bodypart,
//                        phizioemail, sessiontime, actiontime, holdtime, numofreps,
//                        angleCorrection, patientid, patientname, tsLong, bodyOrientation, dateofjoin, exercise_selected_position,body_part_selected_position,
//                        muscle_name,exercise_name,min_angle_selected,max_angle_selected,max_emg_selected,repsselected,layout,emgJsonArray,romJsonArray,phizio_packagetype,hold_angle_session);
//                feedback.showWindow();
//                layout_MainMenu.getForeground().setAlpha(160);
//
//                //feedback.storeLocalSessionDetails(emgJsonArray,romJsonArray);
//                if(phizio_packagetype!=STANDARD_PACKAGE)
//                    repository.getPatientSessionNo(patientid);
//                feedback.setOnSessionDataResponse(new MqttSyncRepository.OnSessionDataResponse() {
//                    @Override
//                    public void onInsertSessionData(Boolean response, String message) {
//                        if (response)
//                            showToast(message);
//                    }
//
//                    @Override
//                    public void onSessionDeleted(Boolean response, String message) {
//                        showToast(message);
//                    }
//
//                    @Override
//                    public void onMmtValuesUpdated(Boolean response, String message) {
//                        showToast(message);
//                    }
//
//                    @Override
//                    public void onCommentSessionUpdated(Boolean response) {
//                    }
//                });
//
//
//            }
//        });

        summary_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeScreenShot screenShot = new TakeScreenShot(context, patientname, patientid);
                File file = screenShot.takeScreenshot(report);
                Uri pdfURI = FileProvider.getUriForFile(context, ((Activity) context).getApplicationContext().getPackageName() + ".my.package.name.provider", file);

                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_STREAM, pdfURI);
                i.setType("application/jpg");
                ((Activity) context).startActivity(Intent.createChooser(i, "share pdf"));
            }
        });

        summary_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup != null) {
                    popup.dismiss();
                }
                report.dismiss();
            }
        });

        if (patientid.length() > 3) {
            String temp = patientid.substring(0, 3) + "xxx";
            tv_patient_id.setText(temp);
        } else {
            tv_patient_id.setText(patientid);
        }
        tv_patient_name.setText(patientname);

        //for held on date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
        dateString = formatter.format(new Date(tsLong));
        String dateString_date = formatter_date.format(new Date(tsLong));
        tv_held_on.setText(dateString_date);
        tv_min_angle.setText(String.valueOf(minAngle).concat("°"));
        Log.e("1111111111111111555555555555555555555555555555555", String.valueOf(minAngle));
        tv_min_angle_Normal.setText(String.valueOf(minAngle).concat("°"));
        tv_max_angle.setText(String.valueOf(maxAngle).concat("°"));
        tv_max_angle_Normal.setText(String.valueOf(maxAngle).concat("°"));

        //total session time
        sessiontime = sessiontime.substring(0, 2) + "m" + sessiontime.substring(3, 7) + "s";


        tv_total_time.setText(sessiontime);

//        int a = Integer.parseInt(actiontime);
//        int t = Integer.parseInt(sessiontime);
        String at = actiontime;
        String tt = sessiontime;

        Log.e("time_a", String.valueOf(at));
        Log.e("time_t", String.valueOf(tt));

//        if( at.equals(tt)){
//
//            tv_total_time.setText(sessiontime);
//        }else {
//            tv_total_time.setText(actiontime);
//        }

//        Log.e("time_cal", String.valueOf(tv_total_time));
        tv_total_time.setText(sessiontime);
        tv_action_time_summary.setText(actiontime);
        tv_active_time_summary_Premium.setText(actiontime);
        tv_active_time_summary_Premium_Print.setText(actiontime);
        tv_action_time_summary_Normal.setText(actiontime);
        tv_hold_time.setText(holdtime);
        tv_hold_time_normal.setText(holdtime);
        tv_hold_time_summary_Premium_print.setText(holdtime);
        tv_hold_time_summary_Premium.setText(holdtime);
        tv_num_of_reps.setText(numofreps);
        tv_num_of_reps_Normal.setText(numofreps);
        tv_max_emg.setText(String.valueOf(maxEmgValue).concat(((Activity) context).getResources().getString(R.string.emg_unit)));

        tv_range.setText(String.valueOf(maxAngle - minAngle).concat("°"));

        //Creating the arc
        ArcViewInside arcView = layout.findViewById(R.id.session_summary_arcview);
        arcView.setMaxAngle(maxAngle);
        arcView.setMinAngle(minAngle);
        arcView.setRangeColor(color);

        if (!min_angle_selected.equals("") && !max_angle_selected.equals("")) {
            int reference_min_angle = Integer.parseInt(min_angle_selected);
            int reference_max_angle = Integer.parseInt(max_angle_selected);
            arcView.setEnableAndMinMax(reference_min_angle, reference_max_angle, true);
        }


        TextView tv_180 = layout.findViewById(R.id.tv_180);
        if (((Activity) context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tv_180.setPadding(5, 1, 170, 1);
        }
        String Kranthi = tv_target_emg.getText().toString();
        tv_target_emg_value.setText(Kranthi + " μV");

        SharedPreferences preferences_injured = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String patient_injured = preferences_injured.getString("patient injured", "");
        if (patient_injured.equalsIgnoreCase("Left") && orientation.equalsIgnoreCase("Left")) {
            body_potion.setImageResource(R.drawable.left_side_injured);
            body_potion.setVisibility(View.VISIBLE);
        }
        if (patient_injured.equalsIgnoreCase("Right") && orientation.equalsIgnoreCase("Right")) {
            body_potion.setImageResource(R.drawable.right_side_injured);
            body_potion.setVisibility(View.VISIBLE);

        }
        if (patient_injured.equalsIgnoreCase("Left") && orientation.equalsIgnoreCase("Right")) {
            body_potion.setImageResource(R.drawable.ref_right_side_injured);
            body_potion.setVisibility(View.VISIBLE);

        }
        if (patient_injured.equalsIgnoreCase("Right") && orientation.equalsIgnoreCase("Left")) {
            body_potion.setImageResource(R.drawable.ref_left_side_injured);
            body_potion.setVisibility(View.VISIBLE);

        }
        if (patient_injured.equalsIgnoreCase("Bi-Lateral") && orientation.equalsIgnoreCase("Right")) {
            body_potion.setImageResource(R.drawable.right_side_injured);
            body_potion.setVisibility(View.VISIBLE);

        }
        if (patient_injured.equalsIgnoreCase("Bi-Lateral") && orientation.equalsIgnoreCase("Left")) {
            body_potion.setImageResource(R.drawable.left_side_injured);
            body_potion.setVisibility(View.VISIBLE);

        }


        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String value_emg = preferences.getString("Name", "");

        SharedPreferences normative_value_emg = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String normative_value_emg_data = normative_value_emg.getString("normative_value_emg", "");


        Log.e("value_emg", normative_value_emg_data);

        SharedPreferences server_emg = PreferenceManager.getDefaultSharedPreferences(context);
        String server_emg_data = server_emg.getString("emg_value", "");

        SharedPreferences server_rom = PreferenceManager.getDefaultSharedPreferences(context);
        String server_rom_data = server_rom.getString("server_rom_mqt", "");

        if (server_emg_data.equalsIgnoreCase("no")) {
            tv_pervious_emg.setText("0".concat(" μV"));
        } else {
            tv_pervious_emg.setText(server_emg_data.concat(" μV"));
        }

        if (server_rom_data.equalsIgnoreCase("no")) {
            tv_pervious_rom.setText(max_angle_selected.concat("°"));
        } else {
            tv_pervious_rom.setText(max_angle_selected.concat("°"));
        }

        int current = maxAngle - minAngle;
        if (current > Integer.parseInt(max_angle_selected)) {
            status_web_rom.setImageResource(R.drawable.up_arrow);
        } else {
            status_web_rom.setImageResource(R.drawable.down_arrow);
        }

//        try {
//            SessionSummeryOppositesideTableData sessionSummeryOppositesideTableData=new SessionSummeryOppositesideTableData(patientid,json_phizioemail,orientation,bodypart,bodyOrientation,exercise_name,muscle_name);
//            Call<SessionSummeryableDataRec> sessionSummeryableDataRecCall = getDataService.session_summery_table_data(sessionSummeryOppositesideTableData);
//
//        }catch (Exception e){
//
//        }
//        floatingActionButtonPrint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                animateFabUp(floatingActionButton, 300);
//                animateFabLeft(floatingActionButtonShare, 300);
//            }
//        });

        SessionSummeryLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Click outside the FABs, return them to their original positions
                    if (popup != null) {
                        popup.dismiss();
                    }
                    return true;
                }
                return false;
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


                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                    } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
                    } else {
                        if (bluetooth_status == true) {
                            floatingActionButtonConnected.setVisibility(View.VISIBLE);
                            floatingActionButtonPrint.setVisibility(View.GONE);
//                            if (BluetoothPrintersConnections.selectFirstPaired() != null) {
//                            popup = new PopupWindow(context);
//                            View customView = LayoutInflater.from(context).inflate(R.layout.printerlayour, null);
//                            popup.setContentView(customView);
//                            int location[] = new int[2];
//                            int x = location[0] + customView.getWidth();
//                            int y = location[1] + 50; // add 50 pixels from the top
//                            popup.showAtLocation(customView, Gravity.TOP | Gravity.RIGHT, x, 250);
//
//                            LinearLayout thermal_printer = customView.findViewById(R.id.thermal_printer);
//
//                            LinearLayout printer = customView.findViewById(R.id.other_printer);
//                            printer.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                    new CountDownTimer(2000, 1000) {
//                                        @Override
//                                        public void onTick(long l) {
//
//                                        }
//
//                                        @Override
//                                        public void onFinish() {
//                                            popup.dismiss();
//
//                                        }
//                                    }.start();
//
//                                }
//                            });
//                            thermal_printer.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    // Open the PDF file
//                                    File pdfFile = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
//                                    final Dialog dialog_dp = new Dialog(context);
//                                    dialog_dp.setContentView(R.layout.dialog_progress);
//                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                                    lp.copyFrom(dialog_dp.getWindow().getAttributes());
//                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                                    dialog_dp.getWindow().setAttributes(lp);
//                                    dialog_dp.setCanceledOnTouchOutside(false);
//                                    dialog_dp.show();
////                ROMIsoetricView.setVisibility(View.VISIBLE);
//
//                                    loopCompleted = false;
//                                    if (pdfFile.exists()) {
//                                        if (pdfFile.delete()) {
//                                            // File deleted successfully
//                                            Log.d("99999999999999999999999", "File deleted successfully");
//                                            convertLayoutToPdf(SessionSummeryLayout);
//
//                                        } else {
//                                            // Failed to delete the file
//                                            Log.d("99999999999999999999999", "Failed to delete the file");
//                                        }
//                                    } else {
//                                        // File doesn't exist
//                                        Log.d("99999999999999999999999", "File doesn't exist");
//                                        convertLayoutToPdf(SessionSummeryLayout);
//
//                                    }
//                                    File file = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
//
//                                    new CountDownTimer(2000, 1000) {
//                                        @Override
//                                        public void onTick(long l) {
//
//                                        }
//
//                                        @Override
//                                        public void onFinish() {
//
//
//                                            try {
//                                                ParcelFileDescriptor parcelFileDescriptor = null;
//                                                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
//                                                renderer = new PdfRenderer(parcelFileDescriptor);
//                                            } catch (IOException e) {
//                                                throw new RuntimeException(e);
//                                            }
//                                            // Create an array to hold the bitmaps
//                                            Bitmap[] bitmaps = new Bitmap[renderer.getPageCount()];
//                                            int i;
//                                            // Render each page into a bitmap
//                                            for (i = 0; i < renderer.getPageCount(); i++) {
//                                                PdfRenderer.Page page = renderer.openPage(i);
//                                                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
//                                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                                                ColorMatrix matrix = new ColorMatrix();
//                                                matrix.setSaturation(0);
//                                                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
//                                                Paint paint = new Paint();
//                                                paint.setColorFilter(filter);
//                                                Canvas canvas = new Canvas(bitmap);
//                                                canvas.drawBitmap(bitmap, 0, 0, paint);
//                                                bitmaps[i] = bitmap;
//                                                decodedByte = bitmap;
//
//                                                try {
//                                                    printer_th = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 70f, 48);
//                                                    int width = decodedByte.getWidth(), height = decodedByte.getHeight();
//                                                    StringBuilder textToPrint = new StringBuilder();
//                                                    for (int y = 0; y < height; y += 256) {
//                                                        resizedBitmap = Bitmap.createBitmap(decodedByte, 0, y, width, (y + 256 >= height) ? height - y : 256);
//                                                        textToPrint.append("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer_th, resizedBitmap) + "</img>\n");
//                                                    }
//                                                    printer_th.printFormattedTextAndCut(textToPrint.toString());
//                                                    decodedByte.recycle();
//                                                    page.close();
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                            loopCompleted = true;
//                                            // Clean up resources
//                                            renderer.close();
//                                        }
//                                    }.start();
//                                    handler = new Handler();
//
//                                    final Runnable r = new Runnable() {
//                                        public void run() {
//                                            if (loopCompleted) {
//                                                dialog_dp.dismiss();
//                                                printer_th.disconnectPrinter();
//                                                ROMIsoetricView.setVisibility(View.GONE);
//                                            }
//
//                                            handler.postDelayed(this, 1000);
//                                        }
//                                    };
//                                    handler.postDelayed(r, 1000);
//
//
//                                }
//                            });

//                            }
                        } else if (bluetooth_status == false) {
                            popup = new PopupWindow(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.buyprinter, null);
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

                            buy_now_ptr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popup.dismiss();
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
                                                Intent intent = new Intent(getApplicationContext(), BuyPrinterActivity.class);
                                                intent.putExtra("pt_name", pt_name);
                                                intent.putExtra("pt_number", pt_number);
                                                intent.putExtra("pt_email", pt_email);
                                                context.startActivity(intent);
                                            }

                                        }


                                        @Override
                                        public void onFailure(Call<BluetoothCommunication> call, Throwable t) {


                                        }
                                    });



                                }
                            });
                            other_printer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popup.dismiss();
                                    File file = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
                                    Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", file);
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("application/pdf");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                    context.startActivity(Intent.createChooser(shareIntent, "Share file"));
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
        floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File pdfFile = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");

                if (pdfFile.exists()) {
                    if (pdfFile.delete()) {
                        // File deleted successfully
                        Log.d("99999999999999999999999", "File deleted successfully");
                        convertLayoutToPdf(SessionSummeryLayout);

                    } else {
                        // Failed to delete the file
                        Log.d("99999999999999999999999", "Failed to delete the file");
                    }
                } else {
                    // File doesn't exist
                    Log.d("99999999999999999999999", "File doesn't exist");
                    convertLayoutToPdf(SessionSummeryLayout);

                }
                File file =new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
                Uri fileUri =  FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", file);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                context.startActivity(Intent.createChooser(shareIntent, "Share file"));
            }
        });
        floatingActionButtonConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, SessionSummeryThermalPrint.class);
//                context.startActivity(intent);
                RegularEMG.setVisibility(View.GONE);
                RegularEMG_Print.setVisibility(View.VISIBLE);
                RegularROM.setVisibility(View.GONE);
                RegularROM_Print.setVisibility(View.VISIBLE);
                tv_musle_movement.setVisibility(View.GONE);
                tv_musle_movement_Print.setVisibility(View.VISIBLE);
                Repititons_count.setVisibility(View.GONE);
                Repititons_count_Print.setVisibility(View.VISIBLE);
                ActiveTimeViewThermal.setVisibility(View.VISIBLE);
                ActiveTimeView.setVisibility(View.GONE);
                HoldTimeViewThermal.setVisibility(View.VISIBLE);
                HoldTimeView.setVisibility(View.GONE);
                if (exercise_name.equalsIgnoreCase("Isometric")) {
                    ROM_Layout_Normal.setVisibility(View.GONE);
                    ROM_Layout.setVisibility(View.GONE);
                }
                else{
                    ROM_Layout_Normal.setVisibility(View.GONE);
                    ROM_Layout.setVisibility(View.VISIBLE);
                }

                File pdfFile = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
                final Dialog dialog_dp = new Dialog(context);
                dialog_dp.setContentView(R.layout.dialog_progress);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog_dp.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog_dp.getWindow().setAttributes(lp);
                dialog_dp.setCanceledOnTouchOutside(false);
                dialog_dp.show();

                Handler handler1 = new Handler();
                // Define the code you want to run after one second
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loopCompleted = false;
                        if (pdfFile.exists()) {
                            if (pdfFile.delete()) {
                                // File deleted successfully
                                Log.d("99999999999999999999999", "File deleted successfully");
                                convertLayoutToPdf(SessionSummeryLayout);

                            } else {
                                // Failed to delete the file
                                Log.d("99999999999999999999999", "Failed to delete the file");
                            }
                        } else {
                            // File doesn't exist
                            Log.d("99999999999999999999999", "File doesn't exist");
                            convertLayoutToPdf(SessionSummeryLayout);

                        }
                        ROM_Layout.setVisibility(View.GONE);
                        ROM_Layout_Normal.setVisibility(View.VISIBLE);
                        File file = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");
                        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", file);

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
                    }
                };

                // Post the Runnable with a delay of 1000 milliseconds (1 second)
                handler1.postDelayed(runnable, 1000);


                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (loopCompleted) {
                            dialog_dp.dismiss();
                            RegularEMG.setVisibility(View.VISIBLE);
                            RegularEMG_Print.setVisibility(View.GONE);
                            RegularROM.setVisibility(View.VISIBLE);
                            RegularROM_Print.setVisibility(View.GONE);
                            tv_musle_movement.setVisibility(View.VISIBLE);
                            tv_musle_movement_Print.setVisibility(View.GONE);
                            Repititons_count.setVisibility(View.VISIBLE);
                            Repititons_count_Print.setVisibility(View.GONE);
                            ActiveTimeViewThermal.setVisibility(View.GONE);
                            ActiveTimeView.setVisibility(View.VISIBLE);
                            HoldTimeViewThermal.setVisibility(View.GONE);
                            HoldTimeView.setVisibility(View.VISIBLE);
                            printer_th.disconnectPrinter();
                            if (exercise_name.equalsIgnoreCase("Isometric")) {
                                ROM_Layout_Normal.setVisibility(View.GONE);
                                ROM_Layout.setVisibility(View.GONE);
                            }
                            else{
                                ROM_Layout_Normal.setVisibility(View.VISIBLE);
                                ROM_Layout.setVisibility(View.GONE);
                            }

                        }

                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(r, 1000);

            }
        });
        try {

            PremiumSubscriptionUserData premiumSubscriptionUserData = new PremiumSubscriptionUserData(phizioemail);
            Call<PremiumSubscriptionDataRec> premiumSubscriptionDataRecCall = getDataService.premium_subscription_user_data(premiumSubscriptionUserData);
            int finalROM_Normatives = ROM_Normatives;
            double finalEMG_Normatives = EMG_Normatives;
            premiumSubscriptionDataRecCall.enqueue(new Callback<PremiumSubscriptionDataRec>() {
                @Override
                public void onResponse(Call<PremiumSubscriptionDataRec> call, Response<PremiumSubscriptionDataRec> response) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    } catch (Exception e) {
                        Log.e("11111111111111", String.valueOf(e));
                    }
                    if (response.code() == 200) {
                        PremiumSubscriptionDataRec res = response.body();

                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CustomerType", res.getCustomerType());
                        editor.putString("StartDate", res.getStartDate());
                        editor.putString("EndDate", res.getEndDate());
                        editor.putString("ReportGenerated", res.getReportGenerated());
                        editor.putString("ReportAccessible", res.getReportAccessible());
                        editor.apply();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        if (res.getCustomerType().equalsIgnoreCase("null") || res.getCustomerType().equalsIgnoreCase("")
                                || res.getCustomerType().equalsIgnoreCase("subscription_mode") || res.getCustomerType().equalsIgnoreCase("time_period_mode")
                                || res.getCustomerType().equalsIgnoreCase("old_user")) {

                            if (res.getCustomerType().equalsIgnoreCase("null") || res.getCustomerType().equalsIgnoreCase("")) {
                                RegularTable.setVisibility(View.VISIBLE);
                                ROM_Layout.setVisibility(View.GONE);
                                ROM_Layout_Normal.setVisibility(View.VISIBLE);
                                floatingActionButtonPrint.setVisibility(View.VISIBLE);
//                                floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                floatingActionButtonShare.setVisibility(View.VISIBLE);
                                PremiumTable.setVisibility(View.GONE);
                                alertsetvisible.setVisibility(View.GONE);
                                if(bluetooth_status==true){
                                    floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                    floatingActionButtonPrint.setVisibility(View.GONE);

                                }

                            } else if (res.getCustomerType().equalsIgnoreCase("subscription_mode")) {
                                int difference = Integer.valueOf(res.getReportAccessible()) - Integer.valueOf(res.getReportGenerated());
                                if (difference < 0) {
                                    RegularTable.setVisibility(View.VISIBLE);
                                    ROM_Layout.setVisibility(View.GONE);
                                    ROM_Layout_Normal.setVisibility(View.VISIBLE);
                                    floatingActionButtonPrint.setVisibility(View.VISIBLE);
                                    floatingActionButtonShare.setVisibility(View.VISIBLE);
//                                    floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                    PremiumTable.setVisibility(View.GONE);
                                    alertsetvisible.setVisibility(View.GONE);
                                    if(bluetooth_status==true){
                                        floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                        floatingActionButtonPrint.setVisibility(View.GONE);

                                    }
                                } else {
                                    RegularTable.setVisibility(View.GONE);
                                    ROM_Layout_Normal.setVisibility(View.GONE);
                                    PremiumTable.setVisibility(View.VISIBLE);
                                    ROM_Layout.setVisibility(View.VISIBLE);
                                    if (exercise_name.equalsIgnoreCase("Isometric")) {
                                        ROM_Layout.setVisibility(View.GONE);
                                        ROM_Layout_Normal.setVisibility(View.GONE);
                                    }

                                }

                            } else if (res.getCustomerType().equalsIgnoreCase("time_period_mode")) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1; // Month value is 0-based, so adding 1
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                // Create a string representation of the date
                                String currentDate1 = day + "-" + month + "-" + year;


                                String currentDateStr = currentDate1;
                                String lastDateStr = res.getEndDate();
                                String firstDateStr = res.getStartDate();
                                // Print the current date
                                if (currentDateStr != null && !currentDateStr.equalsIgnoreCase("null") && !currentDateStr.equalsIgnoreCase("") &&
                                        lastDateStr != null && !lastDateStr.equalsIgnoreCase("null") && !lastDateStr.equalsIgnoreCase("")) {
                                    LocalDate currentDate = LocalDate.parse(currentDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                                    LocalDate lastDate = LocalDate.parse(lastDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                                    LocalDate firstDate = LocalDate.parse(firstDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                                    long difference_In_Days = ChronoUnit.DAYS.between(currentDate, lastDate);
                                    if (difference_In_Days < 0) {
                                        RegularTable.setVisibility(View.VISIBLE);
                                        ROM_Layout.setVisibility(View.GONE);
                                        ROM_Layout_Normal.setVisibility(View.VISIBLE);
                                        floatingActionButtonPrint.setVisibility(View.VISIBLE);
                                        floatingActionButtonShare.setVisibility(View.VISIBLE);
//                                        floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                        PremiumTable.setVisibility(View.GONE);
                                        alertsetvisible.setVisibility(View.GONE);
                                        if(bluetooth_status==true){
                                            floatingActionButtonConnected.setVisibility(View.VISIBLE);
                                            floatingActionButtonPrint.setVisibility(View.GONE);

                                        }
                                    } else {
                                        RegularTable.setVisibility(View.GONE);
                                        ROM_Layout_Normal.setVisibility(View.GONE);
                                        PremiumTable.setVisibility(View.VISIBLE);
                                        ROM_Layout.setVisibility(View.VISIBLE);
                                        if (exercise_name.equalsIgnoreCase("Isometric")) {
                                            ROM_Layout.setVisibility(View.GONE);
                                            ROM_Layout_Normal.setVisibility(View.GONE);
                                        }

                                    }
                                }

                            } else if (res.getCustomerType().equalsIgnoreCase("old_user")) {
                                RegularTable.setVisibility(View.VISIBLE);
                                ROM_Layout.setVisibility(View.VISIBLE);
                                ROM_Layout_Normal.setVisibility(View.GONE);
                                floatingActionButtonPrint.setVisibility(View.GONE);
                                floatingActionButtonShare.setVisibility(View.GONE);
                                floatingActionButtonConnected.setVisibility(View.GONE);
                                PremiumTable.setVisibility(View.GONE);
                                alertsetvisible.setVisibility(View.GONE);
                            }


                            if (exercise_name.equalsIgnoreCase("Isometric")) {
                                ROM_Layout.setVisibility(View.GONE);
                                ROM_Layout_Normal.setVisibility(View.GONE);
                            }

                            RegularEMG.setText(Integer.toString(maxEmgValue).concat(" μV"));
                            RegularEMG_Print.setText(Integer.toString(maxEmgValue).concat(" μV"));
                            int rom_value=0;
                            if (minAngle < 0) {
                                rom_value = Math.max(maxAngle, 0); // Take the maximum of maxAngle and 0
                            } else if (minAngle == 0) {
                                rom_value = Math.max(maxAngle, 0); // Take the maximum of maxAngle and 0
                            } else if (minAngle > 0) {
                                rom_value = Math.max(maxAngle - minAngle, 0); // Calculate the difference, or 0 if negative
                            }
                            if((bodypart.equalsIgnoreCase("Shoulder") || bodypart.equalsIgnoreCase("Hip"))){
                                rom_value=maxAngle-minAngle;
                            }
                            if ((bodypart.equalsIgnoreCase("Elbow") || bodypart.equalsIgnoreCase("Knee")) && exercise_name.equalsIgnoreCase("Extension")) {
                                rom_value = minAngle > 0 ? minAngle : 0;
                            }
                            RegularROM.setText(Integer.toString(rom_value).concat("°"));
                            RegularROM_Print.setText(Integer.toString(rom_value).concat("°"));
                            double rom_percentage = 0;
                            if ((bodypart.equalsIgnoreCase("Elbow") || bodypart.equalsIgnoreCase("Knee")) && exercise_name.equalsIgnoreCase("Extension")) {
                                rom_value = minAngle > 0 ? minAngle : 0;
                                rom_percentage = (((double) (140 - rom_value) / (double) 140) * 100);
                            } else {
                                rom_percentage = (((double) rom_value / (double) finalROM_Normatives) * 100);
                            }
                            double emg_percentage = (((double) maxEmgValue / finalEMG_Normatives) * 100);
                            if (HaveEMGNormatives) {
                                if ((int) emg_percentage >= 0 && (int) emg_percentage <= 25) {
                                    RegularEMG.setTextColor(Color.parseColor("#FE0302"));

                                } else if ((int) emg_percentage > 25 && (int) emg_percentage <= 50) {
                                    RegularEMG.setTextColor(Color.parseColor("#E6A000"));

                                } else if ((int) emg_percentage > 50 && (int) emg_percentage <= 75) {
                                    RegularEMG.setTextColor(Color.parseColor("#00B286"));

                                } else if ((int) emg_percentage > 75) {
                                    RegularEMG.setTextColor(Color.parseColor("#00B286"));

                                }
                            }

                            int emg_percentage_convert, rom_percentage_convert;
                            emg_percentage_convert = (int) emg_percentage;
                            if (emg_percentage > 100) {
                                emg_percentage_convert = 100;
                            }
                            rom_percentage_convert = (int) rom_percentage;
                            if (rom_percentage_convert > 100) {
                                rom_percentage_convert = 100;
                            }
                            double semi_perc;
                            if (exercise_name.equalsIgnoreCase("Isometric")) {
                                if (emg_percentage > 100) {
                                    semi_perc = 100;
                                } else {
                                    semi_perc = emg_percentage;
                                }
                            } else {
                                semi_perc = (emg_percentage_convert + rom_percentage_convert) / 2;
                            }

//                            Log.e("1111111111111111111111111111111", String.valueOf(semi_perc));
                            if (semi_perc < 100) {
                                semiCircleArcProgressBar.setPercent((int) semi_perc);
                                progress_bar_data.setText(String.valueOf((int) semi_perc).concat("%"));
                            } else {
                                semiCircleArcProgressBar.setPercent(100);
                                progress_bar_data.setText("100%");
                            }

                            if ((bodypart.equalsIgnoreCase("Elbow") || bodypart.equalsIgnoreCase("Knee")) && exercise_name.equalsIgnoreCase("Extension")) {
                                RegularDegreeRemaining.setVisibility(View.VISIBLE);
                                if ((int) rom_percentage >= 0 && (int) rom_percentage <= 88) {
                                    RegularROM.setTextColor(Color.parseColor("#FE0302"));
                                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                                } else if ((int) rom_percentage > 88 && (int) rom_percentage <= 92) {
                                    RegularROM.setTextColor(Color.parseColor("#E6A000"));
                                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                                } else if ((int) rom_percentage > 92) {
                                    RegularROM.setTextColor(Color.parseColor("#00B286"));
                                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");
                                }
                            } else {
                                RegularDegreeRemaining.setVisibility(View.GONE);
                                if ((int) rom_percentage >= 0 && (int) rom_percentage <= 33) {
                                    RegularROM.setTextColor(Color.parseColor("#FE0302"));
                                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                                } else if ((int) rom_percentage > 34 && (int) rom_percentage <= 66) {
                                    RegularROM.setTextColor(Color.parseColor("#E6A000"));
                                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                                } else if ((int) rom_percentage > 67 && (int) rom_percentage <= 100) {
                                    RegularROM.setTextColor(Color.parseColor("#00B286"));
                                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");

                                } else if ((int) rom_percentage > 100) {
                                    RegularROM.setTextColor(Color.parseColor("#00B286"));
                                    tv_rom_perc1.setText("100%");
                                }
                            }

                        } else {
                            RegularTable.setVisibility(View.GONE);
                            ROM_Layout_Normal.setVisibility(View.GONE);
                            PremiumTable.setVisibility(View.VISIBLE);
                            ROM_Layout.setVisibility(View.VISIBLE);
                            if (exercise_name.equalsIgnoreCase("Isometric")) {
                                ROM_Layout.setVisibility(View.GONE);
                                ROM_Layout_Normal.setVisibility(View.GONE);
                            }

                        }

                    }
                }

                @Override
                public void onFailure(Call<PremiumSubscriptionDataRec> call, Throwable t) {

                }
            });
        } catch (Exception e) {

        }

        /**
         * This is for Healthy side session and Isomatric session
         * Commented by :Vyanktesh Bargale.
         */
        if (patient_injured.equalsIgnoreCase("Left") && orientation.equalsIgnoreCase("Right")) {
            ProgressBar_Outer_Layout.setVisibility(View.GONE);
        } else if (patient_injured.equalsIgnoreCase("Right") && orientation.equalsIgnoreCase("Left")) {
            ProgressBar_Outer_Layout.setVisibility(View.GONE);
        }
        if (exercise_name.equalsIgnoreCase("Isometric")) {
            Rom_Table_Outerview.setVisibility(View.GONE);
            ROM_Layout.setVisibility(View.GONE);
            ROM_Layout_Normal.setVisibility(View.GONE);
        }


        /**
         * This code is for new Session Summery screen calculations.
         * Commented By: Vyanktesh Bargale.
         */
        try {
            int outerView;
            alertlayoutouter.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // This method will be called when the layout has been measured and rendered

                    // Get the width of the LinearLayout

                    int startWidth = alertlayout.getWidth();
                    int outerView = alertlayoutouter.getWidth();
                    int startMargin = ((ViewGroup.MarginLayoutParams) alertlayout.getLayoutParams()).leftMargin;
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    int targetWidth = displayMetrics.widthPixels;
                    ValueAnimator animator = ValueAnimator.ofInt(startWidth, startWidth + (outerView - startWidth)); // increase width by 200px
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = alertlayout.getLayoutParams();
                            layoutParams.width = value;
                            alertlayout.setLayoutParams(layoutParams);
                        }
                    });
                    animator.start();
                    new CountDownTimer(1000, 1000) {

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
//                    alertText1.setVisibility(View.VISIBLE);
                            alertText2.setVisibility(View.VISIBLE);
                            alertText2.setText("Assess the healthy side for accurate insights!");

                        }
                    }.start();
                    // Do something with the width
                    // ...

                    // Remove the listener to avoid multiple calls
                    alertlayoutouter.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });


//            int startWidth = alertlayout.getWidth();
//            int outerView=alertlayoutouter.getWidth();
            tv_current_emg.setText(Integer.toString(maxEmgValue).concat(" μV"));
//            int rom_value=maxAngle-minAngle;
            int rom_value = 0;
//            if(minAngle<0 && maxAngle<0){
//                rom_value=0;
//            }else if(minAngle<0 && maxAngle==0){
//                rom_value=0;
//            } else if (minAngle<0 && maxAngle>0) {
//                rom_value=maxAngle;
//            } else if (minAngle==0 && maxAngle==0) {
//                rom_value=0;
//            } else if (minAngle==0 && maxAngle>0) {
//                rom_value=maxAngle;
//            } else if (minAngle>0 && maxAngle>0) {
//                rom_value=maxAngle-minAngle;
//            }
            if (minAngle < 0) {
                rom_value = Math.max(maxAngle, 0); // Take the maximum of maxAngle and 0
            } else if (minAngle == 0) {
                rom_value = Math.max(maxAngle, 0); // Take the maximum of maxAngle and 0
            } else if (minAngle > 0) {
                rom_value = Math.max(maxAngle - minAngle, 0); // Calculate the difference, or 0 if negative
            }
            if((bodypart.equalsIgnoreCase("Shoulder") || bodypart.equalsIgnoreCase("Hip"))){
                rom_value=maxAngle-minAngle;
            }
            if ((bodypart.equalsIgnoreCase("Elbow") || bodypart.equalsIgnoreCase("Knee")) && exercise_name.equalsIgnoreCase("Extension")) {
                rom_value = minAngle > 0 ? minAngle : 0;
            }
            tv_current_rom.setText(Integer.toString(rom_value).concat("°"));
//            int startMargin = ((ViewGroup.MarginLayoutParams)alertlayout.getLayoutParams()).leftMargin;
//            DisplayMetrics displayMetrics = new DisplayMetrics();
//            int targetWidth = displayMetrics.widthPixels;
//            ValueAnimator animator = ValueAnimator.ofInt(startWidth, startWidth + (outerView-startWidth)); // increase width by 200px
//            animator.setDuration(1000);
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int value = (int) animation.getAnimatedValue();
//                    ViewGroup.LayoutParams layoutParams = alertlayout.getLayoutParams();
//                    layoutParams.width = value;
//                    alertlayout.setLayoutParams(layoutParams);
//                }
//            });
//            animator.start();
//            new CountDownTimer(1000,1000){
//
//                @Override
//                public void onTick(long l) {
//
//                }
//
//                @Override
//                public void onFinish() {
////                                    alertText1.setVisibility(View.VISIBLE);
//                    alertText2.setVisibility(View.VISIBLE);
//                }
//            }.start();
            double rom_percentage = 0;


            if ((bodypart.equalsIgnoreCase("Elbow") || bodypart.equalsIgnoreCase("Knee")) && exercise_name.equalsIgnoreCase("Extension")) {
                rom_value = minAngle > 0 ? minAngle : 0;
                rom_percentage = (((double) (140 - rom_value) / (double) 140) * 100);
            } else {
                rom_percentage = (((double) rom_value / (double) ROM_Normatives) * 100);
            }

            double emg_percentage = (((double) maxEmgValue / EMG_Normatives) * 100);
            Log.e("111111111112222", String.valueOf(emg_percentage));
            Log.e("111111111112222", String.valueOf(maxEmgValue));
            Log.e("111111111112222", String.valueOf(EMG_Normatives));
            if (rom_percentage > 100 && emg_percentage > 100) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "100%" + " of the ideal value and the Muscle Activity is also excellent!");
            } else if (rom_percentage > 100 && (emg_percentage <= 100 && emg_percentage > 75)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "100%" + " of the ideal value and the Muscle Activity is also excellent!");
            } else if (rom_percentage > 100 && (emg_percentage <= 75 && emg_percentage > 50)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "100%" + " of the ideal value and the Muscle Activity is good!");
            } else if (rom_percentage > 100 && (emg_percentage <= 50 && emg_percentage > 25)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "100%" + " of the ideal value and the Muscle Activity is fair!");
            } else if (rom_percentage > 100 && (emg_percentage <= 25 && emg_percentage > 0)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "100%" + " of the ideal value and the Muscle Activity is poor!");
            } else if (rom_percentage > 100 && emg_percentage < 0) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "100%" + " of the ideal value and the Muscle Activity is poor!");
            } else if ((rom_percentage > 67 && rom_percentage < 100) && emg_percentage >= 100) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is also excellent!");
            } else if ((rom_percentage > 67 && rom_percentage < 100) && (emg_percentage <= 100 && emg_percentage > 75)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is also excellent!");
            } else if ((rom_percentage > 67 && rom_percentage < 100) && (emg_percentage <= 75 && emg_percentage > 50)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is good!");
            } else if ((rom_percentage > 67 && rom_percentage < 100) && (emg_percentage <= 50 && emg_percentage > 26)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is fair!");
            } else if ((rom_percentage > 67 && rom_percentage < 100) && (emg_percentage <= 25 && emg_percentage > 0)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is poor!");
            } else if ((rom_percentage > 67 && rom_percentage < 100) && emg_percentage < 0) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is poor!");
            } else if ((rom_percentage > 33 && rom_percentage <= 67) && emg_percentage > 100) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is also Excellent!");
            } else if ((rom_percentage > 33 && rom_percentage <= 67) && (emg_percentage <= 100 && emg_percentage > 75)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is also Excellent!");
            } else if ((rom_percentage > 33 && rom_percentage <= 67) && (emg_percentage <= 75 && emg_percentage > 50)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is good!");
            } else if ((rom_percentage > 33 && rom_percentage <= 67) && (emg_percentage <= 50 && emg_percentage > 25)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is fair!");
            } else if ((rom_percentage > 33 && rom_percentage <= 67) && (emg_percentage <= 25 && emg_percentage > 0)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is poor!");
            } else if ((rom_percentage > 33 && rom_percentage <= 67) && emg_percentage < 0) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is poor!");
            } else if ((rom_percentage > 0 && rom_percentage <= 33) && emg_percentage > 100) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is Excellent!");
            } else if ((rom_percentage > 0 && rom_percentage <= 33) && (emg_percentage <= 100 && emg_percentage > 75)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is Excellent!");
            } else if ((rom_percentage > 0 && rom_percentage <= 33) && (emg_percentage <= 75 && emg_percentage > 50)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is good!");
            } else if ((rom_percentage > 0 && rom_percentage <= 33) && (emg_percentage <= 50 && emg_percentage > 25)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is fair!");
            } else if ((rom_percentage > 0 && rom_percentage <= 33) && (emg_percentage <= 25 && emg_percentage > 0)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is also poor!");
            } else if ((rom_percentage > 0 && rom_percentage <= 33) && emg_percentage < 0) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + (int) rom_percentage + "% of the ideal value and the Muscle Activity is also poor!");
            }
            if (rom_percentage <= 0 && emg_percentage > 100) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "0" + " but the Muscle Activity is excellent!");
            } else if (rom_percentage <= 0 && (emg_percentage <= 100 && emg_percentage > 75)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "0" + " but the Muscle Activity is excellent!");
            } else if (rom_percentage <= 0 && (emg_percentage <= 75 && emg_percentage > 50)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "0" + " but the Muscle Activity is good!");
            } else if (rom_percentage <= 0 && (emg_percentage <= 50 && emg_percentage > 25)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "0" + " but the Muscle Activity is fair!");
            } else if (rom_percentage <= 0 && (emg_percentage <= 25 && emg_percentage > 0)) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "0" + " and the Muscle Activity is poor!");
            } else if (rom_percentage <= 0 && emg_percentage < 0) {
                SessionStatements.setText("Joint Mobility for the " + orientation + " side is " + "0%" + " and the Muscle Activity is poor!");
            }
            Log.e("PAtientenjured", patient_injured);
            if (patient_injured.equalsIgnoreCase("Left")) {
                Healthyside = "Right";
            } else {
                Healthyside = "Left";
            }
            if (patient_injured.equalsIgnoreCase("Bi-Lateral")) {
                Healthyside = "";
            }

            if (exercise_name.equalsIgnoreCase("Isometric")) {
                if (emg_percentage > 100) {
                    SessionStatements.setText("The peak EMG is Excellent!");
                } else if (emg_percentage < 100 && emg_percentage > 75) {
                    SessionStatements.setText("The peak EMG is Excellent!");
                } else if (emg_percentage <= 75 && emg_percentage > 50) {
                    SessionStatements.setText("The peak EMG is good!");
                } else if (emg_percentage <= 50 && emg_percentage > 25) {
                    SessionStatements.setText("The peak EMG is fair!");
                } else if (emg_percentage <= 25 && emg_percentage > 0) {
                    SessionStatements.setText("The peak EMG is poor!");
                } else if (emg_percentage < 0) {
                    SessionStatements.setText("The peak EMG is poor!");
                }
            }
            if (orientation.equalsIgnoreCase(Healthyside)) {

                SessionStatements.setText("This data will be used for report generation to give accurate insights and better comparison with the affected side.");
            }


//                          tv_rom_perc.setText("100");
            if ((int) emg_percentage >= 0 && (int) emg_percentage <= 25) {
                tv_emg_percentage.setTextColor(Color.parseColor("#FE0302"));
                tv_emg_percentage.setText("Poor");

            } else if ((int) emg_percentage > 25 && (int) emg_percentage <= 50) {
                tv_emg_percentage.setTextColor(Color.parseColor("#E6A000"));
                tv_emg_percentage.setText("Fair");


            } else if ((int) emg_percentage > 50 && (int) emg_percentage <= 75) {
                tv_emg_percentage.setTextColor(Color.parseColor("#00B286"));
                tv_emg_percentage.setText("Good");


            } else if ((int) emg_percentage > 75) {
                tv_emg_percentage.setTextColor(Color.parseColor("#00B286"));
                tv_emg_percentage.setText("Excellent");

            }

            if (!HaveEMGNormatives) {
                ProgressBar_Outer_Layout.setVisibility(View.GONE);
                tv_emg_percentage.setText("-");
                tv_emg_percentage.setTextColor(Color.parseColor("#012E57"));
                if (orientation.equalsIgnoreCase(patient_injured)) {
                    int IntRomPercentage = rom_percentage > 100 ? 100 : (int) rom_percentage;
                    SessionStatements.setText("Joint Mobility is " + IntRomPercentage + "% of the ideal value");
                } else {
                    SessionStatements.setText("This data will be used for report generation to give accurate insights and better comparison with the affected side.");
                }
                if (exercise_name.equalsIgnoreCase("Isometric")) {
                    if (orientation.equalsIgnoreCase(patient_injured)) {
                        StatementOuterView.setVisibility(View.GONE);
                        SessionStatements.setText("");
                    } else if (patient_injured.equalsIgnoreCase("Bi-Lateral")) {
                        StatementOuterView.setVisibility(View.GONE);
                        SessionStatements.setText("");
                    } else {
                        SessionStatements.setText("This data will be used for report generation to give accurate insights and better comparison with the affected side.");
                    }
                }

            }

            int emg_percentage_convert, rom_percentage_convert;
            emg_percentage_convert = (int) emg_percentage;
            if (emg_percentage > 100) {
                emg_percentage_convert = 100;
            }
            rom_percentage_convert = (int) rom_percentage;
            if (rom_percentage_convert > 100) {
                rom_percentage_convert = 100;
            }
            double semi_perc;
            if (exercise_name.equalsIgnoreCase("Isometric")) {
                if (emg_percentage > 100) {
                    semi_perc = 100;
                } else {
                    semi_perc = emg_percentage;
                }
            } else {
                semi_perc = (emg_percentage_convert + rom_percentage_convert) / 2;
            }


//                            Log.e("1111111111111111111111111111111", String.valueOf(semi_perc));
            if (semi_perc < 100) {
                semiCircleArcProgressBar.setPercent((int) semi_perc);
                progress_bar_data.setText(String.valueOf((int) semi_perc).concat("%"));
            } else {
                semiCircleArcProgressBar.setPercent(100);
                progress_bar_data.setText("100%");
            }

            if ((bodypart.equalsIgnoreCase("Elbow") || bodypart.equalsIgnoreCase("Knee")) && exercise_name.equalsIgnoreCase("Extension")) {
                PremiumDegreeRemaining1.setVisibility(View.VISIBLE);
                PremiumDegreeRemaining2.setVisibility(View.VISIBLE);
                if ((int) rom_percentage >= 0 && (int) rom_percentage <= 88) {
                    tv_current_rom.setTextColor(Color.parseColor("#FE0302"));
                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                } else if ((int) rom_percentage > 88 && (int) rom_percentage <= 92) {
                    tv_current_rom.setTextColor(Color.parseColor("#E6A000"));
                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                } else if ((int) rom_percentage > 92) {
                    tv_current_rom.setTextColor(Color.parseColor("#00B286"));
                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");

                }
            } else {
                PremiumDegreeRemaining1.setVisibility(View.GONE);
                PremiumDegreeRemaining2.setVisibility(View.GONE);
                if ((int) rom_percentage >= 0 && (int) rom_percentage <= 33) {
                    tv_current_rom.setTextColor(Color.parseColor("#FE0302"));
                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                } else if ((int) rom_percentage > 34 && (int) rom_percentage <= 66) {
                    tv_current_rom.setTextColor(Color.parseColor("#E6A000"));
                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");


                } else if ((int) rom_percentage > 67 && (int) rom_percentage <= 100) {
                    tv_current_rom.setTextColor(Color.parseColor("#00B286"));
                    tv_rom_perc1.setText(String.valueOf((int) rom_percentage) + "%");

                } else if ((int) rom_percentage > 100) {
                    tv_current_rom.setTextColor(Color.parseColor("#00B286"));
                    tv_rom_perc1.setText("100%");
                }
            }


            if (orientation.equalsIgnoreCase("Left")) {
                oppositeside = "Right";
            } else if (orientation.equalsIgnoreCase("Right")) {
                oppositeside = "Left";
            }
            SessionSummeryTableData sessionSummeryTableData = new SessionSummeryTableData(patientid, json_phizioemail, oppositeside, bodypart, bodyOrientation, exercise_name, muscle_name);
            Call<SessionSummeryDataRec> sessionSummeryDataRecCall = getDataService.session_summery_table_data(sessionSummeryTableData);

            sessionSummeryDataRecCall.enqueue(new Callback<SessionSummeryDataRec>() {
                @Override
                public void onResponse(Call<SessionSummeryDataRec> call, Response<SessionSummeryDataRec> response) {
                    if (response.code() == 200) {
                        SessionSummeryDataRec res = response.body();
                        String MaxEMG = res.getMax_emg();
                        String MaxROM = res.getMax_rom();
                        Log.e("hellojbdbchdbucbfdbvubuf", "SAMPLEVALUTETOCHECK");

                        if (!MaxEMG.equalsIgnoreCase("-") && (patient_injured.equalsIgnoreCase(orientation))) {
                            alertsetvisible.setVisibility(View.GONE);
                        }
                        if (orientation.equalsIgnoreCase(Healthyside)) {
                            alertsetvisible.setVisibility(View.GONE);
                        }

                        if (patient_injured.equalsIgnoreCase("Bi-Lateral")) {
                            alertsetvisible.setVisibility(View.GONE);
                        }
                        try {

                            tv_goal_reacted.setText(MaxROM);
                            tv_healthy_emg.setText(MaxEMG);
                            if (MaxEMG.equalsIgnoreCase("-")) {

                            } else {

                                tv_healthy_emg.setText(MaxEMG.concat(" μV"));
                            }
                            if (MaxROM.equalsIgnoreCase("-")) {

                            } else {
                                tv_goal_reacted.setText(MaxROM.concat("°"));

                            }

                            if (patient_injured.equalsIgnoreCase("Left") && orientation.equalsIgnoreCase("Left")) {
                                CurrentAffectedSide.setText("Left");
                                ExerciseOtherSide.setText("Right");
                            } else if (patient_injured.equalsIgnoreCase("Left") && orientation.equalsIgnoreCase("Right")) {
                                CurrentAffectedSide.setText("Right");
                                ExerciseOtherSide.setText("Left");
                            }
                            if (patient_injured.equalsIgnoreCase("Right") && orientation.equalsIgnoreCase("Right")) {
                                CurrentAffectedSide.setText("Right");
                                ExerciseOtherSide.setText("Left");
                            } else if (patient_injured.equalsIgnoreCase("Right") && orientation.equalsIgnoreCase("Left")) {
                                CurrentAffectedSide.setText("Left");
                                ExerciseOtherSide.setText("Right");
                            }

                            if (patient_injured.equalsIgnoreCase("Bi-Lateral") && orientation.equalsIgnoreCase("Left")) {
                                CurrentAffectedSide.setText("Left");
                                ExerciseOtherSide.setText("Right");
                            } else if (patient_injured.equalsIgnoreCase("Bi-Lateral") && orientation.equalsIgnoreCase("Right")) {
                                CurrentAffectedSide.setText("Right");
                                ExerciseOtherSide.setText("Left");
                            }


//                      int curr_emg=Integer.parseInt((String) tv_current_emg.getText());
//                        int healthy_emg=Integer.parseInt(MaxEMG);
//                        int healthy_rom=Integer.parseInt(MaxROM);
//                            new CountDownTimer(10000, 1000) {
//                                @Override
//                                public void onTick(long l) {
//
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    int emg_percentage = (maxEmgValue/healthy_emg)*100;
//                                    Log.e("2222222222222222222222222",String.valueOf(emg_percentage));
//                                    int rom_percentage = (rom_value/healthy_rom)*100;
//                                    Log.e("1111111111111111111111111",String.valueOf(rom_percentage));
//                                    int goal_reached = (emg_percentage+rom_percentage)/2;
//                                    if(goal_reached >= 100){
//                                        goal_reached = 100;
//                                    }
//                                    Log.e("333333333333333333333333333",String.valueOf(goal_reached));
//                                    tv_healthy_emg.setText(MaxEMG);
//                                    tv_goal_reacted.setText(MaxROM);
////                        tv_rom_perc.setText(ROM_Percentage);
//                                    int max_percentage = 0;
//
//                                    if(emg_percentage>100){
//                                        max_percentage=100;
//                                    }
//

//                                }
//                            }.start();


//                        Log.e("9999999999999999999999999",String.valueOf(NormativeValue));
//                        Log.e("7777777777777777777777777777", String.valueOf(percentage));

                        } catch (Exception e) {
                            Log.e("Exception", String.valueOf(e));
                        }
                    }

                }

                @Override
                public void onFailure(Call<SessionSummeryDataRec> call, Throwable t) {
                    Log.e("bydcydycbydbycydbc", "Fail");
                }
            });


        } catch (Exception e) {
            Log.e("Exception occure", String.valueOf(e));
        }

        /**/


        try {
            if (Integer.parseInt(normative_value_emg_data) == 0) {
//                progress_bar_data.setText("0");
            } else {

                if (exercise_name.equalsIgnoreCase("Isometric")) {
                    float upper_value_emg = maxEmgValue;
                    float lower_value_emg = Integer.parseInt(normative_value_emg_data);
                    Log.e("goal_reached_upper_emg", String.valueOf(upper_value_emg));
                    Log.e("goal_reached_lower_emg", String.valueOf(lower_value_emg));
                    float value_max_emg = 0;
                    if (upper_value_emg > lower_value_emg) {
                        value_max_emg = 1;
                    } else {
                        value_max_emg = upper_value_emg / lower_value_emg;
                    }
                    Log.e("goal_reached_emg", String.valueOf(value_max_emg));

                    float goal_reached = Math.toIntExact(Math.round((100) * (value_max_emg)));
//                    progress_bar_data.setText(String.valueOf((int) goal_reached));
//                    Progress_Bar.setPercent(50);

                } else {

                    float upper_value_emg = maxEmgValue;
                    float lower_value_emg = Integer.parseInt(normative_value_emg_data);
                    Log.e("goal_reached_upper_emg", String.valueOf(upper_value_emg));
                    Log.e("goal_reached_lower_emg", String.valueOf(lower_value_emg));
                    float value_max_emg = 0;
                    if (upper_value_emg > lower_value_emg) {
                        value_max_emg = 1;
                    } else {
                        value_max_emg = upper_value_emg / lower_value_emg;
                    }
                    Log.e("goal_reached_emg", String.valueOf(value_max_emg));


                    float upper_value_rom = maxAngle - minAngle;
                    float lower_value_rom = Integer.parseInt(max_angle_selected);
                    Log.e("goal_reached_upper_rom", String.valueOf(upper_value_rom));
                    Log.e("goal_reached_lower_rom", String.valueOf(lower_value_rom));
                    float value_max_rom = 0;
                    if (upper_value_rom > lower_value_rom) {
                        value_max_rom = 1;

                    } else {
                        value_max_rom = upper_value_rom / lower_value_rom;
                    }
                    Log.e("goal_reached_rom", String.valueOf(value_max_rom));


                    float goal_reached = Math.toIntExact(Math.round((0.5 * 100) * (value_max_emg + value_max_rom)));

                    Log.e("goal_reached", String.valueOf(goal_reached));

//                    progress_bar_data.setText(String.valueOf(goal_reached));


                }


            }
        } catch (NumberFormatException e) {
//            progress_bar_data.setText("0");
        }


        if (server_emg_data.equalsIgnoreCase("no")) {

        } else {
            if (maxEmgValue > Integer.parseInt(server_emg_data)) {
                status_web.setImageResource(R.drawable.up_arrow);
            } else {
                status_web.setImageResource(R.drawable.down_arrow);
            }
        }


        pb_max_emg.setMax(Integer.parseInt(normative_value_emg_data));
        pb_max_emg.setProgress(maxEmgValue);
        pb_max_emg.setEnabled(false);
        LayerDrawable bgShape = (LayerDrawable) pb_max_emg.getProgressDrawable();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bgShape.findDrawableByLayerId(bgShape.getId(1)).setTint(emg_color);
        }


        tv_delete_pateint_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = tv_delete_pateint_session.getText().toString();
                if (type.toLowerCase().contains("delete")) {

                    // Custom notification added by Haaris
                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.notification_dialog_box);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    TextView notification_title = dialog.findViewById(R.id.notification_box_title);
                    TextView notification_message = dialog.findViewById(R.id.notification_box_message);

                    Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                    Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                    Notification_Button_ok.setText("Confirm");
                    Notification_Button_cancel.setText("Cancel");

                    // Setting up the notification dialog
                    notification_title.setText("Deleting a session");
                    notification_message.setText("Are you sure you want to delete the \n session from the list. Please Confirm");

                    // On click on Continue
                    Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_delete_pateint_session.setText("New Session");
                            ll_click_to_next.setVisibility(View.GONE);
                            Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            tv_delete_pateint_session.setAnimation(aniFade);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("phizioemail", phizioemail);
                                object.put("patientid", patientid);
                                object.put("heldon", dateString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MqttSync mqttSync = new MqttSync(mqtt_delete_pateint_session, object.toString());
                            new StoreLocalDataAsync(mqttSync).execute();
                            dialog.dismiss();

                        }
                    });
                    // On click Cancel
                    Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);

                    // End


                } else {
//                    report.dismiss();
                    ((Activity) context).finish();
                }
            }
        });

        report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (IS_SCEDULED_SESSIONS_COMPLETED) {
                    if (context != null)
                        ((MonitorActivity) context).sceduledSessionsHasBeenCompletedDialog();
                }
            }
        });
    }

    private void startActivityForResult(Intent enableBtIntent, int requestEnableBt) {
        if (requestEnableBt == 0) {
            bluetooth_status = true;
        } else {
            bluetooth_status = false;
        }
    }

    private void convertLayoutToPdf(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        // Create a file to store the PDF
        File pdfFile = new File(getApplicationContext().getExternalFilesDir(null), "layout.pdf");

        try {
            // Create a PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            // Draw the bitmap onto the PDF page
            Canvas pdfCanvas = page.getCanvas();
            pdfCanvas.drawBitmap(bitmap, 0, 0, null);

            // Finish the page and save the document
            document.finishPage(page);
            document.writeTo(new FileOutputStream(pdfFile));
            document.close();


            // Show a success message
            Toast.makeText(getApplicationContext(), "PDF saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void openReportActivity(String patientid, String patientname, String dateofjoin ) {
        if (NetworkOperations.isNetworkAvailable(context)) {
            Intent mmt_intent = new Intent(context, SessionReportActivity.class);
            mmt_intent.putExtra("patientid", patientid);
            mmt_intent.putExtra("patientname", patientname);
            mmt_intent.putExtra("dateofjoin", dateofjoin);
            mmt_intent.putExtra("phizioemail", phizioemail);
            ((Activity)context).startActivity(mmt_intent);

        } else {
            NetworkOperations.networkError(context);
        }
    }

    private void showToast(String nothing_selected) {
        Toast.makeText(context, nothing_selected, Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout ll_container = ((LinearLayout)v);
            LinearLayout parent = (LinearLayout) ll_container.getParent();
            for (int i=0;i<parent.getChildCount();i++){
                LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                TextView tv_childs = (TextView) ll_child.getChildAt(0);
                tv_childs.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
                tv_childs.setTextColor(ContextCompat.getColor(context,R.color.pitch_black));
            }
            TextView tv_selected = (TextView) ll_container.getChildAt(0);
            tv_selected.setBackgroundColor(Color.YELLOW);
            mmt_selected=tv_selected.getText().toString();
            tv_selected.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
            tv_selected.setTextColor(ContextCompat.getColor(context,R.color.white));
        }
    };

    /**
     * Sending data to the server and storing locally
     */
    public class StoreLocalDataAsync extends AsyncTask<Void,Void,Long> {
        private MqttSync mqttSync;
        public StoreLocalDataAsync(MqttSync mqttSync){
            this.mqttSync = mqttSync;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return PheezeeDatabase.getInstance(context).mqttSyncDao().insert(mqttSync);
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            new SendDataToServerAsync(mqttSync,id).execute();
        }
    }

    /**
     * Sending data to the server and storing locally
     */
    public class SendDataToServerAsync extends AsyncTask<Void, Void, Void> {
        private MqttSync mqttSync;
        private Long id;
        public SendDataToServerAsync(MqttSync mqttSync, Long id){
            this.mqttSync = mqttSync;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject object = new JSONObject(mqttSync.getMessage());
                object.put("id",id);
                if(NetworkOperations.isNetworkAvailable(context)){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if(mqttSync.getTopic()==mqtt_publish_update_patient_mmt_grade){
                        if(session_inserted_in_server){
                            MmtData data = gson.fromJson(object.toString(),MmtData.class);
                            repository.updateMmtData(data);
                        }
                        else {

                        }
                    } else  if(mqttSync.getTopic()==mqtt_delete_pateint_session){
                        if(session_inserted_in_server){
                            DeleteSessionData data = gson.fromJson(object.toString(),DeleteSessionData.class);
                            repository.deleteSessionData(data);
                        }
                        else {

                        }
                    }
                    else {
                        SessionData data = gson.fromJson(object.toString(),SessionData.class);
                        repository.insertSessionData(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * collects all the data of the session and sends to async task to send the data to the server and also to store locally.
     * @param emgJsonArray
     * @param romJsonArray
     */
    public void storeLocalSessionDetails( JSONArray emgJsonArray, JSONArray romJsonArray) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject();
                    object.put("heldon",dateString);
                    object.put("maxangle",maxAngle);
                    object.put("minangle",minAngle);
                    object.put("anglecorrected",angleCorrection);
                    object.put("maxemg",maxEmgValue);
                    object.put("holdtime",holdtime);
                    object.put("holdangle",hold_angle_session);
                    object.put("bodypart",bodypart);
                    object.put("sessiontime",sessiontime);
                    object.put("numofreps",numofreps);
                    object.put("numofsessions",sessionNo);
                    object.put("phizioemail",phizioemail);
                    object.put("patientid",patientid);
                    object.put("painscale","");
                    object.put("muscletone","");
                    object.put("exercisename",exercise_name);
                    object.put("commentsession","");
                    object.put("symptoms","");
                    object.put("activetime",actiontime);
                    object.put("orientation", orientation);
                    object.put("mmtgrade",mmt_selected);
                    object.put("bodyorientation",bodyOrientation);
                    object.put("sessiontype",session_type);
                    object.put("repsselected",repsselected);
                    object.put("musclename", muscle_name);
                    object.put("maxangleselected",max_angle_selected);
                    object.put("minangleselected",min_angle_selected);
                    object.put("maxemgselected",max_emg_selected);
                    object.put("sessioncolor",ValueBasedColorOperations.getCOlorBasedOnTheBodyPartExercise(bodypart,exercise_selected_position,maxAngle,minAngle,context));
                    Gson gson = new GsonBuilder().create();
                    Lock lock = new ReentrantLock();
                    lock.lock();
                    SessionData data = gson.fromJson(object.toString(),SessionData.class);
                    data.setEmgdata(emgJsonArray);
                    data.setRomdata(romJsonArray);
                    data.setActivityList(PatientActivitySingleton.getInstance().getactivitylist());
                    object = new JSONObject(gson.toJson(data));
                    MqttSync sync = new MqttSync(mqtt_publish_add_patient_session_emg_data,object.toString());
                    lock.unlock();
                    new StoreLocalDataAsync(sync).execute();
                    int numofsessions = Integer.parseInt(sessionNo);
                    repository.setPatientSessionNumber(String.valueOf(numofsessions),patientid);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    MqttSyncRepository.OnSessionDataResponse onSessionDataResponse = new MqttSyncRepository.OnSessionDataResponse() {
        @Override
        public void onInsertSessionData(Boolean response, String message) {
            if(response_data!=null){
                if(response){
                    session_inserted_in_server = true;
                }
                response_data.onInsertSessionData(response,message);
            }
        }



        @Override
        public void onSessionDeleted(Boolean response, String message) {
            if(response_data!=null){
                response_data.onSessionDeleted(response,message);
            }
        }

        @Override
        public void onMmtValuesUpdated(Boolean response, String message) {
            if(response_data!=null){
                response_data.onMmtValuesUpdated(response,message);
            }
        }

        @Override
        public void onCommentSessionUpdated(Boolean response) {
            if(response_data!=null){
                response_data.onCommentSessionUpdated(response);
            }
        }

    };
    private void new_pheezee_printer(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {

        }
        SessionSummaryPopupWindow.BluetoothTask bluetoothTask = new SessionSummaryPopupWindow.BluetoothTask();
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
    public void setOnSessionDataResponse(MqttSyncRepository.OnSessionDataResponse response){
        this.response_data = response;
    }





}