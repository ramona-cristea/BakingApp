package com.baking.app.data;

import android.arch.lifecycle.LiveData;

import com.baking.app.model.Recipe;

import java.util.ArrayList;

public interface RecipesRepository {

    LiveData<ArrayList<Recipe>> getRecipes();

}
