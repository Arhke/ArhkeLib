//package com.Arhke.WRCore.District.Commands.DistrictCommand.character;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.TimeZone;
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
//public class OnlineCommand extends SubCommandsBase{
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.user.character.online")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//
//			if(args.length == 2){
//				String player = args[1];
//				String uuid = getPlayerByUUID(player);
//				if(uuid == null){
//					p.sendMessage(this.dm.getTCM("a.12").replace("%p%", player));
//				}else{
//					User u = Feudal.getUser(uuid);
//					if(u != null){
//						int hourUTC = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);
//						int offset = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - hourUTC; //Math.round((PLAYER TIME HERE - (days * 86400000)) / 3600000);
//						p.sendMessage(this.dm.getTCM("commands.465").replace("{0}", u.getName() + ""));
//						p.sendMessage(this.dm.getTCM("commands.466").replace("{0}", Feudal.niceTime(System.currentTimeMillis()) + ""));
//						p.sendMessage(this.dm.getTCM("commands.467").replace("{0}", Feudal.niceTime(u.getLastOnline()) + ""));
//						double percent = u.getOnlineTime().getOnlinePercent(u.getFirstJoinTime());
//						if(percent != -1){
//							p.sendMessage(this.dm.getTCM("commands.470").replace("{0}", percent + ""));
//						}
//						ArrayList<String> times = u.getOnlineTime().getPlayerOutput(offset);
//						for(String s : times){
//							p.sendMessage(this.dm.getTCM("commands.474").replace("{0}", s + ""));
//						}
//					}else{
//						p.sendMessage(this.dm.getTCM("commands.477").replace("{0}", player + ""));
//					}
//				}
//			}else{
//				p.sendMessage(Feudal.getMessage("commands.481"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.user.character.online",
//				"commandHelp.114")};
//	}
//
//}
