package com.Arhke.ArhkeLib.Lib.CustomEvents;

import org.bukkit.inventory.ItemStack;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public enum ArmorType{
	HELMET(3), CHESTPLATE(2), LEGGINGS(1), BOOTS(0);

	private final int slot;

	ArmorType(int slot){
		this.slot = slot;
	}

	/**
	 * Attempts to match the ArmorType for the specified ItemStack.
	 *
	 * @param itemStack The ItemStack to parse the type of.
	 * @return The parsed ArmorType, or null if not found.
	 */
	public static ArmorType matchType(final ItemStack itemStack){
		if(CustomEventListener.isAirOrNull(itemStack)) return null;
		String type = itemStack.getType().name();
		if(type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD")) return HELMET;
		else if(type.endsWith("_CHESTPLATE")) return CHESTPLATE;
		else if(type.endsWith("_LEGGINGS")) return LEGGINGS;
		else if(type.endsWith("_BOOTS")) return BOOTS;
		else return null;
	}
	public static ArmorType matchSlot(int i){
		for(ArmorType at: ArmorType.values()){
			if(at.getSlot() == i) return at;
		}
		return null;
	}
	public int getSlot(){
		return slot;
	}
}