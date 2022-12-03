package com.Arhke.ArhkeLib.Lib.CustomEvents;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 *
 * This event is called when a player moves from one chunk to another
 *
 * @author William Lin
 *
 */
public class MoveChunkEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;
    Chunk from, to;
    Player player;

    public MoveChunkEvent(Player player, Chunk from, Chunk to){
        this.player = player;
        this.from = from;
        this.to = to;
    }

    public Player getPlayer() {return this.player;}
    public Chunk getFrom() {
        return this.from;
    }
    public Chunk getTo() {
        return this.to;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    /**
     * Check if event is cancelled.
     * @return true if cancelled
     */
    public boolean isCancelled() {
        return cancel;
    }
    /**
     * Canceling said event.
     * @param cancel
     */
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
