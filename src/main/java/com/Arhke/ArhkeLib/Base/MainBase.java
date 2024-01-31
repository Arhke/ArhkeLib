package com.Arhke.ArhkeLib.Base;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MainBase<T extends JavaPlugin> extends Base{
    private final T plugin;
    public MainBase(T instance){
        plugin = instance;
    }
    public T getPlugin(){
        return plugin;
    }
    public void exceptDisable(String Msg){
        Bukkit.getPluginManager().disablePlugin(plugin);
        throw new RuntimeException(Msg);
    }

}
