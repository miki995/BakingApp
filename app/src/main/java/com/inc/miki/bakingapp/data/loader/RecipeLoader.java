package com.inc.miki.bakingapp.data.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.inc.miki.bakingapp.data.Recipe;
import com.inc.miki.bakingapp.data.utils.NetworkUtils;

import java.util.List;

/**
 * Created by miroslav on 3/20/18.
 */

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
