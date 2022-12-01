package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class KingdomsManager extends MainBase {
    volatile Map<UUID, Kingdom> kingdomMap = new HashMap<>();
    DirectoryManager dm;
    public  KingdomsManager(Main instance, DirectoryManager dm){
        super(instance);
        this.dm = dm;
        for(FileManager fm: dm.getFMList()){
            try{
                Kingdom k = new Kingdom(getPlugin(), fm);
                this.kingdomMap.put(k.getId(), k);
            } catch (Exception e) {
                e.printStackTrace();
                error("[WRCore|KingdomsManager.java] Unable to load kingdoms data for "+fm.getFileName()+". Skipping...");
            }
        }
    }
    //========<Getters and Setters>=======

    /**
     * returns the kingdom with the specified UUID. Returns null if not found
     * @param uuid
     * @return
     */
    public Kingdom getKingdom(UUID uuid){
//        Optional<Kingdom> okingdom = this.kingdomMap.values().stream().filter((k)->k.getId().equals(uuid)).findFirst();

        return kingdomMap.get(uuid);
    }
    /**
     * Adds a kingdom to the kingdom manager
     * @param town Capital of the kingdom
     * @return the newly created kingdom if the creation was successful, null if the creation wasn't
     */
    @Nullable
    public Kingdom addNewKingdom(Town town, TUser user) {
        UUID uuid = UUID.randomUUID();
        FileManager fm = this.dm.getOrNewFM(uuid.toString() + ".yml");
        try {
            Kingdom kingdom = new Kingdom(getPlugin(), fm, uuid, town);
            this.kingdomMap.put(kingdom.getId(), kingdom);
            user.setKingdom(kingdom);
            kingdom.setRank(user.getID(), Rank.LEADER);
            return kingdom;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public Collection<Kingdom> getKingdoms(){
        return this.kingdomMap.values();
    }
    public boolean contains(Kingdom k){
        return kingdomMap.containsValue(k);
    }

    /**
     * removes the kingdom with the specified name.
     * @param k
     * @return false if the kingdom is not found or true if the kingdom was successfully removed
     */
    public boolean removeKingdom(Kingdom k){
        if  (k == null){
            return false;
        }
        k.remove();
        return !this.kingdomMap.remove(k.getId(), k);
    }

    public void saveKingdoms() {
        this.dm.deleteContents();
        this.kingdomMap.values().forEach((e)-> {
            try {
                e.save();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
