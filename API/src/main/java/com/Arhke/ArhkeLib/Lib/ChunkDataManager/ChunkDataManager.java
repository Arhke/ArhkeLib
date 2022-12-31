package com.Arhke.ArhkeLib.Lib.ChunkDataManager;

import com.Arhke.ArhkeLib.Lib.FileIO.DirectoryManager;
import com.Arhke.ArhkeLib.Lib.FileIO.FileManager;
import com.Arhke.ArhkeLib.Lib.Utils.ExpandableList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class ChunkDataManager<T extends ChunkData>{
    List<List<T>> xz = new ExpandableList<>();
    List<List<T>> x_z = new ExpandableList<>();
    List<List<T>> _xz = new ExpandableList<>();
    List<List<T>> _x_z = new ExpandableList<>();
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
    public T getChunkData(int x, int z) {
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
    public T getChunkData(Chunk c) {
        return getChunkData(c.getX(), c.getZ());
    }
    /**
     * @param loc Bukkit Location Object specifying location of the data
     * @return <b>ChunkData</b> of the chunk for the given chunk
     */
    public T getChunkData(Location loc) {
        return getChunkData(loc.getChunk());
    }
    private T fetchChunkData(List<List<T>> dataMap, int x, int z){
        int absX = Math.abs(x);
        int absZ = Math.abs(z);
        List<T> dataList;
        try{
            dataList = dataMap.get(absX);
            if (dataList == null){
                dataMap.set(absX, dataList = new ExpandableList<>());
            }
        }catch(IndexOutOfBoundsException e){
            dataMap.set(absX, dataList = new ExpandableList<>());
        }
        T data;
        try{
            data = dataList.get(absZ);
            if (data == null){
                dataList.set(absZ, data = p.get());
            }
        }catch(IndexOutOfBoundsException e){
            dataList.set(absZ, data = p.get());
        }
        data.setX(x); data.setZ(z); data.setWorld(this.world);
        data.load(dm.getOrNewFM(x + "." + z + ".yml").getDataManager());
        offLoadQueue.add(data);
        return data;
    }
    private FileManager unFetchChunkData(List<List<T>> dataMap, int x, int z){
        int absX = Math.abs(x);
        int absZ = Math.abs(z);
        List<T> dataList;
        try{
            dataList = dataMap.remove(absX);
            if (dataList == null){
                return null;
            }
        }catch(IndexOutOfBoundsException e){
            return null;
        }
        T data;
        try{
            data = dataList.remove(absZ);
            if (data == null){
                return null;
            }
        }catch(IndexOutOfBoundsException e){
            return null;
        }

        return dm.getOrNewFM(x + "." + z + ".yml");
    }
    public FileManager removeChunkData(int x, int z){
        if (x >= 0){
            if (z >= 0){
                return unFetchChunkData(xz, x, z);

            }else {
                return unFetchChunkData(x_z, x, z);
            }
        }else{
            if(z >= 0){
                return unFetchChunkData(_xz, x, z);
            }else {
                return unFetchChunkData(_x_z, x, z);
            }
        }
    }
    public Queue<T> getOffLoadQueue(){
        return this.offLoadQueue;
    }
    public void save(){
        for (List<T> cdList : xz) {
            if (cdList == null) {
                continue;
            }
            for (T cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
                cd.write(fm.getDataManager());
                fm.save();
            }
        }
        for (List<T> cdList : xz) {
            if (cdList == null) {
                continue;
            }
            for (T cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM("-" + cd.getX() + "." + cd.getZ() + ".yml");
                cd.write(fm.getDataManager());
                fm.save();
            }
        }
        for (List<T> cdList : x_z) {
            if (cdList == null) {
                continue;
            }
            for (T cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + ".-" + cd.getZ() + ".yml");
                cd.write(fm.getDataManager());
                fm.save();
            }
        }
        for (List<T> cdList : _x_z) {
            if (cdList == null) {
                continue;
            }
            for (T cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM("-" + cd.getX() + ".-" + cd.getZ() + ".yml");
                cd.write(fm.getDataManager());
                fm.save();
            }
        }
    }




}
