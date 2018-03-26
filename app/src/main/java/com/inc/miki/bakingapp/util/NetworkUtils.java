package com.inc.miki.bakingapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;


import com.inc.miki.bakingapp.R;
import com.inc.miki.bakingapp.data.Ingredient;
import com.inc.miki.bakingapp.data.Recipe;
import com.inc.miki.bakingapp.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private NetworkUtils() {

    }

    public static List<Recipe> fetchRecipes(Context context) {
        URL url = getUrl(context);
        String jsonResponse = getResponse(context, url);
        return extractRecipes(context, jsonResponse);
    }

    private static URL getUrl(Context context) {
        URL url = null;
        try {
            url = new URL(context.getString(R.string.request_url));
        } catch (MalformedURLException e) {
            Log.e(TAG, context.getString(R.string.error_creating_url), e);
        }
        return url;
    }

    private static String getResponse(Context context, URL url) {
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(context.getString(R.string.get));
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFrom(inputStream);
            } else {
                Log.e(TAG, context.getString(R.string.error_retrieving_data_response_code) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, context.getString(R.string.error_retrieving_data), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, context.getString(R.string.error_closing_input_stream), e);
                }
            }
        }
        return jsonResponse;
    }

    private static String readFrom(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream == null) {
            return null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    private static List<Recipe> extractRecipes(Context context, String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Recipe> recipes = new ArrayList<>();
        try {
            JSONArray baseJsonResponse = new JSONArray(jsonResponse);
            for (int i = 0; i < baseJsonResponse.length(); i++) {
                JSONObject recipeJson = baseJsonResponse.getJSONObject(i);
                Recipe recipe = getRecipe(context, recipeJson);
                recipes.add(recipe);
            }

        } catch (JSONException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_response), e);
        }

        return recipes;
    }

    private static Recipe getRecipe(Context context, JSONObject recipeJson) throws JSONException {
        String name = recipeJson.getString(context.getString(R.string.name));
        List<Ingredient> ingredients = getIngredients(context, recipeJson);
        List<Step> steps = getSteps(context, recipeJson);
        int servings = recipeJson.getInt(context.getString(R.string.servings));
        String imageUrl = recipeJson.getString(context.getString(R.string.image));

        return new Recipe(name, ingredients, steps, servings, imageUrl);
    }

    private static List<Ingredient> getIngredients(Context context, JSONObject recipeJson) throws JSONException {
        JSONArray ingredientsJsonArray = recipeJson.getJSONArray(context.getString(R.string.ingredients));
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsJsonArray.length(); i++) {
            JSONObject ingredientJson = ingredientsJsonArray.getJSONObject(i);
            String name = ingredientJson.getString(context.getString(R.string.ingredient));
            String measure = ingredientJson.getString(context.getString(R.string.measure));
            String quantity = ingredientJson.getString(context.getString(R.string.quantity));

            Ingredient ingredient = new Ingredient(name, measure, quantity);
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private static List<Step> getSteps(Context context, JSONObject recipeJson) throws JSONException {
        JSONArray stepsJsonArray = recipeJson.getJSONArray(context.getString(R.string.steps));
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < stepsJsonArray.length(); i++) {
            JSONObject stepJson = stepsJsonArray.getJSONObject(i);
            String shortDescription = stepJson.getString(context.getString(R.string.short_description));
            String description = stepJson.getString(context.getString(R.string.description));
            String videoUrl = stepJson.getString(context.getString(R.string.video_url));
            String thumbnailUrl = stepJson.getString(context.getString(R.string.thumbnail_url));

            Step step = new Step(shortDescription, description, videoUrl, thumbnailUrl);
            steps.add(step);
        }
        return steps;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
