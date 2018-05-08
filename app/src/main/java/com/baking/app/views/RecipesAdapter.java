package com.baking.app.views;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baking.app.R;
import com.baking.app.model.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private List<Recipe> mRecipes;
    private final RecipeAdapterHandler mRecipeClickListener;

    RecipesAdapter(List<Recipe> recipes, RecipeAdapterHandler listener) {
        mRecipes = recipes;
        mRecipeClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recipe_item_layout, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        final Recipe recipe = mRecipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return mRecipes == null ? 0 : mRecipes.size();
    }

    public void setData(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public interface RecipeAdapterHandler {
        void onItemClicked(Recipe recipe);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.thumbnail_recipe)
        ImageView recipeThumbnail;

        @BindView(R.id.title_recipe)
        TextView recipeTitle;

        @BindView(R.id.cardview_recipe)
        CardView layoutRecipeItem;

        private Recipe mRecipe;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layoutRecipeItem.setOnClickListener(this);
        }

        void bind(@NonNull Recipe recipe) {
            mRecipe = recipe;
            recipeTitle.setText(recipe.name);
            if(mRecipe.steps != null && mRecipe.steps.size() > 0) {
                long interval = 5 * 1000;
                RequestOptions options = new RequestOptions().frame(interval).placeholder(R.drawable.vector_cupcake);
                Glide.with(recipeThumbnail.getContext()).asBitmap()
                        .load(Uri.parse(mRecipe.steps.get(mRecipe.steps.size() - 1).videoURL))
                        .apply(options)
                        .into(recipeThumbnail);
            }
        }

        @Override
        public void onClick(View view) {
            mRecipeClickListener.onItemClicked(mRecipe);
        }
    }

}
