package com.Arhke.ArhkeLib.Lib.Base;

import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpMessage;
import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpPerm;
import com.Arhke.ArhkeLib.Lib.FileIO.DataManager;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * 
 * @author William Lin
 *
 */
public abstract class SubCommandsBase<T extends JavaPlugin> extends MainBase<T>{
	protected DataManager dm;
	protected String commandName;
	protected CommandType ct = CommandType.PLAYER;
	public static final String CommandAlias = "commandAlias", HelpKey = "subCommandHelpString";

	/**
	 * @param dm the DataManager of CommandsBase
	 */
	public SubCommandsBase(T instance, String commandName, DataManager dm){
		super(instance);
		this.commandName = commandName.toLowerCase();
		this.dm = dm.getDataManager(commandName);
		dm.isOrDefault(this.commandName, CommandAlias);
		this.commandName = dm.getString(CommandAlias).toLowerCase();
		setDefaults();
		dm.isOrDefault("/{0} {1} &7- &6this is the default help message", HelpKey);
	}
	/**
	 * @return specifies the SubCommand Name that would be checked.
	 */
	public String getCommandName(){
		return this.commandName;
	}


	public CommandType getType(){
		return ct;
	}
	public void addHelpMessage(Consumer<HelpPerm> hm){ hm.accept(new HelpPerm(dm.getString(HelpKey).replace("{1}", this.getCommandName())));}
	public abstract boolean run(String[] args, Player p);
	public abstract void setDefaults();
	public enum CommandType{
		PLAYER, SERVER;
	}
	
}
