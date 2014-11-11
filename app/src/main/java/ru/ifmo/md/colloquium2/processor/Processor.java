package ru.ifmo.md.colloquium2.processor;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import org.xml.sax.SAXException;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ru.ifmo.md.colloquium2.Candidate;
import ru.ifmo.md.colloquium2.provider.QueriesManager;

/**
 * Created by pva701 on 06.11.14.
 */
public class Processor {
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_CLOSED = 2;

    public static final int GET_STATE = 0;

    private static Processor instance;
    private Handler handler;

    public static Processor get(Context context) {
        if (instance == null)
            instance = new Processor(context.getApplicationContext());
        return instance;
    }

    private Context context;
    private Processor(Context context) {
        this.context = context;
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void removeHandler() {handler = null;}

    public void insertCandidate(Candidate candidate) {
        QueriesManager.get(context).insertCandidate(candidate);
        //if (handler != null)
            //handler.obtainMessage(Processor.UPDATE_NEWS).sendToTarget();
    }

    public void getState() {
        int state = QueriesManager.get(context).getState();
        if (handler != null)
            handler.obtainMessage(Processor.GET_STATE, state, 0).sendToTarget();
    }

    public void vote(int id, int votes) {
        QueriesManager.get(context).voteCandidate(id, votes);
    }

    public void changeState(int newState) {
        QueriesManager.get(context).changeState(newState);
        if (handler != null)
            handler.obtainMessage(Processor.GET_STATE, newState, 0).sendToTarget();
    }

    public void updateCandidate(int id, String name) {
        QueriesManager.get(context).updateCandidate(id, name);
    }

    public void deleteAllCandidates() {
        QueriesManager.get(context).deleteAll();
    }

}
