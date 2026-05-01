package com.Arhke.ArhkeLib.ItemUtil.CustomItem;

import com.Arhke.ArhkeLib.Base.Base;
import com.Arhke.ArhkeLib.Base.MainBase;
import com.Arhke.ArhkeLib.Utils.Pair;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static com.Arhke.ArhkeLib.ItemUtil.CustomItem.CustomProtectionCalculation.getDamageAfterProtection;

@SuppressWarnings("ALL")
public class CustomAttributeListener extends MainBase implements Listener {

    public CustomAttributeListener(JavaPlugin instance) {
        super(instance);
    }

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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            //Ranged Damage
            if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof LivingEntity) {
                Player player = (Player) ((Arrow) event.getDamager()).getShooter();
                double damage = event.getDamage() * Math.max(0, 1 + ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.RANGEDDAMAGE));
                event.setDamage(damage);
            }
            //Deflect
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (Base.randNum(100) < ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DEFLECT) * 100) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 3, 3);
                    displayMarker(ChatColor.BOLD + "" + ChatColor.BLUE + "⛉ Deflected", player.getEyeLocation().add(0, 0.2, 0),
                            player, false, 13, 0.5f, 0.1f, 0.5f);
                    event.setCancelled(true);
                    return;
                }
                double damage = event.getDamage();
                //defense
                damage = Math.max(0.001, damage - ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DEFENSE));
                event.setDamage(damage);
                //DmgCap
                damage = Math.min(damage, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DMGCAP));
                if (damage != 0) {
                    event.setDamage(Math.max(damage, 0.001));
                }
            }


        } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            if (event.getEntity() instanceof Player player) {
                if (event.getDamager() instanceof LivingEntity le && le.getEquipment().getItemInMainHand() != null && le.getEquipment().getItemInMainHand().getType() == Material.MACE && ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.MACERESISTANCE)) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 10, 0.75f);
                    displayMarker(ChatColor.BOLD + "" + ChatColor.GRAY + "⛉ Mace Immune", player.getEyeLocation().add(0, 0.2, 0),
                            player, false, 13, 0.5f, 0.1f, 0.5f);
                    event.setCancelled(true);
                    return;
                }
                //DODGE
                if (Base.randNum(100) < ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DODGE) * 100) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 10, 0.5f);
                    displayMarker(ChatColor.BOLD + "" + ChatColor.BLUE + "⛉ Dodged", player.getEyeLocation().add(0, 0.2, 0),
                            player, false, 13, 0.5f, 0.1f, 0.5f);
                    event.setCancelled(true);
                    return;
                }

                double damage = event.getDamage();
                //defense
                damage = Math.max(0.001, damage - ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DEFENSE));
                event.setDamage(damage);
                //DmgCap
                damage = Math.min(damage, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.DMGCAP));
                if (damage != 0) {
                    event.setDamage(Math.max(damage, 0.001));
                }
            }
            if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
                Player player = (Player) event.getDamager();
                LivingEntity le = (LivingEntity) event.getEntity();
                boolean isBlocking = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0;
                //Valid On-Hit
                if (player.getAttackCooldown() > 0.95 && !isBlocking) {
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITPOISON)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITSLOW)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITWITHER)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 90, 1, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITWEAKNESS)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITBLIND)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITHUNGER)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 5, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITABSORPTION)) {
                        player.setAbsorptionAmount(Math.max(player.getAbsorptionAmount(), 2));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITREGENERATION)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 2, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITCLEANSER)) {
                        le.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                        le.removePotionEffect(PotionEffectType.RESISTANCE);
                        le.removePotionEffect(PotionEffectType.STRENGTH);
                        le.removePotionEffect(PotionEffectType.REGENERATION);
                        le.removePotionEffect(PotionEffectType.ABSORPTION);
                        le.setAbsorptionAmount(0);
                        event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);
                        le.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                        le.removePotionEffect(PotionEffectType.WATER_BREATHING);
                        le.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                        le.removePotionEffect(PotionEffectType.HASTE);
                        le.removePotionEffect(PotionEffectType.JUMP_BOOST);
                        le.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL1)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 100, 0, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL2)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 100, 1, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL3)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 100, 2, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ANTIHEAL4)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 100, 3, false, false));
                    }
                    if (ArmorTags.hasPlayerEffect(player, Attributes.CustomEffects.ONHITHEAL)) {
                        EntityRegainHealthEvent ere = new EntityRegainHealthEvent(player, 2d, EntityRegainHealthEvent.RegainReason.CUSTOM);
                        Bukkit.getPluginManager().callEvent(ere);
                        if (!ere.isCancelled())
                            player.setHealth(Math.min(ere.getAmount() + player.getHealth(), player.getMaxHealth()));
                    }


                }


                //Setup Base Damage
                double damage = event.getDamage();

                //CRIT
                if (isCrit(player)) {
                    damage = Math.max(0, damage + ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.CRITDMG));
                }

                //BREACH
                int breach = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.BREACH);

                float cumulativeDmg = 0, cumulativeArmorRedux = 0, cumulativeProtectionRedux = 0, blockingDmg = 0;
                double val = 0; Pair<Float, Float> pair;
                //Physical
                val = Math.max(0, 1 + ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.PHYSDMG)) * damage;
                pair = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.ENTITY_ATTACK, le, val, breach * 0.15f);
                cumulativeDmg += val; cumulativeArmorRedux += pair.getKey(); cumulativeProtectionRedux += pair.getValue();


                //Blast
                val = Math.max(0, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.BLASTDMG)) * damage;
                pair = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, le, val, breach * 0.15f);
                cumulativeDmg += val; cumulativeArmorRedux += pair.getKey(); cumulativeProtectionRedux += pair.getValue();

                //Proj
                val = Math.max(0, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.PROJDMG)) * damage;
                pair = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.PROJECTILE, le, val, breach * 0.15f);
                cumulativeDmg += val; cumulativeArmorRedux += pair.getKey(); cumulativeProtectionRedux += pair.getValue();

                //Modify Blocking Dmg (pause)
                if (isBlocking) {
                    cumulativeArmorRedux = 0;
                    cumulativeProtectionRedux = 0;
                    event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, -cumulativeDmg);
                }

                //Fire
                val = Math.max(0, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.FIREDMG)) * damage;
                pair = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.FIRE, le, val, breach * 0.15f);
                cumulativeDmg += val; cumulativeArmorRedux += pair.getKey(); cumulativeProtectionRedux += pair.getValue();

                //Magic
                val = Math.max(0, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.MAGICDMG)) * damage;
                pair = CustomProtectionCalculation.customDamageReduction(EntityDamageEvent.DamageCause.MAGIC, le, val, breach * 0.15f);
                cumulativeDmg += val; cumulativeArmorRedux += pair.getKey(); cumulativeProtectionRedux += pair.getValue();
                if (le instanceof Player p)
                    cumulativeProtectionRedux += (val + pair.getKey() + pair.getValue())
                            * Math.min(1, ArmorTags.getPlayerAttribute(p, Attributes.CustomAttributes.MAGICRESIST));

                //Modify Base, Armor, Protection Dmg
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, cumulativeDmg);
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, cumulativeArmorRedux);
                event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, cumulativeProtectionRedux);


                //Resistance (if resistance, reduce damage)
                PotionEffect pe = le.getPotionEffect(PotionEffectType.RESISTANCE);
                double preAbsorbDmg = event.getFinalDamage() - event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION);
                double preResistAndAbsorbDmg = preAbsorbDmg - event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE);
                if (pe != null && preAbsorbDmg > 0) {
                    event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, -preResistAndAbsorbDmg * Math.min(5, pe.getAmplifier()+1) * 0.2);
                }

                //Absorption
                if (!(le instanceof Player) && -event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION) > le.getAbsorptionAmount()) {
                    //special case for mythic mobs blocking damage
                    event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, -preAbsorbDmg);
                } else {
                    event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION,
                            -Math.min(preAbsorbDmg, le.getAbsorptionAmount()));
                }
                Player arhke = Bukkit.getPlayer("Arhke");
                if(arhke != null && arhke.hasPermission("arhkelib.displaydmg")){
                    arhke.sendMessage("===================================");
                    arhke.sendMessage("Damager: " + player.getName() + " Entity: " + le.getName());
                    for(EntityDamageEvent.DamageModifier dm:EntityDamageEvent.DamageModifier.values()){
                        arhke.sendMessage(capitalize(dm.name()) + ": " + event.getOriginalDamage(dm) + " -> " + event.getDamage(dm));
                    }
                    arhke.sendMessage("Final Damage: " + event.getFinalDamage());
                }
                if (event.getFinalDamage() > 0) {

                    double value = ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.LIFESTEAL);
                    if (value != 0) {
                        EntityRegainHealthEvent ere = new EntityRegainHealthEvent(player, Math.min(le.getHealth(), event.getFinalDamage() * value), EntityRegainHealthEvent.RegainReason.CUSTOM);
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
    public void onPotion(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() != EntityPotionEffectEvent.Cause.COMMAND) {
            Player p = (Player) event.getEntity();
            if (event.getNewEffect() != null) {
                if (event.getModifiedType().equals(PotionEffectType.POISON) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.POISONRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.WITHER) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.WITHERRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.INFESTED) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.ANTIHEALRESISTANCE)) {
                    event.setCancelled(true);
                    displayMarker(ChatColor.BOLD + "" + ChatColor.GRAY + "⛨ Anti-Heal Immune", p.getEyeLocation().add(0, 0.2, 0),
                            p, false, 13, 0.5f, 0.25f, 0.5f);
                    return;
                } else if (event.getModifiedType().equals(PotionEffectType.WEAKNESS) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.WEAKNESSRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.SLOWNESS) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.SLOWRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.HUNGER) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.HUNGERRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.BLINDNESS) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.BLINDRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.MINING_FATIGUE) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.FATIGUERESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.LEVITATION) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.LEVITATIONRESISTANCE)) {
                    event.setCancelled(true);
                } else if (event.getModifiedType().equals(PotionEffectType.NAUSEA) && ArmorTags.hasPlayerEffect(p, Attributes.CustomEffects.CONFUSIONRESISTANCE)) {
                    event.setCancelled(true);
                } else {
                    return;
                }
                displayMarker(ChatColor.BOLD + "" + ChatColor.GRAY + "⛨ " + capitalize(event.getModifiedType().getName().replace('_', ' ')) + " Immune", p.getEyeLocation().add(0, 0.2, 0),
                        p, false, 13, 0.5f, 0.25f, 0.5f);
            }
        }

    }

//    @EventHandler
//    public void onAccessory(InventoryClickEvent event){
//        ItemStack current = event.getCurrentItem();
//        ItemStack cursor = event.getCursor();
//        if(current == null || cursor == null || current.getType() == Material.AIR || cursor.getType() == Material.AIR) return;
//        ItemStack newItem = new ItemStack(current);
//        newItem.setAmount(1);
//        NBTItem nbtiCurrent = new NBTItem(newItem);
//        NBTItem nbtiCursor = new NBTItem(cursor);
//        if(!ArmorTags.isAccessory(nbtiCursor) || !ArmorTags.canAttach(nbtiCurrent)) return;
//        ArmorTags at = new ArmorTags(nbtiCurrent);
//        at.addAccessory(nbtiCursor, EquipmentType.getEquipmentType(current.getType()).getSlot());
//        Base.itemAmountMinus(cursor);
//        Base.itemAmountMinus(current);
//        newItem = at.getItem();
//        if (event.getWhoClicked() instanceof Player) {
//            Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
//            assert p != null;
//            Base.addItemToPlayer(p, newItem);
//        }else{
//            event.getWhoClicked().getInventory().addItem(newItem);
//        }
//    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            double healRedux = 1d;
            PotionEffect pe = p.getPotionEffect(PotionEffectType.UNLUCK);
            if (pe != null) {
                if (pe.getAmplifier() == 0) {
                    healRedux = 0.75d;
                } else if (pe.getAmplifier() == 1) {
                    healRedux = 0.6d;
                } else if (pe.getAmplifier() == 2) {
                    healRedux = 0.40d;
                } else if (pe.getAmplifier() >= 3) {
                    healRedux = 0.0d;
                }
            }
            event.setAmount(healRedux * event.getAmount() * (1 + Math.max(-1, ArmorTags.getPlayerAttribute(p, Attributes.CustomAttributes.HEALING))));
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            int change = p.getFoodLevel() - event.getFoodLevel();
            if (change <= 0) return;
            double value = ArmorTags.getPlayerAttribute(p, Attributes.CustomAttributes.HUNGER);
            value = change * Math.max(0.0d, 1d - value);
            change = (int) value;
            if (Math.random() < value) {
                change++;
            }
            change = p.getFoodLevel() - change;
            event.setFoodLevel(change);

        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void damaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if ((event.getCause() == EntityDamageEvent.DamageCause.MAGIC ||
                    event.getCause() == EntityDamageEvent.DamageCause.WITHER ||
                    event.getCause() == EntityDamageEvent.DamageCause.POISON)) {
                event.setDamage(event.getDamage() * (1 - Math.min(1, ArmorTags.getPlayerAttribute(player, Attributes.CustomAttributes.MAGICRESIST))));
            } else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                    event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                    event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                if (event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) {
                    return;
                }
                Pair<Float, Float> pair = CustomProtectionCalculation.customDamageReduction(event.getCause(), player, event.getDamage(), 0);
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, pair.getKey());
                event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, pair.getValue());

                //Resistance (if resistance, reduce damage)
                PotionEffect pe = player.getPotionEffect(PotionEffectType.RESISTANCE);
                double preAbsorbDmg = event.getFinalDamage() - event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION);
                double preResistAndAbsorbDmg = preAbsorbDmg - event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE);
                if (pe != null && preAbsorbDmg > 0) {
                    event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, preResistAndAbsorbDmg * (pe.getAmplifier()+1) * 0.2);
                }

                //Absorption
                event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION,
                        -Math.min(preAbsorbDmg, player.getAbsorptionAmount()));
            }


        }
    }

    private static boolean isCrit(Player player){
        return player.getAttackCooldown() > 0.9F && player.getFallDistance() > 0.0F && !player.isOnGround() && !player.isClimbing() && !player.isInWater() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && !player.isInsideVehicle() && !player.isSprinting();
    }
}
//Magic, Physical, Blast, Fire, Projectile
// (Vampirism, LifeSteal, withering, poisonous, Ice Aspect)