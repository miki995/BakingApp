package com.inc.miki.bakingapp.data.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inc.miki.bakingapp.R;
import com.inc.miki.bakingapp.data.Recipe;
import com.inc.miki.bakingapp.util.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private RecipeOnClickHandler clickHandler;

    public RecipeAdapter(Context context, RecipeOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_recipe_list_item,
                parent,
                false
        );
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        if (recipes == null) {
            return;
        }
        Recipe recipe = recipes.get(position);
        holder.textViewRecipeName.setText(recipe.getName());
        holder.textViewServes.setText(context.getString(
                R.string.serves,
                String.valueOf(recipe.getServings())
        ));

        if (!TextUtils.isEmpty(recipe.getImageUrl())) {
            Uri uri = Uri.parse(recipe.getImageUrl());
            Picasso.with(context).load(uri).into(holder.imageViewRecipeItem);
        } else {
            int drawableId = ImageUtils.getImageResourceId(context, recipe.getName());
            Picasso.with(context).load(drawableId).into(holder.imageViewRecipeItem);
        }
    }

    @Override
    public int getItemCount() {
        if (recipes == null) {
            return 0;
        }
        return recipes.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void clear() {
        if (recipes == null) {
            return;
        }
        recipes.clear();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view_recipe_name)
        TextView textViewRecipeName;

        @BindView(R.id.text_view_serves)
        TextView textViewServes;

        @BindView(R.id.image_view_recipe_item)
        ImageView imageViewRecipeItem;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            clickHandler.onClick(recipes.get(index));
        }
    }

    public interface RecipeOnClickHandler {
        void onClick(Recipe recipe);
    }
}
