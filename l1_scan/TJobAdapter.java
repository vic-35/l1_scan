package com.leadbelly.l1_scan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leadbelly.l1_scan.MainListActivity;
import com.leadbelly.l1_scan.P;
import com.leadbelly.l1_scan.R;
import com.leadbelly.l1_scan.TJob;

import java.util.ArrayList;

class TJobAdapter extends ArrayAdapter<TJob> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<TJob> ar;
    private Context context;
    SharedPreferences sPref;

    TJobAdapter(Context context, int resource, ArrayList<TJob> ar_) {
        super(context, resource, ar_);
        this.ar = ar_;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TJob ar_cur = ar.get(position);


        viewHolder.nameView.setText(P.get_short(ar_cur.name,50));
        viewHolder.name1View.setText(P.get_short(ar_cur.name1,50));

        viewHolder.status.setText(P.get_status(ar_cur.status));

        if (ar_cur.status>= 20 ) {
            viewHolder.status.setTextColor(Color.GREEN);
        }
        else {
            if (ar_cur.status >= 10 ) {
                viewHolder.status.setTextColor(Color.BLUE);
            } else
                viewHolder.status.setTextColor(Color.RED);
        }



        if(ar_cur.is_last_edit == 0){
            viewHolder.img1.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.img1.setVisibility(View.VISIBLE);
        }



        viewHolder.OpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putLong("MainActivity_last_id", ar_cur.id);
                ed.apply();


                Intent intent=new Intent(context, MainListActivity.class);
                intent.putExtra("id",ar_cur.id);
                context.startActivity(intent);


            }
        });

        return convertView;
    }

    private class ViewHolder {
        final Button OpenButton;
        final TextView nameView, name1View, status;
        final ImageView img1;
        ViewHolder(View view){
            OpenButton = (Button) view.findViewById(R.id.OpenButton);
            nameView = (TextView) view.findViewById(R.id.nameView);
            name1View = (TextView) view.findViewById(R.id.name1View);
            status = (TextView) view.findViewById(R.id.status);
            img1= (ImageView) view.findViewById(R.id.imageView);

        }
    }
}
