package com.example.fuelid;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Settings {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "settingsFUELIDApp.db";
        private static final String TABLE_NAME = "settingsApp"; 
        private static final String ID = "_id";
        private static final String COMPANY = "usuario";
        private static final String USER = "user";
        private static final String LICENSE = "password";
        private static final String DATE = "fecha";
        private SettingsOpenHelper openHelper;
        private SQLiteDatabase database;

        public Settings(Context context) {
                openHelper = new SettingsOpenHelper(context);
                database = openHelper.getWritableDatabase();
        }
        
        public void saveSettings(String user,String company,String license,String date) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(USER, user); 
                contentValues.put(COMPANY, company);
                contentValues.put(LICENSE, license);
                contentValues.put(DATE, date);
                database.insert(TABLE_NAME, null, contentValues);
                
        }  
        public void deleteSettings()
        {
                 database.delete(TABLE_NAME, null, null);
        }
        public void closedb()
        {
      
                 database.close();

        }
        public Cursor getSettings() {
        return database.rawQuery(
        "SELECT * FROM " + TABLE_NAME, null);
        }

        private class SettingsOpenHelper extends SQLiteOpenHelper {

        public SettingsOpenHelper(Context context) {
                        super(context, DATABASE_NAME, null, DATABASE_VERSION);
                }

                @Override
        public void onCreate(SQLiteDatabase database) {
                        database.execSQL("CREATE TABLE " + TABLE_NAME + "("
                                        + ID + " INTEGER PRIMARY KEY, "
                                            + USER + " TEXT, "
                                                   + COMPANY + " TEXT, "
                                                   + LICENSE + " TEXT, "
                        + DATE + " TEXT) ");

                }

                
                @Override
                public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
                        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
                        onCreate(database);

                }
         
              
        }

}
