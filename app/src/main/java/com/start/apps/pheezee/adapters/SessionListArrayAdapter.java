package com.start.apps.pheezee.adapters;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import start.apps.pheezee.R;
import com.start.apps.pheezee.classes.SessionListClass;


import java.util.ArrayList;


public class SessionListArrayAdapter extends ArrayAdapter<SessionListClass> {

    private TextView tv_bodypart_exercise,tv_muscle_name, tv_orientation_position, tv_session_time,tv_best_session;

    private ImageView tv_body_image,tv_selected_image;
    private Context context;
    public ArrayList<SessionListClass> mSessionArrayList;

    public SessionListArrayAdapter(Context context, ArrayList<SessionListClass> mSessionArrayList){
        super(context, R.layout.sessions_listview_model, mSessionArrayList);
        this.mSessionArrayList=mSessionArrayList;
        this.context = context;
    }

    public void updateList(ArrayList<SessionListClass> mSessionArrayList){
        this.mSessionArrayList.clear();
        this.mSessionArrayList.addAll(mSessionArrayList);
        this.notifyDataSetChanged();
    }

    public int getLength(){
        return mSessionArrayList.size();
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.sessions_listview_model, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.name.setChecked(true);
            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    SessionListClass session_list_element = (SessionListClass) cb.getTag();
                    session_list_element.setSelected(cb.isChecked());

                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SessionListClass selected_item = null;
        try {


            selected_item = mSessionArrayList.get(position);
            holder.name.setChecked(selected_item.isSelected());
            holder.name.setTag(selected_item);

            tv_bodypart_exercise = convertView.findViewById(R.id.tv_bodypart_exercise);
            tv_muscle_name = convertView.findViewById(R.id.tv_muscle_name);
            tv_orientation_position = convertView.findViewById(R.id.tv_session_position);
            tv_session_time = convertView.findViewById(R.id.tv_session_time);
            tv_body_image = convertView.findViewById(R.id.effected_side_image);
            tv_selected_image = convertView.findViewById(R.id.selected_image);
            tv_best_session = convertView.findViewById(R.id.tv_session_best_exercise);
            tv_bodypart_exercise.setText(mSessionArrayList.get(position).getBodypart() + " " + mSessionArrayList.get(position).getExercise());
            tv_muscle_name.setText(mSessionArrayList.get(position).getMuscle_name());
            tv_orientation_position.setText(mSessionArrayList.get(position).getPosition() + "-" + mSessionArrayList.get(position).getOrientation());
            tv_session_time.setText(mSessionArrayList.get(position).getSession_time() + "ec");
            SharedPreferences preferences_injured = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
            String patient_injured = preferences_injured.getString("patient injured", "");
            if (mSessionArrayList.get(position).getBestexercise().equalsIgnoreCase("Yes")) {
                tv_best_session.setVisibility(View.VISIBLE);
                tv_selected_image.setBackgroundResource(R.drawable.exercise_seletednew);
            }
            if (patient_injured.equalsIgnoreCase("Left") && mSessionArrayList.get(position).getPosition().equalsIgnoreCase("Left")) {
                Log.e("111222333444", mSessionArrayList.get(position).getPosition());
                tv_body_image.setBackgroundResource(R.drawable.left_side_injured);
//            body_potion.setImageResource(R.drawable.left_side_injured);
//            Toast.makeText(getApplicationContext(),patient_injured,Toast.LENGTH_SHORT).show();

            } else if (patient_injured.equalsIgnoreCase("Left") && mSessionArrayList.get(position).getPosition().equalsIgnoreCase("Right")) {
//            body_potion.setImageResource(R.drawable.right_side_injured);
                tv_body_image.setBackgroundResource(R.drawable.ref_right_side_injured);
                tv_best_session.setTextColor(Color.parseColor("#44C02F"));
            } else if (patient_injured.equalsIgnoreCase("Right") && mSessionArrayList.get(position).getPosition().equalsIgnoreCase("Right")) {
                tv_body_image.setBackgroundResource(R.drawable.right_side_injured);
            } else if (patient_injured.equalsIgnoreCase("Right") && mSessionArrayList.get(position).getPosition().equalsIgnoreCase("Left")) {
                tv_body_image.setBackgroundResource(R.drawable.ref_left_side_injured);
                tv_best_session.setTextColor(Color.parseColor("#44C02F"));
            } else if (patient_injured.equalsIgnoreCase("Bi-Lateral")) {
                tv_body_image.setBackgroundResource(R.drawable.billateral_side_injured);
            }
        }
        catch (Exception e) {
            Log.e("1122333444555656",String.valueOf(e));
        }
        ImageView image_exercise = convertView.findViewById(R.id.image_exercise);

        // Setting the proper image

        String feedback_icon = mSessionArrayList.get(position).getPosition() + "_" + mSessionArrayList.get(position).getBodypart() + "_" + mSessionArrayList.get(position).getExercise();
        feedback_icon = "ic_fb_" + feedback_icon;
        feedback_icon = feedback_icon.replace(" - ", "_");
        feedback_icon = feedback_icon.replace(" ", "_");
        feedback_icon = feedback_icon.replace(")", "");
        feedback_icon = feedback_icon.replace("(", "");
        feedback_icon = feedback_icon.toLowerCase();

        int res = context.getResources().getIdentifier(feedback_icon, "drawable", context.getPackageName());

        if (res != 0) {
            image_exercise.setImageResource(res);
        }
        CheckBox temp = (CheckBox) convertView.findViewById(R.id.checkBox1);
        temp.setChecked(temp.isChecked());
        selected_item.setSelected(temp.isChecked());

        return convertView;

    }
}
