package com.example.mendez.trackerservice.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mendez on 13/06/2018.
 */

public class BD extends SQLiteOpenHelper {
    private String consulta="CREATE TABLE sms (sms TEXT, fecha TEXT, hora TEXT,  nombre TEXT,tipo TEXT)";

    public BD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(consulta);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS sms");

        //Se crea la nueva versión de la tabla
        onConfigure(db);
    }
}
