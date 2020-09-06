package com.leadbelly.l1_scan;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.*;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;

public class Main4Activity extends AppCompatActivity {
    TextView textView2;
    ListView productList;
    public DBLocal dbLocal;
    ArrayList<TList> products = new ArrayList();
    Button b1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name = getIntent().getStringExtra("name");


        textView2= (TextView) findViewById(   R.id.textView2);

        textView2.setText(name);

        b1=(Button)  findViewById(R.id.button2);


        productList = (ListView) findViewById(R.id.List1);
        TListAdapter adapter = new TListAdapter(this, R.layout.list_item1, products);

        dbLocal =new DBLocal(this);
        dbLocal.open();

        load();

        productList.setAdapter(adapter);

//        SharedPreferences sPref = getSharedPreferences (getApplicationContext().getPackageName()+"_preferences",MODE_PRIVATE);
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);

        b1.setText( sPref.getString("server_in", ""));

    }
    public  void load()
    {
        long id = getIntent().getLongExtra("id",-1);
        //TJob job= dbLocal.GetJob(id);

        if(products.size()!=0){
            products.clear();
        }
        try {


            Cursor mCursor = dbLocal.db.query("list", new String[] {"name","name1"},
                    "id_job = "+id, null, null, null, null);

            // Cursor mCursor = dbLocal.db.rawQuery("select name from list where id_job="+id,null);
            if (mCursor.moveToFirst()) {
                do {
                    TList list=new TList();
                    list.name=mCursor.getString(0);

                    // Adding contact to list
                    products.add(list);

                } while (mCursor.moveToNext());



            }


         //   ContentValues newValues = new ContentValues();
         //   newValues.put("name", job.name+"2");
         //   long ret = dbLocal.db.update("job", newValues, "_id=" + id, null);


        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
        }
    }

}
