package com.Arhke.WRCore.District.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * 
 * @author Michael Forseth
 * This class only operates with 1.9 Spigot
 *
 */
public class Utils1_9 {

	/**
	 * Get the location of an inventory. Can be null
	 * @param inventory
	 * @return
	 */
	public Location getLocation(Inventory inventory) {
		return inventory.getLocation();
	}

	/**
	 * Set a player's item in hand.
	 * @param inventory
	 * @param item
	 */
	public static void setItemInHand(PlayerInventory inventory, ItemStack item) {
		inventory.setItemInMainHand(item);
	}

	/**
	 * Get a player's item in hand.
	 * @param inventory
	 * @return
	 */
	public static ItemStack getItemInHand(PlayerInventory inventory) {
		return inventory.getItemInMainHand();
	}

	public static ItemStack[] getInventoryContents(PlayerInventory inventory) {
		return inventory.getStorageContents();
	}

	@SuppressWarnings("deprecation")
	public static void sendTitle(Player player, String string, String subTitle) {
		player.sendTitle(string, subTitle);
	}

}
