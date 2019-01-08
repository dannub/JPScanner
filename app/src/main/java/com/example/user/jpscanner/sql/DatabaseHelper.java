package com.example.user.jpscanner.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.jpscanner.model.User;

/**
 * Created by User on 09/01/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION= 1;
    private  static final String DATABASE_NAME= "UserManager.db";
    private  static final String TABLE_USER= "user";
    private  static final String COLUMN_USER_ID= "user_id";
    private  static final String COLUMN_USER_name= "user_name";
    private  static final String COLUMN_USER_email= "user_email";
    private  static final String COLUMN_USER_ip= "user_ip";
    private  static final String COLUMN_USER_password= "user_password";

    private String CREATE_USER_TABLE="CREATE TABLE "+TABLE_USER+" ("+COLUMN_USER_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"+COLUMN_USER_name+" TEXT, "+
            COLUMN_USER_email+" TEXT,"+COLUMN_USER_password+" TEXT,"+COLUMN_USER_ip+" TEXT)";
    private String DROP_USER_TABLE ="DROP TABLE IF EXISTS "+TABLE_USER;
    public DatabaseHelper(Context context){
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
    public void addUser(User user)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_USER_name,user.getName());
        values.put(COLUMN_USER_email,user.getEmail());
        values.put(COLUMN_USER_password,user.getPassword());
        values.put(COLUMN_USER_ip,user.getIP());
        db.insert(TABLE_USER,null,values);
        db.close();
    }
    public boolean checkuser(String email,String password){
        String[] colomns={
                COLUMN_USER_ID
        };
        SQLiteDatabase db=this.getWritableDatabase();
        String selection =COLUMN_USER_email+" = ?"+" AND " + COLUMN_USER_password+" = ?" ;
        String[] selectionArgs={email,password};
        Cursor cursor = db.query(TABLE_USER,
                colomns,
                selection,
                selectionArgs,null,null,null);
        int cursorcount =cursor.getCount();
        cursor.close();
        db.close();
        if(cursorcount>0)
        {
            return true;
        }
        return false;
    }
    public String getNama(String email){
        String[] colomns={
                COLUMN_USER_name
        };
        String x;
        SQLiteDatabase db=this.getWritableDatabase();
        String selection =COLUMN_USER_email+" = ?" ;
        String[] selectionArgs={email};
        Cursor cursor = db.query(TABLE_USER,
                colomns,
                selection,
                selectionArgs,null,null,null);

        cursor.moveToNext();
        x=cursor.getString(0);
        return x;
    }
    public String getIP(String email){
        String[] colomns={
                COLUMN_USER_ip
        };
        String x;
        SQLiteDatabase db=this.getWritableDatabase();
        String selection =COLUMN_USER_email+" = ?" ;
        String[] selectionArgs={email};
        Cursor cursor = db.query(TABLE_USER,
                colomns,
                selection,
                selectionArgs,null,null,null);

        cursor.moveToNext();
        x=cursor.getString(0);
        return x;
    }

}
