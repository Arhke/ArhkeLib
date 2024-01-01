package com.Arhke.ArhkeLib.Lib.Base;

import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpPerm;
import com.Arhke.ArhkeLib.Lib.FileIO.DataManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author William Lin
 *
 */
public abstract class SubCommandsBase{
	protected String permission;
	protected DataManager dm;
	protected String commandName;
	protected CommandType ct = CommandType.PLAYER;
	public static final String CommandAlias = "commandAlias", HelpKey = "subCommandHelpString", PermissionKey = "permKey";

	public SubCommandsBase(CommandsBase cb, String commandName){
		this.commandName = commandName.toLowerCase();
		this.dm = cb.dm.getDataManager(commandName);
		this.dm.isOrDefault(this.commandName, CommandAlias);
		this.commandName = this.dm.getString(CommandAlias).toLowerCase();
		setDefaults();
		this.permission = dm.getDefString("", PermissionKey);
		this.dm.isOrDefault("&6/{0} {1} &7- &6this is the default help message", HelpKey);
	}
	/**
	 * @return specifies the SubCommand Name that would be checked.
	 */
	public String getCommandName(){
		return this.commandName;
	}
	public void setPermission(String permission){
		this.permission = permission;
	}

	public CommandType getType(){
		return ct;
	}
	public void setType(CommandType ct){
		this.ct = ct;
	}
	public HelpPerm getHelpPerm(){
		if(permission.length() != 0){
			return new HelpPerm(dm.getString(HelpKey).replace("{1}", this.getCommandName()),permission);
		}
		return new HelpPerm(dm.getString(HelpKey).replace("{1}", this.getCommandName()));
	}
	public abstract boolean run(String[] args, Player p);
	public List<String> tab(String[] args, Player p) {return new ArrayList<>();}
	public abstract void setDefaults();
	public enum CommandType{
		PLAYER, SERVER;
	}
	
}
