package com.Arhke.ArhkeLib.Lib.CustomEvents;

import com.Arhke.ArhkeLib.ArhkeLib;
import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEventListener extends MainBase<JavaPlugin> implements Listener {
    public CustomEventListener(ArhkeLib instance) {
        super(instance);
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.getTo().getChunk().getX() != event.getFrom().getChunk().getX() || event.getTo().getChunk().getZ() != event.getFrom().getChunk().getZ()){
            MoveChunkEvent mce = new MoveChunkEvent(event.getPlayer(), event.getFrom().getChunk(), event.getTo().getChunk());
            Bukkit.getServer().getPluginManager().callEvent(mce);
            event.setCancelled(mce.isCancelled());
        }
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo().getChunk().getX() != event.getFrom().getChunk().getX() || event.getTo().getChunk().getZ() != event.getFrom().getChunk().getZ()) {
            MoveChunkEvent mce = new MoveChunkEvent(event.getPlayer(), event.getFrom().getChunk(), event.getTo().getChunk());
            Bukkit.getServer().getPluginManager().callEvent(mce);
            event.setCancelled(mce.isCancelled());
        }
    }
}
