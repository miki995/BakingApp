package com.inc.miki.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.inc.miki.bakingapp.R;
import com.inc.miki.bakingapp.data.ExoPlayerIdlingResource;
import com.inc.miki.bakingapp.data.Step;
import com.inc.miki.bakingapp.data.utils.FragmentUtils;
import com.inc.miki.bakingapp.fragment.StepDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class StepDetailsActivity extends AppCompatActivity implements
        StepDetailsFragment.StepDetailsOnClickListener {

    @Nullable
    private ExoPlayerIdlingResource idlingResource;

    private List<Step> steps;
    private String recipeName;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.steps))) {
                steps = intent.getParcelableArrayListExtra(getString(R.string.steps));
            }

            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(getString(R.string.step_position));
            } else if (intent.hasExtra(getString(R.string.step_position))) {
                position = intent.getIntExtra(getString(R.string.step_position), position);
            }

            recipeName = intent.getStringExtra(getString(R.string.name));
        }

        if (steps != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipeName);
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.steps), (ArrayList<Step>) steps);
            bundle.putInt(getString(R.string.step_position), position);
            FragmentUtils.addDetailsFragment(this, bundle);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.step_position), position);
    }

    @Override
    public void onStepSelected(int position) {
        if (steps != null) {
            this.position = position;

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && !TextUtils.isEmpty(recipeName)) {
                actionBar.setTitle(recipeName);
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.steps), (ArrayList<Step>) steps);
            bundle.putInt(getString(R.string.step_position), this.position);
            FragmentUtils.replaceDetailsFragment(this, bundle);
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new ExoPlayerIdlingResource();
        }
        return idlingResource;
    }

    public void setIdleState(boolean idle) {
        if (idlingResource == null) {
            return;
        }
        idlingResource.setIdleState(idle);
    }
}
