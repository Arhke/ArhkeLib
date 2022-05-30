package com.Arhke.ArhkeLib.Lib.Utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;

public class MiscUtils {
    public static char toChar(int i){
        return (char)i;
    }
    public static void regenWorld(){
//        World worldtp = Bukkit.getWorld(config.getString("Spawn.World"));
//        double x = config.getDouble("Spawn.X");
//        double y = config.getDouble("Spawn.Y");
//        double z = config.getDouble("Spawn.Z");
//        float yaw = (float) config.getDouble("Spawn.Yaw");
//        float pitch = (float) config.getDouble("Spawn.Pitch");
//        Location location = new Location(worldtp, x, y, z, yaw, pitch);
//        World world = p.getWorld(); // The world you want to reset
//        for(Player player : Bukkit.getOnlinePlayers()){
//            player.teleport(location);
//        }
//
//        String worldName = world.getName(); // The world name
//
//        if (!Bukkit.unloadWorld(world, false)) return false; // unload the world, return if not successful
//
//        File worldFolder = new File(plugin.getDataFolder().getParentFile().getParentFile(), worldName); // World folder
//        worldFolder.delete(); // Delete world folder
//
//        Bukkit.createWorld(new WorldCreator(worldName));
    }
}
