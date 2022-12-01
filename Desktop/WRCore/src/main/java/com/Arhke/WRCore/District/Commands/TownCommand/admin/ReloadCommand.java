//package com.Arhke.WRCore.District.Commands.DistrictCommand.admin;
//
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class ReloadCommand extends SubCommandsBase{
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.reload",
//				"commandHelp.141")};
//	}
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.admin.reload")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//
//			if(Configs.reload()){
//				p.sendMessage(this.dm.getTCM("reloadconfig.done"));
//			}else{
//				Feudal.error("Failed to reload configs.  See the stack trace above.  Send the stack trace to the Feudal managers to get this resolved.");
//				p.sendMessage(this.dm.getTCM("reloadconfig.error"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//}
