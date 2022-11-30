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
//public class AboutCommand extends SubCommandsBase{
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			p.sendMessage(this.dm.getTCM("commands.78"));
//			p.sendMessage("\u00a76\u00a7lCreated By: \u00a77\u00a7lMichael Forseth \u00a77(SpigotMC: Forseth11)");
//			p.sendMessage(this.dm.getTCM("commands.81"));
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.user.general",
//				"commands.3344")};
//	}
//
//}
