package com.Arhke.ArhkeLib.ChunkDataManager;

import com.Arhke.ArhkeLib.FileIO.DirectoryManager;
import com.Arhke.ArhkeLib.FileIO.FileManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.function.Supplier;


public class ChunkDataManager<T extends ChunkData>{
    Map<Integer, Map<Integer, T>> dataMap = new HashMap<>();
    Queue<T> offLoadQueue = new LinkedList<>();
    final World world;
    final DirectoryManager dm;
    final Supplier<T> p;
    protected ChunkDataManager(World world, DirectoryManager dm, Supplier<T> p) {
        this.world = world;
        this.dm = dm;
        this.p = p;
    }


    /**
     * @param x Chunk x coordinates
     * @param z Chunk z coordinates
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public T getChunkData(int x, int z, boolean createNew) {
        Map<Integer, T> dataList = dataMap.get(x);
        if(dataList == null){
            if(dm.getOrLoadFM(x + "." + z + ".yml") == null && !createNew) return null;
            dataMap.put(x, dataList = new HashMap<>());
            T data;
            dataList.put(z, data = p.get());
            data.setX(x); data.setZ(z); data.setWorld(this.world);
            data.load(dm.getOrNewFM(x + "." + z + ".yml").getDataManager());
            offLoadQueue.add(data);
            return data;
        }
        T data = dataList.get(z);
        if(data == null){
            if(dm.getOrLoadFM(x + "." + z + ".yml") == null && !createNew) return null;
            dataList.put(z, data = p.get());
            data.setX(x); data.setZ(z); data.setWorld(this.world);
            data.load(dm.getOrNewFM(x + "." + z + ".yml").getDataManager());
            offLoadQueue.add(data);
        }
        return data;
    }
    /**
     * @param c Bukkit Chunk Object specifying location of the data
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public T getChunkData(Chunk c, boolean createNew) {
        return getChunkData(c.getX(), c.getZ(), createNew);
    }
    /**
     * @param loc Bukkit Location Object specifying location of the data
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public T getChunkData(Location loc, boolean createNew) {
        return getChunkData(loc.getChunk(), createNew);
    }
    public FileManager removeChunkData(int x, int z){
        Map<Integer, T> dataList = dataMap.get(x);
        if(dataList == null){
            return null;
        }
        T data = dataList.remove(z);
        if(dataList.isEmpty()) dataMap.remove(x);
        if(data == null){
            return null;
        }
        return dm.getOrNewFM(x + "." + z + ".yml");
    }
    public Queue<T> getOffLoadQueue(){
        return this.offLoadQueue;
    }
    public void save(){
        for (Map<Integer, T> cdList : dataMap.values()) {
            if (cdList == null) {
                continue;
            }
            for (T cd : cdList.values()) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
                cd.write(fm.getDataManager());
                if (fm.getDataManager().getConfig().getKeys(true).size() != 0 ){
                    fm.save();
                }else{
                    fm.deleteFile();
                }

            }
        }
    }




}
