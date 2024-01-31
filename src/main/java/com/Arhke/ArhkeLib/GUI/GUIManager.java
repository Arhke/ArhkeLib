package com.Arhke.ArhkeLib.GUI;

import com.Arhke.ArhkeLib.Base.MainBase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class GUIManager extends MainBase<JavaPlugin> implements Listener {
    public HashMap<UUID, InventoryGui> guiMap = new HashMap<>();
    public HashMap<UUID, BukkitTask> guiTask = new HashMap<>();
    public GUIManager(JavaPlugin instance){
        super(instance);
    }
    public void openGUI(Player player, InventoryGui gui){
        player.closeInventory();
        BukkitTask bt = guiTask.put(player.getUniqueId(), new BukkitRunnable(){
            @Override
            public void run(){
                guiMap.put(player.getUniqueId(), gui);
                player.openInventory(gui.getInventory());
                guiTask.remove(player.getUniqueId());
            }
        }.runTaskLater(getPlugin(), 1));
        if(bt!=null) bt.cancel();
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
