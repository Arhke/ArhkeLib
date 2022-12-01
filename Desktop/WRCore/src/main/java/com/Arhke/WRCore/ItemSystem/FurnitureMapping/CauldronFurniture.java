package com.Arhke.WRCore.ItemSystem.FurnitureMapping;

import com.Arhke.WRCore.ItemSystem.Recipe.Recipe;
import com.Arhke.WRCore.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class CauldronFurniture extends Furniture {
    public static Set<Material> ValidMaterial = new HashSet<>();
    public static Set<Material> ExplodeMaterial = new HashSet<>();

    public CauldronFurniture(Main instance, Location loc) {
        super(instance, loc);
    }

    @Override
    void initializeFurniture() {
        asyncParse();
    }
    public void asyncParse() {
        _asyncParse = new BukkitRunnable() {
            @Override
            public void run() {
                Set<ItemStack> input;
                Set<Recipe> recipes;
                synchronized(inputs) {
                    input = new HashSet<>(inputs);
                    recipes = getPlugin().getRecipeManager().getAll();
                }
                for(Recipe recipe: recipes){
                    int value = recipe.matchesMaterial(input);
                    if (value==1 && !amountMatches){
                        synchronized (recipeMatch){
                            recipeMatch.add(recipe);
                        }
                    }else if(value == 2){
                        if(!amountMatches){
                            amountMatches = true;
                            synchronized(recipeMatch) {
                                recipeMatch.clear();
                            }
                        }
                        synchronized(recipeMatch) {
                            recipeMatch.add(recipe);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(getPlugin());
    }


    @Override
    void cancelTasks() {

    }

    @Override
    void removeVisuals() {

    }
}
