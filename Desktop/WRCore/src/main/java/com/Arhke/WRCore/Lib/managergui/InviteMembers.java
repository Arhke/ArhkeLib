//package com.Arhke.WRCore.Lib.managergui;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.SkullMeta;
//
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.TUsers.classes.Profession.Type;
//
///**
// * Main manager menu
// *
// * @author Michael Forseth
// * @version Mar 8, 2018
// *
// */
//public class InviteMembers extends InventoryGui2 {
//
//	private int page = 0;
//	private int maxPage = 0;
//	private List<TUser> users;
//	private InventoryGui2 parent;
//
//	public InviteMembers(InventoryGui2 parent, Player player) throws Exception {
//		super(Bukkit.createInventory(null, getInventorySize(54), lim(Feudal.getMessage("m.title5"))), player);
//
//		this.parent = parent;
//		this.users = getUserList();
//		this.maxPage = getMaxPage();
//
//		createInventory();
//		openInventory();
//	}
//
//	private void createInventory() {
//		this.clear();
//		this.getInventory().clear();
//		this.setItem("BACK", 49, createItem(Material.BED, 1, 0, Feudal.getMessage("m.back"), Arrays.asList(new String[] {Feudal.getMessage("m.back2"), "�a", Feudal.getMessage("m.page") + (page+1)})));
//
//		if(page > 0) {
//			//PAGE BACK
//			this.setItem("PAGEBACK", 45, createItem(Material.WOOL, 1, 14, Feudal.getMessage("m.pageBack"), Arrays.asList(new String[] {Feudal.getMessage("m.pageBack2")})));
//		}else if(page < maxPage) {
//			//PAGE FOWARD
//			this.setItem("PAGENEXT", 53, createItem(Material.WOOL, 1, 5, Feudal.getMessage("m.pageForward"), Arrays.asList(new String[] {Feudal.getMessage("m.pageForward2")})));
//		}
//
//		for(int i = 0, m = page * 36; i < 36 && m < users.size(); i++, m++) {
//			//SHOW USERS
//			User user = users.get(m);
//			if(user.getPlayer() != null && user.getPlayer().isOnline()) {
//				List<String> lore = new ArrayList<String>();
//				lore.add(Feudal.getMessage("m.reputation") + user.getReputation());
//				lore.add(Feudal.getMessage("m.profession") + user.getProfession().getType().getName());
//
//				ItemStack item = createItem(Material.SKULL_ITEM, 1, 3, "�d" + user.getPlayer().getName(), lore);
//
//				try {
//					SkullMeta it = (SkullMeta) item.getItemMeta();
//					it.setOwner(user.getPlayer().getName());
//				}catch(Exception e) {}
//
//				this.setItem("I" + m, i, item);
//			}
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
//			}else if(id.equals("PAGEBACK")) {
//				if(page > 0) {
//					page--;
//					soundClick(getPlayer());
//					createInventory();
//					getPlayer().updateInventory();
//				}
//			}else if(id.equals("PAGENEXT")) {
//				if(page < maxPage) {
//					page++;
//					soundClick(getPlayer());
//					createInventory();
//					getPlayer().updateInventory();
//				}
//			}else if(id.startsWith("I")) {
//				getPlayer().closeInventory();
//
//				try {
//					User user = users.get(Integer.parseInt(id.substring(1)));
//					if(user.getPlayer() != null) {
//						soundClick(getPlayer());
//						getPlayer().performCommand("k invite " + user.getPlayer().getName());
//					}else {
//						soundDeny(getPlayer());
//					}
//				}catch(Exception e) {
//					e.printStackTrace();
//					soundDeny(getPlayer());
//				}
//			}
//		}
//	}
//
//	private List<TUser> getUserList() {
//		List<TUser> users = new ArrayList<TUser>();
//
//		for(TUser user : Feudal.getOnlinePlayers().values()) {
//			if(user != null && user.getKingdomUUID() != null &&
//					user.getKingdomUUID().isEmpty() && user.getProfession() != null && user.getProfession().getType() != Type.NONE) {
//				users.add(user);
//			}
//		}
//
//		return users;
//	}
//
//	private int getMaxPage() {
//		return users.size() / 36;
//		//return (int) Math.ceil(((double)users.size()) / 36.0);
//	}
//
//	@Override
//	public void close() {}
//
//	@Override
//	public void reinitiate() {
//		this.users = getUserList();
//		this.maxPage = getMaxPage();
//		page = 0;
//		createInventory();
//		openInventory();
//	}
//
//}
