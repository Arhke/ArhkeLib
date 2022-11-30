package com.Arhke.WRCore.ItemSystem.CraftItem;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomItemManager extends MainBase {
    private Map<String, CustomItem> _customItems = new HashMap<>();
    public CustomItemManager(Main Instance, DirectoryManager dm) {
        super(Instance);
        for (FileManager fm: dm.getFMList()){
            CustomItem ci = new CustomItem(getPlugin(), fm);
            _customItems.put(fm.getFileNameNoExt().toLowerCase(), ci);
        }
    }
    public CustomItem getItem(String id){
        return _customItems.get(id.toLowerCase());
    }
    public Set<String> getItems() {return _customItems.keySet();}
}
