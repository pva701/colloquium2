package ru.ifmo.md.colloquium2.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import ru.ifmo.md.colloquium2.R;

/**
 * Created by pva701 on 11.11.14.
 */
public class CandidatesListActivity extends FragmentActivity {
    CandidatesListFragment f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates_list);
        FragmentManager fm = getSupportFragmentManager();
        f = (CandidatesListFragment)fm.findFragmentById(R.id.container);
        if (f == null) {
            f = new CandidatesListFragment();
            fm.beginTransaction().add(R.id.container, f).commit();
        }
        setTitle("Sources");
    }

    public void addCandidate(View v) {
        f.addCandidate(v);
    }

    public void restart(View v) {
        f.restart(v);
    }

}
