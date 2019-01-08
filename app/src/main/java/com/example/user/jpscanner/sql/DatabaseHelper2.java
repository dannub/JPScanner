package com.example.user.jpscanner.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;

import com.example.user.jpscanner.model.Absen;
import com.example.user.jpscanner.parcer.JSONparser2;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper2  extends SQLiteOpenHelper {
    private  static final int DATABASE_VERSION= 1;
    private  static final String DATABASE_NAME= "AbsenManager.db";
    private  static final String TABLE_ABSEN= "absen";
    private  static final String COLUMN_NAME= "name";
    private  static final String COLUMN_jenis= "jenis";
    private  static final String COLUMN_tgl= "tgl";
    private  static final String COLUMN_time= "time";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";


    private String CREATE_USER_TABLE="CREATE TABLE "+TABLE_ABSEN+" ("+COLUMN_NAME+ " TEXT,"+COLUMN_jenis+" INT, "+
            COLUMN_tgl+" TEXT,"+COLUMN_time+" TEXT)";
    private String DROP_USER_TABLE ="DROP TABLE IF EXISTS "+TABLE_ABSEN;
    public DatabaseHelper2(Context context){

        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int OldVersion,int NewVersion)
    {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }
    public void addAbsen(Absen absen)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_NAME,absen.getName_Abs());
        values.put(COLUMN_jenis,absen.getJenis_Abs());
        values.put(COLUMN_time,absen.getTime_Abs());
        values.put(COLUMN_tgl,absen.getTgl_Abs());
        db.insert(TABLE_ABSEN,null,values);
        db.close();
    }
    public List<Absen> getAllAbsen(String tgl,int jenis){
        ArrayList<Absen> absens = new ArrayList<Absen>();
        String selectQuery = "SELECT  "+COLUMN_NAME+" , "+COLUMN_jenis+" , "+COLUMN_time+" FROM " + TABLE_ABSEN +
                " WHERE "+COLUMN_jenis+" = "+jenis+" AND "+COLUMN_tgl+" = '"+tgl+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Absen abs = new Absen();
                abs.setName_Abs(c.getString(c.getColumnIndex(COLUMN_NAME)));
                abs.setJenis_Abs(c.getInt(c.getColumnIndex(COLUMN_jenis)));
                abs.setTime_Abs(c.getString(c.getColumnIndex(COLUMN_time)));
                // adding to tags list
                absens.add(abs);
            } while (c.moveToNext());
        }
        return  absens;
    }
    public void DeleteAbsen(String tgl,int jenis){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_ABSEN+" WHERE "+ COLUMN_jenis + " = " + jenis +" AND "+COLUMN_tgl + " = '"+tgl+"'");
        db.close();
    }
    public void UpdateData(String ip,String tgl,int jenis){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        DeleteAbsen(tgl,jenis);
            String url_all_products = "http://" + ip + "/absen/get_absens_all.php";
            System.out.println(ip);
            final String TAG_SUCCESS = "success";
            final String TAG_ABSEN = "absens";
            final String TAG_NAME = "name";
            final String TAG_TIME = "time";
            JSONArray absen = null;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tgl", tgl));
            params.add(new BasicNameValuePair("jenis", Integer.toString(jenis)));
            JSONparser2 jParser = new JSONparser2();
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    absen = json.getJSONArray(TAG_ABSEN);


                    // looping through All Products
                    for (int i = 0; i < absen.length(); i++) {
                        JSONObject c = absen.getJSONObject(i);
                        Absen abs = new Absen();
                        abs.setName_Abs(c.getString(TAG_NAME));
                        abs.setTime_Abs(c.getString(TAG_TIME));
                        abs.setJenis_Abs(jenis);
                        abs.setTgl_Abs(tgl);
                        this.addAbsen(abs);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




    }

}
