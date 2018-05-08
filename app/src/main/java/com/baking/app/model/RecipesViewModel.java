package com.baking.app.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.baking.app.data.RecipesRepositoryImpl;

import java.util.ArrayList;

public class RecipesViewModel extends ViewModel {

    private MediatorLiveData<ArrayList<Recipe>> mRecipesLiveData;
    private RecipesRepositoryImpl mRecipesRepository;

    public RecipesViewModel() {
        mRecipesLiveData = new MediatorLiveData<>();
        mRecipesRepository = new RecipesRepositoryImpl();
    }

    @NonNull
    public LiveData<ArrayList<Recipe>> getRecipes() {
        return mRecipesLiveData;
    }

    public void loadRecipes() {
        mRecipesLiveData.addSource(
                mRecipesRepository.getRecipes(),
                recipes -> mRecipesLiveData.setValue(recipes)
        );
    }
}
