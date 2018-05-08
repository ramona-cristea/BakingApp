package com.baking.app.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.baking.app.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesRepositoryImpl implements RecipesRepository{

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    private BakingService mBakingService;

    public RecipesRepositoryImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mBakingService = retrofit.create(BakingService.class);
    }

    @Override
    public LiveData<ArrayList<Recipe>> getRecipes() {
        final MutableLiveData<ArrayList<Recipe>> recipesLiveData = new MutableLiveData<>();
        Call<ArrayList<Recipe>> request = mBakingService.getRecipes();
        request.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {
                recipesLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable error) {
                recipesLiveData.setValue(null);
            }
        });

        return recipesLiveData;
    }
}
