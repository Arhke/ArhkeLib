package com.Arhke.ArhkeLib.ItemUtil.CustomItem;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemFlagCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("arhkelib.admin")) {
            player.sendMessage("§cYou do not have permission to use this.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cUsage: /itemflag <Flag/UNBREAKABLE>");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || item.getItemMeta() == null) {
            player.sendMessage("§cYou must be holding a valid item!");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        String target = args[0].toUpperCase();

        // Special check for Unbreakable since it isn't an ItemFlag in Bukkit, it's a direct boolean
        if (target.equals("UNBREAKABLE")) {
            boolean currentState = meta.isUnbreakable();
            meta.setUnbreakable(!currentState);
            item.setItemMeta(meta);
            player.sendMessage("§aToggled Unbreakable state to: §e" + !currentState);
            return true;
        }

        // Handle standard ItemFlags
        ItemFlag flagToToggle;
        try {
            flagToToggle = ItemFlag.valueOf(target);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid Flag! Use Tab-Complete.");
            return true;
        }

        if (meta.hasItemFlag(flagToToggle)) {
            meta.removeItemFlags(flagToToggle);
            player.sendMessage("§cRemoved §e" + flagToToggle.name() + " §cfrom item.");
        } else {
            meta.addItemFlags(flagToToggle);
            player.sendMessage("§aAdded §e" + flagToToggle.name() + " §ato item.");
        }

        item.setItemMeta(meta);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String search = args[0].toUpperCase();

            // Add native ItemFlags
            for (ItemFlag flag : ItemFlag.values()) {
                if (flag.name().startsWith(search)) {
                    completions.add(flag.name());
                }
            }

            // Add Unbreakable option
            if ("UNBREAKABLE".startsWith(search)) {
                completions.add("UNBREAKABLE");
            }
        }

        return completions;
    }
}