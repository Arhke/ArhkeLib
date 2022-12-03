package com.Arhke.ArhkeLib.Lib.Base.HelpCommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class HelpPerm {

    private final String[] permissions;
    private String message;

    public HelpPerm(String message, String... permissions) {
        this.message = message;
        if(permissions == null){
            this.permissions = new String[0];
        }else {
            this.permissions = permissions;
        }
    }

    public void sendMessage(Player p) {
        if (this.hasPermission(p)) p.sendMessage(message);
    }

    protected boolean hasPermission(Permissible p) {
        for (String perm : permissions) {
            if (p.hasPermission(perm)) {
                return true;
            }
        }
        return false;
    }
    public String getMsg() {
        return message;
    }
    public void setMsg(String msg){
        this.message = msg;
    }

}
