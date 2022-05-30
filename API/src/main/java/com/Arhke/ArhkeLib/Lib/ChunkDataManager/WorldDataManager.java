package com.Arhke.ArhkeLib.Lib.ChunkDataManager;

import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import com.Arhke.ArhkeLib.Lib.FileIO.DataManager;
import com.Arhke.ArhkeLib.Lib.FileIO.DirectoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public abstract class WorldDataManager<T> extends MainBase<JavaPlugin> implements Listener {

    DirectoryManager dm;
    Class<T> clazz;
    Map<String, ChunkDataManager> _worldData = new HashMap<>();
    protected WorldDataManager(JavaPlugin instance, DirectoryManager dm, Class<T> clazz) {
        super(instance);
        this.dm = dm;
        this.clazz = clazz;
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

//    #################Abstract Methods################
    public abstract T loadFromDataManager(DataManager dm);
    public abstract void writeToDataManager(DataManager dm);

//    ###################Data Retrievers################
    public T getData(World world, int x, int z){
        return null; //fixme
    }

    public T getData(Chunk c){
        return getData(c.getWorld(), c.getX(), c.getZ());
    }
    public  T getData(Location location){
        return getData(location.getChunk());
    }
    public ChunkData getOrNewChunkData(World world, int x, int z){
        return getOrNewChunkDataManager(world).getChunkData(x, z);
    }
    public ChunkData getOrNewChunkData(Chunk c){
        return getOrNewChunkData(c.getWorld(), c.getX(), c.getZ());
    }
    public ChunkData getOrNewChunkData(Location location){
        return getOrNewChunkData(location.getChunk());
    }
    public ChunkDataManager getOrNewChunkDataManager(World world){
        ChunkDataManager cdm = _worldData.get(world.getName());
        if (cdm == null){
            _worldData.put(world.getName(), cdm = new ChunkDataManager(world));
        }
        return cdm;
    }
//    public void saveAllChunkData(){
//        _worldData.values().forEach(ChunkDataManager::save);
//    }
    public void save(ChunkData cd) {
//        dm.deleteContents();
//        for (List<ChunkData> cdList : xz) {
//            if (cdList == null) {
//                continue;
//            }
//            for (ChunkData cd : cdList) {
//                if (cd == null) {
//                    continue;
//                }
//                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
//                cd.save(fm);
//            }
//        }
//        for (List<ChunkData> cdList : _xz) {
//            if (cdList == null) {
//                continue;
//            }
//            for (ChunkData cd : cdList) {
//                if (cd == null) {
//                    continue;
//                }
//                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
//                cd.save(fm);
//            }
//        }
//        for (List<ChunkData> cdList : x_z) {
//            if (cdList == null) {
//                continue;
//            }
//            for (ChunkData cd : cdList) {
//                if (cd == null) {
//                    continue;
//                }
//                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
//                cd.save(fm);
//            }
//        }
//        for (List<ChunkData> cdList : _x_z) {
//            if (cdList == null) {
//                continue;
//            }
//            for (ChunkData cd : cdList) {
//                if (cd == null) {
//                    continue;
//                }
//                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
//                cd.save(fm);
//            }
//        }
    }


    //    ######################Events########################
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event){
        ChunkData cd = getOrNewChunkData(event.getChunk());
        DirectoryManager worldDM = dm.getOrNewDM(event.getWorld().getName());
        if (worldDM == null){
//            cd.set
        }
//        .setData(loadFromDataManager(worldDM.getOrLoadFM());
    }
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onChunkLoad(ChunkUnloadEvent event){
//        event.
//    }

}
