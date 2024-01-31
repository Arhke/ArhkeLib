package com.Arhke.ArhkeLib.ItemUtil;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemUtil {
    public static void addEnchant(ItemStack item, Enchantment enchantment, int level) {
        addEnchant(item, enchantment, level, false);
    }
    public static void addEnchant(ItemStack item, Enchantment enchantment, int level, boolean hide) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, level, false);
        if(hide) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
    }
    public static void setColor(ItemStack is, Color c){
        LeatherArmorMeta meta2 = (LeatherArmorMeta) is.getItemMeta();
        meta2.setColor(c);
        is.setItemMeta(meta2);
    }
}
