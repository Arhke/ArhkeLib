//package com.Arhke.WRCore.Lib.managergui;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.Arhke.WRCore.District.kingdoms.Battle;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
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
//public class ManagerMenu extends InventoryGui2 {
//
//	private Kingdom kingdom;
//	private Rank rank;
//
//	public ManagerMenu(TUser user, Kingdom kingdom, Player player) throws Exception {
//		super(Bukkit.createInventory(null, 45, lim(Feudal.getMessage("m.title"))), player);
//
//		this.kingdom = kingdom;
//		this.rank = kingdom.getMembers().get(user.getUUID());
//
//		createInventory();
//		openInventory();
//	}
//
//	private void createInventory() {
//		this.clear();
//		this.getInventory().clear();
//		//Manage members
//		setItem("KINGDOM", 22, getBanner());
//
//		//TP HOME
//		if(getPlayer().hasPermission("feudal.commands.user.kingdoms.home")) {
//			setItem("HOME", 40, createItem(Material.ENDER_PEARL, 1, 0, Feudal.getMessage("m.home"), Arrays.asList(new String[] {Feudal.getMessage("m.home2")})));
//		}
//
//		if(rank.equals(Rank.EXECUTIVE) || rank.equals(Rank.LEADER)) {//CLAIMING & SETHOME
//			Kingdom kingdomLand = Feudal.getAPI().getKingdom(getPlayer().getLocation());
//			if(kingdomLand == null) {//claim
//				if(getPlayer().hasPermission("feudal.commands.user.kingdoms.claim")) {
//					setItem("CLAIM", 0, createItem(Material.WOOL, 1, 5, Feudal.getMessage("m.claim"), Arrays.asList(new String[] {Feudal.getMessage("m.claim2")})));
//				}
//			}else if(kingdomLand == kingdom){//unclaim & SETHOME
//				if(getPlayer().hasPermission("feudal.commands.user.kingdoms.unclaim")) {
//					setItem("UNCLAIM", 0, createItem(Material.WOOL, 1, 1, Feudal.getMessage("m.unclaim"), Arrays.asList(new String[] {Feudal.getMessage("m.unclaim2")})));
//				}
//
//				//SETHOME
//				if(getPlayer().hasPermission("feudal.commands.user.kingdoms.sethome")) {
//					setItem("SETHOME", 8, createItem(Material.BED, 1, 0, Feudal.getMessage("m.sethome"), Arrays.asList(new String[] {Feudal.getMessage("m.sethome2")})));
//				}
//			}
//		}
//
//		//FIND CLAIMS
//		if(getPlayer().hasPermission("feudal.commands.user.kingdoms.findclaims")) {
//			setItem("FINDCLAIMS", 5, createItem(Material.BOOK, 1, 0, Feudal.getMessage("m.findclaims"), Arrays.asList(new String[] {Feudal.getMessage("m.findclaims2")})));
//		}
//		//FIND CLAIMS
//		if(getPlayer().hasPermission("feudal.commands.user.kingdoms.view")) {
//			setItem("VIEW", 3, createItem(Material.MAP, 1, 0, Feudal.getMessage("m.view"), Arrays.asList(new String[] {Feudal.getMessage("m.view2")})));
//		}
//
//		if(rank.equals(Rank.LEADER)) {
//			//DELETE KINGDOM
//			if(getPlayer().hasPermission("feudal.commands.user.kingdoms.delete")) {
//				setItem("DELETE", 44, createItem(Material.TNT, 1, 0, Feudal.getMessage("m.delete"), Arrays.asList(new String[] {Feudal.getMessage("m.delete2")})));
//			}
//		}else {
//			//LEAVE KINGDOM
//			if(getPlayer().hasPermission("feudal.commands.user.kingdoms.leave")) {
//				setItem("LEAVE", 44, createItem(Material.TNT, 1, 0, Feudal.getMessage("m.leave"), Arrays.asList(new String[] {Feudal.getMessage("m.leave2")})));
//			}
//		}
//
//		//CHALLENGE INFORMATION
//		if(getPlayer().hasPermission("feudal.commands.user.kingdoms.challenge")) {
//			setItem("WAR", 36, createItem(Material.DIAMOND_SWORD, 1, 0, Feudal.getMessage("m.war"), Arrays.asList(new String[] {Feudal.getMessage("m.war2") + getChallengeCount(), "�c", Feudal.getMessage("m.war3")})));
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
//			if(id.equals("HOME")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k home");
//			}else if(id.equals("SETHOME")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k sethome");
//			}else if(id.equals("CLAIM")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k claim");
//			}else if(id.equals("UNCLAIM")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k unclaim");
//			}else if(id.equals("FINDCLAIMS")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k findclaims");
//			}else if(id.equals("VIEW")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k map me");
//			}else if(id.equals("DELETE")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k delete");
//			}else if(id.equals("LEAVE")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k leave");
//			}else if(id.equals("WAR")) {
//				List<Battle> battles = Feudal.getAPI().getChallenges(kingdom);
//				if(battles.size() != 0) {
//					getPlayer().closeInventory();
//					soundClick(getPlayer());
//					try {
//						if(battles.size() == 1) {
//							new ManagerWar(this, kingdom, battles.get(0), rank);
//						}else {
//							new ManagerWarList(this, kingdom, battles, rank);
//						}
//					}catch(Exception e) {
//						ErrorManager.error(814382018, e);
//						getPlayer().sendMessage(Feudal.getMessage("m.error2"));
//					}
//				}else {
//					getPlayer().closeInventory();
//					soundClick(getPlayer());
//					getPlayer().sendMessage(Feudal.getMessage("m.warNone"));
//				}
//			}else if(id.equals("KINGDOM")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				try {
//					new ManagerMembers(this, kingdom, getPlayer(), rank);
//				}catch(Exception e) {
//					ErrorManager.error(319392018, e);
//					getPlayer().sendMessage(Feudal.getMessage("m.error2"));
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
//	private ItemStack getBanner() {
//		ItemStack stack = null;
//		if(Feudal.getVersion().contains("1.7")) {
//			stack = new ItemStack(Material.ENDER_CHEST);
//		}else {
//			stack = Bannerss.getBannerItem(kingdom);
//		}
//
//		ItemMeta meta = stack.getItemMeta();
//
//		meta.setDisplayName("�d" + kingdom.getName());
//		List<String> lore = new ArrayList<String>();
//		lore.add(Feudal.getMessage("m.banner1") + kingdom.getMembers().size());
//		lore.add(Feudal.getMessage("m.banner2") + String.format("%.2f", kingdom.getTreasury()));
//		lore.add(Feudal.getMessage("m.banner3") + kingdom.getChallengesWon() + " / " + (kingdom.getChallengesLost()+kingdom.getChallengesWon()));
//		lore.add("�c");
//		lore.add(Feudal.getMessage("m.banner4"));
//		meta.setLore(lore);
//		stack.setItemMeta(meta);
//
//		return stack;
//	}
//
//	private int getChallengeCount() {
//		return Feudal.getAPI().getChallenges(kingdom).size();
//	}
//
//}
