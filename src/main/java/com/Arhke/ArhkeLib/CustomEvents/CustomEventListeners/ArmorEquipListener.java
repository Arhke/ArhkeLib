package com.Arhke.ArhkeLib.CustomEvents.CustomEventListeners;

import com.Arhke.ArhkeLib.CustomEvents.ArmorEquipEvent1_8;
import com.Arhke.ArhkeLib.CustomEvents.ArmorType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"unused", "deprecation"})
public class ArmorEquipListener implements Listener {


    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(final InventoryClickEvent e){
        boolean shift = false, numberkey = false;
        if(e.isCancelled() || e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.PLAYER || !(e.getWhoClicked() instanceof Player) || e.getAction() == InventoryAction.NOTHING) return;
        Player player = (Player)e.getWhoClicked();
        ArmorEquipEvent1_8 aee;
        if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY){
            if(e.getInventory().getType() != InventoryType.CRAFTING) return;
            ArmorType at = ArmorType.matchType(e.getCurrentItem());
            if(at == null) return;
            if(e.getSlotType() == InventoryType.SlotType.ARMOR){
                aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.SHIFT_CLICK, at, e.getCurrentItem(), null);
            }else if(e.getSlotType() == InventoryType.SlotType.CONTAINER || e.getSlotType() == InventoryType.SlotType.QUICKBAR){
                if(!isAirOrNull(player.getInventory().getArmorContents()[at.getSlot()])) return;
                aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.SHIFT_CLICK, at, null, e.getCurrentItem());
            }else{
                return;
            }
        }else if (e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD){
            if(e.getSlotType() != InventoryType.SlotType.ARMOR) return;
            ArmorType at = ArmorType.matchType(e.getCurrentItem());
            if(at == null) return;
            if(e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD){
                aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.HOTBAR_SWAP, at, e.getCurrentItem(), null);
            }else{
                ItemStack hotBarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.HOTBAR_SWAP, at, e.getCurrentItem(), hotBarItem);
            }
        }else if(e.getAction() == InventoryAction.DROP_ONE_SLOT) {
            if(e.getSlotType() != InventoryType.SlotType.ARMOR) return;
            ArmorType at = ArmorType.matchType(e.getCurrentItem());
            if(at == null) return;
            aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.HOTBAR_SWAP, at, e.getCurrentItem(), null);

        }else if(e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE){
            if(e.getSlotType() != InventoryType.SlotType.ARMOR) return;
            ArmorType at = ArmorType.matchType(e.getCursor());
            if(at == null || 8-e.getRawSlot() != at.getSlot()) return;
            aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.PICK_DROP, at, null, e.getCursor());
        }else if(e.getAction() == InventoryAction.PICKUP_HALF || e.getAction() == InventoryAction.PICKUP_ALL){
            if(e.getSlotType() != InventoryType.SlotType.ARMOR) return;
            ArmorType at = ArmorType.matchType(e.getCurrentItem());
            if(at == null) return;
            aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.PICK_DROP, at, e.getCurrentItem(), null);
        }else if(e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            if(e.getSlotType() != InventoryType.SlotType.ARMOR) return;
            ArmorType at = ArmorType.matchType(e.getCurrentItem());
            if(at == null) return;
            aee = new ArmorEquipEvent1_8(player, ArmorEquipEvent1_8.EquipMethod.PICK_DROP, at, e.getCurrentItem(), e.getCursor());
        }else {
            return;
        }
        Bukkit.getServer().getPluginManager().callEvent(aee);
        if(aee.isCancelled()){
            e.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.useItemInHand().equals(Event.Result.DENY)) return;
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            ArmorType newArmorType = ArmorType.matchType(e.getItem());
            if (newArmorType != null) {
                if (isAirOrNull(e.getPlayer().getInventory().getArmorContents()[newArmorType.getSlot()])) {
                    ArmorEquipEvent1_8 armorEquipEvent = new ArmorEquipEvent1_8(e.getPlayer(), ArmorEquipEvent1_8.EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if (armorEquipEvent.isCancelled()) {
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event){
        ArmorType type = ArmorType.matchType(event.getOldCursor());
        if(event.getRawSlots().isEmpty()) return;
        if(type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
            ArmorEquipEvent1_8 armorEquipEvent = new ArmorEquipEvent1_8((Player) event.getWhoClicked(), ArmorEquipEvent1_8.EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e){
        ArmorType type = ArmorType.matchType(e.getBrokenItem());
        if(type != null){
            Player p = e.getPlayer();
            ArmorEquipEvent1_8 armorEquipEvent = new ArmorEquipEvent1_8(p, ArmorEquipEvent1_8.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                if(type.equals(ArmorType.HELMET)){
                    p.getInventory().setHelmet(i);
                }else if(type.equals(ArmorType.CHESTPLATE)){
                    p.getInventory().setChestplate(i);
                }else if(type.equals(ArmorType.LEGGINGS)){
                    p.getInventory().setLeggings(i);
                }else if(type.equals(ArmorType.BOOTS)){
                    p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(e.getKeepInventory()) return;
        for(ItemStack i : p.getInventory().getArmorContents()){
            if(!isAirOrNull(i)){
                Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent1_8(p, ArmorEquipEvent1_8.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
            }
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public static boolean isAirOrNull(ItemStack item){
        return item == null || item.getType().equals(Material.AIR);
    }
}
