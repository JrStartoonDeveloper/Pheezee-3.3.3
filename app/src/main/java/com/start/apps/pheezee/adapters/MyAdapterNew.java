package com.start.apps.pheezee.adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.activities.SortHistoryYear;
import com.start.apps.pheezee.activities.ViewPatientScreen;
import com.start.apps.pheezee.popup.ViewPopUpWindow;
import com.start.apps.pheezee.repository.MqttSyncRepository;

import org.json.JSONArray;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import start.apps.pheezee.R;

public class MyAdapterNew extends RecyclerView.Adapter<MyAdapterNew.MyViewHolder> implements MqttSyncRepository.onServerResponse {

    String data1[], data2[], data3[], data4[];
    ArrayList<String> imgurl;
    Context context;

    MqttSyncRepository repository;



    public MyAdapterNew(Context ct, String[] car_name_arr, String[] car_des_arr, String[] car_col_arr, String[] car_id_arr, ArrayList<String> img_url){
        data1 = car_name_arr;
        data2 = car_des_arr;
        data3 = car_col_arr;
        data4 = car_id_arr;
        context = ct;
        imgurl = img_url;

    }

    @Override
    public void onDeletePateintResponse(boolean response) {

    }

    @Override
    public void onUpdatePatientDetailsResponse(boolean response) {

    }

    @Override
    public void onUpdatePatientStatusResponse(boolean response) {

    }

    @Override
    public void onSyncComplete(boolean response, String message) {

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        TextView text3;

        TextView text4;
        CircleImageView myimage;

        LinearLayout datapassing_t;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.car_name);
            text2 = itemView.findViewById(R.id.car_des);
            text3 = itemView.findViewById(R.id.text_year);
            text4 = itemView.findViewById(R.id.patient_id);
            myimage = itemView.findViewById(R.id.imageView);
            datapassing_t = itemView.findViewById(R.id.datapassing_t);

        }

        public ImageView getImage(){
            return this.myimage;
        }

    }

    @NonNull
    @Override
    public MyAdapterNew.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_template,parent,false);



        return new MyAdapterNew.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapterNew.MyViewHolder holder, int position) {


        try {
            holder.text1.setText(data1[position]);
            holder.text2.setText(data2[position]);
            holder.text3.setText(data3[position]);
            holder.text4.setText(data4[position]);

            if(holder.text3.getText().toString().equalsIgnoreCase("0")){
                holder.text3.setVisibility(View.GONE);
            }else{
                holder.text3.setVisibility(View.VISIBLE);
            }






            holder.myimage.layout(0, 0, 0, 0);

            Glide.with(context)
                    .load(imgurl.get(position))
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(150)))//this line of code help to
                    .placeholder(R.drawable.icon_no_image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.icon_no_image)
//                    .override(600, 500)
                    .into(holder.getImage());

            holder.datapassing_t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String patient_id = holder.text4.getText().toString();
                    Intent view_patient = new Intent(getApplicationContext(), ViewPatientScreen.class);
                    view_patient.putExtra("patient_id", patient_id);
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putString("patient_id", patient_id);
                    editor.apply();
                    context.startActivity(view_patient);
                }
            });




        }catch (NumberFormatException e){

        }



    }




    @Override
    public int getItemCount() {
        return data1.length;
    }




}

