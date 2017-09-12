package com.example.fuelid;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserPermissionsDatabaseHelper {

        private static final int DATABASE_VERSION = 2;
        private static final String DATABASE_NAME = "FIUPermission.db";
        private static final String TABLE_NAME = "UPermission_Table";
        private static final String PERMISSION_ID = "_id";
        private static final String UID = "userid";
        private static final String FUELSTATIONCODE = "fuelstationcode";
        private static final String FUELSTATIONID = "fuelstationid";
        private static final String COMPANY = "company";
        private static final String GASTYPE = "gastype";
        private ContactOpenHelper openHelper;
        private SQLiteDatabase database;

        public UserPermissionsDatabaseHelper(Context context) {
                openHelper = new ContactOpenHelper(context);
                database = openHelper.getWritableDatabase();
        }
        
        public void savePermissions(String uid,String fuelstationcode,String fuelstationid,String company,String gastype) {
        	    ContentValues contentValues = new ContentValues();
                contentValues.put(UID, uid);
                contentValues.put(FUELSTATIONCODE, fuelstationcode);
                contentValues.put(FUELSTATIONID, fuelstationid);
                contentValues.put(COMPANY, company);
                contentValues.put(GASTYPE, gastype);
                database.insert(TABLE_NAME, null, contentValues);
        }
        public void deleteAllData()
        {
                 database.delete(TABLE_NAME, null, null);
        }
        public Cursor getAllItemPermissionLines() {
                return database.rawQuery(
                             "SELECT * FROM " + TABLE_NAME+"", 
                             null);
        } 
        public Cursor getAllUserPermissionLines(String User) {
            return database.rawQuery(
                         "SELECT * FROM " + TABLE_NAME+" WHERE "+UID+" ='"+User+"'", 
                         null);
    } 

        private class ContactOpenHelper extends SQLiteOpenHelper {

                public ContactOpenHelper(Context context) {
                        super(context, DATABASE_NAME, null, DATABASE_VERSION);
                }
                
                @Override
                public void onCreate(SQLiteDatabase database) {
                        database.execSQL("CREATE TABLE " + TABLE_NAME + "("
                                        + PERMISSION_ID + " INTEGER PRIMARY KEY, "
                                        + UID + " TEXT, "
                                        + FUELSTATIONCODE + " TEXT, "
                                        + FUELSTATIONID + " TEXT, "
                                        + COMPANY + " TEXT, "
                                        + GASTYPE + " TEXT)");
                } 
                @Override
                public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
                        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
                        onCreate(database);
                }

        }

}

