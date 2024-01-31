package com.Arhke.ArhkeLib.Utils.MultiInstruction;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerCmd implements MultiInst{
    String command;
    public PlayerCmd(String content) {
        this.command = content.charAt(0) == '/'? content.substring(1):content;
    }

    @Override
    public void apply(OfflinePlayer p) {
        if(p instanceof Player)
            Bukkit.dispatchCommand((Player)p, command);
    }

}
