package ru.ifmo.md.colloquium2.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Date;

import ru.ifmo.md.colloquium2.Candidate;

/**
 * Created by pva701 on 06.11.14.
 */
public class QueriesManager {
    private static QueriesManager instance;

    public static QueriesManager get(Context context) {
        if (instance == null)
            instance = new QueriesManager(context);
        return instance;
    }
    private Context context;

    private QueriesManager(Context context) {
        this.context = context;
    }

    public void insertCandidate(Candidate candidate) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_CANDIDATES_NAME, candidate.getName());
        cv.put(DatabaseHelper.COLUMN_CANDIDATES_VOTES, 0);
        context.getContentResolver().insert(CandidatesContentProvider.CANDIDATES_CONTENT_URI, cv);
        //QueriesManager.get(context).insertCandidate(candidate);
    }

    public void voteCandidate(int id, int votes) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_CANDIDATES_VOTES, votes);
        context.getContentResolver().update(CandidatesContentProvider.CANDIDATES_CONTENT_URI, cv,
                DatabaseHelper.COLUMN_CANDIDATES_ID + " = " + id, null);
    }

    public int getState() {
        Cursor cursor = context.getContentResolver().query(CandidatesContentProvider.STATE_CONTENT_URI,
                null, null, null, null);
        cursor.moveToNext();
        return cursor.getInt(1);
    }

    public void changeState(int state) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.TABLE_COLUMN_STATE, state);
        context.getContentResolver().update(CandidatesContentProvider.STATE_CONTENT_URI, cv, "_id > 0", null);
    }

    public void deleteAll() {
        context.getContentResolver().delete(CandidatesContentProvider.CANDIDATES_CONTENT_URI, null, null);
    }

    public void updateCandidate(int id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_CANDIDATES_NAME, name);
        context.getContentResolver().update(CandidatesContentProvider.CANDIDATES_CONTENT_URI, cv,
                DatabaseHelper.COLUMN_CANDIDATES_ID + " = " + id, null);
    }
}
