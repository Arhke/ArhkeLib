package com.Arhke.WRCore.ItemSystem.FurnitureMapping;

import com.Arhke.WRCore.ItemSystem.Enumeration.MatchType;
import com.Arhke.WRCore.ItemSystem.Recipe.Recipe;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class Smeltery extends Furniture{
    public Smeltery(Main instance, Location loc) {
        super (instance, loc);
    }

    @Override
    void initializeFurniture() {
        _bt = new BukkitRunnable() {
            final Furnace furnace = ((Furnace)(getLocation().getBlock().getState()));
            @Override
            public void run() {
                furnace.setBurnTime((short)1200);
                furnace.update();
            }
        }.runTaskTimer(getPlugin(), 0,2400);
        asyncParse();
    }
    @Override
    public void addItem(ItemStack is){
        super.addItem(is);
        if(_asyncParse != null && !_asyncParse.isCancelled()){
            _asyncParse.cancel();
        }
        asyncParse();
    }
    @Override
    void cancelTasks() {
        _bt.cancel();
    }

    @Override
    void removeVisuals() {
        Furnace furnace = ((Furnace)(_loc.getBlock().getState()));
        furnace.setBurnTime((short)0);
        furnace.update();
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
                amountMatches = false;
                recipeMatch.clear();
                for(Recipe recipe: recipes){
//                    Bukkit.broadcastMessage(recipe.getId());
//                    for (ItemStack is: recipe.input) Bukkit.broadcastMessage(is.toString());
//                    Bukkit.broadcastMessage("OUTPUT");
//                    for (ItemStack is: recipe.output) Bukkit.broadcastMessage(is.toString());
                    int value = recipe.matchesMaterial(input);
                    if (value==1 && !amountMatches){
//                    Bukkit.broadcastMessage("1");
                        synchronized (recipeMatch){
                            recipeMatch.add(recipe);
                        }
                    }else if(value == 2){
//                        Bukkit.broadcastMessage("2");

                        if(!amountMatches){
                            amountMatches = true;
                            synchronized(recipeMatch) {
                                recipeMatch.clear();
                            }
                        }
                        synchronized(recipeMatch) {
                            recipeMatch.add(recipe);
                        }
                    }else {
//                        Bukkit.broadcastMessage("0");
                    }
                }
            }
        }.runTaskLaterAsynchronously(getPlugin(), 10L);
    }


    BukkitTask _bt;
    public static Set<Material> ValidMaterial = new HashSet<>();
    public static Set<Material> ExplodeMaterial = new HashSet<>();
}
