package com.Arhke.ArhkeLib.ItemUtil.CustomItem;

import com.Arhke.ArhkeLib.Utils.Pair;
import net.minecraft.util.Mth;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@SuppressWarnings("ALL")
public class CustomProtectionCalculation {
    public static float getDamageAfterAbsorb(float damage, float armorPen, float armorPoints, float toughness) {
        float effectiveToughness = 2.0F + Math.min(20, toughness) / 4.0F;
        float effectiveArmor = Mth.clamp(armorPoints - damage / effectiveToughness, armorPoints * 0.2F, 20.0F);
        float effectiveReductionPercentage = effectiveArmor / 25.0F;
        float postArmorPenReductionPercentage = Math.max(0, effectiveReductionPercentage - armorPen);
        float var8 = 1.0F - postArmorPenReductionPercentage;
        return damage * var8;
    }

    //Return Armor and Protection dmg reduction
    public static Pair<Float, Float> customDamageReduction(EntityDamageEvent.DamageCause dc, org.bukkit.entity.LivingEntity entity, double dDamage, float armorPen){
        float damage = (float)dDamage;
        if(damage <= 0){
            return new Pair<>(0f,0f);
        }
        float armorRedux = 0, protectionRedux = 0;
        if (dc == EntityDamageEvent.DamageCause.FIRE || dc == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (entity.isInWater() || entity.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                return new Pair<>(0f,-damage);
            }
        }else if (dc == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || dc == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || dc == EntityDamageEvent.DamageCause.PROJECTILE || dc == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                dc == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
            armorRedux = getDamageAfterAbsorb(damage, armorPen, (float)Objects.requireNonNull(entity.getAttribute(Attribute.ARMOR)).getValue(), (float)Objects.requireNonNull(entity.getAttribute(Attribute.ARMOR_TOUGHNESS)).getValue()) - damage;
        }else if (dc != EntityDamageEvent.DamageCause.MAGIC){
            return new Pair<>(0f,0f);
        }
        protectionRedux = getDamageAfterProtection(entity, dc, damage + armorRedux) - damage - armorRedux;
        return new Pair<>(armorRedux, protectionRedux);
    }

    public static float getDamageAfterProtection(org.bukkit.entity.LivingEntity entity, EntityDamageEvent.DamageCause dc, float val) {
        EntityEquipment armor;
        if((armor = entity.getEquipment()) == null){
            return val;
        }
        int otherEnchSum = 0;
        if (dc == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || dc == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            otherEnchSum = iterateInventoryArmorForEnchantment(armor.getArmorContents(), Enchantment.BLAST_PROTECTION);
        } else if (dc == EntityDamageEvent.DamageCause.FIRE || dc == EntityDamageEvent.DamageCause.FIRE_TICK || dc == EntityDamageEvent.DamageCause.LAVA) {
            otherEnchSum = iterateInventoryArmorForEnchantment(armor.getArmorContents(), Enchantment.FIRE_PROTECTION);
        }else if (dc == EntityDamageEvent.DamageCause.MAGIC || dc == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || dc == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            ;
        }else if (dc == EntityDamageEvent.DamageCause.PROJECTILE){
            otherEnchSum = iterateInventoryArmorForEnchantment(armor.getArmorContents(), Enchantment.PROJECTILE_PROTECTION);
        }else{
            return val;
        }
        int protEnchSum = iterateInventoryArmorForEnchantment(armor.getArmorContents(), Enchantment.PROTECTION);
//        val *= Math.max(0.2, 1 - protEnchSum * 0.04f);
//        val *= Math.max(0.2, 1 - otherEnchSum * 0.08f);
        val *= Math.max(0.18, 1 - otherEnchSum * 0.08f-protEnchSum * 0.04f);
        return val;

    }
    private static int iterateInventoryArmorForEnchantment (ItemStack[] armor, Enchantment enchantment){
        int ret = 0;
        for(ItemStack is:armor){
            if(is == null) continue;
            ret += is.getEnchantmentLevel(enchantment);
        }
        return ret;
    }


}