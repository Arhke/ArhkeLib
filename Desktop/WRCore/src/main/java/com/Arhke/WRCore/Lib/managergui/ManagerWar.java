//package com.Arhke.WRCore.Lib.managergui;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.Arhke.WRCore.District.kingdoms.Battle;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Bannerss;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.kingdoms.Rank;
//
///**
// * Main manager menu
// *
// * @author Michael Forseth
// * @version Mar 8, 2018
// *
// */
//public class ManagerWar extends InventoryGui2 {
//
//	private Kingdom kingdom;
//	private Battle battle;
//	private InventoryGui2 parent;
//	private Rank rank;
//	private Kingdom enemy;
//
//	public ManagerWar(InventoryGui2 parent, Kingdom kingdom, Battle battle, Rank rank) throws Exception {
//		super(Bukkit.createInventory(null, 27, lim(Feudal.getMessage("m.title3"))), parent.getPlayer());
//
//		this.kingdom = kingdom;
//		this.battle = battle;
//		this.parent = parent;
//		this.rank = rank;
//		this.enemy = getEnemy();
//
//		createInventory();
//		openInventory();
//	}
//
//	private void createInventory() {
//		this.clear();
//		this.getInventory().clear();
//		this.setItem("BACK", 21, createItem(Material.BED, 1, 0, Feudal.getMessage("m.back"), Arrays.asList(new String[] {Feudal.getMessage("m.back2")})));
//
//		this.setItem("INFO", 23, getBanner(battle));
//
//		boolean canAccept = !battle.isAccepted() && rank != Rank.GUEST;
//		boolean canSurrender = rank == Rank.EXECUTIVE || rank == Rank.LEADER;
//
//		int teleport = 4;
//		int accept = 2;
//		int surrender = 6;
//
//		if(canAccept && canSurrender) {
//			teleport = 4;
//			surrender = 6;
//		}else if(canAccept && !canSurrender) {
//			accept = 3;
//			teleport = 5;
//		}else if(!canAccept && canSurrender) {
//			teleport = 3;
//			surrender = 5;
//		}
//
//		//TELEPORT
//		this.setItem("TELEPORT", teleport, createItem(Material.ENDER_PEARL, 1, 0, Feudal.getMessage("m.teleport"), Arrays.asList(new String[] {Feudal.getMessage("m.teleport2")})));
//
//		//ACCEPT CHALLENGE
//		if(canAccept) {
//			this.setItem("ACCEPT", accept, createItem(Material.WOOL, 1, 5, Feudal.getMessage("m.accept"), Arrays.asList(new String[] {Feudal.getMessage("m.accept2"), Feudal.getMessage("m.accept3")})));
//		}
//
//		//SURRENDER
//		if(canSurrender) {
//			this.setItem("SURRENDER", surrender, createItem(Material.TNT, 1, 0, Feudal.getMessage("m.surrender"), Arrays.asList(new String[] {Feudal.getMessage("m.surrender2"), Feudal.getMessage("m.surrender3"), Feudal.getMessage("m.surrender4")})));
//		}
//	}
//
//	private void openInventory() {
//		getPlayer().openInventory(getInventory());
//	}
//
//	@Override
//	public void click(String id) {
//		if(id != null) {
//			if(id.equals("BACK")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				parent.reload();
//			}else if(id.equals("TELEPORT")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k war tp " + enemy.getName());
//			}else if(id.equals("ACCEPT")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k war accept " + enemy.getName());
//			}else if(id.equals("SURRENDER")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k war surrender " + enemy.getName());
//			}
//		}
//	}
//
//	@Override
//	public void close() {}
//
//	@Override
//	public void reinitiate() {
//		createInventory();
//		openInventory();
//	}
//
//	private ItemStack getBanner(Battle battle) {
//		Kingdom enemy = battle.getAttacker();
//		boolean attacker = true;
//		if(enemy == kingdom) {
//			enemy = battle.getDefender();
//			attacker = false;
//		}
//
//		ItemStack stack = null;
//		if(Feudal.getVersion().contains("1.7")) {
//			stack = new ItemStack(Material.CHEST);
//		}else {
//			stack = Bannerss.getBannerItem(enemy);
//		}
//
//		ItemMeta meta = stack.getItemMeta();
//
//		meta.setDisplayName("�d" + enemy.getName());
//		List<String> lore = new ArrayList<String>();
//		if(attacker) {
//			lore.add(Feudal.getMessage("m.warList2"));
//		}else {
//			lore.add(Feudal.getMessage("m.warList1"));
//		}
//		lore.add(Feudal.getMessage("m.warList4") + getState(battle, attacker));
//		lore.add("�c");
//		lore.add(Feudal.getMessage("m.warList3"));
//		meta.setLore(lore);
//		stack.setItemMeta(meta);
//
//		return stack;
//	}
//
//	private static String getState(Battle battle, boolean attacker) {
//		if(battle.isFighting()) {
//			return "�cFIGHTING";
//		}else if(battle.isAccepted()) {
//			return "�aACCEPTED";
//		}else if(attacker) {
//			return "�7WAITING for enemy to accept";
//		}else {
//			return "�dYou must accept";
//		}
//	}
//
//	private Kingdom getEnemy() {
//		Kingdom enemy = battle.getAttacker();
//		if(enemy == kingdom) {
//			enemy = battle.getDefender();
//		}
//		return enemy;
//	}
//
//}
