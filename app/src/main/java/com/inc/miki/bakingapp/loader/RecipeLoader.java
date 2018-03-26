package com.inc.miki.bakingapp.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;


import com.inc.miki.bakingapp.data.Recipe;
import com.inc.miki.bakingapp.util.NetworkUtils;

import java.util.List;

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    private List<Recipe> recipes;

    public RecipeLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (recipes != null) {
            deliverResult(recipes);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Recipe> loadInBackground() {
        return NetworkUtils.fetchRecipes(getContext());
    }

    @Override
    public void deliverResult(List<Recipe> recipes) {
        this.recipes = recipes;
        super.deliverResult(recipes);
    }
}
