package com.Arhke.ArhkeLib;

import com.Arhke.ArhkeLib.Lib.CustomEvents.CustomEventListener;
import com.Arhke.ArhkeLib.Lib.GUI.GUIManager;
//import com.Arhke.ArhkeLib.Lib.Hook.Hook;
import com.Arhke.ArhkeLib.Lib.Utils.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ArhkeLib extends JavaPlugin {

    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();

    File kingdomsDataFolder, townDataFolder, tUserDataFolder, worldDataFolder,
            customItemFolder, recipeFolder;

    //=============<Managers>=============
    private GUIManager guiManager;


    private static ArhkeLib plugin ;
    @Override
    public void onEnable() {
        plugin = this;
        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib started loading...");

        //register the event listeners
        initializeListeners();
        RecipeUtil.instance = this;
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
        Bukkit.getPluginManager().registerEvents(new CustomEventListener(this), this);

    }


}

