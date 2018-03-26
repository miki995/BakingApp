package com.inc.miki.bakingapp.util;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.inc.miki.bakingapp.R;
import com.inc.miki.bakingapp.fragment.StepDetailsFragment;


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
