package com.inc.miki.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.inc.miki.bakingapp.R;
import com.inc.miki.bakingapp.data.Ingredient;
import com.inc.miki.bakingapp.data.Recipe;

import java.util.List;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<Ingredient> ingredients;

    ListRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.baking_preferences),
                0
        );

        String serializedRecipe = sharedPreferences.getString(
                context.getString(R.string.serialized_recipe),
                null);

        sharedPreferences.edit().clear().apply();
        if (TextUtils.isEmpty(serializedRecipe)) {
            ingredients = null;
            return;
        }

        Recipe recipe = Recipe.fromJson(serializedRecipe);
        ingredients = recipe.getIngredients();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredients == null) {
            return 0;
        }
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients == null || ingredients.size() == 0) {
            return null;
        }

        Ingredient ingredient = ingredients.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_ingredient_list_item);
        String quantityMeasurement = ingredient.getQuantity() + " " + ingredient.getMeasure().toLowerCase();
        views.setTextViewText(R.id.widget_text_view_ingredient_quantity_measurement, quantityMeasurement);
        views.setTextViewText(R.id.widget_text_view_ingredient_name, ingredient.getName());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
