package com.Arhke.ArhkeLib.Lib.ItemUtil.CustomItem;
import com.Arhke.ArhkeLib.Lib.Base.Base;
import com.Arhke.ArhkeLib.Lib.ItemUtil.EquipmentType;
import com.Arhke.ArhkeLib.VDC.v19.CustomProtectionCalculation;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class CustomAttributeListener implements Listener {
    @EventHandler
    public void durabilityEvent(PlayerItemDamageEvent event) {
        NBTItem nbti = new NBTItem(event.getItem());
        double value = ArmorTags.getCustomTagDouble(nbti, ArmorTags.Slot.MAINHAND.getCode(), Attributes.CustomAttributes.DURABILITY);
        value = event.getDamage() / Math.max(0.0001d, 1d + value);
        event.setDamage((int) value);
        value = value - (int) value;
        if (Math.random() < value) {
            event.setDamage(event.getDamage() + 1);
        }

    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            //Ranged Damage
            //Deflect
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (Base.randNum(100) < ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DEFLECT)*100) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 3, 3);
                    event.setCancelled(true);
                    return;
                }
                double damage = event.getDamage();
                //defense
                damage = Math.max(0.001, damage - ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DEFENSE));
                event.setDamage(damage);
            }
            if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof LivingEntity) {
                Player player =     (Player) ((Arrow) event.getDamager()).getShooter();
                double damage = event.getDamage() * Math.max(0,1 + ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.RANGEDDAMAGE));
                //toughness
                double toughness = ((LivingEntity)event.getEntity()).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
                damage *= (1f - Math.min(Math.max((damage * toughness - 120) / (damage * toughness + 0.0000001), 0f), 0.9f));
                event.setDamage(damage);
            }

        } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            //DODGE
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (Base.randNum(100) < ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DODGE)*100) {
                    player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_IN, 3, 3);
                    event.setCancelled(true);
                    return;
                }
                double damage = event.getDamage();
                //defense
                damage = Math.max(0.001, damage - ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DEFENSE));
                event.setDamage(damage);
            }
            if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
                Player player = (Player) event.getDamager();
                LivingEntity le = (LivingEntity) event.getEntity();
                //Valid On-Hit
                if(player.getAttackCooldown()>0.95 && (!(event.getEntity() instanceof Player) || !((Player)event.getEntity()).isBlocking())) {
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITPOISON)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITSLOW)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITWITHER)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 90, 1, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITWEAKNESS)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60,0, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITBLIND)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60,0, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITHUNGER)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 5, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITABSORPTION)){
                        player.setAbsorptionAmount(Math.max(player.getAbsorptionAmount(), 2));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITCLEANSER)){
                        le.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                        le.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        le.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        le.removePotionEffect(PotionEffectType.REGENERATION);
                        le.removePotionEffect(PotionEffectType.ABSORPTION);
                        le.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                        le.removePotionEffect(PotionEffectType.WATER_BREATHING);
                        le.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                        le.removePotionEffect(PotionEffectType.FAST_DIGGING);
                        le.removePotionEffect(PotionEffectType.JUMP);
                        le.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL1)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120,0, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL2)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120,1, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL3)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120,2, false, false));
                    }if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL4)){
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120,3, false, false));
                    }
                    if(ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITHEAL)){
                        EntityRegainHealthEvent ere = new EntityRegainHealthEvent(player,1d, EntityRegainHealthEvent.RegainReason.CUSTOM);
                        Bukkit.getPluginManager().callEvent(ere);
                        if(!ere.isCancelled())
                            player.setHealth(Math.min(ere.getAmount() + player.getHealth(), player.getMaxHealth()));
                    }
                    //CRIT
                    if (player.getFallDistance() > 0.0F && !player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                        event.setDamage(Math.max(0.01, event.getDamage() + ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.CRITDMG)));
                    }
                }
                double damage = event.getDamage();
                //toughness
                double toughness = Objects.requireNonNull(le.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).getValue();
                damage *= (1f - Math.min(Math.max((damage * toughness - 120) / (damage * toughness + 0.0000001), 0f), 0.8f));
                //physical
                double val = (1 + ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.PHYSDMG)) * damage;
                event.setDamage(val);
                //blast
                val = ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.BLASTDMG) * damage;
                val = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, le, val);
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, val + event.getDamage(EntityDamageEvent.DamageModifier.BASE));
                //Proj
                val = ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.PROJDMG) * damage;
                val = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.PROJECTILE, le, val);
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, val + event.getDamage(EntityDamageEvent.DamageModifier.BASE));
                //Fire
                val = ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.FIREDMG) * damage;
                val = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.FIRE, le, val);
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, val + event.getDamage(EntityDamageEvent.DamageModifier.BASE));
                //Magic
                val = ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.MAGICDMG) * damage;
                val = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.MAGIC, le, val);
                if(le instanceof Player) val = val * (1 - Math.min(1, ArmorTags.getPlayerAttribute((Player)le, Attributes.CustomAttributes.MAGICRESIST)));
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, val + event.getDamage(EntityDamageEvent.DamageModifier.BASE));
                if(event.getFinalDamage() != 0){
                    double value = ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.LIFESTEAL);
                    if(value != 0) {
                        EntityRegainHealthEvent ere = new EntityRegainHealthEvent(player, event.getFinalDamage()*value, EntityRegainHealthEvent.RegainReason.CUSTOM);
                        Bukkit.getPluginManager().callEvent(ere);
                        if (!ere.isCancelled())
                            player.setHealth(Math.min(ere.getAmount() + player.getHealth(), player.getMaxHealth()));
                    }

                }
            }
            if (event.getDamage() <= 0) {
                event.setDamage(0.001);
            }


        }
    }
    @EventHandler
    public void onPotion(EntityPotionEffectEvent event){
        if(event.getEntity() instanceof Player && event.getCause() != EntityPotionEffectEvent.Cause.COMMAND){
            Player p = (Player) event.getEntity();
            if(event.getNewEffect() != null){
                if(event.getModifiedType().equals(PotionEffectType.POISON) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.POISONRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }
                if(event.getModifiedType().equals(PotionEffectType.WITHER) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.WITHERRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }
                if(event.getModifiedType().equals(PotionEffectType.BAD_OMEN) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.ANTIHEALRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }if(event.getModifiedType().equals(PotionEffectType.WEAKNESS) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.WEAKNESSRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }
                if(event.getModifiedType().equals(PotionEffectType.SLOW) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.SLOWRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }if(event.getModifiedType().equals(PotionEffectType.HUNGER) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.HUNGERRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }if(event.getModifiedType().equals(PotionEffectType.BLINDNESS) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.BLINDRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }if(event.getModifiedType().equals(PotionEffectType.SLOW_DIGGING) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.FATIGUERESISTANCE)){
                    event.setCancelled(true);
                    return;
                }if(event.getModifiedType().equals(PotionEffectType.LEVITATION) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.LEVITATIONRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }if(event.getModifiedType().equals(PotionEffectType.CONFUSION) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.CONFUSIONRESISTANCE)){
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onAccessory(InventoryClickEvent event){
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if(current == null || cursor == null || current.getType() == Material.AIR || cursor.getType() == Material.AIR) return;
        ItemStack newItem = new ItemStack(current);
        newItem.setAmount(1);
        NBTItem nbtiCurrent = new NBTItem(newItem);
        NBTItem nbtiCursor = new NBTItem(cursor);
        if(!ArmorTags.isAccessory(nbtiCursor) || !ArmorTags.canAttach(nbtiCurrent)) return;
        ArmorTags at = new ArmorTags(nbtiCurrent);
        at.addAccessory(nbtiCursor, EquipmentType.getEquipmentType(current.getType()).getSlot());
        Base.itemAmountMinus(cursor);
        Base.itemAmountMinus(current);
        newItem = at.getItem();
        if (event.getWhoClicked() instanceof Player) {
            Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
            assert p != null;
            Base.addItemToPlayer(p, newItem);
        }else{
            event.getWhoClicked().getInventory().addItem(newItem);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHeal(EntityRegainHealthEvent event){
        if(event.getEntity() instanceof Player){
            Player p = (Player)event.getEntity();
            double healRedux = 1d;
            PotionEffect pe = p.getPotionEffect(PotionEffectType.BAD_OMEN);
            if(pe != null) {
                if (pe.getAmplifier() == 0) {
                    healRedux = 0.75d;
                } else if (pe.getAmplifier() == 1) {
                    healRedux = 0.50d;
                } else if (pe.getAmplifier() == 2) {
                    healRedux = 0.25d;
                } else if (pe.getAmplifier() >= 3) {
                    healRedux = 0.0d;
                }
            }
            event.setAmount(healRedux * event.getAmount() * (1 + Math.max(-1, ArmorTags.getPlayerAttribute(p, Attributes.CustomAttributes.HEALING))));
        }
    }
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        if(event.getEntity() instanceof Player){
            Player p = (Player)event.getEntity();
            int change = p.getFoodLevel()-event.getFoodLevel();
            if(change <=0)return;
            double value = ArmorTags.getPlayerAttribute(p, Attributes.CustomAttributes.HUNGER);
            value = change*Math.max(0.0d, 1d-value);
            change = (int)value;
            if(Math.random() < value){
                change++;
            }
            change = p.getFoodLevel()-change;
            event.setFoodLevel(change);

        }
    }
    @EventHandler
    public void damaged(EntityDamageEvent event){
        if ((event.getCause() == EntityDamageEvent.DamageCause.MAGIC ||
                event.getCause() == EntityDamageEvent.DamageCause.WITHER ||
                event.getCause() == EntityDamageEvent.DamageCause.POISON) && event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            event.setDamage(event.getDamage() * (1 - Math.min(1, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.MAGICRESIST))));
        }
    }
}
//Magic, Physical, Blast, Fire, Projectile
// (Vampirism, LifeSteal, withering, poisonous, Ice Aspect)