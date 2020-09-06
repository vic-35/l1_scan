package com.leadbelly.l1_scan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by vic on 18.05.17.
 */
class P{

    static String get_short(String s,int len)
    {
        if(s.length() > len+10)
        {
            return  s.substring(0,len)+"...";
        }
        return s;
    }

    static String get_status(long l)
    {
        switch ((int)l){
            case 0: return "Новое задание";
            case 10: return "Выполняется";
            case 20: return "Сверен";
            case 30: return "Отправлен";

        }
        return "Неизвестный статус -"+l;


    }

    static void E(Context ctx, String s, String s1)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Oшибка!")
                .setMessage(s+" "+s1)
                //.setIcon(R.drawable.ic_android_cat)
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    static void Msg(Context ctx, String s, String s1)
    {
        SoundPool mSoundPool= new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        int soundId = mSoundPool.load(ctx, R.raw.n1, 1);
        mSoundPool.play(soundId, 1, 1, 0, 0, 1);


        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Информация")
                .setMessage(s+" "+s1)
                //.setIcon(R.drawable.ic_android_cat)
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    static void T(Context ctx, String s, String s1)
    {
        Toast toast = Toast.makeText(ctx,
                s+" "+s1,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    static  void Ch(Context ctx,final DialogInterface.OnClickListener listener,String s,String s1)
    {
        Ch(ctx,listener,s,s1,false);
    }
    static  void Ch(Context ctx,final DialogInterface.OnClickListener listener,String s,String s1,boolean add_no)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
        // заголовок
        adb.setTitle(s);
        // сообщение
        adb.setMessage(s1);
        // иконка
      //  adb.setIcon(android.R.drawable.ic_dialog_info);
        // кнопка положительного ответа
        adb.setPositiveButton("Да", listener);
        // кнопка отрицательного ответа
      if(add_no) adb.setNegativeButton("Нет", listener);
        // кнопка нейтрального ответа
        adb.setNeutralButton("Отмена", listener);

        //adb.setCancelable(false);
        // создаем диалог
        adb.create();
        AlertDialog alert = adb.create();
        alert.show();
    }
    static  void Inp(Context ctx,final DialogInterface.OnClickListener listener,String s,String s1,String s2)
    {
        final EditText txtUrl = new EditText(ctx);
        txtUrl.setText(s1);
        txtUrl.setHint(s2);
        txtUrl.setId(R.id.textView);


        AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
        // заголовок
        adb.setTitle(s);
        // иконка
       // adb.setIcon(android.R.drawable.ic_dialog_alert);
//
        // кнопка положительного ответа
        adb.setPositiveButton("Да", listener);

        // кнопка нейтрального ответа
        adb.setNeutralButton("Отмена", listener);
        adb.setView(txtUrl);

        //adb.setCancelable(false);
        // создаем диалог
        adb.create();
        AlertDialog alert = adb.create();
        alert.show();
    }
}
class ScanManager {
    public static final String ACTION_SEND_SCAN_RESULT = "com.android.action.SEND_SCAN_RESULT";
    public static final String EXTRA_SCAN_RESULT_ONE_BYTES = "scan_result_one_bytes";
    //public static final String EXTRA_SCAN_RESULT_TWO_BYTES = "scan_result_two_bytes";

    private ScanManager() {
    }
/*
    public static ScanManager getInstance() {
        return null;
    }

    public boolean setScanEnable(boolean enable) {
        return false;
    }

    public synchronized boolean startScan() {
        return true;
    }

    public boolean stopScan() {
        return true;
    }

    public boolean enableBeep() {
        return true;
    }

    public boolean disableBeep() {
        return true;
    }

    public boolean setOutpuMode(int outputMode) {
        return true;
    }

    public boolean setScanMode(int scanMode) {
        return true;
    }

    public boolean setTriggerEnable(String trigger, boolean on) {
        return true;
    }

    public boolean setScanTimeout(long scanTimeout) {
        return true;
    }

    public boolean setScanIntervalTime(long scanIntervalTime) {
        return true;
    }

    public boolean setAutoNewLineEnable(boolean enable) {
        return false;
    }

    public boolean setPrefixEnable(boolean enable) {
        return false;
    }

    public boolean setSuffixEnable(boolean enable) {
        return false;
    }

    public boolean setPrefix(String prefix) {
        return true;
    }

    public boolean setSuffix(String suffix) {
        return true;
    }

    public boolean setScanPromptVibrateEnable(boolean enable) {
        return true;
    }

    public boolean setScanPromptLEDEnable(boolean enable) {
        return false;
    }

    public Map<String, String> getScanSettings() {
        return null;
    }

    public String getScannerModel() {
        return null;
    }

    public int getScannerType() {
        return 1;
    }

    public boolean setScanEncode(int charsetType) {
        return false;
    }

    public String getScanerSetting(String codeId, String property, String defValue) {
        return defValue;
    }

    public boolean setCodeParam(String codeId, String property, String value) {
        return true;
    }
    */
}
