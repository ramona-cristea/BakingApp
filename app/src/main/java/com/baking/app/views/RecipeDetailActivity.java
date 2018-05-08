package com.baking.app.views;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baking.app.R;
import com.baking.app.widget.RecipeWidgetProvider;
import com.baking.app.model.Recipe;
import com.google.gson.Gson;

public class RecipeDetailActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.RecipeStepListener,
        StepDetailFragment.StepNavigationListener{

    Recipe mRecipe;
    private int mSelectedStepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);


        if(getIntent() != null && getIntent().hasExtra("recipe")) {
            mRecipe = getIntent().getParcelableExtra("recipe");

            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeDetailsFragment recipeDetailsFragment = (RecipeDetailsFragment) fragmentManager.findFragmentByTag("recipe_info_tag");

            // create the fragment and data the first time

            if(null == recipeDetailsFragment) {
                recipeDetailsFragment = new RecipeDetailsFragment();
                recipeDetailsFragment.setRecipeInfo(mRecipe);

                fragmentManager.beginTransaction()
                        .replace(R.id.container_recipe, recipeDetailsFragment, "recipe_info_tag")
                        .addToBackStack("recipe_info")
                        .commit();
            }

            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(mRecipe.name);
            }
        }
        else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(isTablet && mSelectedStepIndex > 0) {
            navigateToStep(mSelectedStepIndex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                if(isTablet) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                else {
                    showSystemUI();
                    if(getSupportFragmentManager().getBackStackEntryCount() > 1) {
                        getSupportFragmentManager().popBackStackImmediate();
                    } else {
                        NavUtils.navigateUpFromSameTask(this);
                    }
                }
                return true;

            case R.id.action_add_to_widget:
                addRecipeToWidget();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRecipeToWidget() {
        if(mRecipe != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("recipe_widget", new Gson().toJson(mRecipe));
            editor.apply();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
            //Now update all widgets
            RecipeWidgetProvider.updateBakingAppWidgets(this, appWidgetManager, appWidgetIds, mRecipe);

            Toast.makeText(this, getString(R.string.recipe_add_to_widget), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStepSelected(int stepIndex) {
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setRecipeSteps(mRecipe.steps);
        stepDetailFragment.setStepIndex(stepIndex);
        stepDetailFragment.setNavigationListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        navigateToStep(stepIndex);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        fragmentManager.beginTransaction()
                .replace(isTablet ? R.id.container_step : R.id.container_recipe, stepDetailFragment, "recipe_step_tag")
                .addToBackStack("recipe_step")
                .commit();
    }

    @Override
    public void navigateToStep(int index) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(isTablet) {
            mSelectedStepIndex = index;
            RecipeDetailsFragment recipeDetailsFragment = (RecipeDetailsFragment) getSupportFragmentManager().findFragmentByTag("recipe_info_tag");
            if(recipeDetailsFragment != null) {
                recipeDetailsFragment.markSelectedStep(index);
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(isTablet) {
            finish();
        }
        else {
            showSystemUI();
            if(getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStackImmediate();
            } else {
                finish();
            }
        }
    }

    private void showSystemUI() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(!isTablet) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_details_menu, menu);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected_step_index", mSelectedStepIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedStepIndex = savedInstanceState.getInt("selected_step_index");
    }
}
