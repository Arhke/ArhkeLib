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
//import com.Arhke.WRCore.District.util.ErrorManager;
//
///**
// * Main manager menu
// *
// * @author Michael Forseth
// * @version Mar 8, 2018
// *
// */
//public class ManagerWarList extends InventoryGui2 {
//
//	private Kingdom kingdom;
//	private List<Battle> battles;
//	private ManagerMenu parent;
//	private Rank rank;
//
//	public ManagerWarList(ManagerMenu parent, Kingdom kingdom, List<Battle> battles, Rank rank) throws Exception {
//		super(Bukkit.createInventory(null, 18, lim(Feudal.getMessage("m.title2"))), parent.getPlayer());
//
//		this.kingdom = kingdom;
//		this.battles = battles;
//		this.parent = parent;
//		this.rank = rank;
//
//		createInventory();
//		openInventory();
//	}
//
//	private void createInventory() {
//		this.clear();
//		this.getInventory().clear();
//		this.setItem("BACK", 13, createItem(Material.BED, 1, 0, Feudal.getMessage("m.back"), Arrays.asList(new String[] {Feudal.getMessage("m.back2")})));
//
//		for(int i = 0; i < battles.size() && i < 9; i++) {
//			this.setItem("C" + i, i, getBanner(battles.get(i)));
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
//			}else if(id.startsWith("C")) {
//				getPlayer().closeInventory();
//
//				try {
//					Battle battle = battles.get(Integer.parseInt(id.substring(1)));
//					try {
//						new ManagerWar(this, kingdom, battle, rank);
//						soundClick(getPlayer());
//					}catch(Exception e) {
//						ErrorManager.error(124392018, e);
//						soundDeny(getPlayer());
//						getPlayer().sendMessage(Feudal.getMessage("m.error2"));
//					}
//				}catch(Exception e) {
//					e.printStackTrace();
//					soundDeny(getPlayer());
//				}
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
//}
