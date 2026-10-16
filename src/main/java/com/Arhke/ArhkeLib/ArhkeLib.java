package com.Arhke.ArhkeLib;

//import com.Arhke.ArhkeLib.Hook.Hook;
import com.Arhke.ArhkeLib.Base.PluginBase;
import com.Arhke.ArhkeLib.Hook.Plugins;
import com.Arhke.ArhkeLib.ItemUtil.CustomItem.AddAttributeCommand;
import com.Arhke.ArhkeLib.ItemUtil.CustomItem.ItemFlagCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ArhkeLib extends PluginBase {

    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
    public static  ArhkeLib plugin;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib started loading...");
        plugin = this;
        //register the event listeners
        registerHooks(Plugins.PROTOCOLLIB, Plugins.NBTAPI);

        registerCustomAttributeEvents();
//        registerEconXPSync();

        getCommand("addattribute").setExecutor(new AddAttributeCommand());
        getCommand("addattribute").setTabCompleter(new AddAttributeCommand());
        getCommand("itemflag").setExecutor(new ItemFlagCommand());
        getCommand("itemflag").setTabCompleter(new ItemFlagCommand());

        consoleSender.sendMessage("[ArhkeLib] " + ChatColor.GREEN + "Arhke-Lib is fully loaded!");
    }

    @Override
    public void onDisable() {


    }

    private void initializeListeners() {

    }


}

