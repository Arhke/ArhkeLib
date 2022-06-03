package com.Arhke.ArhkeLib.Lib.Base;

import com.Arhke.ArhkeLib.Lib.FileIO.DataManager;
import com.Arhke.ArhkeLib.Lib.Utils.HelpMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author William Lin
 *
 */
public abstract class SubCommandsBase<T extends JavaPlugin> extends MainBase<T>{
	protected DataManager dm;
	protected String commandName;
	public static final String ArgKey = "arg";
	public SubCommandsBase(T instance, String commandName, DataManager dm){
		super(instance);
		this.commandName = commandName.toLowerCase();
		this.dm = dm.getDataManager(this.commandName);
//		setHelpMessage();
		setArgumentsDefault();
		setDefaults();
	}
	/**
	 * @return specifies the SubCommand Name that would be checked.
	 */
	public String getCommandName(){
		return this.commandName;
	}

//	private void setHelpMessage(){
//		HelpMessage[] msgs = getHelpMessage();
//		int count = 0;
//		for(HelpMessage msg : msgs){
//			if(msg != null){
//				boolean customIsPerms = this instanceof CustomHelpPermissions;
//				if(customIsPerms) {
//					msg.setCustomHelpPerms((CustomHelpPermissions) this);
//				}
//				if(this.getType() == CommandType.SERVER) {
//					continue;
//				}
//				if(this instanceof CustomCommand){
//					int index = ((CustomCommand) this).getHelpIndex();
//					if(index >= 0){
////						CommandHelp.getHelpMessages().add(index + count, msg);
//						count++;
//					}
//				}else{
////					CommandHelp.getHelpMessages().add(msg);
//				}
//			}
//		}
//	}

	protected boolean isAlpha(String name) {
		char[] chars = name.toLowerCase().toCharArray();
		for (char c : chars) {
			if(c < 97 || c > 122) {
				return false;
			}
		}

		return true;
	}

	protected void setArgumentsDefault() {
		ConfigurationSection cs = dm.getConfig();
		if(!cs.isString(ArgKey))
			cs.set(ArgKey, getCommandName());
	}
	protected boolean isArgument(String arg) {
		String[] args = dm.getString(ArgKey).split(" ");
		for(String s : args) {
			if(s.equalsIgnoreCase(arg)) {
				return true;
			}
		}
		return false;
	}
	protected boolean isArgument(String argKey, String arg) {
		if(!dm.getConfig().isString(argKey)){
			return argKey.equalsIgnoreCase(arg);
		}
		String[] args = dm.getString(argKey).split(" ");
		for(String s : args) {
			if(s.equalsIgnoreCase(arg)) {
				return true;
			}
		}
		return false;
	}

	protected String getLastOnlineTime(boolean online, long lastOnline) {
		if(online){
			return "ONLINE NOW";
		}
		long rem = System.currentTimeMillis() - lastOnline;
		long years = rem / 31557600000L;
		long weeks = (rem - (years * 31557600000L)) / 604800000L;
		long days = (rem-(years * 31557600000L)-(weeks * 604800000L)) / 86400000;
		long hours = ((rem - (days * 86400000) - (years * 31557600000L)-(weeks * 604800000L)) / 3600000);
		long minutes = (rem - (days * 86400000) - (hours * 3600000)-(years * 31557600000L)-(weeks * 604800000L)) / 60000;
		long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)-(years * 31557600000L)-(weeks * 604800000L)) / 1000;
		if(years == 0){
			if(weeks == 0){
				return days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
			}else{
				return weeks + (weeks == 1 ? " week" : " weeks") + ", " + days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
			}
		}else{
			return years + (years == 1 ? " year" : " years") + ", " + weeks + (weeks == 1 ? " week" : " weeks") + ", " + days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
		}
	}
//	protected String getPlayerByUUID(String player) {//Gets players uuid from name
//		String uuid = null;
//		for(Player pla : Bukkit.getOnlinePlayers()){
//			if(pla.getName().equalsIgnoreCase(player)){
//				uuid = pla.getUniqueId().toString();
//				break;
//			}
//		}
//		if(uuid == null){
//			uuid = getPlugin().getTUserManager().get.getString(player.toLowerCase());
//		}
//		return uuid;

//	}
	public abstract CommandType getType();
	public abstract HelpMessage[] getHelpMessage();
	public abstract boolean run(String[] args, Player p);
	public abstract void setDefaults();
	public enum CommandType{
		PLAYER, SERVER;
	}
	
}
