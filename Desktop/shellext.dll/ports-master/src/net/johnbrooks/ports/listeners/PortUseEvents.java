package net.johnbrooks.ports.listeners;

import java.util.Optional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.johnbrooks.ports.ports.Port;

public class PortUseEvents implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.isCancelled() && event.getTo() != null) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                Optional<Port> optionalPort = Port.getPort(event.getTo());
                Optional<Port> notifiedPort = Port.getPort(event.getFrom());
                if (notifiedPort.isPresent() &&(!optionalPort.isPresent()||!optionalPort.get().equals(notifiedPort.get()))) {
                	notifiedPort.get().notified.remove(event.getPlayer());
                }
                optionalPort.ifPresent(port -> port.useBridge(event.getPlayer()));
            }
        }
    }
    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
    	
    }
}
