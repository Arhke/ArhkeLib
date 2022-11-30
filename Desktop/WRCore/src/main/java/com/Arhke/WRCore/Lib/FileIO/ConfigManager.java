package com.Arhke.WRCore.Lib.FileIO;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager extends DataManager{
    FileManager fm;
    public ConfigManager(FileManager fm) {
        super(fm.getDataManager().getConfig());
        this.fm = fm;
    }
    public FileManager getFM() {
        return this.fm;
    }
    public Set<Material> buildMaterialSet(String... path){
        List<String> materials = getStringList(path);
        Set<Material> ret = new HashSet<>();
        materials.forEach(s -> {
            try{
                ret.add(Material.valueOf(s));
            }catch(IllegalArgumentException e){

            }
        });
        return ret;
    }

}
