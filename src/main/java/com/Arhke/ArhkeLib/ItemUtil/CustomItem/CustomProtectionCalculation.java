package com.Arhke.ArhkeLib.ItemUtil.CustomItem;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class CustomProtectionCalculation {
    public static float getDamageAfterAbsorb(float damage, DamageSource var1, float armorPoints, float toughness) {
        float var4 = 2.0F + toughness / 4.0F;
        float var5 = Mth.clamp(armorPoints - damage / var4, armorPoints * 0.2F, 20.0F);
        float var6 = var5 / 25.0F;
        float var7 = EnchantmentHelper.calculateArmorBreach(var1.getEntity(), var6);
        float var8 = 1.0F - var7;
        return damage * var8;
    }

    public static float getDamageAfterMagicAbsorb(float damage, float enchantLevel) {
        float var2 = Mth.clamp(enchantLevel, 0.0F, 20.0F);
        return damage * (1.0F - var2 / 25.0F);
    }

    public static int getDamageProtection(Iterable<ItemStack> var0, DamageSource var1) {
        org.apache.commons.lang3.mutable.MutableInt var2 = new MutableInt();
        runIterationOnInventory((var2x, var3) -> var2.add(CustomProtectionCalculation.getDamageProtection(var2x, var3, var1)), var0);
        return var2.intValue();
    }
    private static void runIterationOnInventory(EnchantmentVisitor var0, Iterable<ItemStack> var1) {

        for (ItemStack var3 : var1) {
            runIterationOnItem(var0, var3);
        }

    }
    private static void runIterationOnItem(EnchantmentVisitor var0, ItemStack var1) {
        ItemEnchantments var2 = var1.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> var4 : var2.entrySet()) {
            var0.accept(var4.getKey().value(), var4.getIntValue());
        }

    }
    public static int getDamageProtection(net.minecraft.world.item.enchantment.Enchantment enchantment, int var0, DamageSource var1) {
        if(!(enchantment instanceof ProtectionEnchantment )){
            return enchantment.getDamageProtection(var0, var1);
        }
        ProtectionEnchantment pe = (ProtectionEnchantment)enchantment;
        ProtectionEnchantment.Type type = pe.type;
        if (var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        } else if (type == ProtectionEnchantment.Type.ALL) {
            return var0;
        } else if (type == ProtectionEnchantment.Type.FIRE && var1.is(DamageTypeTags.IS_FIRE)) {
            return (int)(var0 * 3.5);
        } else if (type == ProtectionEnchantment.Type.FALL && var1.is(DamageTypeTags.IS_FALL)) {
            return var0 * 3;
        } else if (type == ProtectionEnchantment.Type.EXPLOSION && var1.is(DamageTypeTags.IS_EXPLOSION)) {
            return (int)(var0 * 3.5);
        } else {
            return type == ProtectionEnchantment.Type.PROJECTILE && var1.is(DamageTypeTags.IS_PROJECTILE) ? var0 * 3 : 0;
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
            ds = le.damageSources().explosion(null);
        } else if (dc == EntityDamageEvent.DamageCause.FIRE || dc == EntityDamageEvent.DamageCause.FIRE_TICK) {
            ds = le.damageSources().inFire();
            if(entity.isInWater()){
                return 0d;
            }
            if (entity.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                value *= 0.8;
            }

        }else if (dc == EntityDamageEvent.DamageCause.MAGIC) {
            ds = le.damageSources().magic();
        }else if (dc == EntityDamageEvent.DamageCause.PROJECTILE){
            if(entity instanceof Player && ((Player)entity).isBlocking()){
                return 0d;
            }
            net.minecraft.world.entity.projectile.Arrow arrow =
                    new net.minecraft.world.entity.projectile.Arrow(EntityType.ARROW, le.level());
            ds = le.damageSources().arrow(arrow, le);
        }else{
            return value;
        }
        float val = (float)value;

        val = getDamageAfterAbsorb(val, ds, (float)Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ARMOR)).getValue(), (float)Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).getValue());
        int protection = getDamageProtection(le.getArmorAndBodyArmorSlots(), ds);
        val = getDamageAfterMagicAbsorb(val, protection);
        return val;
    }

}