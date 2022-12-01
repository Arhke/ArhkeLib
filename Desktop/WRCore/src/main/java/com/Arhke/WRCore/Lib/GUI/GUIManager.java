package com.Arhke.WRCore.Lib.GUI;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Main;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class GUIManager extends MainBase {
    public HashMap<UUID, InventoryGui> guiMap = new HashMap<>();
    public GUIManager(Main instance){
        super(instance);
    }
    public void add(Player player, InventoryGui gui){
        guiMap.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }
    public InventoryGui remove(UUID uuid){
        return guiMap.remove(uuid);

    }
    @Nullable
    public InventoryGui get(UUID uuid){
        return guiMap.get(uuid);
    }
}
