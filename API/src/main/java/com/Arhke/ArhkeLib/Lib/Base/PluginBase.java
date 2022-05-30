package com.Arhke.ArhkeLib.Lib.Base;

import com.Arhke.ArhkeLib.Lib.Configs.ConfigFile;
import com.Arhke.ArhkeLib.Lib.Configs.ConfigLoader;
import com.Arhke.ArhkeLib.Lib.FileIO.ConfigManager;
import com.Arhke.ArhkeLib.Lib.GUI.GUIManager;
import com.Arhke.ArhkeLib.Lib.Hook.Hook;
import com.Arhke.ArhkeLib.Lib.Hook.Plugins;
import com.Arhke.ArhkeLib.Lib.Utils.RecipeUtil;
import com.earth2me.essentials.libs.checkerframework.checker.nullness.qual.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public abstract class PluginBase extends JavaPlugin {
    protected ConfigLoader configLoader;
    protected Hook hook;
    protected GUIManager guiManager;
    public Hook getHook() {return hook;}
    public GUIManager getGUIManager(){
        return guiManager;
    }
    public ConfigLoader getConfigLoader() {
        return this.configLoader;
    }
    public ConfigManager getConfig(ConfigFile cf){
        return this.configLoader.getConfigManager(cf);
    }
    public void registerCommands(CommandsBase<?>... commands) {
        for (CommandsBase<?> command: commands) {
            PluginCommand pc = getCommand(command.getCmd());
            if(command.getDM() instanceof ConfigManager){
                ((ConfigManager)command.getDM()).getFM().save();
            }
            if(pc == null){
                Base.warn("Could not register" + command.getCmd());
                continue;
            }
            pc.setExecutor(command);

        }

    }
    public void registerGUI() {
        this.guiManager = new GUIManager(this);
        Bukkit.getPluginManager().registerEvents(this.guiManager, this);
    }
    public void registerHooks(Plugins... support) {
        this.hook = new Hook(this, support);
    }
    @SafeVarargs
    public final void registerConfigLoader(@Nullable Consumer<ConfigLoader> staticConfig, Class<? extends ConfigFile>... configEnums){
        this.configLoader = new ConfigLoader(this, staticConfig, configEnums);
    }
    @SafeVarargs
    public final void registerConfigLoader(Class<? extends ConfigFile>... configEnums){
        this.configLoader = new ConfigLoader(this, null, configEnums);
    }
    public void registerRecipe(String name, ItemStack is, Material... materials){
        RecipeUtil.registerRecipe(name, is, materials);
    }
}
