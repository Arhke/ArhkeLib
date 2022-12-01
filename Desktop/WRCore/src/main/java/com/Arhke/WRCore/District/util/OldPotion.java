package com.Arhke.WRCore.District.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.Arhke.WRCore.District.core.Feudal;

/**
 * 1.8 potion stuff
 * @author Michael Forseth
 *
 */
@SuppressWarnings("deprecation")
public class OldPotion {
	/**
	 * Add ingredient to a potion for 1.8 potions
	 * @param potionItem
	 * @param ingr
	 * @return
	 */
	public static Potion addIngredient(ItemStack potionItem, ItemStack ingr) {
		if(potionItem.getType().equals(Material.POTION)){
			short dur = potionItem.getDurability();
			if(ingr.getType().equals(Material.FERMENTED_SPIDER_EYE) && dur < 2){
				return new Potion(PotionType.WEAKNESS);
			}else if(dur == 16){
				if(ingr.getType().equals(Material.GOLDEN_CARROT)){
					return new Potion(PotionType.NIGHT_VISION);
				}else if(/*!Feudal.getVersion().equals("1.7") && */ingr.getType().equals(Material.getMaterial("RABBIT_FOOT"))){
					return new Potion(PotionType.valueOf("JUMP"));
				}else if(ingr.getType().equals(Material.MAGMA_CREAM)){
					return new Potion(PotionType.FIRE_RESISTANCE);
				}else if(ingr.getType().equals(Material.SUGAR)){
					return new Potion(PotionType.SPEED);
				}else if(ingr.getType().equals(Material.RAW_FISH) && ingr.getDurability() == 3){
					return new Potion(PotionType.WATER_BREATHING);
				}else if(ingr.getType().equals(Material.SPECKLED_MELON)){
					return new Potion(PotionType.INSTANT_HEAL);
				}else if(ingr.getType().equals(Material.SPIDER_EYE)){
					return new Potion(PotionType.POISON);
				}else if(ingr.getType().equals(Material.GHAST_TEAR)){
					return new Potion(PotionType.REGEN);
				}else if(ingr.getType().equals(Material.BLAZE_POWDER)){
					return new Potion(PotionType.STRENGTH);
				}else{
					return null;
				}
			}else{
				Potion potion = Potion.fromItemStack(potionItem);
				if(potion != null && potion.getType() != null && !potion.getType().equals(PotionType.WATER)){
					if(ingr.getType().equals(Material.SULPHUR)){
						potion.setSplash(true);
					}else if(ingr.getType().equals(Material.GLOWSTONE_DUST)){
						if(!potion.hasExtendedDuration()){
							potion.setLevel(2);
						}
					}else if(ingr.getType().equals(Material.REDSTONE)){
						if(potion.getLevel() == 1){
							potion.setHasExtendedDuration(true);
						}
					}else if(ingr.getType().equals(Material.FERMENTED_SPIDER_EYE) && !potion.isSplash()){
						if(potion.getType().equals(PotionType.NIGHT_VISION)){
							potion.setType(PotionType.INVISIBILITY);
						}else if(potion.getType().equals(PotionType.INSTANT_HEAL)){
							potion.setType(PotionType.INSTANT_DAMAGE);
						}else if(potion.getType().equals(PotionType.POISON)){
							potion.setType(PotionType.INSTANT_DAMAGE);
						}else if(/*!Feudal.getVersion().equals("1.7") && */potion.getType().equals(PotionType.valueOf("JUMP")) && potion.getLevel() == 1 && !potion.hasExtendedDuration()){
							potion.setType(PotionType.SLOWNESS);
						}else if(potion.getType().equals(PotionType.FIRE_RESISTANCE)){
							potion.setType(PotionType.SLOWNESS);
						}else if(potion.getType().equals(PotionType.SPEED)){
							potion.setType(PotionType.SLOWNESS);
						}
					}
					return potion;
				}else{
					return null;
				}
			}
		}
		return null;
	}
}
