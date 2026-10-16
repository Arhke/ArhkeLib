package com.Arhke.ArhkeLib.ItemUtil.CustomItem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddAttributeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(tcm("&cOnly players can use this command."));
            return true;
        }

        if (!player.hasPermission("kitabilities.admin")) {
            player.sendMessage(tcm("&cYou do not have permission to use this."));
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(tcm("&cUsage: /addattribute <Attribute> <Slot> <Operation> <Strength>"));
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(tcm("&cYou must be holding an item in your main hand!"));
            return true;
        }

        Attributes attribute = Attributes.getAttribute(args[0].toUpperCase());
        if (attribute == null) {
            player.sendMessage(tcm("&cInvalid Attribute! Use Tab-Complete to find valid names."));
            return true;
        }

        ArmorTags.Slot slot;
        try {
            slot = ArmorTags.Slot.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(tcm("&cInvalid Slot! Valid slots: " + Arrays.toString(ArmorTags.Slot.values())));
            return true;
        }

        ArmorTags.Operations operation;
        try {
            operation = ArmorTags.Operations.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(tcm("&cInvalid Operation!"));
            return true;
        }

        double strength;
        try {
            strength = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(tcm("&cStrength must be a number!"));
            return true;
        }

        ArmorTags tags = new ArmorTags(itemInHand);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        try {
            tags.applyTag(uniqueId, attribute, slot, operation, strength);

            ItemStack modifiedItem = tags.getItem();
            ItemMeta meta = modifiedItem.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            // Generate the new lore line
            String loreLine = generateLoreLine(attribute, strength, slot);

            // Insert it strictly into the 5-step hierarchy
            if (loreLine != null && !loreLine.isEmpty()) {
                insertLoreLine(lore, loreLine, attribute, slot);
            }

            meta.setLore(lore);
            modifiedItem.setItemMeta(meta);

            player.getInventory().setItemInMainHand(modifiedItem);
            player.sendMessage(tcm("&aSuccessfully applied &e" + attribute.getVerbose() + " &ato the held item!"));

        } catch (Exception e) {
            player.sendMessage(tcm("&cAn error occurred applying the tag."));
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Parses the current lore and injects the new line into the strict hierarchy:
     * 1. Slot Header
     * 2. Mainhand Exception Stats
     * 3. Vanilla/Custom Stats
     * 4. Incantations
     * 5. Abilities
     */
    private void insertLoreLine(List<String> lore, String line, Attributes attribute, ArmorTags.Slot slot) {
        boolean isAbility = attribute instanceof Attributes.CustomEffects;
        boolean isIncantation = isIncantationAttribute(attribute);
        boolean isMainHandException = slot == ArmorTags.Slot.MAINHAND &&
                (attribute == Attributes.MCAttributes.ATTACKDAMAGE || attribute == Attributes.MCAttributes.ATTACKSPEED);

        // 1. Establish Headers
        String preposition = (slot == ArmorTags.Slot.MAINHAND || slot == ArmorTags.Slot.OFFHAND) ? "in" : "on";
        String slotHeader = ChatColor.GRAY + "When " + preposition + " " + slot.getName() + ":";
        String incHeader = tcm("&7---[ &6Incantations &7]----");
        String abHeader = tcm("&7-----[ &6Abilities &7]------");

        // Ensure Slot Header exists
        int slotIdx = lore.indexOf(slotHeader);
        if (slotIdx == -1) {
            if (!lore.isEmpty()) lore.add("");
            lore.add(slotHeader);
            slotIdx = lore.size() - 1;
        }

        int incIdx = lore.indexOf(incHeader);
        int abIdx = lore.indexOf(abHeader);
        int insertIdx = -1;

        // 2. Determine exact insertion index based on hierarchy
        if (isMainHandException) {
            // Main hand attack damage/speed go immediately under the slot header
            insertIdx = slotIdx + 1;
            while (insertIdx < lore.size() && lore.get(insertIdx).startsWith(ChatColor.DARK_GREEN.toString())) {
                insertIdx++;
            }
        } else if (isIncantation) {
            if (incIdx == -1) {
                // Determine where to create the Incantation Header (before abilities, or at end)
                insertIdx = (abIdx != -1) ? (abIdx > 0 && lore.get(abIdx - 1).isEmpty() ? abIdx - 1 : abIdx) : lore.size();
                if (insertIdx > 0 && !lore.get(insertIdx - 1).isEmpty()) lore.add(insertIdx++, "");
                lore.add(insertIdx++, incHeader);
                incIdx = insertIdx - 1;
                if (abIdx != -1) abIdx += 2; // Shift abilities index down if we added lines
            }
            // Insert at the bottom of the incantations list
            insertIdx = incIdx + 1;
            while (insertIdx < lore.size() && !lore.get(insertIdx).equals(abHeader) && !lore.get(insertIdx).isEmpty()) {
                insertIdx++;
            }
        } else if (isAbility) {
            if (abIdx == -1) {
                // Create Abilities Header at the very bottom
                insertIdx = lore.size();
                if (insertIdx > 0 && !lore.get(insertIdx - 1).isEmpty()) lore.add(insertIdx++, "");
                lore.add(insertIdx++, abHeader);
            }
            insertIdx = lore.size();
        } else {
            // Standard Vanilla/Custom Stat (Goes under Mainhand stats, but before Incantations/Abilities)
            if (incIdx != -1) {
                insertIdx = incIdx;
            } else if (abIdx != -1) {
                insertIdx = abIdx;
            } else {
                insertIdx = lore.size();
            }
            // If there's an empty spacer line before the next header, insert above the spacer
            if (insertIdx > 0 && lore.get(insertIdx - 1).isEmpty()) {
                insertIdx--;
            }
        }

        lore.add(insertIdx, line);
    }

    private String generateLoreLine(Attributes attribute, double strength, ArmorTags.Slot slot) {
        if (slot == ArmorTags.Slot.MAINHAND) {
            if (attribute == Attributes.MCAttributes.ATTACKDAMAGE) {
                return ChatColor.DARK_GREEN + " " + (strength == (long)strength ? (long)strength+1 : strength+1) + " Attack Damage";
            } else if (attribute == Attributes.MCAttributes.ATTACKSPEED) {
                double speedVal = 4.0 + strength;
                String speedStr = speedVal == (long) speedVal ? String.valueOf((long) speedVal) : String.format("%.2f", speedVal);
                return ChatColor.DARK_GREEN + " " + speedStr + " Attack Speed";
            }
        }

        if (attribute instanceof Attributes.CustomEffects) {
            return tcm("&7- " + attribute.getVerbose());
        }

        boolean isPercentage = isPercentageAttribute(attribute);
        if(attribute == Attributes.MCAttributes.SPEED) strength *= 10;
        return loreAssist(attribute.getVerbose(), strength, true, isPercentage);
    }

    private String loreAssist(String display, double strength, boolean displayNum, boolean percentage) {
        if (strength == 0) return "";
        String add = strength < 0 ? tcm("&c-") : tcm("&9+");

        if (displayNum) {
            if (percentage) {
                add += Math.round(Math.abs(strength * 100)) + "% ";
            } else {
                if (strength == (long) strength) add += (long) Math.abs(strength) + " ";
                else add += String.format("%.2f", Math.abs(strength)) + " ";
            }
        }
        add += tcm(display);
        return add;
    }

    private boolean isPercentageAttribute(Attributes attribute) {
        if (attribute instanceof Attributes.MCAttributes mc) {
            return mc == Attributes.MCAttributes.SPEED || mc == Attributes.MCAttributes.KNOCKBACKRESIST;
        } else if (attribute instanceof Attributes.CustomAttributes ca) {
            return switch (ca) {
                case RANGEDDAMAGE, DODGE, DEFLECT, HEALING, DURABILITY, MAGICRESIST, PHYSDMG, MAGICDMG, FIREDMG, PROJDMG, BLASTDMG,
                     LIFESTEAL, HUNGER -> true;
                default -> false;
            };
        }
        return false;
    }

    private boolean isIncantationAttribute(Attributes attribute) {
        if (attribute instanceof Attributes.CustomAttributes) {
            Attributes.CustomAttributes ca = (Attributes.CustomAttributes) attribute;
            switch (ca) {
                case PHYSDMG: case MAGICDMG: case FIREDMG: case BLASTDMG: case PROJDMG: return true;
                default: return false;
            }
        }
        return false;
    }

    private String tcm(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> all = new ArrayList<>();
            for (Attributes.MCAttributes attr : Attributes.MCAttributes.values()) all.add(attr.name());
            for (Attributes.CustomAttributes attr : Attributes.CustomAttributes.values()) all.add(attr.name());
            for (Attributes.CustomEffects attr : Attributes.CustomEffects.values()) all.add(attr.name());
            completions.addAll(all.stream().filter(a -> a.startsWith(args[0].toUpperCase())).collect(Collectors.toList()));
        } else if (args.length == 2) {
            for (ArmorTags.Slot slot : ArmorTags.Slot.values()) if (slot.name().startsWith(args[1].toUpperCase())) completions.add(slot.name());
        } else if (args.length == 3) {
            for (ArmorTags.Operations op : ArmorTags.Operations.values()) if (op.name().startsWith(args[2].toUpperCase())) completions.add(op.name());
        } else if (args.length == 4) {
            completions.addAll(Arrays.asList("1", "5", "10", "0.5"));
        }
        return completions;
    }
}