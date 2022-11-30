package com.Arhke.ArhkeLib.VDC.v18;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

import java.util.Objects;

public class DragonRider extends EnderDragon {
    public DragonRider(Location loc) {
        super(EntityType.ENDER_DRAGON, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setCustomNameVisible(true); // Custom Name Visible
        this.xpReward = 3;
        this.setCustomName(new TextComponent(ChatColor.GREEN + "Dragon Remnant")); // Custom Name
        this.setPos(loc.getX(), loc.getY(), loc.getZ());

    }
    protected boolean canRide(Entity entity) {
        return true;
    }
}
