package com.Arhke.WRCore.District.Commands;

import org.bukkit.entity.Player;

/**
 * Used if a command uses more than just permissions
 * 
 * @author Michael Forseth
 * @version Apr 15, 2018
 *
 */
public interface  CustomHelpPermissions {

	public boolean hasPermission(Player player);
	
}
