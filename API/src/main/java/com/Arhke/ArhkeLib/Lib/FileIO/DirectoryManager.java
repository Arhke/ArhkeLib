package com.Arhke.ArhkeLib.Lib.FileIO;

import com.Arhke.ArhkeLib.Lib.Base.Base;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DirectoryManager extends Base {
    File dir;
    Set<DirectoryManager> directorySet = new HashSet<>();
    Set<FileManager> fileSet = new HashSet<>();


    public DirectoryManager(File file) throws Exception {
        makeDir(file);
        this.dir = file;
        for (File dirFile : Objects.requireNonNull(this.dir.listFiles()))
            if (dirFile.isFile())
                fileSet.add(new FileManager(dirFile));
        for (File dirFile : Objects.requireNonNull(this.dir.listFiles()))
            if (!dirFile.isFile())
                directorySet.add(new DirectoryManager(dirFile));
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
    public Collection<FileManager> getFMList() {
        return fileSet;
    }
    public Collection<DirectoryManager> getDMList(){
        return directorySet;
    }
    public FileManager getOrNewFM(String path){
        try {
            return new FileManager(Paths.get(this.dir.toString(), path).toFile());
        }catch(Exception e){
            throw new RuntimeException("Cannot get new FM");
        }
    }

    /**
     * @param path file path of the requested file relative to the directory
     * @return <b>FileManager</b> of the requested file <br> <b>null</b> if file does not exist, or if file opening generated an exception
     */
    @Nullable
    public FileManager getOrLoadFM(String path){
        File file = Paths.get(this.dir.toString(), path).toFile();
        if(!file.exists() || !file.isFile()){
            return null;
        }
        try {
            return new FileManager(file);
        }catch(Exception e){
            return null;
        }
    }

    /**
     * @param path Directory name
     * @return <b>DirectoryManager</b> for given path<br><b>null</b> if creation failed
     */
    @Nullable
    public DirectoryManager getOrNewDM(String path){
        try {
            return new DirectoryManager(Paths.get(this.dir.toString(), path).toFile());
        }catch(Exception e){
            return null;
        }
    }

    /**
     * @param path directory name
     * @return <b>DirectoryManager</b> with name path<br><b>null</b> if directory doesn't exist or failed
     */
    @Nullable
    public DirectoryManager getOrLoadDM(String path){
        File file = Paths.get(this.dir.toString(), path).toFile();
        if(!file.exists())
            return null;
        if(!file.isDirectory())
            return null;
        try {
            return new DirectoryManager(Paths.get(this.dir.toString(), path).toFile());
        }catch(Exception e){
            return null;
        }
    }
    public void deleteContents() {
        getFMList().forEach(FileManager::deleteFile);
        getDMList().forEach(DirectoryManager::deleteDir);
    }

    /**
     * @return <b>true</b> if directory delete was successful
     */
    public boolean deleteDir() {
        deleteContents();
        return this.dir.delete();
    }


    //================<Static Helper>==============
    /**
     * @param file directory to create or make sure exists
     * @throws Exception if directory cannot be made
     */
    public static void makeDir(File file) throws Exception{

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new Exception();
            }
        } else if (!file.isDirectory() && (!file.delete() || !file.mkdirs())) {
            throw new Exception();
        }
    }
}
