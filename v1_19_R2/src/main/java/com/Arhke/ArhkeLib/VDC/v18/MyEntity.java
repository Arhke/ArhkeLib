package com.Arhke.ArhkeLib.VDC.v18;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Skeleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

public class MyEntity extends Skeleton {

    public MyEntity(Location loc) {
        super(EntityType.SKELETON, ((CraftWorld) loc.getWorld()).getHandle());

        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        this.setCanPickUpLoot(false); // Can Pick up Loot
        this.setAggressive(true); // Aggressive
        this.setCustomNameVisible(true); // Custom Name Visible
        this.setCustomName(new TextComponent(ChatColor.GREEN + "MyEntity")); // Custom Name
        ServerLevel wrld = ((CraftWorld) loc.getWorld()).getHandle();
        wrld.addFreshEntity(this);
        for (WrappedGoal availableGoal : this.goalSelector.getAvailableGoals()) {
            Bukkit.broadcastMessage(availableGoal.getClass().getSimpleName());
        }

    }
//    @Override
//    public void registerGoals() {
//        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Skeleton.class, 6f, 1d, 1.2d));
//        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, ServerPlayer.class, 8.0F));
//        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true, false));
//    }

}