package com.Arhke.ArhkeLib.Hook;

import com.Arhke.ArhkeLib.ArhkeLib;
import net.ess3.api.events.UserBalanceUpdateEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("ALL")
public class PredictiveXpSync implements Listener {

    private final Economy econ;

    // Make sure to pass your main plugin instance when initializing this!
    // Example: new PredictiveXpSync(this, econ);
    public PredictiveXpSync( Economy econ) {
        this.econ = econ;
    }

    // --- 1. Master Controller: Essentials -> XP ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEconomyChange(UserBalanceUpdateEvent event) {
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;

        int targetXp = event.getNewBalance().intValue();

        if (getRealTotalExperience(player) != targetXp) {
            setRealTotalExperience(player, targetXp);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int targetXp = (int) econ.getBalance(player);

        if (getRealTotalExperience(player) != targetXp) {
            setRealTotalExperience(player, targetXp);
        }
    }

    // --- 2. Death Synchronization ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int currentBal = (int) econ.getBalance(player);

        int xpToKeep = event.getNewTotalExp();

        // Wipe the Vault balance to match the death loss
        if (currentBal > xpToKeep) {
            econ.withdrawPlayer(player, currentBal - xpToKeep);
        } else if (currentBal < xpToKeep) {
            econ.depositPlayer(player, xpToKeep - currentBal);
        }
    }

    // --- 3. Predictive Gains (XP Orbs) ---

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExpGain(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int amount = event.getAmount();

        event.setAmount(0);
        if (amount > 0) {
            econ.depositPlayer(player, amount);
        } else if (amount < 0) {
            econ.withdrawPlayer(player, Math.abs(amount));
        }
    }

    // --- 4. Flat-Rate Drains (Enchanting & Anvils) ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        int costLevels = event.getExpLevelCost();

        // Flat rate: Calculates the XP cost exclusively from level 0 up to the cost amount.
        int flatXpCost = getExpAtLevel(costLevels) - getExpAtLevel(Math.max(0, costLevels - event.whichButton()-1));

        if (flatXpCost > 0) {
            econ.withdrawPlayer(player, flatXpCost);
        }
        scheduleUpdate(player);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnvilUse(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof AnvilInventory && event.getSlotType() == InventoryType.SlotType.RESULT) {
            ItemStack result = event.getCurrentItem();

            if (result != null && result.getType() != Material.AIR) {
                Player player = (Player) event.getWhoClicked();
                int costLevels = ((AnvilInventory) event.getClickedInventory()).getRepairCost();
                int levelBefore = player.getLevel();

                // Wait 1 server tick to see if they actually successfully picked up the item
                int flatXpCost = getExpAtLevel(costLevels);

                if (flatXpCost > 0 && player.getGameMode() != GameMode.CREATIVE) {
                    econ.withdrawPlayer(player, flatXpCost);
                }
                scheduleUpdate(player);
            }
        }
    }

    // --- Visual XP Math Helpers ---

    private int getRealTotalExperience(Player player) {
        return getExpAtLevel(player.getLevel()) + Math.round(player.getExp() * player.getExpToLevel());
    }

    private int getExpAtLevel(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + (6 * level));
        } else if (level <= 31) {
            return (int) ((2.5 * Math.pow(level, 2)) - (40.5 * level) + 360);
        } else {
            return (int) ((4.5 * Math.pow(level, 2)) - (162.5 * level) + 2220);
        }
    }
    public void scheduleUpdate(final Player player){
        new BukkitRunnable(){
            @Override
            public void run() {
                setRealTotalExperience(player, (int)econ.getBalance(player));
            }
        }.runTaskLater(ArhkeLib.plugin, 0L);
    }
    private void setRealTotalExperience(Player player, int amount) {
        if (amount < 0) amount = 0;

        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);

        while (amount > 0) {
            int expToLevel = player.getExpToLevel() - Math.round(player.getExp() * player.getExpToLevel());
            if (amount >= expToLevel) {
                amount -= expToLevel;
                player.giveExp(expToLevel);
            } else {
                player.giveExp(amount);
                amount = 0;
            }
        }
    }
}