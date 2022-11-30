package com.Arhke.WRCore.Lib.FileIO;

import com.Arhke.WRCore.Lib.Base.Base;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DirectoryManager extends Base {
    File dir;

    public DirectoryManager(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                this.dir = file;
            } else {
                file.delete();
                except("[DirectoryManager.java] File in Constructor must be directory");
            }
        } else {
            file.mkdirs();
            this.dir = file;
        }
    }
    public boolean exists() {
        return dir.exists() && dir.isDirectory();
    }
    public File[] getFileList() {
        return this.dir.listFiles();
    }
    public String getDirName(){
        return this.dir.getName();
    }
    public List<FileManager> getFMList() {
        List<FileManager> ret = new ArrayList<>();
        for (File dirFile : Objects.requireNonNull(this.dir.listFiles()))
            if (dirFile.isFile())
                ret.add(new FileManager(dirFile));
        return ret;
    }
    public List<DirectoryManager> getDMList(){
        List<DirectoryManager> ret = new ArrayList<>();
        for (File dirFile : Objects.requireNonNull(this.dir.listFiles()))
            if (!dirFile.isFile())
                ret.add(new DirectoryManager(dirFile));
        return ret;
    }
    public FileManager getOrNewFM(String path){
        File file = Paths.get(this.dir.toString(), path).toFile();
        if(file.exists() && !file.isFile()){
            file.delete();
        }
        return new FileManager(file);
    }
    @Nullable
    public FileManager getOrLoadFM(String path){
        File file = Paths.get(this.dir.toString(), path).toFile();
        if(!file.exists())
            return null;
        if(!file.isFile()){
            return null;
        }
        return new FileManager(file);
    }

    public DirectoryManager getOrNewDM(String path){
        File file = Paths.get(this.dir.toString(), path).toFile();
        if(file.exists() && !file.isDirectory())
            file.delete();
        return new DirectoryManager(file);
    }
    public DirectoryManager getOrLoadDM(String path){
        File file = Paths.get(this.dir.toString(), path).toFile();
        if(!file.exists())
            return null;
        if(!file.isDirectory())
            return null;
        return new DirectoryManager(file);
    }
    public void deleteContents() {
        getFMList().forEach(FileManager::deleteFile);
        getDMList().forEach(DirectoryManager::delete);
    }
    public void delete() {
        deleteContents();
        dir.delete();
    }
}
