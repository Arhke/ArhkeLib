package com.Arhke.ArhkeLib.Lib.Utils.MultiInstruction;

import com.Arhke.ArhkeLib.Lib.Base.Base;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.Arhke.ArhkeLib.Lib.Base.Base.tcm;

public class PlayerMsg implements MultiInst{
    String msg;


    public PlayerMsg(String msg){
        this.msg = tcm(msg);
    }
    public void apply(OfflinePlayer player){
        if(player instanceof Player){
            Player p = (Player)player;
            p.sendMessage(tcm(p, msg));
        }
    }
}
