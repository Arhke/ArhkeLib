package com.Arhke.ArhkeLib;

//import com.Arhke.ArhkeLib.Lib.Hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ArhkeLib extends JavaPlugin {

    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();



    private static ArhkeLib plugin ;
    @Override
    public void onEnable() {
        plugin = this;
        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib started loading...");

        //register the event listeners
        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib is fully loaded!");
    }

    @Override
    public void onDisable() {


//        getWorldDataManager().saveAllChunkData();
    }

    public static ArhkeLib getPlugin() {
        return plugin;
    }

    private void initializeListeners() {

    }


}

