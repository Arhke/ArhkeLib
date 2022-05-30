package com.Arhke.ArhkeLib.Lib.Configs;


import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import com.Arhke.ArhkeLib.Lib.FileIO.ConfigManager;
import com.Arhke.ArhkeLib.Lib.FileIO.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigLoader extends MainBase<JavaPlugin> {
    Map<ConfigFile, ConfigManager> configMap = new HashMap<>();

    /**
     * An abstract class for managing the config and data files of a plugin
     * @param instance The JavaPlugin instance
     * @param setStaticConfig Function to set static values for the plugin (<i>Leave null if it isn't needed</i>).
     * @param enumClasses An array of Enum classes that implement the ConfigFile interface.
     */
    @SafeVarargs
    public ConfigLoader (JavaPlugin instance, @Nullable Consumer<ConfigLoader> setStaticConfig, Class<? extends ConfigFile>... enumClasses){
        super(instance);
        for (Class<? extends ConfigFile> enumClass: enumClasses){
            if (!enumClass.isEnum()) continue;
            for(ConfigFile configFile:enumClass.getEnumConstants()) {
                FileManager fm;
                try {
                    fm = new FileManager(Paths.get(getPlugin().getDataFolder().toString(), configFile.getFilePaths()).toFile());
                }catch(Exception e){
                    e.printStackTrace();
                    except("Unable to setup FileManager");
                    continue;
                }
                configFile.createConfigDefaults(fm);
                fm.save();
                ConfigManager cm = new ConfigManager(fm);
                configMap.put(configFile, cm);
            }
        }
        if (setStaticConfig != null) setStaticConfig.accept(this);
    }
    /**
     * @param cf Config File Enum
     * @return <b>ConfigManager</b> of the corresponding ConfigFile<br><b>null</b> if not found
     */
    public ConfigManager getConfigManager(ConfigFile cf){
        return configMap.get(cf);
    }




}
