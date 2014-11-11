package ru.ifmo.md.colloquium2.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.ifmo.md.colloquium2.Candidate;

/**
 * Created by pva701 on 17.10.14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static class CandidateCursor extends CursorWrapper {
        public CandidateCursor(Cursor cursor) {
            super(cursor);
        }

        public Candidate getCandidate() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            return new Candidate(getInt(getColumnIndexOrThrow(COLUMN_CANDIDATES_ID)), getString(getColumnIndexOrThrow(COLUMN_CANDIDATES_NAME)),
                                          getInt(getColumnIndexOrThrow(COLUMN_CANDIDATES_VOTES)));
        }
    }

    public static final int VERSION = 1;
    public static final String DB_NAME = "Candidates";

    public static final String TABLE_CANDIDATES = "candidates";
    public static final String COLUMN_CANDIDATES_ID = "_id";
    public static final String COLUMN_CANDIDATES_NAME = "name";
    public static final String COLUMN_CANDIDATES_VOTES = "votes";

    public static final String TABLE_STATE = "state";
    public static final String TABLE_COLUMN_STATE = "state";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table candidates (_id integer primary key autoincrement, " +
                                                  "name varchar(64), " +
                                                  "votes integer)");

        db.execSQL("create table state (_id integer primary key autoincrement, state integer)");
        ContentValues cv = new ContentValues();
        cv.put(TABLE_COLUMN_STATE, 2);
        db.insert(TABLE_STATE, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //none
    }
}
