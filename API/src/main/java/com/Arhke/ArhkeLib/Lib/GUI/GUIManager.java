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
    public HashMap<UUID, InventoryGui<? extends JavaPlugin>> guiMap = new HashMap<>();
    public GUIManager(JavaPlugin instance){
        super(instance);
    }
    public void openGUI(Player player, InventoryGui<? extends JavaPlugin> gui){
        guiMap.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }
    public InventoryGui<? extends JavaPlugin> remove(UUID uuid){
        return guiMap.remove(uuid);

    }
    public InventoryGui<? extends JavaPlugin> get(UUID uuid){
        return guiMap.get(uuid);
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent ice){
        InventoryGui<? extends JavaPlugin> ig = this.get(ice.getWhoClicked().getUniqueId());
        if(ig != null){
            ig.onPress(ice);
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent event){
        InventoryGui<? extends JavaPlugin> ig = this.remove(event.getPlayer().getUniqueId());
        if(ig != null){
            ig.onClose(event);
        }
    }
    @EventHandler
    public void onOpen(InventoryOpenEvent event){
        InventoryGui<? extends JavaPlugin> ig = this.get(event.getPlayer().getUniqueId());
        if(ig != null){
            ig.onOpen(event);
        }
    }
}
