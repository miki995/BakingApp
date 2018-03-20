package com.inc.miki.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;


/**
 * Created by miroslav on 3/20/18.
 */

public class Recipe implements Parcelable {
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String imageUrl;

    public Recipe(String name, List<Ingredient> ingredients, List<Step> steps, int servings, String imageUrl) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }

    private Recipe(Parcel in) {
        name = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
        servings = in.readInt();
        imageUrl = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.readTypedList(ingredients);
        parcel.writeTypedList(steps);
        parcel.writeInt(servings);
        parcel.writeString(imageUrl);
    }
    public String serialize(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static Recipe fromJson(String serializedRecipe){
        Gson gson = new Gson();
        return gson.fromJson(serializedRecipe, Recipe.class);
    }
}
