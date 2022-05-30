package com.Arhke.ArhkeLib.Lib.ChunkDataManager;

import com.Arhke.ArhkeLib.Lib.Base.MainBase;
import com.Arhke.ArhkeLib.Lib.FileIO.DirectoryManager;
import com.Arhke.ArhkeLib.Lib.FileIO.FileManager;
import com.Arhke.ArhkeLib.Lib.Utils.ExpandableList;
import com.Arhke.ArhkeLib.ArhkeLib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ChunkDataManager<T>{
    List<List<ChunkData>> xz = new ExpandableList<>();
    List<List<ChunkData>> x_z = new ExpandableList<>();
    List<List<ChunkData>> _xz = new ExpandableList<>();
    List<List<ChunkData>> _x_z = new ExpandableList<>();
    World world;
    protected ChunkDataManager(World world) {
        this.world = world;
    }


    /**
     * @param x Chunk x coordinates
     * @param z Chunk z coordinates
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public ChunkData getChunkData(int x, int z) {
        if (x >= 0){
            if (z >= 0){
                return fetchChunkData(xz, x, z);

            }else {
                return fetchChunkData(x_z, x, z);
            }
        }else{
            if(z >= 0){
                return fetchChunkData(_xz, x, z);
            }else {
                return fetchChunkData(_x_z, x, z);
            }
        }
    }
    /**
     * @param c Bukkit Chunk Object specifying location of the data
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public ChunkData getChunkData(Chunk c) {
        return getChunkData(c.getX(), c.getZ());
    }
    /**
     * @param loc Bukkit Location Object specifying location of the data
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public ChunkData getChunkData(Location loc) {
        return getChunkData(loc.getChunk());
    }
    private ChunkData fetchChunkData(List<List<ChunkData>> dataMap, int x, int z){
        int absX = Math.abs(x);
        int absZ = Math.abs(z);
        List<ChunkData> dataList;
        try{
            dataList = dataMap.get(absX);
            if (dataList == null){
                dataMap.set(absX, dataList = new ExpandableList<>());
            }
        }catch(IndexOutOfBoundsException e){
            dataMap.set(absX, dataList = new ExpandableList<>());
        }
        ChunkData cd;
        try{
            cd = dataList.get(absZ);
            if (cd == null){
                dataList.set(absZ, cd = new ChunkData(world.getChunkAt(x, z)));
            }
        }catch(IndexOutOfBoundsException e){
            dataList.set(absZ, cd = new ChunkData(world.getChunkAt(x, z)));
        }
        return cd;
    }


}
