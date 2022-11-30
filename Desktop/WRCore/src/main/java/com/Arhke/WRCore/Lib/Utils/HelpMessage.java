package com.Arhke.WRCore.Lib.Utils;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.Commands.CustomHelpPermissions;
import com.Arhke.WRCore.Main;
import org.bukkit.entity.Player;

import com.Arhke.WRCore.District.core.Feudal;

/**
 * 
 * @author Michael Forseth
 *
 */
public class HelpMessage {
	private String[] permissions;
	private String message;
	private CustomHelpPermissions permsCustom;
	
	public HelpMessage(String message, String permission){
		if(Main.getPlugin().getConfig(ConfigLoader.ConfigFile.TOWNCMDLANG).getConfig().contains(message)){
//			this.message = Feudal.getMessage(message).replace("{0}", "\u00a72");
		}else{
			this.message = message.replace("{0}", "\u00a72");
		}
		this.permissions = new String[]{permission};
	}
	
	public HelpMessage(String message, String... permissions){
		if(Main.getPlugin().getConfig(ConfigLoader.ConfigFile.TOWNCMDLANG).getConfig().contains(message)){
//			this.message = Feudal.getMessage(message).replace("{0}", "\u00a72");
		}else{
			this.message = message.replace("{0}", "\u00a72");
		}
		this.permissions = permissions;
	}
	
	public String getMessage(){
		return message;
	}
	
	public boolean hasPermission(Player p){
		if(permsCustom != null) {
			return permsCustom.hasPermission(p);
		}
		for(String perm : permissions){
			if(p.hasPermission(perm)){
				return true;
			}
		}
		return false;
	}

	public void setCustomHelpPerms(CustomHelpPermissions customHelpPermissions) {
		permsCustom = customHelpPermissions;
	}
}
