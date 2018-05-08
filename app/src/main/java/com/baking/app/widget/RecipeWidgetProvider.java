package com.baking.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.text.Html;
import android.widget.RemoteViews;

import com.baking.app.R;
import com.baking.app.model.Ingredient;
import com.baking.app.model.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        if(recipe != null) {
            views.setTextViewText(R.id.text_recipe_title, recipe.name);
            StringBuilder ingredients = new StringBuilder("<html>");

            if (recipe.ingredients != null && recipe.ingredients.size() > 0) {
                ingredients.append("<ol>");
                for(Ingredient ingredient : recipe.ingredients) {
                    ingredients.append("<li>")
                            .append(ingredient.ingredient)
                            .append(" - ")
                            .append(ingredient.quantity)
                            .append(" ")
                            .append(ingredient.measure)
                            .append("</li>");
                }
                ingredients.append("</ol>");
            }
            ingredients.append("</html>");

            views.setTextViewText(R.id.text_recipe_ingredients, Html.fromHtml(ingredients.toString()));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RecipeWidgetService.startActionUpdateRecipeWidget(context);
    }

    public static void updateBakingAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                              int[] appWidgetIds, Recipe recipe) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

