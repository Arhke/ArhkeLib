package com.Arhke.ArhkeLib.Utils.MultiInstruction;


import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.Arhke.ArhkeLib.Base.Base.tcm;


public class PlayerPerm implements MultiInst {
    static{
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            perm = permissionProvider.getProvider();
        }
    }
    static Permission perm;
    String permissionString;
    public PlayerPerm(String content) {
        this.permissionString = content;
    }
    @Override
    public void apply(OfflinePlayer p) {
        if(perm != null) {
            perm.playerAdd(null, p, tcm(p, permissionString));
        }
    }
}
