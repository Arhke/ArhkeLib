package com.Arhke.ArhkeLib.Lib.Hook;


import org.bukkit.inventory.ItemStack;

public class NBTItem extends de.tr7zw.nbtapi.NBTItem {
    public NBTItem(ItemStack item, boolean directApply) {
        super(item, directApply);
    }


    public NBTItem(ItemStack item) {
        super(item);
    }
}
