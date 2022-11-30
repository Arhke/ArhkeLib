//package com.Arhke.WRCore.District.Commands.DistrictCommand.other;
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
//public class TutorialCommand extends SubCommandsBase{
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.user.general.tutorial")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			p.sendMessage(this.dm.getTCM("a.2"));
//			p.sendMessage(this.dm.getTCM("a.3").replace("{0}", "http://video.feudal.coremod.com"));//SHORT TUTORIAL
//			p.sendMessage("");
//			p.sendMessage(this.dm.getTCM("a.5"));
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.user.general.tutorial",
//				"commandHelp.20")};
//	}
//
//}
