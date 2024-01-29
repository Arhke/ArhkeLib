package com.Arhke.ArhkeLib.Lib.GUI;

import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class GUIManager extends MainBase<JavaPlugin> implements Listener {
    public HashMap<UUID, InventoryGui> guiMap = new HashMap<>();
    public GUIManager(JavaPlugin instance){
        super(instance);
    }
    public void openGUI(Player player, InventoryGui gui){
        guiMap.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }
    public InventoryGui remove(UUID uuid){
        return guiMap.remove(uuid);

    }
    public InventoryGui get(UUID uuid){
        return guiMap.get(uuid);
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent ice){
        InventoryGui ig = this.get(ice.getWhoClicked().getUniqueId());
        if(ig != null){
            ig.onPress(ice);
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent event){
        InventoryGui ig = this.remove(event.getPlayer().getUniqueId());
        if(ig != null){
            ig.onClose(event);
        }
    }
    @EventHandler
    public void onOpen(InventoryOpenEvent event){
        InventoryGui ig = this.get(event.getPlayer().getUniqueId());
        if(ig != null){
            ig.onOpen(event);
        }
    }
}
