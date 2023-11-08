package com.start.apps.pheezee.adapters;

import static com.start.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.start.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.start.apps.pheezee.activities.SessionReportActivity.phizioemail;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.nayaastra.skewpdfview.SkewPdfView;
import com.start.apps.pheezee.activities.BluetoothScreen;
import com.start.apps.pheezee.classes.SessionListClass;
import com.start.apps.pheezee.pojos.GetReportDataResponse;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.utils.DateOperations;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.WriteResponseBodyToDisk;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import start.apps.pheezee.R;


public class SessionReportListArrayAdapter extends ArrayAdapter<SessionListClass> implements MqttSyncRepository.OnReportDataResponseListner {

    private TextView tv_s_no,tv_date, tv_exercise_no,tv_download_date;

    LinearLayout Error_Message;

    private ImageView share_icon ,view_button_i, print_button_i, print_button_i_2;

    private Button download_icon,download_icon_disable;

    SkewPdfView skewPdfView;

    GetDataService getDataService;

    private AdapterView.OnItemClickListener onItemClickListener;

    private Context context;
    public ArrayList<SessionListClass> mSessionArrayList;
    MqttSyncRepository repository;
    ProgressDialog report_dialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String bodypart, exercisename, musclename, maxemg, maxangle, minangle, maxangleselected, minangleselected;

    String count_status_number;

    String[] strArrayone , strArraytwo,strArraythree,strArrayfour,strArrayfive,strArraysix;

    int i, j, k;

    public SessionReportListArrayAdapter(Context context, ArrayList<SessionListClass> mSessionArrayList, Application application){
        super(context, R.layout.sessionsreport_listview_model, mSessionArrayList);
        Configuration config = ((Activity)context).getResources().getConfiguration();
        View layout;
        this.mSessionArrayList=mSessionArrayList;
        repository = new MqttSyncRepository(application);
        repository.setOnReportDataResponseListener(this);
        this.context = context;

    }



    public void updateList(ArrayList<SessionListClass> mSessionArrayList){
        this.mSessionArrayList.clear();
        this.mSessionArrayList.addAll(mSessionArrayList);
        this.notifyDataSetChanged();
    }

    public String getCountStatusNumber() {
        return count_status_number;
    }

    public void setCountStatusNumber(String countStatusNumber) {
        this.count_status_number = countStatusNumber;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            Configuration config = ((Activity)context).getResources().getConfiguration();
            View layout;
            if (config.screenWidthDp >= 600) {
                convertView = vi.inflate(R.layout.sessionreport_listview_model_tablet, null);
            } else {
                convertView = vi.inflate(R.layout.sessionsreport_listview_model, null);
            }
//            convertView = vi.inflate(R.layout.sessionsreport_listview_model, null);
//
//            holder = new ViewHolder();
//            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
//            holder.name.setChecked(true);
//            convertView.setTag(holder);
//
//            holder.name.setOnClickListener( new View.OnClickListener() {
//                public void onClick(View v) {
//                    CheckBox cb = (CheckBox) v ;
//                    SessionListClass session_list_element = (SessionListClass) cb.getTag();
//                    session_list_element.setSelected(cb.isChecked());
//                }
//            });
        }
//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }


        SessionListClass selected_item = mSessionArrayList.get(position);
//        holder.name.setChecked(selected_item.isSelected());
//        holder.name.setTag(selected_item);

        tv_s_no = convertView.findViewById(R.id.tv_s_no);
        tv_date = convertView.findViewById(R.id.tv_date);
        tv_exercise_no = convertView.findViewById(R.id.tv_exercise_no);
        Error_Message=convertView.findViewById(R.id.report_alert);
        tv_download_date = convertView.findViewById(R.id.tv_download_date);
        view_button_i = convertView.findViewById(R.id.view_button);
        print_button_i = convertView.findViewById(R.id.print_icon);
        print_button_i_2 = convertView.findViewById(R.id.print_icon_2);
        share_icon = convertView.findViewById(R.id.share_icon);
        download_icon = convertView.findViewById(R.id.download_icon);
        download_icon_disable = convertView.findViewById(R.id.download_icon_disable);
        tv_s_no.setText(String.valueOf(mSessionArrayList.size()-position)+".");
        String CustomerType,StartDate,EndDate,ReportGenerated,ReportAccessible;
        //Date
        String Kranthi = mSessionArrayList.get(position).getHeldon();
        Kranthi = Kranthi.replace("-","/");
        String test = mSessionArrayList.get(position).getHeldon();
        test = test.replace("-","/");
        String[] date_split = test.split("/");
        test = date_split[2]+"/"+date_split[1]+"/"+ date_split[0];

        test = DateOperations.getDateInMonthAndDateYear(test);
        tv_date.setText(test);


//        if(date_split[1]=="01"){
//            test = "Jan"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="02"){
//            test = "Feb"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="03"){
//            test = "Mar"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="04"){
//            test = "April"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="05"){
//            test = "May"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="06"){
//            test = "June"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="07"){
//            test = "July"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="08"){
//            test = "August"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="09"){
//            test = "September"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="10"){
//            test = "October"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="11"){
//            test = "November"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }else if(date_split[1]=="12"){
//            test = "December"+ date_split[1]+"'"+date_split[0];
//            tv_date.setText(test);
//        }



//        test = DateOperations.getDateInMonthAndDate(test);
        try{
            SharedPreferences view_data_s = PreferenceManager.getDefaultSharedPreferences(context);
            CustomerType = view_data_s.getString("CustomerType", "");
            StartDate = view_data_s.getString("StartDate", "");
            EndDate = view_data_s.getString("EndDate", "");
            ReportGenerated = view_data_s.getString("ReportGenerated", "");
            ReportAccessible = view_data_s.getString("ReportAccessible", "");

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Month value is 0-based, so adding 1
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a string representation of the date
            String currentDate1 = day + "-" + month + "-" + year;

            String currentDateStr = currentDate1;
            String lastDateStr = EndDate;
            String firstDateStr= StartDate;

            if (currentDateStr != null && !currentDateStr.equalsIgnoreCase("null") && !currentDateStr.equalsIgnoreCase("") &&
                    lastDateStr != null && !lastDateStr.equalsIgnoreCase("null") && !lastDateStr.equalsIgnoreCase("")) {
                LocalDate currentDate = LocalDate.parse(currentDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                LocalDate lastDate = LocalDate.parse(lastDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                LocalDate firstDate= LocalDate.parse(firstDateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
                long difference_In_Days = ChronoUnit.DAYS.between(currentDate, lastDate);

                Log.e("1112323345463543654534",CustomerType);
                boolean b = (Integer.valueOf(ReportGenerated) > Integer.valueOf(ReportAccessible));
                if(CustomerType.equalsIgnoreCase("null")||CustomerType.equalsIgnoreCase("")){
                    if(mSessionArrayList.get(position).getMuscle_name() == null){
                        download_icon.setVisibility(View.GONE);
                        download_icon_disable.setVisibility(View.VISIBLE);
                    }
                }
                else if(CustomerType.equalsIgnoreCase("subscription_mode")){
                    if(b){
                        if(mSessionArrayList.get(position).getMuscle_name() == null){
                            download_icon.setVisibility(View.GONE);
                            download_icon_disable.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else if(CustomerType.equalsIgnoreCase("time_period_mode")){
                    Log.e("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq", String.valueOf(difference_In_Days));
                    if(difference_In_Days<0){
                        if(mSessionArrayList.get(position).getMuscle_name() == null){
                            download_icon.setVisibility(View.GONE);
                            download_icon_disable.setVisibility(View.VISIBLE);
                        }
                    }else{
                        download_icon.setVisibility(View.VISIBLE);
                        download_icon_disable.setVisibility(View.GONE);
                    }
                }
                else if(CustomerType.equalsIgnoreCase("grace_period")){
                    download_icon.setVisibility(View.VISIBLE);
                    download_icon_disable.setVisibility(View.GONE);
                }
                else if(CustomerType.equalsIgnoreCase("force_stop")){
                    download_icon.setVisibility(View.GONE);
                    download_icon_disable.setVisibility(View.VISIBLE);
                }
            }
            else{
                download_icon.setVisibility(View.GONE);
                download_icon_disable.setVisibility(View.VISIBLE);
            }


        }catch (Exception e){

        }

        // Exercise
        tv_exercise_no.setText(mSessionArrayList.get(position).getSession_time());
        int count=Integer.valueOf(mSessionArrayList.get(position).getSession_time());
        if(count<20){
            if(mSessionArrayList.get(position).getMuscle_name() != null) {
                tv_download_date.setText("Report downloaded");
                tv_download_date.setTextColor(context.getResources().getColor(R.color.background_green));
                count_status_number = "false";
            }else
            {
//            tv_download_date.setText("View report by downloading");
                tv_download_date.setText("Report download pending");
                tv_download_date.setTextColor(context.getResources().getColor(R.color.red));
                count_status_number = "true";

//

            }
        }
        else {
            Error_Message.setVisibility(View.VISIBLE);
            tv_exercise_no.setTextColor(context.getResources().getColor(R.color.red));
            tv_exercise_no.setTextSize(18);
            tv_download_date.setVisibility(View.GONE);

        }
        // Downloaded date - Using muscle name data as a substitute


        download_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkOperations.isNetworkAvailable(context)) {
                    if(mSessionArrayList.get(position).getMuscle_name() != null) {
                        count_status_number = "false";
                    }else {
                        count_status_number = "true";
                    }

                    getDayReport(mSessionArrayList.get(position).getHeldon(),count_status_number);

                } else {
                    NetworkOperations.networkError(context);
                }
            }
        });

        view_button_i.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(context)){
                    if(mSessionArrayList.get(position).getMuscle_name() != null) {
                        count_status_number = "false";
                    }else {
                        count_status_number = "true";
                    }

                    getDayReport(mSessionArrayList.get(position).getHeldon(),count_status_number);

                }
                else {
                    NetworkOperations.networkError(context);
                }
            }
        });








        share_icon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(context))
                    getDayReportshare(mSessionArrayList.get(position).getHeldon(),context);
                else
                    NetworkOperations.networkError(context);
            }
        });

        return convertView;

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    /**
     * Retrofit call to get the report pdf from the server
     * @param date
     */



    private void getDayReport(String date, String musclename){
        Log.e("rrrrrrrrrrrrrrrrrrrrrrr",musclename);
//        getreport_thermal_printer
//        getreport_testing_kranthi_new
        String url = "/getreport/"+patientId+"/"+phizioemail+"/" + date;
        String new_url = "/getreport_thermal_printer/"+patientId+"/"+phizioemail+"/" + date;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("kranthi_date",date);
        editor.apply();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(WriteResponseBodyToDisk.checkFileInDisk(patientId+date) && !sharedPreferences.getBoolean(patientId+date,true))
        {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", WriteResponseBodyToDisk.GetFileFromDisk(patientId+date)), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                context.startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
            return;
        }


        report_dialog = new ProgressDialog(context);
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setMessage("Please Wait");    //("Generating session report held on "+date+", please wait..");
        report_dialog.show();
        report_dialog.dismiss();
        Intent session = new Intent(context,BluetoothScreen.class);
        session.putExtra("patientId", patientId);
        session.putExtra("date", date);
        session.putExtra("phizioemail", phizioemail);
        session.putExtra("count_status",musclename);
        context.startActivity(session);
        getDayReportThermalPrinter(date);
        repository.getDayReport(url,patientId+date);
    }

    private void getDayReportThermalPrinter(String date){
        String url = "/getreport_thermal_printer/"+patientId+"/"+phizioemail+"/" + date;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(WriteResponseBodyToDisk.checkFileInDisk(patientId+date) && !sharedPreferences.getBoolean(patientId+date,true))
        {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", WriteResponseBodyToDisk.GetFileFromDisk(patientId+date)), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                context.startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
            return;
        }

        report_dialog = new ProgressDialog(context);
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setMessage("Please Wait");    //("Generating session report held on "+date+", please wait..");
        report_dialog.show();
        report_dialog.dismiss();
        repository.getDayReportThermalPrinter(url,patientId+date+"thermal_printer");
    }
    /**
     * Retrofit call to get the report pdf from the server
     * @param date
     */


    private void getDayReportshare(String date, Context context){
        String url = "/getreport/"+patientId+"/"+phizioemail+"/" + date;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(WriteResponseBodyToDisk.checkFileInDisk(patientId+date) && !sharedPreferences.getBoolean(patientId+date,true))
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", WriteResponseBodyToDisk.GetFileFromDisk(patientId+date));
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(shareIntent);
            return;
        }

        report_dialog = new ProgressDialog(context);
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setMessage("Please Wait");       // ("Sharing session report held on "+date+", please wait..");
        report_dialog.show();
        repository.getDayReportshare(url,patientName+"-day",context,report_dialog);

    }

    public void sendToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void sendShortToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReportDataReceived(GetReportDataResponse array, boolean response) {

    }

    @Override
    public void onDayReportReceived(File file, String message, Boolean response) {
        report_dialog.dismiss();
        if(response){
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", file), "application/pdf");
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                context.startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
        else {
            //    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }


}
