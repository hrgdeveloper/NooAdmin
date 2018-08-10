package com.developer.hrg.nooadmin.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.developer.hrg.nooadmin.Models.Comment_Read;

import java.util.ArrayList;

/**
 * Created by hamid on 8/9/2018.
 */

public class AdminData extends SQLiteOpenHelper {
public final static String DATABASE_NAME ="admin_data" ;

    public static final String TABLE_UNREAD_COMMENT = "tbl_comment_unread";


    public static final String ID_UNREAD="unread_id" ;
    public static final String UNREAD_CHANEL_ID="unread_chanel_id" ;
    public static final String READ_COUNT="read_count" ;
    String CREATE_TABLE_UNREAD= "CREATE TABLE " + TABLE_UNREAD_COMMENT+ " ("+ID_UNREAD+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            +UNREAD_CHANEL_ID+" INTEGER NOT NULL , " +
            READ_COUNT+ " INTEGER NOT NULL)";

    SQLiteDatabase sqLiteDatabase ;


    public AdminData(Context context) {
        super(context, DATABASE_NAME, null, 1);
        sqLiteDatabase=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_UNREAD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_UNREAD_COMMENT);
        onCreate(sqLiteDatabase);

    }


    public boolean hasreadData() {
        long cnt  = DatabaseUtils.queryNumEntries(sqLiteDatabase, TABLE_UNREAD_COMMENT);
        return cnt > 0;

    }
    public void addread(Comment_Read unread) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNREAD_CHANEL_ID,unread.getChanel_id());
        contentValues.put(READ_COUNT,unread.getRead_cout());
        sqLiteDatabase.insert(TABLE_UNREAD_COMMENT,null,contentValues);
    }
    public ArrayList<Comment_Read> getAllReads() {
        ArrayList<Comment_Read> reads = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_UNREAD_COMMENT,new String [] {UNREAD_CHANEL_ID,READ_COUNT},null,null,null,null,null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                int chanel_id = cursor.getInt(cursor.getColumnIndexOrThrow(UNREAD_CHANEL_ID));
                int readCount = cursor.getInt(cursor.getColumnIndexOrThrow(READ_COUNT));
                Comment_Read unRead = new Comment_Read(chanel_id,readCount);
                reads.add(unRead);
            }while (cursor.moveToNext());
            return reads;
        }else {
            return  null ;
        }
    }

    public void updateRead(int readCount  , int chanel_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(READ_COUNT, readCount);
        sqLiteDatabase.update(TABLE_UNREAD_COMMENT,contentValues,UNREAD_CHANEL_ID+ " LIKE ? " ,new String[]{String.valueOf(chanel_id)});
    }

}
