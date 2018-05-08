package com.baking.app.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baking.app.R;
import com.baking.app.model.Ingredient;
import com.baking.app.model.ItemObject;
import com.baking.app.model.Recipe;
import com.baking.app.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailsFragment extends Fragment {

    private Recipe mRecipe;
    private CustomRecyclerViewAdapter mAdapter;

    @BindView(R.id.recycler_view_recipe_info)
    RecyclerView mRecyclerViewRecipeInfo;


    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mRecipe != null) {
            updateUi();
        }
    }

    public void setRecipeInfo(Recipe recipe) {
        mRecipe = recipe;
    }

    private void updateUi(){
        ArrayList<ItemObject> items = new ArrayList<>();
        items.add(new ItemObject(getString(R.string.ingredients)));
        if(mRecipe.ingredients != null) {
            for(Ingredient ingredient : mRecipe.ingredients) {
                items.add(new ItemObject(ingredient));
            }
        }

        items.add(new ItemObject(getString(R.string.steps)));
        if(mRecipe.steps != null) {
            for(Step step : mRecipe.steps) {
                items.add(new ItemObject(step));
            }
        }

        mAdapter = new CustomRecyclerViewAdapter(items, (RecipeDetailActivity) getActivity());
        mRecyclerViewRecipeInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewRecipeInfo.setAdapter(mAdapter);
    }

    public void markSelectedStep(int stepIndex){
        if(mAdapter != null) {
            mAdapter.setSelectedStepPosition(mRecipe.ingredients.size() + 2 + stepIndex);
            mAdapter.notifyDataSetChanged();
        }
    }
}
