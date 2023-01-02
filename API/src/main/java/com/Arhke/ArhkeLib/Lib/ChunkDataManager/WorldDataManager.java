package com.Arhke.ArhkeLib.Lib.ChunkDataManager;

import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import com.Arhke.ArhkeLib.Lib.FileIO.DirectoryManager;
import com.Arhke.ArhkeLib.Lib.FileIO.FileManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WorldDataManager<T extends ChunkData> extends MainBase<JavaPlugin> {

    DirectoryManager dm;
    Map<String, ChunkDataManager<T>> worldData = new HashMap<>();
    Supplier<T> supplier;
    public WorldDataManager(JavaPlugin instance, DirectoryManager dm, Supplier<T> supplier) {
        super(instance);
        this.dm = dm;
        this.supplier = supplier;
    }


//    ###################Data Retrievers################
    public T getData(World world, int x, int z){
        ChunkDataManager<T> cdm = worldData.get(world.getName());
        if (cdm == null) return null;
        return cdm.getChunkData(x, z);
    }

    public T getData(Chunk c){
        return getData(c.getWorld(), c.getX(), c.getZ());
    }
    public  T getData(Location location){
        return getData(location.getChunk());
    }
    public T getOrNewChunkData(World world, int x, int z){
        return getOrNewChunkDataManager(world).getChunkData(x, z);
    }
    public T getOrNewChunkData(Chunk c){
        return getOrNewChunkData(c.getWorld(), c.getX(), c.getZ());
    }
    public T getOrNewChunkData(Location location){
        return getOrNewChunkData(location.getChunk());
    }
    public ChunkDataManager<T> getOrNewChunkDataManager(World world){
        ChunkDataManager<T> cdm = worldData.get(world.getName());
        if (cdm == null){
            worldData.put(world.getName(), cdm = new ChunkDataManager<T>(world, dm.getOrNewDM(world.getName()), supplier));
        }
        return cdm;
    }
    public T getRelative(T start, Relative rel){
        return getOrNewChunkData(start.getWorld(), start.getX() + rel.getX(), start.getZ() + rel.getZ());
    }
    public FileManager removeChunkData(World world, int x, int z){
        ChunkDataManager<T> cdm = worldData.get(world.getName());
        if (cdm == null){
            return null;
        }
        return cdm.removeChunkData(x, z);
    }
    public FileManager removeChunkData(ChunkData cd){
        return removeChunkData(cd.getWorld(), cd.getX(), cd.getZ());
    }
    public Collection<ChunkDataManager<T>> getChunkDataManagers(){
        return this.worldData.values();
    }
    public void saveAllChunkData(){
        worldData.values().forEach(ChunkDataManager::save);
    }

    public enum Relative{
        UP(0,1),
        DOWN(0,-1),
        RIGHT(1,0),
        LEFT(-1,0);
        final int x, z;
        Relative(int x, int z){
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }
    }

}
