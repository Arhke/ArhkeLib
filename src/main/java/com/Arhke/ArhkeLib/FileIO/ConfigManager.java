package com.Arhke.ArhkeLib.FileIO;

public class  ConfigManager extends DataManager{
    FileManager fm;
    public ConfigManager(FileManager fm) {
        super(fm.getConfig());
        this.fm = fm;
    }
    public FileManager getFM() {
        return this.fm;
    }
    public void reloadConfig() throws Exception {
        this.fm.reloadConfig();
        config = fm.getConfig();
    }


}
