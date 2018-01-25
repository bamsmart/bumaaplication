package com.buma.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbGreenCard extends SQLiteOpenHelper {
    private static final String NAMA_DATABASE = "dbMonitoring";
    private static final String NAMA_TABLE = "tb_greenCard";

    public DbGreenCard(Context context) {
        super(context, NAMA_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // method createTable untuk membuat table greenCard
    public void createTable(SQLiteDatabase db) {
        db.execSQL("Create Table If not Exists " + NAMA_TABLE
                + "  ( _id Integer  Primary Key Autoincrement Not Null,"
                + "  noreg varchar(35),"
                + "  danger_type varchar(30), "
                + "  place varchar(50) , "
                + "  time varchar(20) ,"
                + "  detail text , "
                + "  planning text ,"
                + "  danger char(2), "
                + "  flag varchar(25) , "
                + "  image_data BLOB ); ");
    }

    public Cursor selectGreenCard(SQLiteDatabase db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    // method insertGreenCard untuk mengisikan data ke tb_greencrard.
    public void insertGreenCard(SQLiteDatabase db, String[] data) {
        ContentValues cv = new ContentValues();
        cv.put("danger_type", data[0]);
        cv.put("place", data[1]);
        cv.put("time", data[2]);
        cv.put("detail", data[3]);
        cv.put("planning", data[4]);
        cv.put("danger", data[5]);
        cv.put("flag", data[6]);
        db.insert(NAMA_TABLE, null, cv);
    }

    // method insertGreenCard untuk mengisikan data ke tb_greencrard.
    public void insertGreenCardWithImage(SQLiteDatabase db, String[] data, byte[] imageByte) {
        ContentValues cv = new ContentValues();
        cv.put("danger_type", data[0]);
        cv.put("place", data[1]);
        cv.put("time", data[2]);
        cv.put("detail", data[3]);
        cv.put("planning", data[4]);
        cv.put("danger", data[5]);
        cv.put("flag", data[6]);
        cv.put("image_data", imageByte);
        db.insert(NAMA_TABLE, null, cv);
    }

    // method updateGreenCard untuk mengubah data ke greencard.
    public void updateGreenCard(SQLiteDatabase db, String[] data, byte[] byteImage) {
        ContentValues cv = new ContentValues();
        cv.put("danger_type", data[1]);
        cv.put("place", data[2]);
        cv.put("time", data[3]);
        cv.put("detail", data[4]);
        cv.put("planning", data[5]);
        cv.put("danger", data[6]);
        cv.put("flag", data[7]);
        cv.put("image_data", byteImage);
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(data[0])};
        db.update(NAMA_TABLE, cv, whereClause, whereArgs);
    }

    // method updateGreenCard untuk mengisikan data ke greencard.
    public void updateFlagGreenCard(SQLiteDatabase db, String[] data) {
        ContentValues cv = new ContentValues();
        cv.put("flag", data[1]);
        cv.put("noreg", data[2]);
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(data[0])};
        db.update(NAMA_TABLE, cv, whereClause, whereArgs);
    }

    // method updateGreenCard untuk mengisikan data ke greencard.
    public void updateStatusGreenCard(SQLiteDatabase db, String[] data) {
        ContentValues cv = new ContentValues();
        cv.put("flag", data[1]);
        String whereClause = "noreg=?";
        String[] whereArgs = new String[]{String.valueOf(data[0])};
        db.update(NAMA_TABLE, cv, whereClause, whereArgs);
    }

    // method deleteGreenCard untuk mengisikan data ke GreenCard.
    public void deleteGreenCard(SQLiteDatabase db, String _id) {
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(_id)};
        db.delete(NAMA_TABLE, whereClause, whereArgs);
    }

    public void deleteAllActivity(SQLiteDatabase db) {
        db.delete(NAMA_TABLE, null, null);
    }

}