package com.Arhke.ArhkeLib.Lib.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Makes easy to use an inventory as a GUI by saving items with string.
 *
 * @author William Lin
 *
 */
public abstract class InventoryGui {

	private final Inventory inventory;
	boolean canModify = false, closeAfterPress = false;
	List<Integer> canModifySlots = new ArrayList<>();
	List<Integer> exemptCloseAfterPress = new ArrayList<>();
	private final HashMap<Integer, Consumer<InventoryClickEvent>> items = new HashMap<>();
	/**
	 * Creates now inventory gui.
	 *
	 * @param rows put in the number of rows of the inventory
	 * sets the main instance of this plugin and stores a copy of the inventory parameter.
	 */
	public InventoryGui(int rows, String title){
	    this.inventory = Bukkit.createInventory(null, Math.min(Math.max(1, rows), 6)*9, title);
	}public InventoryGui(int rows, String title, Player player){
	    this.inventory = Bukkit.createInventory(null, Math.min(Math.max(1, rows), 6)*9, title);
	    setItems(player);
	}
	public abstract void setItems(Player p);
	public void setItems(){setItems(null);}
	public abstract void onOpen(InventoryOpenEvent event);
	public abstract void onClose(InventoryCloseEvent event);


	//Assumes the player already has this inventory open.
	public void onPress(InventoryClickEvent event){
		if (modifiesGUI(event)){
			if(!(canModify || canModifySlots.contains(event.getRawSlot()))){
				event.setCancelled(true);

			}
		}

		int is = event.getRawSlot();
		Consumer<InventoryClickEvent> cons = items.get(is);
		if (cons != null){
			cons.accept(event);
			if(closeAfterPress)event.getWhoClicked().closeInventory();
		}
	}
    /**
     * Gets the bukkit inventory.
     * @return
     */
    public Inventory getInventory(){
        return inventory;
    }
	/**
	 * sets an item in the item function hashmap and inventory
	 * @param index
	 * @param item
	 */
	public void setItem(int index, ItemStack item, Consumer<InventoryClickEvent> consumer){
		inventory.setItem(index, item);
		items.put(index, consumer);
	}
	/**
	 * sets an item in the inventory
	 * @param index
	 * @param item
	 */
	public void setItem(int index, ItemStack item){
		inventory.setItem(index, item);
	}
	public void fill(ItemStack is){
		for(int i = 0; i < getInventory().getSize(); i++){
			this.inventory.setItem(i, is);
		}
	}
	public void fill(ItemStack is, Predicate<Integer> predicate){
		for(int i = 0; i < getInventory().getSize(); i++){
			if(predicate.test(i))this.inventory.setItem(i, is);
		}
	}
	public void fillRest(ItemStack is){
		for (int i = 0; i < this.inventory.getSize(); i++){
			if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR){
				inventory.setItem(i, is);
			}
		}
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

	//===========<Protected>==========
	protected void setCloseAfterPress(boolean close){
		this.closeAfterPress = close;
	}protected void setCanModify(boolean modify){
		this.canModify = modify;
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
	public boolean modifiesGUI(InventoryClickEvent event){
		if(event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
				event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
				event.getAction() == InventoryAction.CLONE_STACK){
			return true;
		}
		if (event.getClickedInventory() != null && event.getClickedInventory().equals(getInventory())){
			return isPlace(event.getAction()) || isPickUp(event.getAction()) || isHotBarSwap(event.getAction()) || isDrop(event.getAction());
		}
		return false;
	}


	}
