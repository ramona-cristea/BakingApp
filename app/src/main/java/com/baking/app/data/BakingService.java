package com.baking.app.data;

import com.baking.app.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
