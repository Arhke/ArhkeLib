//package com.Arhke.WRCore.District.Commands.DistrictCommand.console;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class ServerReloadCommand extends SubCommandsBase{
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{};
//	}
//
//	@Override
//	public CommandType getType() {
//		return CommandType.SERVER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(args[0].equalsIgnoreCase("reload")){
//			if(Configs.reload()){
//				Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("reloadconfig.done"));
//			}else{
//				Feudal.error("Failed to reload configs.  See the stack trace above.  Send the stack trace to the Feudal managers to get this resolved.");
//				Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("reloadconfig.error"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//}
