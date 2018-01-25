package com.buma.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbActivity extends SQLiteOpenHelper {
    private static final String NAMA_DATABASE = "dbMonitoring";
    private static final String NAMA_TABLE = "tb_activity";

    public DbActivity(Context context) {
        super(context, NAMA_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //method createTable untuk membuat table activity
    public void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists " + NAMA_TABLE + "  ( _id INTEGER  PRIMARY KEY autoincrement not null  ," +
                "  judul VARCHAR(50), " +
                "  tanggal VARCHAR(12), " +
                "  lokasi VARCHAR(50)," +
                "  deskripsi TEXT , status VARCHAR(20));");
        db.close();
    }

    public Cursor selectBiodata(SQLiteDatabase db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    //method insertActivity untuk mengisikan data ke tb_activity.
    public void insertActivity(SQLiteDatabase db, String[] data) {
        ContentValues cv = new ContentValues();
        cv.put("judul", data[1]);
        cv.put("tanggal", data[2]);
        cv.put("lokasi", data[3]);
        cv.put("deskripsi", data[4]);
        cv.put("status", data[5]);
        db.insert(NAMA_TABLE, null, cv);
        db.close();
    }

    //method updateAcivity untuk mengisikan data ke Activity.
    public void updateActivity(SQLiteDatabase db, String[] data) {
        ContentValues cv = new ContentValues();
        cv.put("judul", data[1]);
        cv.put("tanggal", data[2]);
        cv.put("lokasi", data[3]);
        cv.put("deskripsi", data[4]);
        cv.put("status", data[5]);
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(data[0])};
        db.update(NAMA_TABLE, cv, whereClause, whereArgs);
        db.close();
    }

    //method deleteActivity untuk mengisikan data ke Activity.
    public void deleteActivity(SQLiteDatabase db, String _id) {
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(_id)};
        db.delete(NAMA_TABLE, whereClause, whereArgs);
        db.close();
    }

    public void deleteAllActivity(SQLiteDatabase db) {
        db.delete(NAMA_TABLE, null, null);
        db.close();
    }
}