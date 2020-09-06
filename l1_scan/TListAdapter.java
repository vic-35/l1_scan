package com.leadbelly.l1_scan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leadbelly.l1_scan.MainListActivity;
import com.leadbelly.l1_scan.R;
import com.leadbelly.l1_scan.TList;

import java.util.ArrayList;

class TListAdapter extends ArrayAdapter<TList> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<TList> ar;
    private Context context;

    TListAdapter(Context context, int resource, ArrayList<TList> ar_) {
        super(context, resource, ar_);
        this.ar = ar_;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
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
        final TList ar_cur = ar.get(position);

        if(ar_cur.is_last_edit == 0){
            viewHolder.img1.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.img1.setVisibility(View.VISIBLE);
        }

        viewHolder.nameView.setText(P.get_short(ar_cur.name,50)+" "+P.get_short(ar_cur.name1,50));
        //viewHolder.name1View.setText(ar_cur.scan);


        int all_col=0;
        int all_col_scan=0;
        StringBuilder s_count= new StringBuilder();

        for(TScan s:ar_cur.scan)
        {
            if(s_count.length() > 0) s_count.append("\n");
            if(ar_cur.scan.size() == 1)
                s_count.append( s.scan);
            else
                s_count.append( s.scan+" = "+s.col_scan+"("+s.col+")");


            all_col+=s.col;
            all_col_scan+=s.col_scan;


        }
        viewHolder.name1View.setText(s_count);
        viewHolder.countView.setText( all_col_scan + "  ( "+all_col+" )");

        if(all_col == all_col_scan && all_col_scan >0 ) viewHolder.countView.setTextColor(Color.GREEN);
        else viewHolder.countView.setTextColor(Color.RED);


        viewHolder.name1View.setTextColor(Color.BLUE);

        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainListActivity)context).add_col_scan(ar_cur);

            }
        });
        return convertView;
    }


    private class ViewHolder {
        final Button addButton;
        final TextView nameView,name1View, countView;
        final ImageView img1;
        ViewHolder(View view){
            addButton = (Button) view.findViewById(R.id.addButton);
            nameView = (TextView) view.findViewById(R.id.nameView);
            name1View = (TextView) view.findViewById(R.id.name1View);
            countView = (TextView) view.findViewById(R.id.countView);
            img1= (ImageView) view.findViewById(R.id.imageView);
        }
    }
}

