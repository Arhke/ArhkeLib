package com.Arhke.WRCore.Lib.FileIO;

import com.Arhke.WRCore.Lib.Base.Base;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager extends Base {
    private File file;
    private YamlConfiguration config;
    private DataManager dataManager;
    public FileManager(File file){
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(!file.isFile()){
                file.delete();
                except("[DataReader.java] File must not be a Directory");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        this.file = file;
        dataManager = new DataManager(config);
    }
    public boolean exists() {
        return file.exists() && file.isFile();
    }
    /**
     * @return returns the file name for the file manager, removing the .yml file extension if it exists
     */
    public String getFileName(){
        return file.getName();
    }
    /**
     * @return returns the file name for the filemanager, removing any file extension if there is any
     * @return
     */
    public String getFileNameNoExt() {
        return FilenameUtils.removeExtension(file.getName());
    }
    public YamlConfiguration getConfig() {
        return config;
    }
    public DataManager getDataManager() {
        return dataManager;
    }
    public void saveToFile(File file) {
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(!file.isFile()){
            except("FileManager.java saveToFile file has to be file and not directory");
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void reloadConfig(){
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(!file.isFile()){
                file.delete();
                error("[DataReader.java] File must not be a Directory");
            }
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        dataManager = new DataManager(config);
    }
    public void wipeConfig() {
        for (String key : this.config.getKeys(false)) {
            config.set(key, null);
        }
    }
    public void save(){
        saveToFile(file);
    }
    public void deleteFile(){
        wipeConfig();
        file.delete();
    }


}
