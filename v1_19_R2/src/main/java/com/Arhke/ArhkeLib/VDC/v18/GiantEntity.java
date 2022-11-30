package com.Arhke.ArhkeLib.VDC.v18;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

import java.util.Objects;

public class GiantEntity extends Giant {
    @SuppressWarnings("ConstantConditions")
    public GiantEntity(Location loc) {
        super(EntityType.GIANT, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setCanPickUpLoot(false); // Can Pick up Loot
        this.setAggressive(true); // Aggressive
        this.setCustomNameVisible(true); // Custom Name Visible
        this.setCustomName(new TextComponent(ChatColor.GREEN + "Fee Fi Fo Fum")); // Custom Name
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(50);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(50);

        this.setHealth(100);
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        ServerLevel wrld = ((CraftWorld) loc.getWorld()).getHandle();
        wrld.addFreshEntity(this);

    }


    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new GiantAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        if (this.level.spigotConfig.zombieAggressiveTowardsVillager) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        }

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
    static class GiantAttackGoal extends MeleeAttackGoal {
        private final GiantEntity zombie;
        private int raiseArmTicks;

        public GiantAttackGoal(GiantEntity var0, double var1, boolean var3) {
            super(var0, var1, var3);
            this.zombie = var0;
        }

        public void start() {
            super.start();
            this.raiseArmTicks = 0;
        }

        public void stop() {
            super.stop();
            this.zombie.setAggressive(false);
        }

        public void tick() {
            super.tick();
            ++this.raiseArmTicks;
            this.zombie.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2);

        }
    }

}
