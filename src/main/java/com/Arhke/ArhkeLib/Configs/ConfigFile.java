package com.Arhke.ArhkeLib.Configs;


import com.Arhke.ArhkeLib.FileIO.FileManager;

/**
 * <p>Abstract class. Implement this with an enum class</p>
 */
public interface ConfigFile{;
    void createConfigDefaults(FileManager fm);
    String[] getFilePath();
}