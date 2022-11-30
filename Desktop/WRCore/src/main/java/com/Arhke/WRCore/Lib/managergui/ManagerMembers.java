//package com.Arhke.WRCore.Lib.managergui;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.SkullMeta;
//
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.kingdoms.Member;
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
//public class ManagerMembers extends InventoryGui2 {
//
//	private Kingdom kingdom;
//	private Rank rank;
//	private List<Member> members;
//	private InventoryGui2 parent;
//
//	public ManagerMembers(InventoryGui2 parent, Kingdom kingdom, Player player, Rank rank) throws Exception {
//		super(Bukkit.createInventory(null, getInventorySize(kingdom.getMembers().size()+9), lim(Feudal.getMessage("m.title4"))), player);
//
//		this.parent = parent;
//		this.kingdom = kingdom;
//		this.rank = rank;
//		this.members = kingdom.getMembersOrdered();
//
//		createInventory();
//		openInventory();
//	}
//
//	private void createInventory() {
//		this.clear();
//		this.getInventory().clear();
//		Iterator<Member> iterator = members.iterator();
//		while(iterator.hasNext()) {
//			Member mem = iterator.next();
//			if(mem.getPlayer() != null && mem.getPlayer().getUniqueId().equals(getPlayer().getUniqueId())) {
//				iterator.remove();
//			}
//		}
//
//		boolean invite = rank != Rank.GUEST;
//
//		this.setItem("BACK", getInventory().getSize()-(invite ? 6 : 5), createItem(Material.BED, 1, 0, Feudal.getMessage("m.back"), Arrays.asList(new String[] {Feudal.getMessage("m.back2")})));
//
//		if(invite) {
//			this.setItem("INVITE", getInventory().getSize()-4, createItem(Material.BOOK, 1, 0, Feudal.getMessage("m.inviteList"), Arrays.asList(new String[] {Feudal.getMessage("m.inviteList2")})));
//		}
//
//		for(int i = 0; i < members.size() && i < getInventory().getSize() - 9; i++) {
//			Member member = members.get(i);
//
//			String color = "�7";
//			if(member.getRank() == Rank.MEMBER) {
//				color = "�e";
//			}else if(member.getRank() == Rank.EXECUTIVE) {
//				color = "�d";
//			}else if(member.getRank() == Rank.LEADER) {
//				color = "�b�l";
//			}
//
//			List<String> lore = new ArrayList<String>();
//			lore.add(Feudal.getMessage("m.rank") + member.getRank());
//			if(member.isFighter()) {
//				lore.add(Feudal.getMessage("m.fighter"));
//			}
//			lore.add("�c");
//			if(member.isOnline()) {
//				lore.add(Feudal.getMessage("m.online"));
//			}else {
//				lore.add(Feudal.getMessage("m.offline"));
//			}
//
//			ItemStack item = createItem(Material.SKULL_ITEM, 1, 3, color + member.getPlayer().getName(), lore);
//
//			try {
//				SkullMeta it = (SkullMeta) item.getItemMeta();
//				it.setOwner(member.getPlayer().getName());
//			}catch(Exception e) {}
//
//			this.setItem("M" + i, i, item);
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
//			}else if(id.startsWith("M")) {
//				getPlayer().closeInventory();
//
//				try {
//					Member member = members.get(Integer.parseInt(id.substring(1)));
//					try {
//						new ManagerMember(this, getPlayer(), kingdom, rank, member, this.getItemStack(id).clone());
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
//			}else if(id.equals("INVITE")) {
//				getPlayer().closeInventory();
//				try {
//					new InviteMembers(this, getPlayer());
//					soundClick(getPlayer());
//				}catch(Exception e) {
//					ErrorManager.error(124392018, e);
//					soundDeny(getPlayer());
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
//		this.members = kingdom.getMembersOrdered();
//		createInventory();
//		openInventory();
//	}
//
//}
