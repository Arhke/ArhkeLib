package com.Arhke.WRCore.District.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Used to support methods which work above 1.7
 * 
 * @author Michael Forseth
 * @version Dec 12, 2017
 *
 */
public class UtilsAbove1_7 {

	public static void pistonRetract(BlockPistonRetractEvent event) {
		try{
//			if(ChunkDataManagement.blockIsOnLand(event.getBlock().getLocation())){
//				return;
//			}
//			for(Block b : event.getBlocks()){
//				if(ChunkDataManagement.piston(b.getLocation())){
//					event.setCancelled(true);
//					return;
//				}
//			}
		}catch(Exception e){
			ErrorManager.error(44, e);
		}
	}

	public static double getHealth(LivingEntity entity) {
		return entity.getHealth();
	}

	public static double getMaxHealth(LivingEntity entity) {
		return entity.getMaxHealth();
	}

	public static PotionEffect getHiddenPotionEffect(PotionEffectType type, int duration, int level) {
		return new PotionEffect(type, duration, level, true, false);
	}

	public static int countMatches(String shape, String string) {
		return StringUtils.countMatches(shape, string);
	}

	public static void setItemProtector(Item item, String string) {
		item.setCustomName(string);
		item.setCustomNameVisible(true);
	}

	public static boolean isArmorStand(Entity entity) {
		return (entity instanceof ArmorStand);
	}



	public static void keepInventory(PlayerDeathEvent event) {
		event.setKeepInventory(true);
	}

}
