package com.start.apps.pheezee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.start.apps.pheezee.classes.CircularRevealTransition;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import start.apps.pheezee.R;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles,titiles2;
    List<Integer> images;
    LayoutInflater inflater;

    public Adapter(Context ctx, List<String> titles,List<String> titles2, List<Integer> images){
        this.titles = titles;
        this.titiles2 = titles2;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title.setText(titles.get(position));
            holder.titiles2.setText(titiles2.get(position));
            holder.gridIcon.setImageResource(images.get(position));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,titiles2;

//        ImageView gridIcon;

        CircleImageView gridIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView2);
            titiles2 = itemView.findViewById(R.id.textView3);
            gridIcon = itemView.findViewById(R.id.imageView2);


//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int Kranthi = getAdapterPosition();
//                    if(Kranthi == 0){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, Temple.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    if(Kranthi == 1){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, TouristPlaces.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//
//                    if(Kranthi == 2){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, culture.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    if(Kranthi == 3){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, EmgService.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    if(Kranthi == 4){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, ImportantPlaces.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    if(Kranthi == 5){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, Others.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    if(Kranthi == 6){
//                        Context context = v.getContext();
//                        String geoUri = "http://maps.google.com/maps?q=loc:" + 18.4675335 + "," + 78.870309 ;
//                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 18.4681062,78.867226);
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//
//                    if(Kranthi == 9){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, Faqs.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//
//                    if(Kranthi == 8){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, AppInfo.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    if(Kranthi == 7){
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, Donations.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//
//                }
//            });
        }
    }

//    public boolean isConnected() {
//        boolean connected = false;
//        try {
//            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo nInfo = cm.getActiveNetworkInfo();
//            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
//            return connected;
//        } catch (Exception e) {
//            Log.e("Connectivity Exception", e.getMessage());
//        }
//        return connected;
//    }
}

