package com.Arhke.ArhkeLib.Lib.Configs;


import com.Arhke.ArhkeLib.Lib.FileIO.FileManager;

/**
 * <p>Abstract class. Implement this with an enum class</p>
 */
public interface ConfigFile{;
    void createConfigDefaults(FileManager fm);
    String[] getFilePaths();
}