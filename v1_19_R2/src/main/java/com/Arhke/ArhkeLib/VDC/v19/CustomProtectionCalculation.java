package com.Arhke.ArhkeLib.VDC.v19;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class CustomProtectionCalculation {
    protected static float getDamageAfterArmorAbsorb(LivingEntity le, DamageSource damagesource, float f) {
        if (!damagesource.isBypassArmor()) {
            f = CombatRules.getDamageAfterAbsorb(f, (float)le.getArmorValue(), (float)le.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }

        return f;
    }
    protected static float getDamageAfterMagicAbsorb(LivingEntity le, DamageSource damagesource, float f) {
        if (damagesource.isBypassMagic()) {
            return f;
        } else if (f <= 0.0F) {
            return 0.0F;
        } else if (damagesource.isBypassEnchantments()) {
            return f;
        } else {
            int i = getDamageProtection(le.getArmorSlots(), damagesource);
            if (i > 0) {
                f = CombatRules.getDamageAfterMagicAbsorb(f, (float)i);
            }

            return f;
        }
    }
    public static int getDamageProtection(Iterable<net.minecraft.world.item.ItemStack> var0, DamageSource var1) {
        MutableInt var2 = new MutableInt();
        runIterationOnInventory((var2x, var3) -> {
            var2.add(getDamageProtection(var2x, var3, var1));
        }, var0);
        return var2.intValue();
    }
    private static void runIterationOnInventory(EnchantmentVisitor var0, Iterable<net.minecraft.world.item.ItemStack> var1) {

        for (net.minecraft.world.item.ItemStack var3 : var1) {
            runIterationOnItem(var0, var3);
        }
    }
    private static void runIterationOnItem(EnchantmentVisitor var0, net.minecraft.world.item.ItemStack var1) {
        if (!var1.isEmpty()) {
            ListTag var2 = var1.getEnchantmentTags();

            for(int var3 = 0; var3 < var2.size(); ++var3) {
                CompoundTag var4 = var2.getCompound(var3);
                BuiltInRegistries.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId(var4)).ifPresent((var2x) -> {
                    var0.accept(var2x, EnchantmentHelper.getEnchantmentLevel(var4));
                });
            }

        }
    }
    public static int getDamageProtection(net.minecraft.world.item.enchantment.Enchantment enchantment, int var0, DamageSource var1) {
        if(!(enchantment instanceof ProtectionEnchantment )){
            return enchantment.getDamageProtection(var0, var1);
        }
        ProtectionEnchantment pe = (ProtectionEnchantment)enchantment;
        ProtectionEnchantment.Type type = pe.type;
        if (var1.isBypassInvul()) {
            return 0;
        } else if (type == ProtectionEnchantment.Type.ALL) {
            return var0;
        } else if (type == ProtectionEnchantment.Type.FIRE && var1.isFire()) {
            return (int)(var0 * 3.5);
        } else if (type == ProtectionEnchantment.Type.FALL && var1.isFall()) {
            return var0 * 3;
        } else if (type == ProtectionEnchantment.Type.EXPLOSION && var1.isExplosion()) {
            return (int)(var0 * 3.5);
        } else {
            return type == ProtectionEnchantment.Type.PROJECTILE && var1.isProjectile() ? var0 * 3 : 0;
        }
    }
    @FunctionalInterface
    private interface EnchantmentVisitor {
        void accept(net.minecraft.world.item.enchantment.Enchantment var1, int var2);

    }
    public static double customDamageReduction(EntityDamageEvent.DamageCause dc, org.bukkit.entity.LivingEntity entity, double value){
        if(value <= 0){
            return 0d;
        }
        DamageSource ds;
        LivingEntity le = ((CraftLivingEntity)entity).getHandle();
        if (dc == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || dc == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            if(entity instanceof Player && ((Player)entity).isBlocking()){
                return 0d;
            }
             ds = DamageSource.explosion(null);
        } else if (dc == EntityDamageEvent.DamageCause.FIRE || dc == EntityDamageEvent.DamageCause.FIRE_TICK) {
            ds = DamageSource.IN_FIRE;
            if(entity.isInWater()){
                return 0d;
            }
            if (entity.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                value *= 0.8;
            }

        }else if (dc == EntityDamageEvent.DamageCause.MAGIC) {
            ds = DamageSource.MAGIC;
        }else if (dc == EntityDamageEvent.DamageCause.PROJECTILE){
            if(entity instanceof Player && ((Player)entity).isBlocking()){
                return 0d;
            }
            net.minecraft.world.entity.projectile.Arrow arrow =
                    new net.minecraft.world.entity.projectile.Arrow(le.getLevel(),
                            le);
            ds = DamageSource.arrow(arrow, le);
        }else{
            return value;
        }
        float val = (float)value;
        val = getDamageAfterArmorAbsorb(le, ds, val);
        val = getDamageAfterMagicAbsorb(le, ds, val);
        return val;
    }

}
