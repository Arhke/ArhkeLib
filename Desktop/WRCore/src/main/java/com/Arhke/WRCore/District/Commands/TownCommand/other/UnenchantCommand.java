//package com.Arhke.WRCore.District.Commands.DistrictCommand.other;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.EnchantmentStorageMeta;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.District.Commands.CustomHelpPermissions;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.TUsers.classes.Profession.Type;
//import com.Arhke.WRCore.District.util.ErrorManager;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class UnenchantCommand extends Command implements CustomHelpPermissions {
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!hasPermission(p)){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//
//			/*
//			 * Get item in hand
//			 * Check for enchantments
//			 * Remove enchantments
//			 * Drop enchamtnet books
//			 * Reset item in hand
//			 * Send message
//			 */
//			ItemStack item = Feudal.getItemInHand(p);
//			if(item != null && !item.getEnchantments().isEmpty()) {
//				Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>(item.getEnchantments());
//				for(Enchantment e : enchants.keySet()) {
//					item.removeEnchantment(e);
//				}
//
//				Location drop = p.getLocation().clone();
//				for(Enchantment e : enchants.keySet()) {
//					dropEnchantment(e, enchants.get(e).intValue(), drop);
//				}
//
//				Feudal.setItemInHand(p, item);
//				p.updateInventory();
//
//				p.sendMessage(this.dm.getTCM("unenchant.removed"));
//			}else {
//				p.sendMessage(this.dm.getTCM("unenchant.unable"));
//			}
//
//			return true;
//		}
//		return false;
//	}
//
//	private void dropEnchantment(Enchantment enchantment, int level, Location drop) {
//		try {
//			ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
//			EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
//			if(meta != null) {
//				meta.addStoredEnchant(enchantment, level, true);
//				item.setItemMeta(meta);
//			}
//
//			drop.getWorld().dropItem(drop, item);
//		}catch(Exception e) {
//			ErrorManager.error(1064152018, e);
//		}
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.unenchant",
//				"commandHelp.new1246")};
//	}
//
//	@Override
//	public boolean hasPermission(Player player) {
//		if(player.hasPermission("feudal.commands.admin.unenchant") || isScribe(player)) {
//			return true;
//		}else {
//			return false;
//		}
//	}
//
//	private boolean isScribe(Player player) {
//		User user = Feudal.getAPI().getUser(player.getUniqueId());
//		if(user != null && user.getProfession() != null && user.getProfession().getType() != null && user.getProfession().getType() == Type.SCRIBE) {
//			return true;
//		}
//		return false;
//	}
//
//}
