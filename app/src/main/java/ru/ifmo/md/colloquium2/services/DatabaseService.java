package ru.ifmo.md.colloquium2.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.os.Handler;

import ru.ifmo.md.colloquium2.Candidate;
import ru.ifmo.md.colloquium2.processor.Processor;

/**
 * Created by pva701 on 19.10.14.
 */
public class DatabaseService extends IntentService {
    public static final String TAG = "DatabaseService";
    public static final String QUERY_EXTRA =    "query";
    public static final String DATA_EXTRA =     "data";
    public static final String DATA_EXTRA1 =      "data1";
    public static final int GET_STATE = 0;
    public static final int VOTE = 1;
    public static final int CHANGE_STATE = 2;
    public static final int ADD_CAND = 3;
    public static final int DELETE_ALL = 4;

    //public static final String LOAD_NEWS =      "source_id";
    //public static final String HANDLER_EXTRA =  "handler";

    public DatabaseService() {
        super(TAG);
    }
    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning)
            return Service.START_NOT_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }*/


    @Override
    protected void onHandleIntent(Intent intent) {
        int query = intent.getExtras().getInt(QUERY_EXTRA);
        if (query == GET_STATE) {
            Processor.get(getApplicationContext()).getState();
        } else if (query == VOTE) {
            int id = intent.getIntExtra(DATA_EXTRA, 0);
            int votes = intent.getIntExtra(DATA_EXTRA1, 0);
            Processor.get(getApplicationContext()).vote(id, votes);
        } else if (query == CHANGE_STATE) {
            int newst = intent.getIntExtra(DATA_EXTRA, 0);
            Processor.get(getApplicationContext()).changeState(newst);
        } else if (query == ADD_CAND) {
            String name = intent.getStringExtra(DATA_EXTRA);
            Processor.get(getApplicationContext()).insertCandidate(new Candidate(name, 0));
        } else if (query == DELETE_ALL) {
            Processor.get(getApplicationContext()).deleteAllCandidates();
        }
    }
}
