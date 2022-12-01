package com.Arhke.WRCore.ItemSystem.FurnitureMapping;

import com.Arhke.WRCore.ItemSystem.Recipe.Recipe;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class Furniture extends MainBase {

    Location _loc;
    volatile long startTime = 0L;
    volatile Set<ItemStack> inputs = new HashSet<>();
    volatile Set<Recipe> recipeMatch = new HashSet<>();
    volatile boolean amountMatches = false;
    BukkitTask _asyncParse;

    Furniture(Main instance, Location loc) {
        super(instance);
        _loc = loc;
        startTime = System.currentTimeMillis();
    }

    public Location getLocation(){
        return _loc;
    }
    public long getStartTime(){
        return startTime;
    }
    public double getTimePassed() {
        return (System.currentTimeMillis()- startTime)/1000d;
    }
    public Collection<Recipe> getRecipe(){
        return recipeMatch;
    }
    public boolean amountMatches(){
        return amountMatches;
    }
    public void setAmountMatches(boolean matches){
        amountMatches = matches;
    }
    public void addItem(ItemStack is){
        if (inputs.isEmpty()){
            initializeFurniture();
        }
        for (ItemStack item: inputs){
            if(is.isSimilar(item)) {
                item.setAmount(item.getAmount() + is.getAmount());
                return;
            }
        }
        inputs.add(is);
    }

    public void remove(){
        cancelTasks();
        removeVisuals();
    }
    abstract void initializeFurniture();
    abstract void cancelTasks();
    abstract void removeVisuals();
    @Override
    public String toString() {
        return "FurnitureType: " + this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Furniture && ((Furniture)o).getLocation().equals(this.getLocation());
    }








}
