package ru.ifmo.md.colloquium2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.ifmo.md.colloquium2.Candidate;
import ru.ifmo.md.colloquium2.R;
import ru.ifmo.md.colloquium2.processor.Processor;
import ru.ifmo.md.colloquium2.provider.CandidatesContentProvider;
import ru.ifmo.md.colloquium2.provider.DatabaseHelper;
import ru.ifmo.md.colloquium2.services.OperationHelper;

/**
 * Created by pva701 on 11.11.14.
 */
public class CandidatesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String DIALOG_ADD_CANDIDATE = "add_dialog";
    private static final int REQ_DIAL_ADD = 0;
    private ListView listView;
    private CursorAdapter adapter;
    private int currentState;
    private View v;
    private int sumVotes = 0;

    class MyCursorAdapter extends CursorAdapter {
        private DatabaseHelper.CandidateCursor cursor;
        public MyCursorAdapter(Context context, DatabaseHelper.CandidateCursor cursor) {
            super(context, cursor, true);
            this.cursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.candidate_list_item, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cur) {
            Candidate candidate = cursor.getCandidate();
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView votes = (TextView) view.findViewById(R.id.votes);
            name.setText(candidate.getName());
            if (currentState == Processor.STATE_CLOSED)
                votes.setText((int) (100.0 * candidate.getVotes() / sumVotes) + "%");
            else
                votes.setText("" + candidate.getVotes());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (currentState == Processor.STATE_ACTIVE )
            return new CursorLoader(getActivity(),
                CandidatesContentProvider.CANDIDATES_CONTENT_URI, null, null, null, null);
        else if (currentState == Processor.STATE_CLOSED)
            return new CursorLoader(getActivity(),
                    CandidatesContentProvider.CANDIDATES_CONTENT_URI, null, null, null, DatabaseHelper.COLUMN_CANDIDATES_VOTES + " desc");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (currentState == Processor.STATE_CLOSED) {
            sumVotes = 0;
            DatabaseHelper.CandidateCursor cc = new DatabaseHelper.CandidateCursor(cursor);
            while (cc.moveToNext())
                sumVotes += cc.getCandidate().getVotes();
            cursor.moveToFirst();
            cursor.moveToPrevious();
        }

        adapter = new MyCursorAdapter(getActivity(), new DatabaseHelper.CandidateCursor(cursor));
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter = null;
        listView.setAdapter(null);
    }


    private OperationHelper HELPER;
    private OperationHelper.Listener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        HELPER = OperationHelper.get(getActivity());
        listener = new OperationHelper.Listener() {
            @Override
            public void onGetChangeState(int st) {
                currentState = st;
                if (st == Processor.STATE_ACTIVE) {
                    getLoaderManager().restartLoader(0, null, CandidatesListFragment.this);
                    Button but = (Button)v.findViewById(R.id.restart);
                    but.setText("Finish poll");
                } else if (st == Processor.STATE_CLOSED) {
                    Button but = (Button)v.findViewById(R.id.restart);
                    but.setText("Start new poll");
                    getLoaderManager().restartLoader(0, null, CandidatesListFragment.this);
                }

            }

            /*@Override
            public void onChangeState(int st) {

            }*/
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_candidates_list, container, false);
        listView = (ListView)v.findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentState != Processor.STATE_ACTIVE) {
                    Toast.makeText(getActivity(), "Poll is not active", Toast.LENGTH_SHORT).show();
                } else {
                    HELPER.voteCandidate(((DatabaseHelper.CandidateCursor)adapter.getItem(i)).getCandidate());
                }
            }
        });
        HELPER.getState();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        HELPER.addListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        HELPER.removeListener(listener);
    }


    public void addCandidate(View v) {
        if (currentState != Processor.STATE_ACTIVE) {
            Toast.makeText(getActivity(), "Poll is not active", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddChangeSourceDialog dialog = new AddChangeSourceDialog();
        dialog.setTargetFragment(CandidatesListFragment.this, REQ_DIAL_ADD);
        dialog.show(fm, DIALOG_ADD_CANDIDATE);
    }

    public void restart(View v) {
        if (currentState == Processor.STATE_ACTIVE) {
            HELPER.changeState(Processor.STATE_CLOSED);
            currentState = Processor.STATE_CLOSED;
        } else if (currentState == Processor.STATE_CLOSED) {
            HELPER.changeState(Processor.STATE_ACTIVE);
            currentState = Processor.STATE_ACTIVE;
            HELPER.deleteAllCandidates();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_DIAL_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra(AddChangeSourceDialog.EXTRA_NAME);
                HELPER.addCandidate(name);
            }
        }
    }
}
