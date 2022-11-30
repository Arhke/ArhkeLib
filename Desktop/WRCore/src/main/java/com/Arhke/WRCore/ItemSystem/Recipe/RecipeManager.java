package com.Arhke.WRCore.ItemSystem.Recipe;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;

import java.util.HashSet;
import java.util.Set;

public class RecipeManager extends MainBase {
    DirectoryManager dm;
    public Set<Recipe> recipes = new HashSet<>();
    public RecipeManager(Main Instance, DirectoryManager dm) {
        super(Instance);
        this.dm = dm;
        for (FileManager fm:dm.getFMList()){
            try {
                recipes.add(new Recipe(getPlugin(), fm));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public Set<Recipe> getAll() {
        return recipes;
    }
    public Recipe find(String id) {
        for(Recipe recipe: recipes) {
            if(recipe.getId().equals(id))
                return recipe;
        }
        return null;
    }
}
