package com.Arhke.ArhkeLib;

//import com.Arhke.ArhkeLib.Hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ArhkeLib extends JavaPlugin {

    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();


    @Override
    public void onEnable() {
        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib started loading...");

        //register the event listeners
        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib is fully loaded!");
    }

    @Override
    public void onDisable() {


//        getWorldDataManager().saveAllChunkData();
    }

    private void initializeListeners() {

    }


}

