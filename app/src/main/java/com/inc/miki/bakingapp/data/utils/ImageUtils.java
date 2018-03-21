package com.inc.miki.bakingapp.data.utils;

import android.content.Context;

import com.example.android.baking.R;

/**
 * Provides helper methods to assist in loading the correct image for a recipe.
 */

public final class ImageUtils {

    private ImageUtils() {

    }

    public static int getImageResourceId(Context context, String recipeName) {
        int drawableId = -1;
        if (recipeName.equals(context.getString(R.string.nutella_pie))) {
            drawableId = R.drawable.nutella_pie;
        } else if (recipeName.equals(context.getString(R.string.brownies))) {
            drawableId = R.drawable.brownies;
        } else if (recipeName.equals(context.getString(R.string.yellow_cake))) {
            drawableId = R.drawable.yellow_cake;
        } else if (recipeName.equals(context.getString(R.string.cheesecake))) {
            drawableId = R.drawable.cheesecake;
        }
        return drawableId;
    }
}
