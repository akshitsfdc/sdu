package com.akshit.akshitsfdc.allpuranasinhindi.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.akshit.akshitsfdc.allpuranasinhindi.contracts.BooksContract.*;

public class BooksDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "offlineBooks.db";
    public static final int DATABASE_VERSION = 1;

    public BooksDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_OFFLINE_BOOKS_TABLE = "CREATE TABLE " +
                SoftBookEntry.TABLE_NAME_OFFLINE + " (" +
                SoftBookEntry.BOOK_ID + " TEXT PRIMARY KEY, " +
                SoftBookEntry.PIC_URL + " TEXT, " +
                SoftBookEntry.NAME + " TEXT, " +
                SoftBookEntry.LANGUAGE + " TEXT, " +
                SoftBookEntry.FREE + " BOOLEAN, " +
                SoftBookEntry.PRICE + " REAL, " +
                SoftBookEntry.DOWNLOAD_URL + " TEXT, " +
                SoftBookEntry.DESCRIPTION + " TEXT, " +
                SoftBookEntry.PAGES + " TEXT, "+
                SoftBookEntry.VIDEO_OPTION + " BOOLEAN, " +
                SoftBookEntry.PRIORITY + " INTEGER, " +
                SoftBookEntry.TYPE + " TEXT, " +
                SoftBookEntry.CATEGORY + " TEXT, " +
                SoftBookEntry.ADDED_TIME + " INTEGER, " +
                SoftBookEntry.READING_PAGE + " INTEGER, " +
                SoftBookEntry.IS_FAVORITE + " BOOLEAN, " +
                SoftBookEntry.BOOK_URI + " TEXT, "+
                SoftBookEntry.COVER_URI + " TEXT, "+
                SoftBookEntry.COLUMN_TIMESTAMP + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";

        final String TRIGGER_AFTER_UPDATE = "CREATE TRIGGER IF NOT EXISTS "+
                SoftBookEntry.TRIGGER_NAME_AFTER_UPDATE+" AFTER UPDATE ON "+
                SoftBookEntry.TABLE_NAME_OFFLINE+
                " BEGIN " +
                " UPDATE " +SoftBookEntry.TABLE_NAME_OFFLINE+
                " SET "+SoftBookEntry.COLUMN_TIMESTAMP+" = CURRENT_TIMESTAMP"+
                " WHERE "+ SoftBookEntry.BOOK_ID +" = old.bookId; " +
                " END ";
        db.execSQL(SQL_CREATE_OFFLINE_BOOKS_TABLE);
        db.execSQL(TRIGGER_AFTER_UPDATE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SoftBookEntry.TABLE_NAME_OFFLINE);
        onCreate(db);
    }

}
