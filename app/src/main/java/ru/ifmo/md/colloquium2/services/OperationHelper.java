package ru.ifmo.md.colloquium2.services;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import ru.ifmo.md.colloquium2.Candidate;
import ru.ifmo.md.colloquium2.processor.Processor;

/**
 * Created by pva701 on 05.11.14.
 */
public class OperationHelper {//Singlet
    private static OperationHelper instance;
    private ArrayList <Listener> listeners;
    private Context context;
    private OperationHelper(Context c) {
        context = c;
        listeners = new ArrayList<Listener>();

        Processor.get(context).setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Processor.GET_STATE)
                    for (Listener e : listeners)
                        e.onGetChangeState(msg.arg1);
                /*else if (msg.what == Processor.CHANGE_STATE)
                    for (Listener e : listeners)
                        e.onChangeState(msg.arg1);*/
            }
        });
    }

    public static OperationHelper get(Context c) {
        if (instance == null)
            instance = new OperationHelper(c.getApplicationContext());
        return instance;
    }

    public void getState() {
        context.startService(new Intent(context, DatabaseService.class).
        putExtra(DatabaseService.QUERY_EXTRA, DatabaseService.GET_STATE));
    }

    public void changeState(int newState) {
        context.startService(new Intent(context, DatabaseService.class).
                putExtra(DatabaseService.QUERY_EXTRA, DatabaseService.CHANGE_STATE).
                putExtra(DatabaseService.DATA_EXTRA, newState));
    }

    public void addCandidate(String candidate) {
        context.startService(new Intent(context, DatabaseService.class).
                putExtra(DatabaseService.QUERY_EXTRA, DatabaseService.ADD_CANDIDATES).
                putExtra(DatabaseService.DATA_EXTRA, candidate));
    }

    public void voteCandidate(Candidate candidate) {
        context.startService(new Intent(context, DatabaseService.class).
                putExtra(DatabaseService.QUERY_EXTRA, DatabaseService.VOTE).
                putExtra(DatabaseService.DATA_EXTRA, candidate.getId()).
        putExtra(DatabaseService.DATA_EXTRA1, candidate.getVotes() + 1));
    }

    public void deleteAllCandidates() {
        context.startService(new Intent(context, DatabaseService.class).
                putExtra(DatabaseService.QUERY_EXTRA, DatabaseService.DELETE_ALL));
    }

    public void updateCandidate(int id, String name) {
        context.startService(new Intent(context, DatabaseService.class).
        putExtra(DatabaseService.QUERY_EXTRA, DatabaseService.UPDATE_CANDIDATE).
        putExtra(DatabaseService.DATA_EXTRA, id).
        putExtra(DatabaseService.DATA_EXTRA1, name));
    }

    public void addListener(Listener callback) {
        listeners.add(callback);
    }

    public void removeListener(Listener listener) {
        for (int i = 0; i < listeners.size(); ++i)
            if (listeners.get(i) == listener)
                listeners.remove(i);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public static class Listener {
        public void onGetChangeState(int st) {}
        //public void onChangeState(int st) {}
    }

}
