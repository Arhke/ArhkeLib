package com.Arhke.ArhkeLib.ChunkDataManager;

import com.Arhke.ArhkeLib.FileIO.DirectoryManager;
import com.Arhke.ArhkeLib.FileIO.FileManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WorldDataManager<T extends ChunkData> {

    DirectoryManager dm;
    Map<String, ChunkDataManager<T>> worldData = new HashMap<>();
    Supplier<T> supplier;
    public WorldDataManager(DirectoryManager dm, Supplier<T> supplier) {
        this.dm = dm;
        this.supplier = supplier;
    }


//    ###################Data Retrievers################
    public T getData(World world, int x, int z, boolean createNew){
        ChunkDataManager<T> cdm = worldData.get(world.getName());
        if (cdm == null) return null;
        return cdm.getChunkData(x, z, createNew);
    }

    public T getData(Chunk c, boolean createNew){
        return getData(c.getWorld(), c.getX(), c.getZ(), createNew);
    }
    public  T getData(Location location, boolean createNew){
        return getData(location.getChunk(), createNew);
    }
    public T getOrNewChunkData(World world, int x, int z, boolean createNew){
        return getOrNewChunkDataManager(world).getChunkData(x, z, createNew);
    }
    public T getOrNewChunkData(Chunk c, boolean createNew){
        return getOrNewChunkData(c.getWorld(), c.getX(), c.getZ(), createNew);
    }
    public T getOrNewChunkData(Location location, boolean createNew){
        return getOrNewChunkData(location.getChunk(), createNew);
    }
    public ChunkDataManager<T> getOrNewChunkDataManager(World world){
        ChunkDataManager<T> cdm = worldData.get(world.getName());
        if (cdm == null){
            worldData.put(world.getName(), cdm = new ChunkDataManager<T>(world, dm.getOrNewDM(world.getName()), supplier));
        }
        return cdm;
    }
    public T getRelative(T start, Relative rel, boolean createNew){
        return getOrNewChunkData(start.getWorld(), start.getX() + rel.getX(), start.getZ() + rel.getZ(), createNew);
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
