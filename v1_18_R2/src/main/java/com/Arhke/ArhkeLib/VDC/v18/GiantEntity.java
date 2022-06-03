package com.Arhke.ArhkeLib.VDC.v18;

import com.sk89q.worldedit.world.entity.EntityTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Giant;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

import java.util.Objects;

public class GiantEntity extends Giant {
        public GiantEntity(Location loc) {
            super(EntityType.GIANT, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());

            this.setPos(loc.getX(), loc.getY(), loc.getZ());

            this.setCanPickUpLoot(false); // Can Pick up Loot
            this.setAggressive(true); // Aggressive
            this.setCustomNameVisible(true); // Custom Name Visible
            this.setCustomName(new TextComponent(ChatColor.GREEN + "MyEntity")); // Custom Name
        }
    @Override
    public void registerGoals() {
//        this.goalSelector.addGoal(0, new AvoidEntityGoal<Giant>(this, Giant.class, 6f, 1d, 1.2d));
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, ServerPlayer.class, 8.0F));
        this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true, false));
    }

}
