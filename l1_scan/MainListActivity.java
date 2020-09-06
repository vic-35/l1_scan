package com.leadbelly.l1_scan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.os.Vibrator;

import java.util.ArrayList;

public class MainListActivity extends AppCompatActivity {
    ArrayList<TList> ar;
    ListView arList;
    public DBLocal dbLocal;
    TListAdapter adapter;
    SharedPreferences sPref;
    String scan;
    String s_vibr;
    TJob p_job;
    Button button_scan;
    long last_id;
    String last_scan;
    ProgressBar progressBar;
    Boolean is_rsvr=false;
    TextView nameView, name1View, status;
    long id_job;

    IntentFilter intFilter;
    private BroadcastReceiver mResultReceiver;

    SoundPool mSoundPool;
    int alarm1;
    int alarm2;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            nameView = (TextView) findViewById(R.id.nameView);
            name1View = (TextView) findViewById(R.id.name1View);
            status = (TextView) findViewById(R.id.status);
            button_scan= (Button) findViewById(R.id.button_scan);
            last_id=0;


            sPref = PreferenceManager.getDefaultSharedPreferences(this);
            scan = sPref.getString("example_list1", "0");
            s_vibr = sPref.getString("example_list2", "1");

            last_scan="";


            ar = new ArrayList<TList>();


            dbLocal = new DBLocal(this);
            dbLocal.open();

            id_job = getIntent().getLongExtra("id",-1);

            arList = (ListView) findViewById(R.id.List1);
            arList.setDividerHeight(10);
            adapter = new TListAdapter(this, R.layout.list_item_list, ar);

            if(scan.equals("0")) {
                this.intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
                this.mResultReceiver = new Br1();
            }

            mSoundPool= new SoundPool(4, AudioManager.STREAM_RING, 100);
            alarm1 =mSoundPool.load(this, R.raw.a1, 1);
            alarm2 =mSoundPool.load(this, R.raw.n1, 1);


/*
            TList l = new TList();
            l.name = "test";
            l.name1 = "test1";
            l.col = 5;
            l.col_scan = 0;
            l.scan = "55555555";
            l.su = 0;
            l.id_job=p_job.id;
            dbLocal.AddList(l);
//
            l.name = "gggggggggg";
            dbLocal.AddList(l);

*/
        }catch (Exception ex) {
            P.E(this, this.toString(), ex.toString());
        }
    }

    class Br1 extends BroadcastReceiver {
        Br1() {
        }

        public void onReceive(Context context, Intent intent) {
            if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(intent.getAction())) {
               // byte[] bvalue = intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                byte[] bvalue1 = intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                String svalue1 = null;
                if (bvalue1 != null) {
                    try {
                        svalue1 = new String(bvalue1, "GBK");
                    } catch (Exception e) {

                        P.E( context,"Ошибка - ",e.toString());
                        return;
                    }
                }
                if (svalue1 == null) {
                    svalue1 = "ошибка";
                }
                else
                {
                    unreg_rsvr();
                    new_cod_scan(svalue1);
                    reg_rsvr();
                }

            }
        }
    }

    void play_sound(boolean is_err) {

         if( s_vibr.equals("1")) {

             if (is_err) mSoundPool.play(alarm1, 1, 1, 0, 0, 1);
             else mSoundPool.play(alarm2, 1, 1, 0, 0, 1);

             long mills = 300L;
             Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
             vibrator.vibrate(mills);
         }

    }

    void set_add_col_scan(TList l1,int _num_scan) {
        try {

            if(_num_scan < 0 || _num_scan >= l1.scan.size()) return;

            unreg_rsvr();

            final TList l = l1;
            final int num_scan=_num_scan;

            final Dialog d = new Dialog(this);
            d.setTitle(l.scan.get(num_scan).scan);
            d.setContentView(R.layout.dialog_1);
            d.setCancelable(false);

            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(1000000); // max value 100
            np.setMinValue(0);   // min value 0
            np.setValue((int)l.scan.get(num_scan).col_scan);
            np.setWrapSelectorWheel(false);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    l.scan.get(num_scan).col_scan = np.getValue();
                    if (l.scan.size() == 1 && l.scan.get(num_scan).col_scan == 0 && l.scan.get(num_scan).col == 0) {
                        last_id = 0;
                        dbLocal.DelList(l);
                        P.Msg(v.getContext(), "Удален пустой штрих-код - " + l.scan.get(num_scan).scan, "");
                    } else {
                        last_id = l.id;
                        dbLocal.UpdateList(l);
                    }

                    load();
                    test_col_scan();
                    d.dismiss();
                    reg_rsvr();
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss(); // dismiss the dialog
                    reg_rsvr();
                }
            });
            d.show();

        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
        }

    }
    void select_from_list_scan(TList l1)
    {
        try {



            unreg_rsvr();

            final TList l = l1;

            final Dialog d = new Dialog(this);
            d.setTitle("Выберите штрих-код для изменения");
            d.setContentView(R.layout.dialog_4);
            d.setCancelable(false);

            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final Spinner s1= (Spinner) d.findViewById(R.id.spinner) ;

            String[] data = new String[l.scan.size()];
            for (int i=0;i < l.scan.size();i++){
                data[i]=l.scan.get(i).scan;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,data );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s1.setAdapter(adapter);


            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    set_add_col_scan(l, s1.getSelectedItemPosition());
                    d.dismiss();
                    reg_rsvr();
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss(); // dismiss the dialog
                    reg_rsvr();
                }
            });
            d.show();

        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
        }

    }
    void add_col_scan(TList l1)
    {

        if (p_job.status >= 20) {

            P.E(this,"Задание сверено, изменение запрещено","");

            play_sound(true);

            return;

        }


        if( l1.scan.size() == 1) {
            set_add_col_scan(l1,0);
        }
        else
        {
            select_from_list_scan(l1);
        }

    }

    public  void load()
    {


        if(ar.size()!=0){
            ar.clear();
        }
        try {

            p_job= dbLocal.GetJob(id_job);
            if(p_job.id == -1)
            {
                P.E(this, "Не найдено задание -"+id_job,"");
                return;
            }


            nameView.setText(P.get_short( p_job.name,50));
            name1View.setText(P.get_short(p_job.name1,50));
            status.setText(P.get_status( p_job.status));
//            Cursor mCursor = dbLocal.db.query("list", new String[] {"name","name1","scan"},
  //                  "id_job = "+ p_job.id, null, null, null, null);

            Cursor mCursor = dbLocal.db.rawQuery("select * from list where id_job="+ p_job.id,null);
            if (mCursor.moveToFirst()) {
                do {
                    TList list=new TList();
                    dbLocal.GetList(mCursor,list);
                    //----------------------------
                    list.is_last_edit = 0;
                    // Adding contact to list
                    ar.add(list);

                } while (mCursor.moveToNext());
            }
            mCursor.close();
            arList.setAdapter(adapter);

            for (int i = 0; i < ar.size(); i++) {
                if (last_id == ar.get(i).id) {
                    ar.get(i).is_last_edit = 1;
                    arList.setSelection(i);
                    break;
                }
            }

        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
        }

    }

    void reg_rsvr()
    {
        try {
            if(scan.equals("0")) {
                if(!is_rsvr)
                {
                    registerReceiver(mResultReceiver, intFilter);
                    is_rsvr=true;
                }
            }
        } catch (Exception ex) {
            P.E(this,this.toString(),ex.toString());
        }
    }
    void unreg_rsvr()
    {
        try {
            if(scan.equals("0")) {
                if(is_rsvr){
                    is_rsvr=false;
                    unregisterReceiver(mResultReceiver);
                }
            }
        } catch (Exception ex) {
            P.E(this,this.toString(),ex.toString());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        load();
        reg_rsvr();
    }

    protected void onPause() {
        super.onPause();
        unreg_rsvr();

    }
    public void OnScanButton(View view) {
        last_scan="";
        if(scan.equals("1")) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 10);
        }
        else
        {
            is_rsvr=false;
            reg_rsvr();
            sendBroadcast( new Intent ("nlscan.action.SCANNER_TRIG"));

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                String s = (intent.getStringExtra("SCAN_RESULT")).trim();

                new_cod_scan(s);

            } else {
                P.T(this,"Сканирование отменено","");

            }

        }
    }


    void new_cod_scan(String s)
    {
        if (p_job.status >= 20) {

            P.E(this,"Задание сверено, изменение запрещено","");
            play_sound(true);
            return;

        }

        last_scan=s;

        try {
            if (p_job.status == 0) {
                p_job.status = 10;
                dbLocal.UpdateJob(p_job);
            }
        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
            return;
        }

        int f_scan_num=-1;
        int f_scan=-1;
        int c_scan_num=-1;
        int c_scan=-1;

        for (int i_scan = 0; i_scan < ar.size(); i_scan++) {
            for( int i_scan_num =0 ;i_scan_num < ar.get(i_scan).scan.size();i_scan_num++)
            {
                if( s.equals(ar.get(i_scan).scan.get(i_scan_num).scan)){

                    if( f_scan == -1 ){
                        f_scan=i_scan;
                        f_scan_num=i_scan_num;
                    }

                    if( ar.get(i_scan).scan.get(i_scan_num).col_scan <  ar.get(i_scan).scan.get(i_scan_num).col  )
                    {
                        c_scan= i_scan;
                        c_scan_num=i_scan_num;
                    }
                }
            }

        }

        if(c_scan == -1) {
            c_scan= f_scan;
            c_scan_num=f_scan_num;
        }


        if (c_scan != -1) {
            try {
                last_id = ar.get(c_scan).id;

                ar.get(c_scan).scan.get(c_scan_num).col_scan++;
                dbLocal.UpdateList(ar.get(c_scan));
                play_sound(false);

                load();
                test_col_scan();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            } catch (Exception ex) {
                P.E(this, this.toString(), ex.toString());

            }
            return;
        }



        unreg_rsvr();
        play_sound(true);
        P.Ch(this,add_newscan_Listener,"Скан код не найден - "+s,"Добавить ?");


    }
    DialogInterface.OnClickListener add_newscan_Listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    try {

                        TList list=new TList();
                        list.id_job=p_job.id;
                        list.name =last_scan;//"ffffffffffff fffffffffff gggggg d wee  weeeeeeeeeeee rrrrrrrrrrrrrrrrr tttttttttttttt tttttttttttt y0";

                        TScan scan = new TScan();
                        // scan.id_list заполняется при dbLocal.AddList(list);
                        scan.id_job=p_job.id;
                        scan.scan=last_scan;
                        scan.col_scan=1;
                        list.scan.add(scan);
                        last_id=dbLocal.AddList(list);

                        load();

                        P.T(((AlertDialog) dialog).getContext(), "Добавлен код  - ", last_scan);
                        last_scan="";

                    } catch (Exception ex) {
                        P.E(((AlertDialog) dialog).getContext(), "add_newscan_Listener ", ex.toString());
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
            reg_rsvr();

        }
    };
    public void OnOpt(View view) {

        try {

            unreg_rsvr();

            final Dialog d = new Dialog(this);
            d.setTitle("Опции");
            d.setContentView(R.layout.dialog_2);
            d.setCancelable(false);

            Button b3 = (Button) d.findViewById(R.id.button3);
            Button b2 = (Button) d.findViewById(R.id.button2);

            final RadioButton r2= (RadioButton) d.findViewById(R.id.radioButton2);
            final RadioButton r3= (RadioButton) d.findViewById(R.id.radioButton3);
            final RadioButton r= (RadioButton) d.findViewById(R.id.radioButton);


            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( r2.isChecked())
                        P.Ch(v.getContext(),OnOpt_Listener_r2,"Выполнить ?","Установить заданию статус 'Сверен' ?");

                    if( r.isChecked())
                        P.Ch(v.getContext(),OnOpt_Listener_r,"Выполнить ?","Удалить задание ?");

                    if( r3.isChecked())
                        P.Ch(v.getContext(),OnOpt_Listener_r3,"Выполнить ?","Установить заданию статус 'Выполняется' ?");



                    d.dismiss();
                    reg_rsvr();
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss(); // dismiss the dialog
                    reg_rsvr();
                }
            });
            d.show();
        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
        }

    }
    DialogInterface.OnClickListener OnOpt_Listener_r2= new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    try {

                                p_job.status = 20;
                                dbLocal.UpdateJob(p_job);
                                Close();//load();

                                P.T(((AlertDialog) dialog).getContext(), "Заданию установлен статус 'Сверен'", "");

                        }
                        catch (Exception ex)
                        {
                            P.E(((AlertDialog) dialog).getContext(),"OnOpt_Listener_r2 ",ex.toString());
                            return;
                        }

                        last_scan="";
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
            dialog.dismiss();
            reg_rsvr();

        }
    };
    DialogInterface.OnClickListener OnOpt_Listener_r= new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                        try {


                            dbLocal.DelJob(p_job);
                            Close();
                            P.T(((AlertDialog) dialog).getContext(), "Задание удалено", "");
                            dialog.dismiss();
                        }
                        catch (Exception ex)
                        {
                            P.E(((AlertDialog) dialog).getContext(),"OnOpt_Listener_r ",ex.toString());

                        }
                    return;

                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
            dialog.dismiss();
            reg_rsvr();

        }
    };
    DialogInterface.OnClickListener OnOpt_Listener_r3= new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                        try {

                            p_job.status = 10;
                            dbLocal.UpdateJob(p_job);
                            load();

                            P.T(((AlertDialog) dialog).getContext(), "Заданию установлен статус 'Выполняется'", "");

                        }
                        catch (Exception ex)
                        {
                            P.E(((AlertDialog) dialog).getContext(),"OnOpt_Listener_r3 ",ex.toString());
                            return;
                        }

                        last_scan="";
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
            dialog.dismiss();
            reg_rsvr();

        }
    };
    void Close()
    {
        finish();
    }

    void test_col_scan()
    {

        if (p_job.status >= 20) {
            return;
        }

        try {

            int ar_len=ar.size();
            if(ar_len == 0) return;

            for(int i=0; i < ar_len;i++)
            {
                if(!ar.get(i).test_col_scan()) return;
            }
 //           p_job.status = 20;
 //           dbLocal.UpdateJob(p_job);
            play_sound(true);
            P.Msg(this,"Задание выполнено !","Установите статус 'Сверен'  и выполните синхронизацию");
        }
        catch (Exception ex)
        {
            P.E(this,this.toString(),ex.toString());
        }
    }

}
