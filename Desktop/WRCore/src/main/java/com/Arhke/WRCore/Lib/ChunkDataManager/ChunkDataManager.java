package com.Arhke.WRCore.Lib.ChunkDataManager;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Lib.Utils.ExpandableList;
import com.Arhke.WRCore.Main;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class ChunkDataManager extends MainBase {
    List<List<ChunkData>> xz = new ExpandableList<>();
    List<List<ChunkData>> x_z = new ExpandableList<>();
    List<List<ChunkData>> _xz = new ExpandableList<>();
    List<List<ChunkData>> _x_z = new ExpandableList<>();
    DirectoryManager dm;
    World world;
    public ChunkDataManager(DirectoryManager dm, Main instance, World world) {
        super(instance);
        this.dm = dm;
        this.world = world;
        for(FileManager fm: this.dm.getFMList()){
            String[] coords = fm.getFileNameNoExt().split("\\.");
            if (coords.length != 2){
                continue;
            }
            try {
                int x = Integer.parseInt(coords[0]);
                int z = Integer.parseInt(coords[1]);
                ChunkData cd = getOrNewChunkData(x, z);
                cd.load(fm);
            }catch(NumberFormatException ignored){}
        }
    }
    public ChunkData getOrNewChunkData(int x, int z) {
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
    public ChunkData getOrNewChunkData(Location loc) {
        int x = loc.getChunk().getX();
        int z = loc.getChunk().getZ();
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
                dataList.set(absZ, cd = new ChunkData(world.getChunkAt(x, z), getPlugin()));
            }
        }catch(IndexOutOfBoundsException e){
            dataList.set(absZ, cd = new ChunkData(world.getChunkAt(x, z), getPlugin()));
        }
        return cd;
    }

    public void save() {
        dm.deleteContents();
        for (List<ChunkData> cdList : xz) {
            if (cdList == null) {
                continue;
            }
            for (ChunkData cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
                cd.save(fm);
            }
        }
        for (List<ChunkData> cdList : _xz) {
            if (cdList == null) {
                continue;
            }
            for (ChunkData cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
                cd.save(fm);
            }
        }
        for (List<ChunkData> cdList : x_z) {
            if (cdList == null) {
                continue;
            }
            for (ChunkData cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
                cd.save(fm);
            }
        }
        for (List<ChunkData> cdList : _x_z) {
            if (cdList == null) {
                continue;
            }
            for (ChunkData cd : cdList) {
                if (cd == null) {
                    continue;
                }
                FileManager fm = dm.getOrNewFM(cd.getX() + "." + cd.getZ() + ".yml");
                cd.save(fm);
            }
        }
    }

}
