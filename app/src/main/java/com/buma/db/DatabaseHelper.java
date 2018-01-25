package com.buma.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db_allianz";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
		 * Create the hospital directory table and populate it with sample data.
		 * In step 6, we will move these hardcoded statements to an XML document.
		 */
        String sql = "CREATE TABLE IF NOT EXISTS  (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "province TEXT, " +
                "name TEXT, " +
                "type TEXT, " +
                "officePhone TEXT, " +
                "fax TEXT, " +
                "address TEXT, " +
                "email TEXT" +
                "rawatInapMelahirkan VARCHAR(10) " +
                "rawatJalan VARCHAR(10)" +
                "rawatGigi VARCHAR(10))";
        db.execSQL(sql);

        ContentValues values = new ContentValues();

        // JAKARTA PUSAT
        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. HUSADA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-649-0090");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

        values.put("province", "JAKARTA PUSAT");
        values.put("name", "Rs. BUNDA JAKARTA");
        values.put("type", "PLATINUM");
        values.put("officePhone", "021-319-22005");
        values.put("fax", "021-310-1077");
        values.put("address", "Jl. Teuku Cik Ditiro No.28, Menteng ,Jakarta Pusat");
        values.put("email", "buundahospital@bunda.co.id");
        values.put("rawatInapMelahirkan", "Ya");
        values.put("rawatJalan", "Ya");
        values.put("rawatGigi", "Ya");
        db.insert("tb_hospital", "fax", values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_hospital");
        onCreate(db);
    }

}
