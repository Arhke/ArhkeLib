package com.Arhke.ArhkeLib.VDC.v18;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "ConstantConditions"})
public class TameableEnderMan extends EnderMan {
    UUID ownerUUID;

    public TameableEnderMan(Location loc, UUID owner) {
        super(EntityType.ENDERMAN, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
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


        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EndermanFreezeWhenLookedAt(this));

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new EManFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new EndermanLeaveBlockGoal(this));
        this.goalSelector.addGoal(7, new EndermanTakeBlockGoal(this));
        this.targetSelector.addGoal(1, new EManOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EManOwnerHurtTargetGoal(this));
    }

    public boolean wantsToAttack(LivingEntity entityliving, LivingEntity entityliving1) {
        if (!(entityliving instanceof Creeper) && !(entityliving instanceof Ghast)) {
            if (entityliving instanceof TameableEnderMan) {
                TameableEnderMan entityTameableZombie = (TameableEnderMan) entityliving;
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

    public org.bukkit.entity.Enderman getEntity() {
        return (org.bukkit.entity.Enderman) this.getBukkitEntity();
    }

    public boolean isTame() {
        return true;
    }

    public UUID getOwnerUUID() {
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

    boolean isLookingAtMe(Player entityhuman) {
        ItemStack itemstack = entityhuman.getInventory().armor.get(3);
        if (itemstack.is(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        } else {
            Vec3 vec3d = entityhuman.getViewVector(1.0F).normalize();
            Vec3 vec3d1 = new Vec3(this.getX() - entityhuman.getX(), this.getEyeY() - entityhuman.getEyeY(), this.getZ() - entityhuman.getZ());
            double d0 = vec3d1.length();
            vec3d1 = vec3d1.normalize();
            double d1 = vec3d.dot(vec3d1);
            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    public static class EManFollowOwnerGoal extends Goal {
        public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
        private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
        private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
        private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
        private final TameableEnderMan tamable;
        private LivingEntity owner;
        private final LevelReader level;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;
        private float oldWaterCost;
        private final boolean canFly;

        public EManFollowOwnerGoal(TameableEnderMan entitytameableanimal, double d0, float f, float f1, boolean flag) {
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
            } else if (this.tamable.distanceToSqr(entityliving) < (double) (this.startDistance * this.startDistance)) {
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
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float) this.tamable.getMaxHeadXRot());
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

            for (int i = 0; i < 10; ++i) {
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
            if (Math.abs((double) i - this.owner.getX()) < 2.0D && Math.abs((double) k - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(i, j, k))) {
                return false;
            } else {
                CraftEntity entity = this.tamable.getBukkitEntity();
                Location to = new Location(entity.getWorld(), (double) i + 0.5D, j, (double) k + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
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

    public static class EManOwnerHurtByTargetGoal extends TargetGoal {
        private final TameableEnderMan tameAnimal;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;

        public EManOwnerHurtByTargetGoal(TameableEnderMan entitytameableanimal) {
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

    public static class EManOwnerHurtTargetGoal extends TargetGoal {
        private final TameableEnderMan tameAnimal;
        private LivingEntity ownerLastHurt;
        private int timestamp;

        public EManOwnerHurtTargetGoal(TameableEnderMan entitytameableanimal) {
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

    private static class EndermanTakeBlockGoal extends Goal {
        private final EnderMan enderman;

        public EndermanTakeBlockGoal(EnderMan entityenderman) {
            this.enderman = entityenderman;
        }

        public boolean canUse() {
            return this.enderman.getCarriedBlock() == null && (this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.enderman.getRandom().nextInt(reducedTickDelay(20)) == 0);
        }

        public void tick() {
            Random random = this.enderman.getRandom();
            Level world = this.enderman.level;
            int i = Mth.floor(this.enderman.getX() - 2.0D + random.nextDouble() * 4.0D);
            int j = Mth.floor(this.enderman.getY() + random.nextDouble() * 3.0D);
            int k = Mth.floor(this.enderman.getZ() - 2.0D + random.nextDouble() * 4.0D);
            BlockPos blockposition = new BlockPos(i, j, k);
            BlockState iblockdata = world.getBlockState(blockposition);
            Vec3 vec3d = new Vec3((double) this.enderman.getBlockX() + 0.5D, (double) j + 0.5D, (double) this.enderman.getBlockZ() + 0.5D);
            Vec3 vec3d1 = new Vec3((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            BlockHitResult movingobjectpositionblock = world.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.enderman));
            boolean flag = movingobjectpositionblock.getBlockPos().equals(blockposition);
            if (iblockdata.is(BlockTags.ENDERMAN_HOLDABLE) && flag && !CraftEventFactory.callEntityChangeBlockEvent(this.enderman, blockposition, Blocks.AIR.defaultBlockState()).isCancelled()) {
                world.removeBlock(blockposition, false);
                world.gameEvent(this.enderman, GameEvent.BLOCK_DESTROY, blockposition);
                this.enderman.setCarriedBlock(iblockdata.getBlock().defaultBlockState());
            }

        }
    }

    private static class EndermanLeaveBlockGoal extends Goal {
        private final EnderMan enderman;

        public EndermanLeaveBlockGoal(EnderMan entityenderman) {
            this.enderman = entityenderman;
        }

        public boolean canUse() {
            return this.enderman.getCarriedBlock() != null && (this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.enderman.getRandom().nextInt(reducedTickDelay(2000)) == 0);
        }

        public void tick() {
            Random random = this.enderman.getRandom();
            Level world = this.enderman.level;
            int i = Mth.floor(this.enderman.getX() - 1.0D + random.nextDouble() * 2.0D);
            int j = Mth.floor(this.enderman.getY() + random.nextDouble() * 2.0D);
            int k = Mth.floor(this.enderman.getZ() - 1.0D + random.nextDouble() * 2.0D);
            BlockPos blockposition = new BlockPos(i, j, k);
            BlockState iblockdata = world.getBlockState(blockposition);
            BlockPos blockposition1 = blockposition.below();
            BlockState iblockdata1 = world.getBlockState(blockposition1);
            BlockState iblockdata2 = this.enderman.getCarriedBlock();
            if (iblockdata2 != null) {
                iblockdata2 = net.minecraft.world.level.block.Block.updateFromNeighbourShapes(iblockdata2, this.enderman.level, blockposition);
                if (this.canPlaceBlock(world, blockposition, iblockdata2, iblockdata, iblockdata1, blockposition1) && !CraftEventFactory.callEntityChangeBlockEvent(this.enderman, blockposition, iblockdata2).isCancelled()) {
                    world.setBlock(blockposition, iblockdata2, 3);
                    world.gameEvent(this.enderman, GameEvent.BLOCK_PLACE, blockposition);
                    this.enderman.setCarriedBlock((BlockState) null);
                }
            }

        }

        private boolean canPlaceBlock(Level world, BlockPos blockposition, BlockState iblockdata, BlockState iblockdata1, BlockState iblockdata2, BlockPos blockposition1) {
            return iblockdata1.isAir() && !iblockdata2.isAir() && !iblockdata2.is(Blocks.BEDROCK) && iblockdata2.isCollisionShapeFullBlock(world, blockposition1) && iblockdata.canSurvive(world, blockposition) && world.getEntities(this.enderman, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(blockposition))).isEmpty();
        }
    }


    private static class EndermanFreezeWhenLookedAt extends Goal {
        private final TameableEnderMan enderman;
        @Nullable
        private LivingEntity target;

        public EndermanFreezeWhenLookedAt(TameableEnderMan entityenderman) {
            this.enderman = entityenderman;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            this.target = this.enderman.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d0 = this.target.distanceToSqr(this.enderman);
                return !(d0 > 256.0D) && this.enderman.isLookingAtMe((Player) this.target);
            }
        }

        public void start() {
            this.enderman.getNavigation().stop();
        }

        public void tick() {
            this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }


}

