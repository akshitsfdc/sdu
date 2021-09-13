package com.akshit.akshitsfdc.allpuranasinhindi.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.contracts.BooksContract.*;
import com.akshit.akshitsfdc.allpuranasinhindi.helpers.BooksDBHelper;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopy;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;

import java.util.ArrayList;
import java.util.List;

public class SQLService {

    private SQLiteDatabase mDatabase;

    public SQLService(AppCompatActivity activity){
        BooksDBHelper dbHelper = new BooksDBHelper(activity);
        mDatabase = dbHelper.getWritableDatabase();
    }

    public SoftCopyModel getOfflineBookById(String bookId){



        String whereClause = SoftBookEntry.BOOK_ID + " = ?";

        String[] whereArgs = new String[1];
        whereArgs[0] = bookId.trim();

        Cursor cursor = getQueryCursor(whereClause, whereArgs, null, null);

        if (cursor.moveToNext()){

            SoftCopyModel softCopyModel =  getOfflineCopyModel(cursor);

            cursor.close();

            return softCopyModel;
        }

        return null;
    }
    private Uri getUriFromString(String uriStr){
        if(uriStr != null){
            return Uri.parse(uriStr);
        }
        return null;
    }
    public List<SoftCopyModel> showOfflineBooks(String limit){

        String whereClause = SoftBookEntry.BOOK_URI +" IS NOT NULL AND "+SoftBookEntry.BOOK_ID + " != ?";;

        String[] whereArgs = new String[1];
        whereArgs[0] = "";

        Cursor cursor = getQueryCursor(whereClause, whereArgs, null,  limit);

        return cursorToBookList(cursor);
    }
    public List<SoftCopyModel> showFavoriteBooks(String limit){

        String whereClause = SoftBookEntry.IS_FAVORITE + " = ?";

        String[] whereArgs = new String[1];
        whereArgs[0] = "1";

        Cursor cursor = getQueryCursor(whereClause, whereArgs, null, limit);

        return cursorToBookList(cursor);

    }

    public List<String> getFavoriteBookIds(){

        List<String> list = new ArrayList<>();

        String whereClause = SoftBookEntry.IS_FAVORITE + " = ?";
        String[] whereArgs = new String[1];
        whereArgs[0] = "1";

        String[] columns = new String[1];
        columns[0] = SoftBookEntry.BOOK_ID;

        Cursor cursor = getQueryCursor(whereClause, whereArgs, columns, null);

        while (cursor.moveToNext()){

            String bookId = cursor.getString(cursor.getColumnIndex(SoftBookEntry.BOOK_ID));

            list.add(bookId);
        }

        cursor.close();

        return list;

    }
    public long updateUpdateFavorite(SoftCopyModel softCopyModel){

        String bookId = softCopyModel.getBookId().trim();

        ContentValues cv = new ContentValues();
        cv.put(SoftBookEntry.IS_FAVORITE, softCopyModel.isFavorite());


        try{
            if(checkIfExist(bookId)){
                return mDatabase.update(SoftBookEntry.TABLE_NAME_OFFLINE,
                    cv,
                    SoftBookEntry.BOOK_ID + "=\"" + bookId.trim() + "\"" , null);
            }else {
                cv.put(SoftBookEntry.BOOK_ID, bookId);
                cv.put(SoftBookEntry.NAME, softCopyModel.getName());
                cv.put(SoftBookEntry.PIC_URL, softCopyModel.getPicUrl());
                cv.put(SoftBookEntry.DOWNLOAD_URL, softCopyModel.getDownloadUrl());
                cv.put(SoftBookEntry.LANGUAGE, softCopyModel.getLanguage());
                cv.put(SoftBookEntry.FREE, softCopyModel.isFree());
                cv.put(SoftBookEntry.PAGES, softCopyModel.getPages());
                cv.put(SoftBookEntry.PRICE, softCopyModel.getPrice());
                cv.put(SoftBookEntry.DESCRIPTION, softCopyModel.getDescription());
                cv.put(SoftBookEntry.VIDEO_OPTION, softCopyModel.isVideoOption());
                cv.put(SoftBookEntry.PRIORITY, softCopyModel.getPriority());
                cv.put(SoftBookEntry.TYPE, softCopyModel.getType());
                cv.put(SoftBookEntry.CATEGORY, softCopyModel.getCategory());
                cv.put(SoftBookEntry.ADDED_TIME, softCopyModel.getAddedTime());
                cv.put(SoftBookEntry.READING_PAGE, 0);

                return mDatabase.replace(SoftBookEntry.TABLE_NAME_OFFLINE, null, cv);
            }

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public long insertOfflineBook(SoftCopyModel softCopyModel, String coverUri, String bookUri){

        String bookId = softCopyModel.getBookId().trim();
        ContentValues cv = new ContentValues();

        cv.put(SoftBookEntry.BOOK_ID, bookId);
        cv.put(SoftBookEntry.NAME, softCopyModel.getName());
        cv.put(SoftBookEntry.PIC_URL, softCopyModel.getPicUrl());
        cv.put(SoftBookEntry.DOWNLOAD_URL, softCopyModel.getDownloadUrl());
        cv.put(SoftBookEntry.LANGUAGE, softCopyModel.getLanguage());
        cv.put(SoftBookEntry.FREE, softCopyModel.isFree());
        cv.put(SoftBookEntry.PAGES, softCopyModel.getPages());
        cv.put(SoftBookEntry.PRICE, softCopyModel.getPrice());
        cv.put(SoftBookEntry.DESCRIPTION, softCopyModel.getDescription());
        cv.put(SoftBookEntry.VIDEO_OPTION, softCopyModel.isVideoOption());
        cv.put(SoftBookEntry.PRIORITY, softCopyModel.getPriority());
        cv.put(SoftBookEntry.TYPE, softCopyModel.getType());
        cv.put(SoftBookEntry.CATEGORY, softCopyModel.getCategory());
        cv.put(SoftBookEntry.ADDED_TIME, softCopyModel.getAddedTime());
        cv.put(SoftBookEntry.READING_PAGE, 0);
        cv.put(SoftBookEntry.IS_FAVORITE, softCopyModel.isFavorite());
        cv.put(SoftBookEntry.BOOK_URI, bookUri);
        cv.put(SoftBookEntry.COVER_URI, coverUri);

        try {

            if(checkIfExist(bookId)){
                cv.remove(SoftBookEntry.IS_FAVORITE);
                return mDatabase.update(SoftBookEntry.TABLE_NAME_OFFLINE,
                        cv,
                        SoftBookEntry.BOOK_ID + "=\"" + bookId + "\"" , null);
            }else {
                return mDatabase.replace(SoftBookEntry.TABLE_NAME_OFFLINE, null, cv);
            }

//            return mDatabase.insert(SoftBookEntry.TABLE_NAME_OFFLINE, null, cv);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }
    public int updateOfflineBookReadingNumber(String bookId, int pageNumber){

        ContentValues cv = new ContentValues();

        cv.put(SoftBookEntry.READING_PAGE, pageNumber);

        try{

            return mDatabase.update(SoftBookEntry.TABLE_NAME_OFFLINE,
                    cv,
                    SoftBookEntry.BOOK_ID + "=\"" + bookId.trim() + "\"" , null);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    public int deleteOfflineBook(String bookId){

        try{
            return mDatabase.delete(SoftBookEntry.TABLE_NAME_OFFLINE,
                    SoftBookEntry.BOOK_ID + "=\"" + bookId.trim() + "\"", null);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }
    private boolean checkIfExist(String bookId){



        String whereClause = SoftBookEntry.BOOK_ID + " = ?";

        String[] columns = new String[1];
        columns[0] = SoftBookEntry.BOOK_ID;

        String[] whereArgs = new String[1];
        whereArgs[0] = bookId.trim();

        Cursor cursor = mDatabase.query(
                SoftBookEntry.TABLE_NAME_OFFLINE,
                columns,
                whereClause,
                whereArgs,
                null,
                null,

                SoftBookEntry.COLUMN_TIMESTAMP + " DESC"

        );

        boolean exists = (cursor.getCount() > 0);

        cursor.close();

        return exists;
    }

    private Cursor getQueryCursor( String whereClause, String[] whereArgs,String[] columns, String limit){

        Cursor cursor = mDatabase.query(
                SoftBookEntry.TABLE_NAME_OFFLINE,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                SoftBookEntry.COLUMN_TIMESTAMP + " DESC",
                limit

        );
        return cursor;
    }
    private List<SoftCopyModel> cursorToBookList(Cursor cursor){

        List<SoftCopyModel> list = new ArrayList<>();

        while (cursor.moveToNext()){
            list.add(getOfflineCopyModel(cursor));
        }

        cursor.close();

        return list;
    }

    private SoftCopyModel getOfflineCopyModel(Cursor cursor){

        SoftCopyModel softCopyModel = new SoftCopyModel();

        softCopyModel.setBookId(cursor.getString(cursor.getColumnIndex(SoftBookEntry.BOOK_ID)));
        softCopyModel.setName(cursor.getString(cursor.getColumnIndex(SoftBookEntry.NAME)));
        softCopyModel.setPicUrl(cursor.getString(cursor.getColumnIndex(SoftBookEntry.PIC_URL)));
        softCopyModel.setDownloadUrl(cursor.getString(cursor.getColumnIndex(SoftBookEntry.DOWNLOAD_URL)));
        softCopyModel.setLanguage(cursor.getString(cursor.getColumnIndex(SoftBookEntry.LANGUAGE)));
        softCopyModel.setFree(cursor.getInt(cursor.getColumnIndex(SoftBookEntry.FREE)) == 1);
        softCopyModel.setPages(cursor.getString(cursor.getColumnIndex(SoftBookEntry.PAGES)));
        softCopyModel.setPrice(cursor.getFloat(cursor.getColumnIndex(SoftBookEntry.PRICE)));
        softCopyModel.setDescription(cursor.getString(cursor.getColumnIndex(SoftBookEntry.DESCRIPTION)));
        softCopyModel.setVideoOption(cursor.getInt(cursor.getColumnIndex(SoftBookEntry.VIDEO_OPTION)) == 1);
        softCopyModel.setPriority(cursor.getInt(cursor.getColumnIndex(SoftBookEntry.PRIORITY)));
        softCopyModel.setType(cursor.getString(cursor.getColumnIndex(SoftBookEntry.TYPE)));
        softCopyModel.setCategory(cursor.getString(cursor.getColumnIndex(SoftBookEntry.CATEGORY)));
        softCopyModel.setAddedTime(cursor.getLong(cursor.getColumnIndex(SoftBookEntry.ADDED_TIME)));
        softCopyModel.setReadingPage(cursor.getInt(cursor.getColumnIndex(SoftBookEntry.READING_PAGE)));
        softCopyModel.setCategory(cursor.getString(cursor.getColumnIndex(SoftBookEntry.CATEGORY)));
        softCopyModel.setCategory(cursor.getString(cursor.getColumnIndex(SoftBookEntry.CATEGORY)));
        softCopyModel.setFavorite(cursor.getInt(cursor.getColumnIndex(SoftBookEntry.IS_FAVORITE)) == 1);

        String coverUriStr = cursor.getString(cursor.getColumnIndex(SoftBookEntry.COVER_URI));
        String bookUriStr = cursor.getString(cursor.getColumnIndex(SoftBookEntry.BOOK_URI));
        softCopyModel.setCoverUri(getUriFromString(coverUriStr));
        softCopyModel.setBookUri(getUriFromString(bookUriStr));

        return softCopyModel;
    }



}
