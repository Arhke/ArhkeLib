package com.Arhke.ArhkeLib.Lib.Utils;

import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitTaskMap<T extends JavaPlugin> extends MainBase<T> {
    public BukkitTaskMap(T instance){
        super(instance);

    }
    public void addTask(){

    }
    public BukkitTask getTask(){
        return null;
    }
}
