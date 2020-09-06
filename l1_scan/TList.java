package com.leadbelly.l1_scan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vic on 15.05.17.
 */

//private static final String CreateList_table = "create table List (_id integer primary key autoincrement,id_job integer,n_str TEXT,d_doc TEXT,n_doc TEXT,name_k TEXT,kod_t TEXT,name_t TEXT,
// kod_t_p TXT,nomk TEXT,key_str TEXT,kontra TEXT,ss_doc TEXT, name TEXT,name1 TEXT)";

public class TList {

    public String name=   "";
    public String name1=  "";
    public String n_str=  "";
    public String d_doc=  "";
    public String n_doc=  "";
    public String name_k= "";
    public String kod_t=  "";
    public String name_t= "";
    public String kod_t_p="";
    public String nomk=   "";
    public String key_str="";
    public String kontra= "";
    public String ss_doc= "";


    public long id=-1;
    public long id_job=-1;

    List<TScan> scan= new ArrayList<TScan>();

    int find_scan(String s_find) {
        return find_scan(s_find,0);
    }
    int find_scan(String s_find,int i1) {
        for (int i=i1;i < scan.size();i++) {
            if( s_find.equals(scan.get(i).scan)) return i;
        }
        return -1;
    }
    boolean test_col_scan()
    {
        int all_col=0;
        int all_col_scan=0;

        for (int i=0;i < scan.size();i++) {
            all_col+=scan.get(i).col;
            all_col_scan+=scan.get(i).col_scan;
        }

        if(all_col == 0 )return false;
        if(all_col != all_col_scan )return false;


        return true;
    }

    //Не сохранять в БД
    public long is_last_edit=0;


}
