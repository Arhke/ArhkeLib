//package com.Arhke.WRCore.Lib.managergui;
//
//import java.util.Arrays;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.kingdoms.Member;
//import com.Arhke.WRCore.District.kingdoms.Rank;
//
///**
// * Main manager menu
// *
// * @author Michael Forseth
// * @version Mar 8, 2018
// *
// */
//public class ManagerMember extends InventoryGui2 {
//
//	private Kingdom kingdom;
//	private Rank rank;
//	private Member member;
//	private InventoryGui2 parent;
//	private ItemStack skull;
//	private long rankChange = 0;
//
//	public ManagerMember(InventoryGui2 parent, Player player, Kingdom kingdom, Rank rank, Member member, ItemStack skull) throws Exception {
//		super(Bukkit.createInventory(null, 54, lim(Feudal.getMessage("m.title6"))), player);
//
//		this.parent = parent;
//		this.kingdom = kingdom;
//		this.rank = rank;
//		this.member = member;
//		this.skull = skull;
//
//		createInventory();
//		openInventory();
//	}
//
//	private void createInventory() {
//		this.clear();
//		this.getInventory().clear();
//		this.setItem("BACK", 48, createItem(Material.BED, 1, 0, Feudal.getMessage("m.back"), Arrays.asList(new String[] {Feudal.getMessage("m.back2")})));
//
//		this.setItem("SKULL", 50, skull);
//
//		if(rank == Rank.LEADER) {
//			//SET LEADER
//			this.setItem("SETLEADER", 31, createItem(Material.GOLDEN_APPLE, 1, 1, Feudal.getMessage("m.setleader"),
//					Arrays.asList(new String[] {Feudal.getMessage("m.setleader2")})));
//
//			//FIGHTER
//			if(member.isFighter()) {
//				this.setItem("FIGHTER", 27, createItem(Material.WOOL, 1, 14, Feudal.getMessage("m.fighter4"),
//						Arrays.asList(new String[] {Feudal.getMessage("m.fighter5"), "�c", Feudal.getMessage("m.fighter3")})));
//			}else {
//				this.setItem("FIGHTER", 27, createItem(Material.WOOL, 1, 5, Feudal.getMessage("m.fighter1"),
//						Arrays.asList(new String[] {Feudal.getMessage("m.fighter2"), "�c", Feudal.getMessage("m.fighter3")})));
//			}
//		}
//
//		if(rank.ID > member.getRank().ID) {
//			//KICK
//			this.setItem("KICK", rank==Rank.LEADER ? 35 : 31, createItem(Material.TNT, 1, 0, Feudal.getMessage("m.kickmember"),
//					Arrays.asList(new String[] {Feudal.getMessage("m.kickmember2")})));
//		}
//
//		if(member.getRank() == Rank.LEADER) {
//			this.setItem("A", 4, createItem(Material.GOLD_HELMET, 1, 0, "�b�l" + Feudal.getMessage("m.leader"), Arrays.asList(new String[] {Feudal.getMessage("m.currentrank")})));
//		}else {
//			this.setItem("GUEST", 3, createItem(Material.LEATHER_HELMET, 1, 0, "�7" + Feudal.getMessage("m.guest"),
//					Arrays.asList(new String[] {member.getRank() == Rank.GUEST ? Feudal.getMessage("m.currentrank") : "�c"})));
//
//			this.setItem("MEMBER", 4, createItem(Material.IRON_HELMET, 1, 0, "�e" + Feudal.getMessage("m.member"),
//					Arrays.asList(new String[] {member.getRank() == Rank.MEMBER ? Feudal.getMessage("m.currentrank") : "�c"})));
//
//			this.setItem("EXECUTIVE", 5, createItem(Material.DIAMOND_HELMET, 1, 0, "�d" + Feudal.getMessage("m.executive"),
//					Arrays.asList(new String[] {member.getRank() == Rank.EXECUTIVE ? Feudal.getMessage("m.currentrank") : "�c"})));
//
//			int index = 12;
//			if(member.getRank() == Rank.MEMBER) {
//				index = 13;
//			}else if(member.getRank() == Rank.EXECUTIVE) {
//				index = 14;
//			}
//
//			this.setItem("CURRENTRANK", index, createItem(Material.WOOL, 1, 0, Feudal.getMessage("m.currentrank"), null));
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
//			}else if(id.equals("SETLEADER")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k leader " + member.getPlayer().getName());
//			}else if(id.equals("FIGHTER")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k fighter " + member.getPlayer().getName());
//			}else if(id.equals("KICK")) {
//				getPlayer().closeInventory();
//				soundClick(getPlayer());
//				getPlayer().performCommand("k kick " + member.getPlayer().getName());
//			}else if(id.equals("GUEST") || id.equals("MEMBER") || id.equals("EXECUTIVE")) {
//				if(getPlayer().hasPermission("feudal.commands.user.kingdoms.promote") &&
//						getPlayer().hasPermission("feudal.commands.user.kingdoms.demote")) {
//					if(rank == Rank.LEADER || rank == Rank.EXECUTIVE) {
//						if(rank.ID > member.getRank().ID) {
//							if(System.currentTimeMillis() - rankChange > 5000) {
//								//SET RANK
//								Rank newRank = Rank.GUEST;
//
//								if(id.equals("MEMBER")) {
//									newRank = Rank.MEMBER;
//								}else if(id.equals("EXECUTIVE")) {
//									newRank = Rank.EXECUTIVE;
//								}
//
//								if(newRank == member.getRank()) {
//									getPlayer().sendMessage(Feudal.getMessage("m.setrank3"));
//									soundDeny(getPlayer());
//									return;
//								}
//
//								kingdom.setRank(member.getPlayer().getUniqueId().toString(), newRank);
//								/*try {
//									kingdom.save();
//								} catch (Exception e) {
//									ErrorManager.error(35, e);
//									getPlayer().sendMessage(Feudal.getMessage("commands.2834"));
//									return;
//								}*/
//								getPlayer().sendMessage(Feudal.getMessage("m.setrank").replace("{1}", newRank.toString() + "").replace("{0}", member.getPlayer().getName() + ""));
//								if(member.getPlayer().isOnline()){
//									member.getPlayer().getPlayer().sendMessage(Feudal.getMessage("m.setrank2").replace("{1}", newRank.toString() + "").replace("{0}", getPlayer().getName()));
//								}
//
//								rankChange = System.currentTimeMillis();
//
//								soundClick(getPlayer());
//								member.setTempRank(newRank);
//								createInventory();
//								getPlayer().updateInventory();
//							}else {
//								soundDeny(getPlayer());
//								getPlayer().sendMessage(Feudal.getMessage("m.wait") + getTime());
//							}
//						}else {
//							soundDeny(getPlayer());
//						}
//					}else {
//						soundDeny(getPlayer());
//					}
//				}
//			}
//		}
//	}
//
//	private String getTime() {
//		int timeleft = (int) ((5000 - (System.currentTimeMillis() - rankChange)) / 1000);
//		return timeleft + "s";
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
//}
