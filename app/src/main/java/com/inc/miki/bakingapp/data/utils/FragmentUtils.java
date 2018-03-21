package com.inc.miki.bakingapp.data.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.android.baking.R;
import com.example.android.baking.fragment.StepDetailsFragment;

/**
 * Provides helper methods for adding and replacing Fragments.
 */

public final class FragmentUtils {

    private FragmentUtils() {

    }

    public static void addDetailsFragment(FragmentActivity fragmentActivity, Bundle bundle) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        StepDetailsFragment stepDetailsFragment = (StepDetailsFragment) fragmentManager
                .findFragmentByTag(StepDetailsFragment.class.getSimpleName());
        if (stepDetailsFragment != null) {
            return;
        }

        fragmentManager.beginTransaction()
                .add(
                        R.id.container_step_details,
                        StepDetailsFragment.newInstance(bundle),
                        StepDetailsFragment.class.getSimpleName()
                )
                .commit();
    }

    public static void replaceDetailsFragment(FragmentActivity fragmentActivity, Bundle bundle) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(
                        R.id.container_step_details,
                        StepDetailsFragment.newInstance(bundle),
                        StepDetailsFragment.class.getSimpleName()
                )
                .commit();
    }
}
