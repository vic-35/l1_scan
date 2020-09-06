package com.leadbelly.l1_scan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Xml;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.function.ToIntFunction;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressBar progressBar;
    TextView textView_progress;
    ArrayList<TJob> ar;
    ListView arList;
    public DBLocal dbLocal;
    TJobAdapter adapter;
    SharedPreferences sPref;
    MyCopy m_async = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


            ar = new ArrayList<TJob>();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            textView_progress = (TextView) findViewById(R.id.textView_progress);

            sPref = PreferenceManager.getDefaultSharedPreferences(this);
            dbLocal = new DBLocal(this);
            dbLocal.open();


            arList = (ListView) findViewById(R.id.List1);
            arList.setDividerHeight(10);
            adapter = new TJobAdapter(this, R.layout.list_item_main, ar);


        } catch (Exception ex) {
            P.E(this, this.toString(), ex.toString());
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (progressBar.getVisibility() == View.VISIBLE) {
            P.T(this, "Выполняется загрузка данных", "");

            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (progressBar.getVisibility() == View.VISIBLE) {
            P.T(this, "Выполняется загрузка данных", "!");
            return true;
        }

        /*
        if (id == R.id.nav_camera) {
            startActivity(new Intent(this, ScrollingActivity.class));

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this, Main2Activity.class));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this, Main3Activity.class));

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, Main4Activity.class));

        } else if (id == R.id.nav_share) {
            progressBar.setVisibility(View.VISIBLE);


        } else if (id == R.id.nav_send) {
            progressBar.setVisibility(View.INVISIBLE);

            String settings2 = Settings.ACTION_WIFI_SETTINGS; // Беспроводные сети
            startActivity(new Intent(settings2));
        }
        */
        if (id == R.id.nav_send) {

            String settings2 = Settings.ACTION_WIFI_SETTINGS; // Беспроводные сети
            startActivity(new Intent(settings2));
        } else if (id == R.id.nav_share) {


            progressBar.setVisibility(View.VISIBLE);
            textView_progress.setVisibility(View.VISIBLE);
            arList.setVisibility(View.INVISIBLE);

            textView_progress.setText("синхронизация");

            if (m_async == null) {
                m_async = new MyCopy();
                m_async.execute("");
            }

        } else if (id == R.id.nav_view) {
            P.Inp(this, nav_view_Listener, "Введите название или номер задания", "", "");

        } else if (id == R.id.nav_manage) {
            P.Ch(this, nav_manage_Listener, "Внимание !", "Удалить все задания?");

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    DialogInterface.OnClickListener nav_manage_Listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:

                    try {

                        dbLocal.db.execSQL("delete from list");

                        dbLocal.db.execSQL("delete from job");

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putLong("MainActivity_last_id", 0);
                        ed.apply();

                        load();

                        P.Msg(((AlertDialog) dialog).getContext(), "", "Все задания удалены");

                    } catch (Exception ex) {

                        P.E(((AlertDialog) dialog).getContext(), "nav_send_Listener ", ex.toString());
                    }
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

    DialogInterface.OnClickListener nav_view_Listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:

                    String ret = ((TextView) ((AlertDialog) dialog).findViewById(R.id.textView)).getText().toString();
                    try {
                        TJob j = new TJob();
                        j.name = ret;
                        j.name1 = "";
                        j.status = 0;
                        j.type = 1;
                        long id = dbLocal.AddJob(j);

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putLong("MainActivity_last_id", id);
                        ed.apply();

                        load();
                        P.T(((AlertDialog) dialog).getContext(), "Создано задание - ", ret);
                    } catch (Exception ex) {
                        P.E(((AlertDialog) dialog).getContext(), "nav_view_Listener ", ex.toString());
                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        load();

    }

    protected void onPause() {
        super.onPause();
        if (m_async != null) {
            m_async.cancel(true);
        }
    }

    void end_sync(final String ret) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_async = null;
                load();
            }
        });
    }

    void cancel_sync(final String ret) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                arList.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                textView_progress.setVisibility(View.INVISIBLE);

                m_async = null;
            }
        });
    }

    void msg_sync(final String s, final int n_msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (m_async == null) return;
                msg_sync_A(s, n_msg);
            }
        });

    }

    void msg_sync_A(String s, int n_msg) {

        if (n_msg == 1) {
            P.Msg(this, s, "");
            return;
        } else if (n_msg == -1) {
            P.E(this, s, "");
            return;
        }
        textView_progress.setText(s);
    }

    public void load() {


        try {
            if (ar.size() != 0) {
                ar.clear();
            }
            String filtr = sPref.getString("example_list", "");

            if (filtr.equals("1")) {
                filtr = " where status < 30";
            } else {
                filtr = "";
            }

            Cursor mCursor = dbLocal.db.rawQuery("select _id,name,name1,type,status from job" + filtr, null);

            if (mCursor.moveToFirst()) {
                do {
                    TJob job = new TJob();
                    job.id = mCursor.getLong(0);
                    job.name = mCursor.getString(1);
                    job.name1 = mCursor.getString(2);
                    job.type = mCursor.getLong(3);
                    job.status = mCursor.getLong(4);

                    //----------------------------
                    job.is_last_edit = 0;

                    // Adding contact to list
                    ar.add(job);

                } while (mCursor.moveToNext());

            }
            arList.setAdapter(adapter);

            long last_id = sPref.getLong("MainActivity_last_id", 0);
            for (int i = 0; i < ar.size(); i++) {
                if (last_id == ar.get(i).id) {
                    ar.get(i).is_last_edit = 1;
                    arList.setSelection(i);
                    break;
                }
            }

            mCursor.close();

        } catch (Exception ex) {
            P.E(this, this.toString(), ex.toString());
        }

        arList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textView_progress.setVisibility(View.INVISIBLE);


    }

    private class MyCopy extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            //txtinfo.setText(r);
            //pbbar.setVisibility(View.GONE);
            end_sync(r);
        }

        @Override
        protected void onCancelled(String r) {

            cancel_sync(r);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String z = "";
            try {

                String d = sPref.getString("server_domain", "workgroup");
                String u = sPref.getString("server_user", "");
                String p = sPref.getString("password_server", "");

                String server_in = sPref.getString("server_in", "");
                String server_out = sPref.getString("server_out", "");
                String server_id = sPref.getString("server_id", "");

                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
                        d, u, p);

                jcifs.Config.setProperty("resolveOrder", "DNS");

                if(!isCancelled())
                { // отправка файлов

                    z="select from job ";
                    Cursor mCursor = dbLocal.db.rawQuery("select * from job where status >= 20 and status < 30", null);

                    if (mCursor.moveToFirst()) {

                        do {
                            TJob job = new TJob();
                            dbLocal.GetJob(mCursor,job);

                            z="Отправка файла - ";

                            SmbFile sfile = new SmbFile(server_out + server_id + "_" + job.id + ".xml", auth);
                            if (sfile.exists()) {
                                sfile.delete();
                            }

                            if (!isCancelled())
                            {
                                z+=sfile.getUncPath();
                                msg_sync(sfile.getName(), 0);
                            }

                            sfile.createNewFile();

                            SmbFileOutputStream out = new SmbFileOutputStream(sfile, true);

                          //  out.write(job.name.getBytes("windows-1251"));
                            //out.write(job.name.getBytes("UTF-8"));

                            XmlSerializer xmlSerializer = Xml.newSerializer();
                            xmlSerializer.setOutput(out,"UTF-8");
                            xmlSerializer.startDocument("UTF-8", true);
                            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                            xmlSerializer.startTag("", "Root");
                            xmlSerializer.startTag("", "IdDoc");
                            xmlSerializer.attribute("", "UID", job.uid);

                            {
                                Cursor mCursor_L = dbLocal.db.rawQuery("select * from list where id_job="+ job.id,null);
                                if (mCursor_L.moveToFirst()) {
                                    do {
                                        TList list=new TList();
                                        dbLocal.GetList(mCursor_L,list);

                                        xmlSerializer.startTag("", "item");
                                        xmlSerializer.attribute("", "НомерСтроки", list.n_str);
                                        xmlSerializer.attribute("", "ДатаДокумента", list.d_doc);
                                        xmlSerializer.attribute("", "НомерДокумента", list.n_doc);
                                        xmlSerializer.attribute("", "НазваниеКонтрагента", list.name_k);
                                        xmlSerializer.attribute("", "КодТовара", list.kod_t);
                                        xmlSerializer.attribute("", "НаименованиеТовара", list.name_t);
                                        xmlSerializer.attribute("", "КодТовараПоставщика", list.kod_t_p);
                                        xmlSerializer.attribute("", "Номенклатура", list.nomk);
                                        xmlSerializer.attribute("", "КлючСтроки", list.key_str);
                                        xmlSerializer.attribute("", "Контрагент", list.kontra);
                                        xmlSerializer.attribute("", "СсылкаДокумента", list.ss_doc);

                                        xmlSerializer.attribute("", "Количество", Long.toString( list.scan.get(0).col));
                                        xmlSerializer.attribute("", "КоличествоФактическое", Long.toString(list.scan.get(0).col_scan));
                                        xmlSerializer.attribute("", "Штрихкод", list.scan.get(0).scan);
                                        for(int i=1 ; i < list.scan.size();i++)
                                        {
                                            xmlSerializer.startTag("", "SH");
                                            xmlSerializer.attribute("", "Количество", Long.toString( list.scan.get(i).col));
                                            xmlSerializer.attribute("", "КоличествоФакт", Long.toString(list.scan.get(i).col_scan));
                                            xmlSerializer.attribute("", "Штрихкод", list.scan.get(i).scan);
                                            xmlSerializer.endTag("", "SH");
                                        }

                                        xmlSerializer.endTag("", "item");// Adding contact to list

                                    } while (mCursor_L.moveToNext());
                                }
                                mCursor_L.close();
                            }

                            xmlSerializer.endTag("", "IdDoc");
                            xmlSerializer.endTag("", "Root");
                            xmlSerializer.endDocument();




                            out.flush();
                            out.close();

                            job.status = 30;
                            dbLocal.UpdateJob(job);


                            if (isCancelled()) break;

                        } while (mCursor.moveToNext());

                    }
                    mCursor.close();


                }

                if(!isCancelled())
                { // прием файлов
                    z="Прием списка файлов - ";
                    SmbFile lfile = new SmbFile(server_in, auth);

                    String[] listFiles = lfile.list();
                    int count_f = listFiles.length;
                    z+= count_f+" ";

                    for (int i = 0; i < count_f; i++) {

                        z="Прием файла - "+listFiles[i];
                        String f_name = listFiles[i];
                        if (f_name.startsWith(server_id)) {

                            if(!isCancelled())
                                msg_sync(f_name,0);

                            f_name=server_in+listFiles[i];
                            SmbFile sfile = new SmbFile( f_name,auth);

                            InputStream in=sfile.getInputStream();

                            try {
                                XmlPullParser parser = Xml.newPullParser();
                                parser.setInput(in,"UTF-8");
                                parser.nextTag();



                                TJob job=new TJob();
                                TList list=new TList();


                                while (parser.getEventType()!= XmlPullParser.END_DOCUMENT)
                                {
                                    String _tag = DBLocal.nvl(parser.getName());
                                    String ns = DBLocal.nvl(parser.getNamespace());

                                    if( _tag.equals("SH") || _tag.equals("item") || _tag.equals("IdDoc") ) {

                                        if (parser.getEventType() == XmlPullParser.START_TAG) {

                                            if (_tag.equals("SH")) {
                                                TScan scan= new TScan();
                                                scan.col = Long.parseLong(  DBLocal.nvl(parser.getAttributeValue(ns, "Количество"),"0"));

                                                //scan.col_scan = Long.parseLong( DBLocal.nvl(parser.getAttributeValue(ns, "КоличествоФакт"),"0"));
                                                scan.col_scan =0;
                                                scan.scan = DBLocal.nvl(parser.getAttributeValue(ns, "Штрихкод"));
                                                list.scan.add(scan);

                                            }

                                            if (_tag.equals("item")) {

                                                list.scan.clear();
                                                list.name = DBLocal.nvl(parser.getAttributeValue(ns, "НазваниеКонтрагента"));
                                                list.name1=DBLocal.nvl(parser.getAttributeValue(ns, "НаименованиеТовара"));
                                                {
                                                    list.n_str = DBLocal.nvl(parser.getAttributeValue(ns, "НомерСтроки"));
                                                    list.d_doc = DBLocal.nvl(parser.getAttributeValue(ns, "ДатаДокумента"));
                                                    list.n_doc = DBLocal.nvl(parser.getAttributeValue(ns, "НомерДокумента"));
                                                    list.name_k = DBLocal.nvl(parser.getAttributeValue(ns, "НазваниеКонтрагента"));
                                                    list.kod_t = DBLocal.nvl(parser.getAttributeValue(ns, "КодТовара"));
                                                    list.name_t = DBLocal.nvl(parser.getAttributeValue(ns, "НаименованиеТовара"));
                                                    list.kod_t_p = DBLocal.nvl(parser.getAttributeValue(ns, "КодТовараПоставщика"));
                                                    list.nomk = DBLocal.nvl(parser.getAttributeValue(ns, "Номенклатура"));
                                                    list.key_str = DBLocal.nvl(parser.getAttributeValue(ns, "КлючСтроки"));
                                                    list.kontra = DBLocal.nvl(parser.getAttributeValue(ns, "Контрагент"));
                                                    list.ss_doc = DBLocal.nvl(parser.getAttributeValue(ns, "СсылкаДокумента"));

                                                    TScan scan= new TScan();
                                                    scan.col = Long.parseLong(  DBLocal.nvl(parser.getAttributeValue(ns, "Количество"),"0"));
                                                    //scan.col_scan = Long.parseLong( DBLocal.nvl(parser.getAttributeValue(ns, "КоличествоФактическое"),"0"));
                                                    scan.col_scan=0;
                                                    scan.scan = DBLocal.nvl(parser.getAttributeValue(ns, "Штрихкод"));

                                                    list.scan.add(scan);
                                                }
                                            }
                                            if (_tag.equals("IdDoc")) {

                                                job.name1 = job.uid = DBLocal.nvl(parser.getAttributeValue(ns, "UID"));
                                                job.name = sfile.getName();
                                                job.status = 0;
                                                job.type = 1;
                                                job.id = dbLocal.AddJob(job);
                                                list.id_job = job.id; // ---------------id_job-------------

                                            }

                                        }
                                        if (parser.getEventType() == XmlPullParser.END_TAG) {

                                            if( _tag.equals("item") ){
                                                 dbLocal.AddList(list);
                                            }

                                        }
                                    }
                                parser.next();
                                }
                            }
                            catch (Exception ex)
                                    {
                                        if (!isCancelled())
                                            msg_sync(z + " ошибка xml - " + ex.toString(), -1);

                                        return z;
                                    }

                            in.close();
                            sfile.delete(); // удаление файла


                        }
                        if (isCancelled()) break;
                    }

                }

                if (!isCancelled()) {
                    msg_sync("Синхронизация успешно завершена", 1);
                }
            } catch (Exception ex) {
                if (!isCancelled())
                    msg_sync(z+" ошибка - " + ex.toString(), -1);
            }
            return z;
        }
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private String readTitle(XmlPullParser parser,String ns,String title) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, title);
        String ret = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, title);
        return ret;
    }
}
