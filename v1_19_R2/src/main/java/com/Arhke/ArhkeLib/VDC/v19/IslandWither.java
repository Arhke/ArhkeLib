package com.Arhke.ArhkeLib.VDC.v19;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class IslandWither extends WitherBoss {
    public IslandWither(Location loc) {
        super(EntityType.WITHER, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0D);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24);
        ServerLevel wrld = ((CraftWorld) loc.getWorld()).getHandle();
        wrld.addFreshEntity(this);

    }
    protected void registerGoals(){
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, false));

    }
//    protected void customServerAiStep() {
//        int i;
//        int viewDistance;
//        double deltaX;
//        double deltaZ;
//        double distanceSquared;
//        if (this.getInvulnerableTicks() > 0) {
//            i = this.getInvulnerableTicks() - 1;
//            this.bossEvent.setProgress(1.0F - (float)i / 220.0F);
//            if (i <= 0) {
//                Explosion.BlockInteraction explosion_effect = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
//                ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 7.0F, false);
//                this.level.getCraftServer().getPluginManager().callEvent(event);
//                if (!event.isCancelled()) {
//                    this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), event.getRadius(), event.getFire(), explosion_effect);
//                }
//
//                if (!this.isSilent()) {
//                    viewDistance = this.level.getCraftServer().getViewDistance() * 16;
//                    Iterator<ServerPlayer> var6 = MinecraftServer.getServer().getPlayerList().players.iterator();
//
//                    label96:
//                    while(true) {
//                        ServerPlayer player;
//                        do {
//                            if (!var6.hasNext()) {
//                                break label96;
//                            }
//
//                            player = var6.next();
//                            deltaX = this.getX() - player.getX();
//                            deltaZ = this.getZ() - player.getZ();
//                            distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
//                        } while(this.level.spigotConfig.witherSpawnSoundRadius > 0 && distanceSquared > (double)(this.level.spigotConfig.witherSpawnSoundRadius * this.level.spigotConfig.witherSpawnSoundRadius));
//
//                        if (distanceSquared > (double)(viewDistance * viewDistance)) {
//                            double deltaLength = Math.sqrt(distanceSquared);
//                            double relativeX = player.getX() + deltaX / deltaLength * (double)viewDistance;
//                            double relativeZ = player.getZ() + deltaZ / deltaLength * (double)viewDistance;
//                            player.connection.send(new ClientboundLevelEventPacket(1023, new BlockPos((int)relativeX, (int)this.getY(), (int)relativeZ), 0, true));
//                        } else {
//                            player.connection.send(new ClientboundLevelEventPacket(1023, this.blockPosition(), 0, true));
//                        }
//                    }
//                }
//            }
//
//            this.setInvulnerableTicks(i);
//            if (this.tickCount % 10 == 0) {
//                this.heal(10.0F, EntityRegainHealthEvent.RegainReason.WITHER_SPAWN);
//            }
//        } else {
//            super.customServerAiStep();
//
//            int j;
//            int i1;
//            for(i = 1; i < 3; ++i) {
//                if (this.tickCount >= this.nextHeadUpdate[i - 1]) {
//                    this.nextHeadUpdate[i - 1] = this.tickCount + 10 + this.random.nextInt(10);
//                    if (this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) {
//                        i1 = i - 1;
//                        viewDistance = this.idleHeadUpdates[i - 1];
//                        this.idleHeadUpdates[i1] = this.idleHeadUpdates[i - 1] + 1;
//                        if (viewDistance > 15) {
//                            float f = 10.0F;
//                            float f1 = 5.0F;
//                            deltaX = Mth.nextDouble(this.random, this.getX() - 10.0D, this.getX() + 10.0D);
//                            deltaZ = Mth.nextDouble(this.random, this.getY() - 5.0D, this.getY() + 5.0D);
//                            distanceSquared = Mth.nextDouble(this.random, this.getZ() - 10.0D, this.getZ() + 10.0D);
//                            this.performRangedAttack(i + 1, deltaX, deltaZ, distanceSquared, true);
//                            this.idleHeadUpdates[i - 1] = 0;
//                        }
//                    }
//
//                    j = this.getAlternativeTarget(i);
//                    if (j > 0) {
//                        LivingEntity entityliving = (LivingEntity)this.level.getEntity(j);
//                        if (entityliving != null && this.canAttack(entityliving) && this.distanceToSqr(entityliving) <= 900.0D && this.hasLineOfSight(entityliving)) {
//                            this.performRangedAttack(i + 1, entityliving);
//                            this.nextHeadUpdate[i - 1] = this.tickCount + 40 + this.random.nextInt(20);
//                            this.idleHeadUpdates[i - 1] = 0;
//                        } else {
//                            this.setAlternativeTarget(i, 0);
//                        }
//                    } else {
//                        List<LivingEntity> list = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
//                        if (!list.isEmpty()) {
//                            LivingEntity entityliving1 = (LivingEntity)list.get(this.random.nextInt(list.size()));
//                            if (!CraftEventFactory.callEntityTargetLivingEvent(this, entityliving1, EntityTargetEvent.TargetReason.CLOSEST_ENTITY).isCancelled()) {
//                                this.setAlternativeTarget(i, entityliving1.getId());
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (this.getTarget() != null) {
//                this.setAlternativeTarget(0, this.getTarget().getId());
//            } else {
//                this.setAlternativeTarget(0, 0);
//            }
//
//            if (this.destroyBlocksTick > 0) {
//                --this.destroyBlocksTick;
//                if (this.destroyBlocksTick == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
//                    i = Mth.floor(this.getY());
//                    j = Mth.floor(this.getX());
//                    i1 = Mth.floor(this.getZ());
//                    boolean flag = false;
//                    int j1 = -1;
//
//                    while(true) {
//                        if (j1 > 1) {
//                            if (flag) {
//                                this.level.levelEvent((Player)null, 1022, this.blockPosition(), 0);
//                            }
//                            break;
//                        }
//
//                        for(int k1 = -1; k1 <= 1; ++k1) {
//                            for(int l1 = 0; l1 <= 3; ++l1) {
//                                int i2 = j + j1;
//                                int j2 = i + l1;
//                                int k2 = i1 + k1;
//                                BlockPos blockposition = new BlockPos(i2, j2, k2);
//                                BlockState iblockdata = this.level.getBlockState(blockposition);
//                                if (canDestroy(iblockdata) && !CraftEventFactory.callEntityChangeBlockEvent(this, blockposition, Blocks.AIR.defaultBlockState()).isCancelled()) {
//                                    flag = this.level.destroyBlock(blockposition, true, this) || flag;
//                                }
//                            }
//                        }
//
//                        ++j1;
//                    }
//                }
//            }
//
//            if (this.tickCount % 20 == 0) {
//                this.heal(1.0F, EntityRegainHealthEvent.RegainReason.REGEN);
//            }
//
//            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
//        }
//
//    }
//    @Override
//    public void a(int i, int j){
////        Bukkit.broadcastMessage("a " + i + " "+j);
//        if (i==0){
//            super.a(i,j);
//            super.a(1,j);
//            super.a(2,j);
//            return;
//        }
//        if((i == 1 || i == 2) && j != 0){
//            return;
//        }
//        super.a(i,j);
//
//
//    }
//    @Override
//    public void dropDeathLoot(boolean flag, int i){
//    }
//    @Override
//    public SoundEffect F() {
//        return null;
//    }
//    @Override
//    public SoundEffect cf() {
//        return null;
//    }


}
