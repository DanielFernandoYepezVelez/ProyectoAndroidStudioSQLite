package com.example.estudios;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class ClsOpenHelper extends SQLiteOpenHelper {

    public ClsOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table TblCliente(identificacion text primary key, " +
        "nombre text not null, profesion text not null, empresa text not null, " +
        "salario integer not null, ingresoExtra integer not null, gastos integer not null, " +
        "activo text not null default 'si')");

        db.execSQL("create table TblCredito(codigoCredito text primary key, " +
        "identificacion text not null, valorPrestamo integer not null, " +
        "activo text not null default 'si', constraint pk_credito " +
        "foreign key(identificacion) references TblCliente(identificacion))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table TblCliente");
        db.execSQL("drop table TblCredito"); {
            onCreate(db);
        }
    }
}
