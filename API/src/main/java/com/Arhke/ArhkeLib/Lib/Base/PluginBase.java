package com.Arhke.ArhkeLib.Lib.Base;

import com.Arhke.ArhkeLib.Lib.Configs.ConfigFile;
import com.Arhke.ArhkeLib.Lib.Configs.ConfigLoader;
import com.Arhke.ArhkeLib.Lib.CustomEvents.CustomEventListeners.ArmorEquipListener;
import com.Arhke.ArhkeLib.Lib.CustomEvents.CustomEventListeners.MoveChunkListener;
import com.Arhke.ArhkeLib.Lib.FileIO.ConfigManager;
import com.Arhke.ArhkeLib.Lib.GUI.GUIManager;
import com.Arhke.ArhkeLib.Lib.Hook.Hook;
import com.Arhke.ArhkeLib.Lib.Hook.Plugins;
import com.Arhke.ArhkeLib.Lib.Utils.RecipeBuilder;
import com.earth2me.essentials.libs.checkerframework.checker.nullness.qual.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class PluginBase extends JavaPlugin {
    protected ConfigLoader configLoader;
    protected Hook hook;
    protected GUIManager guiManager;
    protected RecipeBuilder recipeBuilder;
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
            command.getDM().getFM().save();
            if(pc == null){
                Base.warn("Could not register" + command.getCmd());
                continue;
            }
            pc.setExecutor(command);
            pc.setTabCompleter(command);

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
    public void registerRecipeBuilder(){
        if(recipeBuilder == null){
            recipeBuilder = new RecipeBuilder(this);
        }
    }
    public RecipeBuilder getRecipeBuilder(){
        return recipeBuilder;
    }
    public final void registerCustomEvents(int version){
        if(version < 12){
            Bukkit.getPluginManager().registerEvents(new ArmorEquipListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new MoveChunkListener(), this);

    }
}
