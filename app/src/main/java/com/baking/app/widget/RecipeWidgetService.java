package com.baking.app.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.baking.app.model.Recipe;
import com.google.gson.Gson;

public class RecipeWidgetService extends IntentService {

    private static final String ACTION_UPATE_RECIPE_WIDGET = "com.baking.app.widget.action.UPATE_RECIPE_WIDGET";

    public RecipeWidgetService() {
        super("RecipeWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPATE_RECIPE_WIDGET.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String recipeJson = preferences.getString("recipe_widget", "");
                if(!TextUtils.isEmpty(recipeJson)) {
                    Recipe recipe = new Gson().fromJson(recipeJson, Recipe.class);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
                    RecipeWidgetProvider.updateBakingAppWidgets(this, appWidgetManager, appWidgetIds, recipe);
                }
            }
        }
    }

    public static void startActionUpdateRecipeWidget(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(ACTION_UPATE_RECIPE_WIDGET);
        context.startService(intent);
    }
}
