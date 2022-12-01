package com.Arhke.WRCore.ItemSystem.FurnitureMapping;

import com.Arhke.WRCore.ItemSystem.Enumeration.MatchType;
import com.Arhke.WRCore.ItemSystem.Recipe.Recipe;
import com.Arhke.WRCore.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tannery extends Furniture{
    public static Set<Material> ValidMaterial = new HashSet<>();
    public static Set<Material> ExplodeMaterial = new HashSet<>();
    public Tannery(Main instance, Location loc) {
        super(instance, loc);
    }
    public void addPossibleRecipes(String recipes){
        _asyncParse = new BukkitRunnable() {
            final String recipeString = recipes;
            @Override
            public void run() {
                String[] recipeArray;
                synchronized(recipeString) {
                    recipeArray = recipes.split(" ");
                }
                Set<Recipe> recipes;
                synchronized(getPlugin().getRecipeManager()) {
                    recipes = getPlugin().getRecipeManager().getAll();
                }
                Set<Recipe> validRecipes = new HashSet<>();
                for (String recipe: recipeArray){
                    recipes.stream().filter((e)-> e.getId().equals(recipe)).findFirst().ifPresent(validRecipes::add);
                }
                synchronized(recipeMatch){
                    recipeMatch.addAll(validRecipes);
                }

            }
        }.runTaskAsynchronously(getPlugin());

    }
    public ItemStack getOutputItem() {
        if(_asyncParse != null && !_asyncParse.isCancelled()){
            _asyncParse.cancel();
        }

        if(recipeMatch.size() == 0){
            return null;
        }
        Recipe outputRecipe = null;
        for(Recipe recipe: recipeMatch){
            if(recipe.matchesTime(startTime)){
                outputRecipe = recipe;
                break;
            }
        }
        if (amountMatches){
            if (outputRecipe == null){
                return MatchType.MATERIALAMOUNT.getItem(getClass());
            }else if(outputRecipe.isFormed()){
                return outputRecipe.getOutputItem();
            } else {
                return MatchType.FAILED.getItem(getClass());
            }
        }else if (outputRecipe != null) {
            return MatchType.MATERIALTIME.getItem(getClass());
        }else {
            return MatchType.MATERIAL.getItem(getClass());
        }
    }

    @Override
    void initializeFurniture() {

    }

    @Override
    void cancelTasks() {

    }

    @Override
    void removeVisuals() {

    }
}
