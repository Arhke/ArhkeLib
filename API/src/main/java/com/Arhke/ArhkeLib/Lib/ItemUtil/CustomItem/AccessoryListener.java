package com.Arhke.ArhkeLib.Lib.ItemUtil.CustomItem;

//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.List;
//
//public class AccessoryListener implements Listener {
//    @EventHandler
//    public void onAccessoryClick(InventoryClickEvent event) {
//        if(event.getCursor() == null || event.getCurrentItem() == null) return;
//        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
//        if(cursor.getItemMeta() == null || current.getItemMeta() == null) return;
//        String mat = current.getType().name();
//        if(!(mat.contains("HELMET") || mat.contains("CHESTPLATE") || mat.contains("LEGGINGS") || mat.contains("BOOTS"))){
//            return;
//        }
//        List<String> cursorLore = cursor.getItemMeta().getLore();
//        if(cursorLore == null) return;
//        if(cursorLore.size() < 1 || !cursorLore.get(0).toLowerCase().contains("accessory")){
//            return;
//        }
//        ArmorAccessories armor = new ArmorAccessories(current), accessory = new ArmorAccessories(cursor);
//        if(!armor.addAccessory(accessory)){
//            event.getWhoClicked().sendMessage(Base.tcm("&cOops! Sorry, you can't do that."));
//            return;
//        }
//        event.setCancelled(true);
//        cursor.setAmount(cursor.getAmount()-1);
//        event.getWhoClicked().setItemOnCursor(cursor);
//        armor.regenerateLore();
//        event.setCurrentItem(armor.getIs());
//
//
//
//    }
//}
