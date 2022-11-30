package com.Arhke.WRCore.Lib.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Makes easy to use an inventory as a GUI by saving items with string.
 *
 * @author William Lin
 *
 */
public abstract class InventoryGui extends MainBase {

	private Inventory inventory;
	boolean canModify = false;
	List<Integer> canModifySlots = new ArrayList<>();
	private HashMap<Integer, Consumer> items = new HashMap<>();
	/**
	 * Creates now inventory gui.
	 *
	 * @param inventory put in the default template of the inventory
	 * sets the main instance of this plugin and stores a copy of the inventory parameter.
	 */
	public InventoryGui(Main instance, Inventory inventory, Object... args){
	    super(instance);
	    this.inventory = Bukkit.createInventory(null, inventory.getSize(), tcm(inventory.getName(), args));
	}
	public abstract void setItems(Player p);
	public abstract void onOpen(InventoryOpenEvent event);
	public abstract void onClose(InventoryCloseEvent event);


	//Assumes the player already has this inventory open.
	public void onPress(InventoryClickEvent event){
		if (modifiesGUI(event)){

			if(!(canModify && canModifySlots.contains(event.getRawSlot()))){
				event.setCancelled(true);
				int is = event.getRawSlot();
				Consumer cons = items.get(is);
				if (cons != null){
					cons.accept(event);
				}
			}
		}
	}
	public void openOtherGUI(InventoryGui gui){}
    /**
     * Gets the bukkit inventory.
     * @return
     */
    public Inventory getInventory(){
        return inventory;
    }
	/**
	 * sets an item in the items hashmap and inventory
	 * @param index
	 * @param item
	 */
	public void setItem(int index, ItemStack item, Consumer consumer){
		inventory.setItem(index, item);
		items.put(index, consumer);
	}


	/**
	 * Clears items hashmap.
	 */
	public void clear(){
		items.clear();
	}

	public static String lim(String message) {//Limit to 31 characters max
		if(message.length() > 31){
			return message.substring(0, 31);
		}else{
			return message;
		}
	}
	//===========<Helper>===========
	public static boolean isPlace(InventoryAction ia){
		return ia == InventoryAction.PLACE_ALL ||
				ia == InventoryAction.PLACE_ONE ||
				ia == InventoryAction.PLACE_SOME ||
				ia == InventoryAction.SWAP_WITH_CURSOR;
	}
	public static boolean isPickUp(InventoryAction ia){
		return ia == InventoryAction.PICKUP_ALL ||
				ia == InventoryAction.PICKUP_HALF ||
				ia == InventoryAction.PICKUP_SOME ||
				ia == InventoryAction.PICKUP_ONE ||
				ia == InventoryAction.SWAP_WITH_CURSOR;
	}
	public static boolean isHotBarSwap(InventoryAction ia){
		return ia == InventoryAction.HOTBAR_SWAP ||
				ia == InventoryAction.HOTBAR_MOVE_AND_READD;
	}
	public static boolean isDrop(InventoryAction ia){
		return ia == InventoryAction.DROP_ALL_CURSOR ||
				ia == InventoryAction.DROP_ALL_SLOT ||
				ia == InventoryAction.DROP_ONE_CURSOR ||
				ia == InventoryAction.DROP_ONE_SLOT;
	}
	public void fill(ItemStack is){
		for(int i = 0; i < getInventory().getSize(); i++){
			this.inventory.setItem(i, is);
		}
	}
	public void fillRest(ItemStack is){
		for (int i = 0; i < this.inventory.getSize(); i++){
			if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR){
				inventory.setItem(i, is);
			}
		}
	}
	public boolean modifiesGUI(InventoryClickEvent event){
		if(event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
				event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
				event.getAction() == InventoryAction.CLONE_STACK){
			return true;
		}
		if (event.getClickedInventory().equals(getInventory())){
			return isPlace(event.getAction()) || isPickUp(event.getAction()) || isHotBarSwap(event.getAction()) || isDrop(event.getAction());
		}
		return false;
	}


	}
