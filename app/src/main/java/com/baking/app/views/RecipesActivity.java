package com.baking.app.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baking.app.R;
import com.baking.app.model.Recipe;
import com.baking.app.model.RecipesViewModel;
import com.baking.app.util.EspressoIdlingResource;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesActivity extends AppCompatActivity implements RecipesAdapter.RecipeAdapterHandler {

    RecipesViewModel mRecipesViewModel;

    @BindView(R.id.recycler_recipes)
    RecyclerView recyclerViewRecipes;

    @BindView(R.id.progress_bar_recipes)
    ProgressBar mLoadingIndicator;

    RecipesAdapter mRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        ButterKnife.bind(this);

        int columns = getResources().getInteger(R.integer.columns);
        recyclerViewRecipes.setLayoutManager(new GridLayoutManager(this, columns));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        recyclerViewRecipes.addItemDecoration(new GridSpacingItemDecoration(columns, spacingInPixels, true, 0));
        recyclerViewRecipes.setItemAnimator(new DefaultItemAnimator());
        mRecipesAdapter = new RecipesAdapter(null, this);
        recyclerViewRecipes.setAdapter(mRecipesAdapter);

        mRecipesViewModel = ViewModelProviders.of(this).get(RecipesViewModel.class);
        // Handle changes emitted by LiveData
        mRecipesViewModel.getRecipes().observe(this, this::handleResponse);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoadingIndicator.setVisibility(View.VISIBLE);
        EspressoIdlingResource.increment();
        mRecipesViewModel.loadRecipes();
    }

    private void handleResponse(ArrayList<Recipe> recipes) {
        mLoadingIndicator.setVisibility(View.GONE);
        if(recipes == null) {
            Toast.makeText(this, getString(R.string.load_recipes_error), Toast.LENGTH_SHORT).show();
            return;
        }
        mRecipesAdapter.setData(recipes);
        EspressoIdlingResource.decrement();
    }

    @Override
    public void onItemClicked(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
