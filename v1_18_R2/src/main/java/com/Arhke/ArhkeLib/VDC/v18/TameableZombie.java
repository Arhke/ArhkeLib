package com.Arhke.ArhkeLib.VDC.v18;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"unused","ConstantConditions"})
public class TameableZombie extends Zombie {
    UUID ownerUUID;
    public TameableZombie(Location loc, UUID owner) {
        super(EntityType.ZOMBIE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.ownerUUID = owner;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24);
        this.getEntity().getScoreboardTags().add("Tamed");

        ServerLevel wrld = ((CraftWorld) loc.getWorld()).getHandle();
        wrld.addFreshEntity(this);

    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new ZombieFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new ZombieOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ZombieOwnerHurtTargetGoal(this));
    }
    public boolean wantsToAttack(LivingEntity entityliving, LivingEntity entityliving1) {
        if (!(entityliving instanceof Creeper) && !(entityliving instanceof Ghast)) {
            if (entityliving instanceof TameableZombie) {
                TameableZombie entityTameableZombie = (TameableZombie)entityliving;
                return !entityTameableZombie.isTame() || entityTameableZombie.getOwner() != entityliving1;
            } else {
                return (!(entityliving instanceof Player) || !(entityliving1 instanceof Player) ||
                        ((Player) entityliving1).canHarmPlayer((Player) entityliving)) && ((!(entityliving instanceof AbstractHorse) ||
                        !((AbstractHorse) entityliving).isTamed()) && (!(entityliving instanceof TamableAnimal) || !((TamableAnimal) entityliving).isTame()));
            }
        } else {
            return false;
        }
    }
    public org.bukkit.entity.Zombie getEntity() {
        return (org.bukkit.entity.Zombie) this.getBukkitEntity();
    }
    public boolean isTame(){
        return true;
    }
    public UUID getOwnerUUID(){
        return this.ownerUUID;
    }
    public LivingEntity getOwner() {
        try {
            UUID var0 = this.getOwnerUUID();
            return var0 == null ? null : this.level.getPlayerByUUID(var0);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }
    public static class ZombieFollowOwnerGoal extends Goal {
        public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
        private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
        private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
        private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
        private final TameableZombie tamable;
        private LivingEntity owner;
        private final LevelReader level;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;
        private float oldWaterCost;
        private final boolean canFly;

        public ZombieFollowOwnerGoal(TameableZombie entitytameableanimal, double d0, float f, float f1, boolean flag) {
            this.tamable = entitytameableanimal;
            this.level = entitytameableanimal.level;
            this.speedModifier = d0;
            this.navigation = entitytameableanimal.getNavigation();
            this.startDistance = f;
            this.stopDistance = f1;
            this.canFly = flag;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(entitytameableanimal.getNavigation() instanceof GroundPathNavigation) && !(entitytameableanimal.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity entityliving = this.tamable.getOwner();
            if (entityliving == null) {
                return false;
            } else if (entityliving.isSpectator()) {
                return false;
            }else if (this.tamable.distanceToSqr(entityliving) < (double)(this.startDistance * this.startDistance)) {
                return false;
            } else {
                this.owner = entityliving;
                return true;
            }
        }

        public boolean canContinueToUse() {
            return !this.navigation.isDone() && (this.tamable.distanceToSqr(this.owner) > (double) (this.stopDistance * this.stopDistance));
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                    if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
                        this.teleportToOwner();
                    } else {
                        this.navigation.moveTo(this.owner, this.speedModifier);
                    }
                }
            }

        }

        private void teleportToOwner() {
            BlockPos blockposition = this.owner.blockPosition();

            for(int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                boolean flag = this.maybeTeleportTo(blockposition.getX() + j, blockposition.getY() + k, blockposition.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean maybeTeleportTo(int i, int j, int k) {
            if (Math.abs((double)i - this.owner.getX()) < 2.0D && Math.abs((double)k - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(i, j, k))) {
                return false;
            } else {
                CraftEntity entity = this.tamable.getBukkitEntity();
                Location to = new Location(entity.getWorld(), (double)i + 0.5D, j, (double)k + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
                EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
                this.tamable.level.getCraftServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                } else {
                    to = event.getTo();
                    this.tamable.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
                    this.navigation.stop();
                    return true;
                }
            }
        }

        private boolean canTeleportTo(BlockPos blockposition) {
            BlockPathTypes pathtype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, blockposition.mutable());
            if (pathtype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState iblockdata = this.level.getBlockState(blockposition.below());
                if (!this.canFly && iblockdata.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockposition1 = blockposition.subtract(this.tamable.blockPosition());
                    return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockposition1));
                }
            }
        }

        private int randomIntInclusive(int i, int j) {
            return this.tamable.getRandom().nextInt(j - i + 1) + i;
        }
    }
    public static class ZombieOwnerHurtByTargetGoal extends TargetGoal {
        private final TameableZombie tameAnimal;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;

        public ZombieOwnerHurtByTargetGoal(TameableZombie entitytameableanimal) {
            super(entitytameableanimal, false);
            this.tameAnimal = entitytameableanimal;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            if (this.tameAnimal.isTame()) {
                LivingEntity entityliving = this.tameAnimal.getOwner();
                if (entityliving == null) {
                    return false;
                } else {
                    this.ownerLastHurtBy = entityliving.getLastHurtByMob();
                    int i = entityliving.getLastHurtByMobTimestamp();
                    return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, entityliving);
                }
            } else {
                return false;
            }
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
            LivingEntity entityliving = this.tameAnimal.getOwner();
            if (entityliving != null) {
                this.timestamp = entityliving.getLastHurtByMobTimestamp();
            }

            super.start();
        }
    }
    public static class ZombieOwnerHurtTargetGoal extends TargetGoal {
        private final TameableZombie tameAnimal;
        private LivingEntity ownerLastHurt;
        private int timestamp;

        public ZombieOwnerHurtTargetGoal(TameableZombie entitytameableanimal) {
            super(entitytameableanimal, false);
            this.tameAnimal = entitytameableanimal;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            if (this.tameAnimal.isTame()) {
                LivingEntity entityliving = this.tameAnimal.getOwner();
                if (entityliving == null) {
                    return false;
                } else {
                    this.ownerLastHurt = entityliving.getLastHurtMob();
                    int i = entityliving.getLastHurtMobTimestamp();
                    return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, entityliving);
                }
            } else {
                return false;
            }
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
            LivingEntity entityliving = this.tameAnimal.getOwner();
            if (entityliving != null) {
                this.timestamp = entityliving.getLastHurtMobTimestamp();
            }

            super.start();
        }
    }


}
