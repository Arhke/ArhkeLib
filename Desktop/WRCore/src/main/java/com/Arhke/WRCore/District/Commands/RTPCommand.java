package com.Arhke.WRCore.District.Commands;

import com.Arhke.WRCore.ItemSystem.CraftItem.CustomItem;
import com.Arhke.WRCore.Lib.Base.CommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Paths;
import java.util.UUID;

public class RTPCommand extends CommandsBase{


    public RTPCommand(Main instance, String command, DataManager dm) {
        super(instance, command, dm);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String Label, String[] args) {
        if (args.length != 2){
            return true;
        }
        if(!sender.isOp()){
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            return true;
        }
        World world = Bukkit.getWorld(args[1]);
        if (world == null){
            return true;
        }
        new BukkitRunnable() {
            public void run(){
                int x = (int)(Math.random()*10000-5000);
                int z = (int)(Math.random()*10000-5000);
                Location loc = world.getHighestBlockAt(x,z).getLocation().add(0,1,0);
                if (loc.getBlock().getBiome().name().contains("OCEAN"))
                    return;
                if (getPlugin().getWorldDataManager().getOrNewChunkData(loc).getTown() != null)
                    return;
                player.teleport(loc);
                this.cancel();
            }
        }.runTaskTimer(getPlugin(), 0L, 1L);
        sender.sendMessage(ChatColor.GREEN + "Teleporting... " );

        return true;

    }

    @Override
    public void setDefaults() {

    }
}

