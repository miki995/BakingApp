package com.inc.miki.bakingapp.data.contract;

import android.net.Uri;

public class RecipeContract {

    private static final String AUTHORITY = "com.inc.miki.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
}
