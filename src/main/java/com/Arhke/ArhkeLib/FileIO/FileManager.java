package com.Arhke.ArhkeLib.FileIO;

import com.Arhke.ArhkeLib.Base.Base;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * FileManager for .yml files
 * @date 10/12/2021
 * @author William Lin
 */
public class FileManager extends Base {
    private final File file;
    private YamlConfiguration config;

    /**
     * Creates a FileManager for easier file management
     * @param file file object
     * @throws RuntimeException if code could not fix & create file
     */
    public FileManager(File file) throws RuntimeException{
        makeFile(file);
        this.config = YamlConfiguration.loadConfiguration(file);
        this.file = file;
    }

    public File getFile(){
        return file;
    }
    /**
     * @return whether or not the file previously stored by file manager still exists
     */
    public boolean exists(){
        return this.file.isFile() && this.file.exists();
    }
    /**
     * @return returns the file name
     */
    public String getFileName(){
        return file.getName();
    }
    /**
     * @return returns the file name for the FileManager, removing any file extension if there is any
     */
    public String getFileNameNoExt() {
        return FilenameUtils.removeExtension(file.getName());
    }

    /**
     * @return return last loaded YamlConfiguration of file
     */
    public YamlConfiguration getConfig() {
        return config;
    }
    /**
     * reloads the YamlConfiguration stored in the filemanager
     * @throws Exception if reload was unsuccessful
     */
    public void reloadConfig() throws Exception{
        makeFile(this.file);
        config = YamlConfiguration.loadConfiguration(file);
    }
    /**
     * clears the data inside of the YamlConfiguration stored
     */
    public void wipeConfig() {
        for (String key : this.config.getKeys(false)) {
            config.set(key, null);
        }
    }

    /**
     * @return returns a new DataManager for the YamlConfig Stored in FM;
     */
    public DataManager getDataManager() {
        return new DataManager(config);
    }


    /**
     * saves the configuration currently in the FileManager
     * @param file the file path to write to
     * @return true if write was successful, false otherwise
     */
    public boolean saveToFile(File file) {
        try {
            makeFile(file);
            config.save(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * write the configuration to the filepath in FileManager
     * @return true if write was successful
     */
    public boolean save(){
        return saveToFile(file);
    }
    /**
     * Deletes the file in the FileManager's stored file path
     * @return true if delete was successful, false otherwise
     */
    public boolean deleteFile(){
        return file.delete();
    }

    //================<Static Helper>==============
    /**
     * @param file file to create or make sure exists
     * @throws Exception if file cannot be made
     */
    public static void makeFile(File file) throws RuntimeException{
        if (!file.exists()) {
            boolean b = file.getParentFile().mkdirs();
            try {
                if (!file.createNewFile()) {
                    except("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (!file.isFile() && (!file.delete() || !file.createNewFile())) {
                    except("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
