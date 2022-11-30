package com.Arhke.ArhkeLib.VDC.v18;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftProjectile;
import org.bukkit.craftbukkit.v1_18_R2.event.CraftEventFactory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Objects;

public class IceologerEntity extends SpellcasterIllager implements RangedAttackMob {
    boolean isCasting = false;
    @SuppressWarnings("ConstantConditions")
    public IceologerEntity(Location loc) {
        super(EntityType.EVOKER, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setCustomNameVisible(true); // Custom Name Visible
        this.setCustomName(new TextComponent(ChatColor.GREEN + "Iceologer")); // Custom Name
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24);
        ServerLevel wrld = ((CraftWorld) loc.getWorld()).getHandle();
        wrld.addFreshEntity(this);

    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
        this.goalSelector.addGoal(3, new IceologerCastingSpellGoal());
        this.goalSelector.addGoal(4, new IceologerDropIceGoal());
//        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));

        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 4.0F, 0.6D, 1.0D, (livingEntity -> this.distanceTo(livingEntity)<9d)));

//        this.goalSelector.addGoal(6, new IceologerDropIceGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 12.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
    }
    public void performRangedAttack(LivingEntity entityliving, float f) {
        Snowball entitysnowball = new Snowball(this.level, this);
        double d0 = entityliving.getEyeY() - 1.100000023841858D;
        double d1 = entityliving.getX() - this.getX();
        double d2 = d0 - entitysnowball.getY();
        double d3 = entityliving.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224D;
        entitysnowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(entitysnowball);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundTag nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    public void addAdditionalSaveData(CompoundTag nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public boolean isAlliedTo(Entity entity) {
        return entity == null ? false : (entity == this ? true : (super.isAlliedTo(entity) ? true : (entity instanceof Vex ? this.isAlliedTo(((Vex)entity).getOwner()) : (entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == MobType.ILLAGER ? this.getTeam() == null && entity.getTeam() == null : false))));
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.EVOKER_HURT;
    }



    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    public void applyRaidBuffs(int i, boolean flag) {
    }

    private class IceologerCastingSpellGoal extends SpellcasterCastingSpellGoal {
        IceologerCastingSpellGoal() {
            super();
        }
        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 100;
        }
        public void tick() {
            if (IceologerEntity.this.getTarget() != null) {
                IceologerEntity.this.getLookControl().setLookAt(IceologerEntity.this.getTarget(), (float)IceologerEntity.this.getMaxHeadYRot(), (float)IceologerEntity.this.getMaxHeadXRot());
            }
        }
    }
    private class IceologerDropIceGoal extends SpellcasterUseSpellGoal {
        IceologerDropIceGoal() {
            super();
        }

        public boolean canUse() {
            return super.canUse();
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 100;
        }
        @SuppressWarnings("ConstantConditions")
        protected void performSpellCasting() {
            LivingEntity entityliving = IceologerEntity.this.getTarget();
            if(entityliving == null){
                return;
            }
            ServerLevel worldserver = (ServerLevel)IceologerEntity.this.level;
            World world = Bukkit.getWorld(worldserver.uuid);
            Location loc =  new Location(world,entityliving.position().x, entityliving.position().y, entityliving.position().z);
            Location x = loc.clone().add(0,5,0);
            Location x1 = x.clone().add(0.5,0,0.5);
            Location x2 = x.clone().add(-0.5,0,0.5);
            Location x3 = x.clone().add(0.5,0,-0.5);
            Location x4 = x.clone().add(-0.5,0,-0.5);
            world.spawnFallingBlock(x1, Bukkit.createBlockData(Material.PACKED_ICE)).setDropItem(false);;
            world.spawnFallingBlock(x2, Bukkit.createBlockData(Material.PACKED_ICE)).setDropItem(false);;
            world.spawnFallingBlock(x3, Bukkit.createBlockData(Material.PACKED_ICE)).setDropItem(false);;
            world.spawnFallingBlock(x4, Bukkit.createBlockData(Material.PACKED_ICE)).setDropItem(false);



        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }
//    private class IceologerDeadlySnowball extends SpellcasterCastingSpellGoal {
//
//        protected int getCastingTime() {
//            return 40;
//        }
//
//        protected int getCastingInterval() {
//            return 100;
//        }
//        @SuppressWarnings("ConstantConditions")
//        protected void performSpellCasting() {
//            LivingEntity entityliving = IceologerEntity.this.getTarget();
//            if(entityliving == null){
//                return;
//            }
//            ServerLevel worldserver = (ServerLevel)IceologerEntity.this.level;
//            World world = Bukkit.getWorld(worldserver.uuid);
//            Location loc =  new Location(world,IceologerEntity.this.getX(), IceologerEntity.this.getY(), IceologerEntity.this.getZ());
//            Projectile snowball = ((CraftProjectile)world.spawnEntity(loc,org.bukkit.entity.EntityType.SNOWBALL)).getHandle();
//            snowball.setOwner(IceologerEntity.this);
//            double d0 = entityliving.getX() - IceologerEntity.this.getX();
//            double d1 = entityliving.getY(0.3333333333333333D) - snowball.getY();
//            double d2 = entityliving.getZ() - IceologerEntity.this.getZ();
//            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
//            snowball.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - IceologerEntity.this.level.getDifficulty().getId() * 4));
//
//            IceologerEntity.this.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F / (IceologerEntity.this.getRandom().nextFloat() * 0.4F + 0.8F));
//
//        }
//
//        protected SoundEvent getSpellPrepareSound() {
//            return SoundEvents.EVOKER_PREPARE_ATTACK;
//        }
//
//        protected IllagerSpell getSpell() {
//            return IllagerSpell.FANGS;
//        }
//    }
}