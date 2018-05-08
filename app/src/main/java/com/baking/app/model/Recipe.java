package com.baking.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe implements Parcelable {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("ingredients")
    public List<Ingredient> ingredients = null;

    @SerializedName("steps")
    public List<Step> steps = null;

    @SerializedName("servings")
    public int servings;

    @SerializedName("image")
    public String image;

    public final static Parcelable.Creator<Recipe> CREATOR = new Creator<Recipe>() {

        @SuppressWarnings({"unchecked"})
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return (new Recipe[size]);
        }
    };

    protected Recipe(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.ingredients = in.readArrayList(Ingredient.class.getClassLoader());
        this.steps = in.readArrayList(Step.class.getClassLoader());
        this.servings = ((int) in.readValue((int.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Recipe() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeValue(servings);
        dest.writeValue(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
