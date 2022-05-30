package com.Arhke.ArhkeLib.Lib.Utils;

import com.Arhke.ArhkeLib.ArhkeLib;
import com.Arhke.ArhkeLib.Lib.Utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

import static com.Arhke.ArhkeLib.Lib.Base.Base.except;

public class RecipeUtil {
    public static ArhkeLib instance;
    public static void registerRecipe(String name, ItemStack is, Material... materials){
        if(instance == null)
            except("[RecipeUtil.java] instance null");
        if(materials.length <=0 || materials.length > 9)
            except("[RecipeUtil.java] Not enough material");
        NamespacedKey key = new NamespacedKey(instance,name);
        ShapedRecipe recipe = new ShapedRecipe(key, is);
        StringBuilder sb = new StringBuilder();
        HashMap<Material, Character> mats = new HashMap<>();
        int charIndex = 65;
        for (Material m:materials) {
            if(m == null || m == Material.AIR || !m.isItem()) {
                sb.append(" ");
                continue;
            }
            Character c;
            if ((c = mats.get(m)) == null) {
                mats.put(m, c = MiscUtils.toChar(charIndex));
                charIndex++;
                recipe.setIngredient(c, m);
            }
            sb.append(c);
        }
        String shape = String.format("%9s",sb);
        recipe.shape(shape.substring(0,3),shape.substring(3,6), shape.substring(6));
        Bukkit.addRecipe(recipe);
    }
}
