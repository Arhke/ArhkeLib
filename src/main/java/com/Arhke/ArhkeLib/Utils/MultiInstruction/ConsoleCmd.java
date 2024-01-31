package com.Arhke.ArhkeLib.Utils.MultiInstruction;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import static com.Arhke.ArhkeLib.Base.Base.tcm;

public class ConsoleCmd implements MultiInst {
    String command;
    public ConsoleCmd(String content) {
        this.command = content.charAt(0) == '/'? content.substring(1):content;
    }
    @Override
    public void apply(OfflinePlayer p) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), tcm(p, command, p.getName()));
    }
}
