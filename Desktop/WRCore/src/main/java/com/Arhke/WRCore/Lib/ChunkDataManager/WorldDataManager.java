package com.Arhke.WRCore.Lib.ChunkDataManager;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class WorldDataManager extends MainBase {

    DirectoryManager dm;
    Map<String, ChunkDataManager> _worldData = new HashMap<>();
    public WorldDataManager(Main Instance, DirectoryManager dm) {
        super(Instance);
        this.dm = dm;
        for (DirectoryManager dir: dm.getDMList()){
            String name = dir.getDirName();
            World world = Bukkit.getWorld(name);
            if (world == null){
                return;
            }
            ChunkDataManager cdm = new ChunkDataManager(dir, getPlugin(), world);
            _worldData.put(name, cdm);
        }
    }
    public ChunkDataManager getOrNewChunkDataManager(World world){
        ChunkDataManager cdm = _worldData.get(world.getName());
        if (cdm == null){
            _worldData.put(world.getName(), cdm = new ChunkDataManager(dm.getOrNewDM(world.getName()), getPlugin(), world));
        }
        return cdm;
    }
    public ChunkData getOrNewChunkData(Location location){
        ChunkDataManager cdm = getOrNewChunkDataManager(location.getWorld());
        return cdm.getOrNewChunkData(location);
    }
    public ChunkData getOrNewChunkData(World world, int x, int z){
        ChunkDataManager cdm = getOrNewChunkDataManager(world);
        return cdm.getOrNewChunkData(x, z);
    }
    public ChunkData getOrNewChunkData(Chunk c){
        ChunkDataManager cdm = getOrNewChunkDataManager(c.getWorld());
        return cdm.getOrNewChunkData(c.getX(), c.getZ());
    }
    public void saveAllChunkData(){
        _worldData.values().forEach(ChunkDataManager::save);
    }
}
