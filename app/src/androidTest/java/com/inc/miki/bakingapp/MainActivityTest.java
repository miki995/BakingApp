package com.inc.miki.bakingapp;

import android.content.Context;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.inc.miki.bakingapp.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests the UI of the main activity.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String BROWNIES = "Brownies";
    private Context context;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void getContext() {
        context = activityRule.getActivity();
    }

    @Test
    public void clickOnRecipeItem_DisplaysCorrectRecipe() {
        onView(withId(R.id.recycler_view_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(allOf(isDescendantOfA(withResourceName(context.getString(R.string.action_bar))), withText(BROWNIES)))
                .check(matches(isDisplayed()));
    }
}
