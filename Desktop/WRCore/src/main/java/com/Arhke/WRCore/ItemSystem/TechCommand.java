package com.Arhke.WRCore.ItemSystem;

import com.Arhke.WRCore.ItemSystem.CraftItem.CustomItem;
import com.Arhke.WRCore.Lib.Base.CommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Paths;
import java.util.UUID;

public class TechCommand extends CommandsBase {

    public TechCommand(Main instance, String command, DataManager dm) {
        super(instance, command, dm);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String Label, String[] args) {
        if (!isPlayer(sender)){
            sender.sendMessage(getHelp());
            return true;
        }
        Player player = (Player)sender;
        if(args.length > 0){
            if (args[0].equalsIgnoreCase("get")){
                if (args.length >= 2){
                    CustomItem ci = getPlugin().getCustomItemManager().getItem(args[1].toLowerCase());
                    if (ci != null) {
                        addItemtoPlayer((Player) sender, ci.getNewItem());
                        sender.sendMessage(ChatColor.GREEN + "Giving " + args[1]);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Item " + args[1] + " not found!");
                    }
                }else{
                    player.sendMessage("Incorrect Number of Args:\n/"+getCmd() + " get <itemname>");
                }
            }
            else if(args[0].equalsIgnoreCase("write")) {
                FileManager fm = new FileManager(Paths.get(getPlugin().getDataFolder().toString(), "Helper.yml").toFile());
                fm.getDataManager().set(player.getItemInHand(), "STUFF");
                fm.save();
                player.sendMessage(player.getEquipment().getItemInMainHand().toString());
            }else if(args[0].equalsIgnoreCase("create")) {
                FileManager fm = new FileManager(Paths.get(getPlugin().getDataFolder().toString(), "Helper.yml").toFile());
                fm.getDataManager().set(player.getItemInHand(), "STUFF");
                fm.save();
                player.sendMessage(player.getEquipment().getItemInMainHand().toString());
            }else if(args[0].equalsIgnoreCase("add")) {
                FileManager fm = new FileManager(Paths.get(getPlugin().getDataFolder().toString(), "Helper.yml").toFile());
                fm.getDataManager().set(player.getItemInHand(), "STUFF");
                fm.save();
                player.sendMessage(player.getEquipment().getItemInMainHand().toString());
            }else if(args[0].equalsIgnoreCase("reload")) {
                getPlugin().reloadTech();
                player.sendMessage("reloaded!");

            }
            else if (args[0].equalsIgnoreCase("list")){
                player.sendMessage(ChatColor.GOLD + "===<CustomItem List>===");
                for(String key:getPlugin().getCustomItemManager().getItems()) {
                    player.sendMessage(ChatColor.GRAY + "- " + key);
                }
            }
            else if (args[0].equalsIgnoreCase("debug")){
                FileManager fm = new FileManager(Paths.get(getPlugin().getDataFolder().toString(), "ItemSystem", "Recipe", "Helper.yml").toFile());
                DataManager dm = fm.getDataManager();

                player.sendMessage(player.getEquipment().getItemInMainHand().toString());
                dm.set(player.getEquipment().getItemInMainHand(), "STUFF");
                fm.save();

            }
            else{
                sender.sendMessage(getHelp());
            }
        } else{
            sender.sendMessage(ChatColor.GRAY + "ItemSystem by Arhke");
            sender.sendMessage(getHelp());
        }
        return true;

    }

    @Override
    public void setDefaults() {

    }
}
