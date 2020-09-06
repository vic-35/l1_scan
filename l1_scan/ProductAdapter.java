package com.leadbelly.l1_scan; /**
 * Created by vic on 10.05.17.
 */

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.DialogInterface;

import java.util.ArrayList;

class ProductAdapter extends ArrayAdapter<TJob> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<TJob> productList;
    private Context context;
    SharedPreferences sPref;

    ProductAdapter(Context context, int resource, ArrayList<TJob> products) {
        super(context, resource, products);
        this.productList = products;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
    }
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:

                    String ret=  ((TextView) ((AlertDialog) dialog).findViewById(R.id.textView)).getText().toString();


                    P.T(context, "e111",ret);
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:

                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
            dialog.dismiss();
        }
    };

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
        final TJob product = productList.get(position);

        viewHolder.nameView.setText(product.name);
        //viewHolder.countView.setText(formatValue(product.getCount(), product.getUnit()));
        //viewHolder.nameView.setTextColor(Color.rgb(200,0,0));
        //((LinearLayout)convertView).setBackgroundColor(Color.rgb(255,255,0));
        //((LinearLayout)convertView).setFocusable(true);
     //   int pos=((ListView) parent).getSelectedItemPosition();

      /*  if(((LinearLayout) convertView).getId() == )
        {
            ((LinearLayout)convertView).setBackgroundResource(R.drawable.border);
        }
*/
        //((LinearLayout)convertView).setOn




        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   int count = product.getCount()-1;
                if(count<0) count=0;
                product.setCount(count);
                viewHolder.countView.setText(formatValue(count, product.getUnit()));*/
/*
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                // заголовок
                adb.setTitle("exit");
                // сообщение
                adb.setMessage("save");
                // иконка
                adb.setIcon(android.R.drawable.ic_dialog_info);
                // кнопка положительного ответа
                adb.setPositiveButton("yes", myClickListener);
                // кнопка отрицательного ответа
                adb.setNegativeButton("no", myClickListener);
                // кнопка нейтрального ответа
                adb.setNeutralButton("cancel", myClickListener);

                adb.setCancelable(false);
                // создаем диалог
                adb.create();
                AlertDialog alert = adb.create();
                alert.show();
*/
               // Dlg.Ch(context,myClickListener,"сохранеее","Вы хотите 7");
                P.Inp(context,myClickListener,"сохранеее","","hint");


            }
        });



        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* int count = product.getCount()+1;
                product.setCount(count);
                viewHolder.countView.setText(formatValue(count, product.getUnit()));*/

                SharedPreferences.Editor ed = sPref.edit();
                ed.putLong("Main4Activity_last_id", product.id);
                ed.apply();

                Intent intent=new Intent(context, Main4Activity.class);
                intent.putExtra("name",product.name);
                intent.putExtra("id",product.id);
                context.startActivity(intent);
            }
        });
        //((LinearLayout)convertView).setBackgroundColor(Color.rgb(100,0,0));
      /*  ((LinearLayout)convertView).setOnFocusChangeListener( new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) v.setBackgroundColor(Color.rgb(100,0,0));
                else  v.setBackgroundColor(Color.rgb(255,255,0));
            }
        });

*/

        ((LinearLayout)convertView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), viewHolder.nameView.getText(), Toast.LENGTH_LONG).show();
                //v.setBackgroundColor(Color.rgb(100,0,0));



            }
        });


        return convertView;
    }

    private String formatValue(int count, String unit){
        return String.valueOf(count) + " " + unit;
    }
    private class ViewHolder {
        final Button addButton, removeButton;
        final TextView nameView, countView;
        ViewHolder(View view){
            addButton = (Button) view.findViewById(R.id.addButton);
            removeButton = (Button) view.findViewById(R.id.removeButton);
            nameView = (TextView) view.findViewById(R.id.nameView);
            countView = (TextView) view.findViewById(R.id.countView);
        }
    }
}


