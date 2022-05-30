package com.Arhke.ArhkeLib.Lib.Utils.MultiInstruction;

import com.Arhke.ArhkeLib.Lib.Base.Base;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.Arhke.ArhkeLib.Lib.Base.Base.tcm;

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
