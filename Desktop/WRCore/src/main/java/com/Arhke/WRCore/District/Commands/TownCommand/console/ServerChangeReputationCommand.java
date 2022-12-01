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
//public class ServerChangeReputationCommand extends SubCommandsBase{
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
//		if(args[0].equalsIgnoreCase("changeReputation")){//f changeReputation <player> <amount>
//			if(args.length == 3){
//				String uuid = getPlayerByUUID(args[1]);
//				if(uuid == null){
//					Bukkit.getConsoleSender().sendMessage(this.dm.getTCM("commands.3238").replace("{0}", args[1] + ""));
//				}else{
//					User u = Feudal.getUser(uuid);
//					if(u != null){
//						int reputation = 0;
//						try{
//							reputation = Integer.parseInt(args[2]);
//						}catch(Exception e){
//							Bukkit.getConsoleSender().sendMessage(this.dm.getTCM("commands.3228"));
//							return true;
//						}
//						if(u.getReputation() + reputation < Feudal.getConfiguration().getConfig().getInt("reputation.min")){
//							reputation = Feudal.getConfiguration().getConfig().getInt("reputation.min") - u.getReputation();
//						}else if(u.getReputation() + reputation > Feudal.getConfiguration().getConfig().getInt("reputation.max")){
//							reputation = Feudal.getConfiguration().getConfig().getInt("reputation.max") - u.getReputation();
//						}
//						u.effectReputation(reputation, this.dm.getTCM("reputation.changed").replace("%player%", "SERVER"));
//						Bukkit.getConsoleSender().sendMessage(this.dm.getTCM("commands.3251").replace("{1}", u.getReputation() + "").replace("{0}", u.getName() + ""));
//					}else{
//						Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("a.12").replace("%p%", args[1]));
//					}
//				}
//			}else{
//				Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("commands.3258"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//}
