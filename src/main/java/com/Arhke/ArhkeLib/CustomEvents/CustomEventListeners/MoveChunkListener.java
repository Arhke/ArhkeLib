package com.Arhke.ArhkeLib.CustomEvents.CustomEventListeners;

import com.Arhke.ArhkeLib.CustomEvents.MoveChunkEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@SuppressWarnings({"unused", "ConstantConditions"})
public class MoveChunkListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getChunk().getX() != event.getFrom().getChunk().getX() || event.getTo().getChunk().getZ() != event.getFrom().getChunk().getZ()) {
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

