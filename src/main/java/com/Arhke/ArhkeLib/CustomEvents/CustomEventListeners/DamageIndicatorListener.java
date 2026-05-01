package com.Arhke.ArhkeLib.CustomEvents.CustomEventListeners;

import com.Arhke.ArhkeLib.Base.MainBase;
import com.Arhke.ArhkeLib.CustomEvents.TrueDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DamageIndicatorListener extends MainBase<JavaPlugin> implements Listener {
    public DamageIndicatorListener(JavaPlugin instance) {
        super(instance);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void ambientDmgEvent (EntityDamageEvent event){
        if (event.getEntity() instanceof Player player) {
            if (event instanceof EntityDamageByEntityEvent edbee) {
                if ((event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) && edbee.getDamager() instanceof Player) {
                    return;
                } else if (edbee.getDamager() instanceof Projectile && ((Projectile) edbee.getDamager()).getShooter() instanceof Player) {
                    return;
                }
            }
            long dmg = (long) (Math.ceil(player.getHealth()) - Math.max(Math.ceil(player.getHealth() - event.getFinalDamage()), 0));
            String health = dmg % 2 == 1 ? ChatColor.BLACK + "❤" : "";
            for (int i = 0; i < dmg / 2; i++) {
                health = "❤" + health;
            }
            setDamageIndicator(player, health, player.getVelocity().clone(), false, false, true);
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void armorstandEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player || event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) ||
                !(event.getEntity() instanceof org.bukkit.entity.LivingEntity) || event.getEntity() instanceof ArmorStand)
            return;
        org.bukkit.entity.LivingEntity entity = (LivingEntity) event.getEntity();
        long dmg = (long) (Math.ceil(entity.getHealth()) - Math.max(Math.ceil(entity.getHealth() - event.getFinalDamage()), 0));
        String health = dmg % 2 == 1 ? ChatColor.BLACK + "❤" : "";
        for (int i = 0; i < dmg / 2; i++) {
            health = "❤" + health;
        }
        setDamageIndicator(entity, health, entity.getVelocity().clone(), event.getDamager() instanceof Player player && isCrit(player));

    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void trueDmgEvent(TrueDamageEvent event) {
        if (event.getDamager() == null ||
                event.getEntity() == null || event.getEntity() instanceof ArmorStand) return;
        LivingEntity entity = event.getEntity();
        int dmg = (int)(Math.min(entity.getHealth(), event.getDamage()));
        String health = dmg % 2 == 1 ? ChatColor.BLACK + "❤" : "";
        for (int i = 0; i < dmg / 2; i++) {
            health = "❤" + health;
        }
        setDamageIndicator(entity, health, entity.getVelocity().clone(), true, false, false);

    }

    public void setDamageIndicator(org.bukkit.entity.LivingEntity entity, String heart, Vector initialVelocity, boolean isCritical) {
        setDamageIndicator(entity, heart, initialVelocity, false, isCritical, false);
    }

    public void setDamageIndicator(org.bukkit.entity.LivingEntity entity, String heart, Vector initialVelocity,
                                   boolean isTrue, boolean isCritical, boolean isEnvironmental) {
        new BukkitRunnable() {
            String hearts = heart;

            @Override
            public void run() {
                if (entity.getVelocity().distance(initialVelocity) > 1) {
                    hearts = "-" + ChatColor.UNDERLINE + hearts;
                } else {
                    hearts = "-" + hearts;
                }
                if(isTrue){
                    hearts = ChatColor.WHITE + hearts + ChatColor.WHITE + "*";
                }else if (isCritical) {
                    hearts = ChatColor.GOLD + hearts + ChatColor.GOLD + "⚔";
                } else if (isEnvironmental) {
                    hearts = ChatColor.DARK_RED + hearts + ChatColor.DARK_RED + "⚠";
                } else {
                    hearts = ChatColor.RED + hearts;
                }
                displayMarker(hearts, entity.getLocation().add(0, 0.5, 0),
                        entity, true, 15, 0.5f, 0.1f, 0.5f);

            }
        }.runTaskLater(getPlugin(), 1);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        int heal = (int) Math.min(event.getAmount(), player.getMaxHealth() - player.getHealth());
        if (heal < 1) return;
        String health = heal % 2 == 1 ? ChatColor.BLACK + "❤" + ChatColor.GREEN + "✔" : "✔";
        for (int i = 0; i < heal / 2; i++) {
            health = "❤" + health;
        }
        displayMarker(ChatColor.BOLD + "" + ChatColor.GREEN + "+" + health, player.getLocation().add(0, 0.5, 0),
                player, true, 15, 0.5f, 0.1f, 0.5f);
    }
    private static boolean isCrit(Player player){
        return player.getAttackCooldown() > 0.9F && player.getFallDistance() > 0.0F && !player.isOnGround() && !player.isClimbing() && !player.isInWater() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && !player.isInsideVehicle() && !player.isSprinting();
    }
}
