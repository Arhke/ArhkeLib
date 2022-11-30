package com.Arhke.WRCore.ItemSystem.Enumeration;

import com.Arhke.WRCore.ItemSystem.FurnitureMapping.Furniture;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.Smeltery;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.Tannery;
import com.Arhke.WRCore.Lib.Base.Base;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public enum MatchType {
    //stroke of luck
    FAILED("FAILED"){{
        _is.put(Smeltery.class, Base.setDisplayName(new ItemStack(Material.GOLD_NUGGET, 1), ChatColor.RED + "Scrap Metal"));
        _is.put(Tannery.class, Base.setDisplayName(new ItemStack(Material.LEATHER, 1), ChatColor.RED + "Scrap Hide"));
    }},
    //CorrectMaterialsAndTime
    MATERIALTIME("BRITTLE"){{
        _is.put(Smeltery.class, Base.setDisplayName(new ItemStack(Material.GOLD_NUGGET, 1), ChatColor.YELLOW+"Brittle Ingot"));
        _is.put(Tannery.class, Base.setDisplayName(new ItemStack(Material.LEATHER, 1), ChatColor.YELLOW + "Brittle Hide"));

    }},
    //CorrectMaterialsAndAmounts
    MATERIALAMOUNT("IMPERFECT"){{
        _is.put(Smeltery.class, Base.setDisplayName(new ItemStack(Material.GOLD_NUGGET, 1), ChatColor.YELLOW+"Imperfect Ingot"));
        _is.put(Tannery.class, Base.setDisplayName(new ItemStack(Material.LEATHER, 1), ChatColor.YELLOW + "Imperfect Hide"));

    }},
    //CorrectMaterial
    MATERIAL("RUINED") {{
        _is.put(Smeltery.class, Base.setDisplayName(new ItemStack(Material.GOLD_NUGGET, 1), ChatColor.RED + "Ruined Ingot"));
        _is.put(Tannery.class, Base.setDisplayName(new ItemStack(Material.LEATHER, 1), ChatColor.RED + "Ruined Hide"));

    }},
    //NONE
    NONE("NONE");
    public  String _label;
    Map<Class<? extends Furniture>,ItemStack> _is = new HashMap<>();
    MatchType(String label ) {
        this._label = label;
    }
    public ItemStack getItem(Class<? extends Furniture> className) {
        ItemStack is = _is.get(className);
        return is == null?null:new ItemStack(is);
    }
}
