package com.Arhke.WRCore.ItemSystem.utils;

import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Utility {






    public static List<PotionEffect> buildPotionEffectList(YamlConfiguration config) {
        List<PotionEffect> lpe = new ArrayList<>();
        for (String s : config.getConfigurationSection("effects").getKeys(false)) {
            PotionEffectType pet = PotionEffectType.getByName(config.getString("effects." + s + ".PotionEffectType"));
            if (pet == null) {
                continue;
            }
            int duration = config.getInt("effects." + s + ".Duration");
            int amplifier = config.getInt("effects." + s + ".Amplifier");
            boolean ambient = config.getBoolean("effects." + s + ".Ambient");
            boolean particles = config.getBoolean("effects." + s + ".Particles");

            if (!config.contains("effects." + s + ".Color")) {
                lpe.add(new PotionEffect(pet, duration, amplifier, ambient, particles));
                continue;
            }
            Color color;
            if (config.isInt("effects." + s + ".Color")) {
                color = Color.fromRGB(config.getInt("effects." + s + ".Color"));
            } else if (config.isColor("effects." + s + ".Color")) {
                color = config.getColor("effects." + s + ".Color");
            } else {
//                color = ColorChart.get(config.getString("effects." + s + ".Color"));
//                if (color == null) {
//                    lpe.add(new PotionEffect(pet, duration, amplifier, ambient, particles));
//                    continue;
//                }
            }
//            lpe.add(new PotionEffect(pet, duration, amplifier, ambient, particles, color));

        }
        return lpe;
    }



    public static int[] buildOutputChanceList(YamlConfiguration config) {
        List<Integer> chances = new ArrayList<>();
        for (String s : config.getConfigurationSection("Output").getKeys(false)) {
            int chance = config.getInt("Output." + s + ".Chance");
            if (chance == 0) {
                return null;
            }
            chances.add(chance);
        }
        int[] ret = new int[chances.size()];
        for (int i = 0; i < chances.size(); i++) {
            ret[i] = chances.get(i);
        }
        return ret;
    }


    public static double getPlayerSlotInteger(Player player, String key) {
        double ret = 0.0;
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length;i++) {
            ItemStack is = armor[i];
            if (is != null && is.getType() != Material.AIR) {
                NBTItem nbti = new NBTItem(is);
                if (nbti.hasKey(key)) {
                    ret += getCustomTagDouble(nbti, i, key);
                }
            }
        }
        ItemStack is = player.getInventory().getItemInOffHand();
        if (is != null && is.getType() != Material.AIR) {
            NBTItem nbti = new NBTItem(is);
            if (nbti.hasKey(key)) {
                ret += getCustomTagDouble(nbti, 4, key);
            }
        }
        is = player.getInventory().getItemInMainHand();
        if (is != null && is.getType() != Material.AIR) {
            NBTItem nbti = new NBTItem(is);
            if (nbti.hasKey(key)) {
                ret += getCustomTagDouble(nbti, 5, key);
            }
        }
        return ret;
    }
    public static double getCustomTagDouble(NBTItem nbti, int slotnum, String key){

        String value = nbti.getString(key);
        int start = value.indexOf(slotnum+":");
        if (start == -1){
            return 0;
        }
        int end = value.indexOf(",", start);
        start += (slotnum+":").length();
        if (end == -1){
            try {
                return Double.parseDouble(value.substring(start));
            }catch(NumberFormatException|IndexOutOfBoundsException e){
                return 0d;
            }
        }else{
            try {
                return Double.parseDouble(value.substring(start, end));
            }catch(NumberFormatException|IndexOutOfBoundsException e){
                return 0d;
            }
        }
    }


}





