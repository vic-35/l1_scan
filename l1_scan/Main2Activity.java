package com.leadbelly.l1_scan;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    ArrayList<TJob> products = new ArrayList();
    ListView productList;
    public DBLocal dbLocal;
    ProductAdapter adapter;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sPref = PreferenceManager.getDefaultSharedPreferences(this);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
        dbLocal = new DBLocal(this);
        dbLocal.open();
        TJob j = new TJob();
        j.name = "test66666";
        j.name1 = "test";
        j.status = 30;
        j.type = 1;
       // long id = dbLocal.AddJob(j);

        j.name = "tty  66666666666 777777 8dfg    9999999  mmmmmmmmmmm hhhhhhhhhhh ,,,,,,,,,,,         .........fffffff nmnx olx po9 fg    9999999  mmmmmmmmmmm hhhhhhhhhhh ,,,,,,,,,,,         .........fffffff nmnx olx po9";
        j.name1 = "zztty  66666666666 777777 8dfg    9999999  mmmmmmmmmmm hhhhhhhhhhh ,,,,,,,,,,,         .........fffffff nmnx olx po91";
        j.status = 30;
        //id=dbLocal.AddJob(j);


        TList l = new TList();
        l.name = "test";
        l.name1 = "test1";
       // l.col = 5;
       // l.col_scan = 0;
       // l.scan = "55555555";
      //  l.su = 0;
        // l.id_job=id;
        //  long id_l=dbLocal.AddList(l);
//
        l.name = "gggggggggg";
        //dbLocal.AddList(l);

        productList = (ListView) findViewById(R.id.productList);
        adapter = new ProductAdapter(this, R.layout.list_item, products);


    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    public void load() {

        try {
            if (products.size() != 0) {
                products.clear();
            }

            //


            //Cursor mCursor = dbLocal.db.query("job", new String[] {"name","name1"},
            //      null, null, null, null, null);

            Cursor mCursor = dbLocal.db.rawQuery("select _id,name from job", null);

            if (mCursor.moveToFirst()) {
                do {
                    TJob job = new TJob();
                    job.id = mCursor.getLong(0);
                    job.name = mCursor.getString(1);

                    // Adding contact to list
                    products.add(job);

                } while (mCursor.moveToNext());

            }
            productList.setAdapter(adapter);

            long last_id = sPref.getLong("Main4Activity_last_id", 0);
            for (int i = 0; i < products.size(); i++) {
                if (last_id == products.get(i).id) {
                    productList.setSelection(i);
                    break;
                }
            }


        } catch (Exception ex) {
            P.E(this, this.toString(), ex.toString());
        }

    }
}
