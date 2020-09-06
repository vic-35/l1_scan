package com.leadbelly.l1_scan;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Main3Activity extends AppCompatActivity {

    public DBLocal dbLocal;
    ListView JobView;
    SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        //dbLocal =new DBLocal(this);
        //dbLocal.open();

      //  Cursor mCursor = dbLocal.db.query("job", new String[] {"name","name1"},
       //         null, null, null, null, null);


/*
        JobView = (ListView) findViewById(R.id.job_list);

        if (mCursor.moveToFirst()) {
            do {
                JobView.add
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());


*/
    }

}
