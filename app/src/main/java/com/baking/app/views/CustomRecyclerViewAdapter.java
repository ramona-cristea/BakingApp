package com.baking.app.views;


import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baking.app.R;
import com.baking.app.StringUtils;
import com.baking.app.model.Ingredient;
import com.baking.app.model.ItemObject;
import com.baking.app.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_INGREDIENT = 1;
    private static final int TYPE_ITEM_STEP = 2;
    private List<ItemObject> itemObjects;
    private List<Step> steps;

    private int selectedStepPosition;

    private RecipeStepListener mListener;

    CustomRecyclerViewAdapter(List<ItemObject> itemObjects, RecipeStepListener listener) {
        this.itemObjects = itemObjects;
        this.mListener = listener;

        steps = new ArrayList<>();
        for(ItemObject itemObject : this.itemObjects) {
            if(itemObject.getContents() instanceof Step) {
                steps.add((Step) itemObject.getContents());
            }
        }
    }

    void setSelectedStepPosition(int index) {
        selectedStepPosition = index;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HEADER) {
            View layoutView = inflater.inflate(R.layout.header_item, parent, false);
            return new HeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM_INGREDIENT) {
            View layoutView = inflater.inflate(R.layout.ingredient_item, parent, false);
            return new IngredientViewHolder(layoutView);
        } else if(viewType == TYPE_ITEM_STEP) {
            View layoutView = inflater.inflate(R.layout.step_item, parent, false);
            return new StepViewHolder(layoutView);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemObject mObject = itemObjects.get(position);
        if(mObject.getContents() instanceof Ingredient) {
            ((IngredientViewHolder)holder).bind((Ingredient) mObject.getContents());
        }
        else if (mObject.getContents() instanceof Step) {
            ((StepViewHolder)holder).bind((Step) mObject.getContents(), position);
        } else {
            ((HeaderViewHolder)holder).bind((String) mObject.getContents());
        }
    }

    @Override
    public int getItemCount() {
        return itemObjects == null ? 0 : itemObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(itemObjects.get(position).getContents() instanceof String) {
            return TYPE_HEADER;
        }
        else if(itemObjects.get(position).getContents() instanceof Ingredient) {
            return TYPE_ITEM_INGREDIENT;
        }
        else return TYPE_ITEM_STEP;
    }

    public void setData(ArrayList<ItemObject> objects) {
        itemObjects = objects;
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_ingredient_name)
        TextView ingredientName;

        @BindView(R.id.text_ingredient_quantity)
        TextView ingredientQuantity;

        private Ingredient mIngredient;

        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(@NonNull Ingredient ingredient) {
            mIngredient = ingredient;
            ingredientName.setText(StringUtils.upperCaseFirst(mIngredient.ingredient));
            ingredientQuantity.setText(String.valueOf(mIngredient.quantity) + " " + mIngredient.measure);
        }

    }

    public interface RecipeStepListener {
        void onStepSelected(int stepIndex);
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.header_title)
        TextView headerTitle;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(@NonNull String title) {
            headerTitle.setText(title);
        }

    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.button_step_video)
        ImageButton stepPlayVideo;

        @BindView(R.id.text_step_short_desc)
        TextView stepShortDesc;

        @BindView(R.id.layout_recipe_step)
        RelativeLayout layoutStep;

        Step mStep;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutStep.setOnClickListener(this);
            stepPlayVideo.setOnClickListener(this);
        }

        void bind(@NonNull Step recipeStep, int position) {
            mStep = recipeStep;
            int indexStep = 0;
            for(Step step : steps) {
                if(step.shortDescription.equals(recipeStep.shortDescription)) {
                    indexStep = steps.indexOf(step);
                    break;
                }
            }
            stepShortDesc.setText(indexStep + ". " + recipeStep.shortDescription);
            if(TextUtils.isEmpty(recipeStep.videoURL) && TextUtils.isEmpty(recipeStep.thumbnailURL)) {
                stepPlayVideo.setVisibility(View.GONE);
            } else {
                stepPlayVideo.setVisibility(View.VISIBLE);
            }

            if(position == selectedStepPosition) {
                layoutStep.setSelected(true);
                stepShortDesc.setTextColor(stepShortDesc.getResources().getColor(R.color.white));
                stepPlayVideo.setColorFilter(ContextCompat.getColor(stepPlayVideo.getContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                layoutStep.setSelected(false);
                stepShortDesc.setTextColor(stepShortDesc.getResources().getColor(R.color.colorPrimary));
                stepPlayVideo.setColorFilter(ContextCompat.getColor(stepPlayVideo.getContext(), R.color.colorPrimaryLight), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.layout_recipe_step || v.getId() == R.id.button_step_video) {
                mListener.onStepSelected(steps.indexOf(mStep));
            }
        }
    }
}

