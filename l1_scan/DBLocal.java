package com.leadbelly.l1_scan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vic on 15.05.17.
 */


public class DBLocal {

    private DatabaseHelper DBHelper;
    private final Context context;
    public SQLiteDatabase db;

    private static final String DB_NAME = "Scan_job.db";
    private static final int DB_VERSION = 3;
    private static final String CreateJob_table = "create table Job (_id integer primary key autoincrement,uid TEXT, name TEXT,name1 TEXT,type integer, status integer)";
    private static final String CreateList_table = "create table List (_id integer primary key autoincrement,id_job integer,n_str TEXT,d_doc TEXT,n_doc TEXT,name_k TEXT,kod_t TEXT,name_t TEXT,kod_t_p TXT,nomk TEXT,key_str TEXT,kontra TEXT,ss_doc TEXT, name TEXT,name1 TEXT)";
    private static final String CreateScan_table = "create table Scan (_id integer primary key autoincrement,id_list integer,id_job integer, scan TEXT,col integer,col_scan integer)";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        Context context;

        public DatabaseHelper(Context context) {
            super(context, DBLocal.DB_NAME, null, DBLocal.DB_VERSION);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(DBLocal.CreateJob_table);
                sqLiteDatabase.execSQL(DBLocal.CreateList_table);
                sqLiteDatabase.execSQL(DBLocal.CreateScan_table);
                P.T(context,"База данных успешно создана","");

            } catch (Exception ex){
                P.E(context,"Error onCreate", ex.toString());
            }
        }
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            try {
                    sqLiteDatabase.execSQL("drop table Scan");
                    sqLiteDatabase.execSQL("drop table List");
                    sqLiteDatabase.execSQL("drop table Job");
                    onCreate(sqLiteDatabase);


            } catch (Exception e) {
                P.E(context,"Error DbOpenHelper.onUpgrade", e.toString());
            }
        }


    }

    public long AddJob(TJob j){
        return SetJob(j,false);
    }
    public long UpdateJob(TJob j){
        return SetJob(j,true);
    }
    public long SetJob(TJob j,boolean is_update)
    {
        long ret=-1;
        ContentValues newValues = new ContentValues();

        newValues.put("name", j.name);
        newValues.put("name1", j.name1);
        newValues.put("type", j.type);
        newValues.put("status", j.status);
        newValues.put("uid",j.uid);


        if( is_update) ret= db.update("job",newValues,"_id="+j.id,null);
        else       ret= db.insert("job", null, newValues);

        return ret;
    }
    public TJob GetJob(long id)
    {
        TJob ret =new TJob();
        Cursor mCursor = db.rawQuery("select * from job where _id="+id,null);
        if (mCursor.moveToFirst()) {

            GetJob(mCursor,ret);
        }
        mCursor.close();
        return ret;
    }
    public void GetJob(Cursor mCursor,TJob ret)
    {
        ret.id = mCursor.getLong(mCursor.getColumnIndex("_id"));
        ret.name = mCursor.getString(mCursor.getColumnIndex("name"));
        ret.name1 = mCursor.getString(mCursor.getColumnIndex("name1"));
        ret.status = mCursor.getLong(mCursor.getColumnIndex("status"));
        ret.type = mCursor.getLong(mCursor.getColumnIndex("type"));
        ret.uid=mCursor.getString(mCursor.getColumnIndex("uid"));
    }

    public TList GetList(long id) {
        TList ret =new TList();
        Cursor mCursor = db.rawQuery("select * from list where _id="+id,null);
        if (mCursor.moveToFirst()) {

            GetList(mCursor,ret);
        }
        mCursor.close();
        return ret;
    }
    public void GetList(Cursor mCursor,TList ret)
    {
        ret.id = mCursor.getLong(mCursor.getColumnIndex("_id"));
        ret.id_job = mCursor.getLong(mCursor.getColumnIndex("id_job"));
        ret.name = mCursor.getString(mCursor.getColumnIndex("name"));
        ret.name1 = mCursor.getString(mCursor.getColumnIndex("name1"));
        ret.d_doc = mCursor.getString(mCursor.getColumnIndex("d_doc"));
        ret.key_str = mCursor.getString(mCursor.getColumnIndex("key_str"));
        ret.kod_t = mCursor.getString(mCursor.getColumnIndex("kod_t"));
        ret.kod_t_p = mCursor.getString(mCursor.getColumnIndex("kod_t_p"));
        ret.kontra = mCursor.getString(mCursor.getColumnIndex("kontra"));
        ret.n_doc = mCursor.getString(mCursor.getColumnIndex("n_doc"));
        ret.n_str = mCursor.getString(mCursor.getColumnIndex("n_str"));
        ret.name_k = mCursor.getString(mCursor.getColumnIndex("name_k"));
        ret.name_t = mCursor.getString(mCursor.getColumnIndex("name_t"));
        ret.nomk = mCursor.getString(mCursor.getColumnIndex("nomk"));
        ret.ss_doc = mCursor.getString(mCursor.getColumnIndex("ss_doc"));

        ret.scan.clear();
        Cursor sCursor = db.rawQuery("select * from scan where id_list="+ret.id,null);
        if (sCursor.moveToFirst()) {

            do{
                TScan scan = new TScan();
                scan.id=sCursor.getLong(sCursor.getColumnIndex("_id"));
                scan.id_job=sCursor.getLong(sCursor.getColumnIndex("id_job"));
                scan.id_list=sCursor.getLong(sCursor.getColumnIndex("id_list"));
                scan.col=sCursor.getLong(sCursor.getColumnIndex("col"));
                scan.col_scan=sCursor.getLong(sCursor.getColumnIndex("col_scan"));
                scan.scan= sCursor.getString(sCursor.getColumnIndex("scan"));

                ret.scan.add(scan);
            } while(sCursor.moveToNext()) ;
        }
        sCursor.close();


    }

    public long AddList(TList l) {
        return SetList(l,false);
    }
    public long UpdateList(TList l){
        return SetList(l,true);
    }

    public long SetList(TList l,boolean is_update)
    {
        long ret=-1;

        ContentValues newValues = new ContentValues();

        newValues.put("id_job", l.id_job);
        newValues.put("name", l.name);
        newValues.put("name1", l.name1);

        newValues.put("d_doc", l.d_doc);
        newValues.put("key_str", l.key_str);
        newValues.put("kod_t", l.kod_t);
        newValues.put("kod_t_p", l.kod_t_p);
        newValues.put("kontra", l.kontra);
        newValues.put("n_doc", l.n_doc);
        newValues.put("n_str", l.n_str);
        newValues.put("name_k", l.name_k);
        newValues.put("name_t", l.name_t);
        newValues.put("nomk", l.nomk);
        newValues.put("ss_doc", l.ss_doc);

        if( is_update) 
        {
            ret= db.update("list",newValues,"_id="+l.id,null);
            if(ret > 0) {
                for (TScan i_scan : l.scan)
                {
                    ContentValues new_scan_Values = new ContentValues();

                    new_scan_Values.put("id_list", i_scan.id_list);
                    new_scan_Values.put("id_job", i_scan.id_job);
                    new_scan_Values.put("scan", i_scan.scan);
                    new_scan_Values.put("col", i_scan.col);
                    new_scan_Values.put("col_scan", i_scan.col_scan);

                    if( db.update("scan",new_scan_Values,"_id="+i_scan.id,null) < 0) return ret;

                }
            }
        }
        else
        {
             ret= db.insert("list", null, newValues);
            // ret содержит id_list !!!!!!!
            if(ret > 0) {
                for (TScan i_scan : l.scan) {
                    ContentValues new_scan_Values = new ContentValues();

                    i_scan.id_list=ret;
                    new_scan_Values.put("id_list", i_scan.id_list);
                    new_scan_Values.put("id_job", i_scan.id_job);
                    new_scan_Values.put("scan", i_scan.scan);
                    new_scan_Values.put("col", i_scan.col);
                    new_scan_Values.put("col_scan", i_scan.col_scan);

                    if( db.insert("scan", null, new_scan_Values) < 0) return ret;
                }
            }

        }

        return ret;
    }

    public long DelList(TList l)
    {
        db.delete("scan","id_list="+l.id,null);
        return db.delete("list","_id="+l.id,null);
    }
    public long DelJob(TJob j)
    {

        db.delete("scan","id_job="+j.id,null);
        db.delete("list","id_job="+j.id,null);
        return db.delete("job","_id="+j.id,null);
    }

    public DBLocal(Context ctx) {
        this.context = ctx;
        this.DBHelper = new DatabaseHelper(this.context);
    }

    public DBLocal open() throws SQLException {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {

        this.DBHelper.close();
    }

    public static String nvl(String s) {
        return nvl(s,"");
    }
    public static String nvl(String s,String def)
    {
        if(s == null) return def;
        return  s;
    }

}
