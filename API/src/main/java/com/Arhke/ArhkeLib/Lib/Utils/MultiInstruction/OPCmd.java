package com.Arhke.ArhkeLib.Lib.Utils.MultiInstruction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class OPCmd implements  MultiInst {
    String command;
    public OPCmd(String content) {
        this.command = content.charAt(0) == '/'? content.substring(1):content;
    }

    @Override
    public void apply(OfflinePlayer player) {
        if(player instanceof Player) {
            Player p = (Player)player;
            if (p.isOp()) {
                p.performCommand(command);
            } else {
                p.setOp(true);
                p.performCommand(command);
                p.setOp(false);
            }
        }
    }

}


