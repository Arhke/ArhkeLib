//package com.Arhke.WRCore.District.Commands.DistrictCommand.admin;
//
//import com.Arhke.WRCore.District.kingdoms.Battle;
//import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class ForceWinCommand extends SubCommandsBase {
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.admin.kingdoms.forcewin")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			if(args.length == 3){
//				Kingdom attacker = null;
//				for(Kingdom k : Feudal.getKingdoms()){
//					if(k.getName().equalsIgnoreCase(args[1])){
//						attacker = k;
//					}
//				}
//				if(attacker == null){
//					//Gets kingdom by players name
//					String uuid = getPlayerByUUID(args[1]);
//					if(uuid != null){
//						attacker = Feudal.getPlayerKingdom(uuid);
//					}
//				}
//				if(attacker == null){
//					p.sendMessage(this.dm.getTCM("commands.2755"));
//					return true;
//				}
//
//				Kingdom defender = null;
//				for(Kingdom k : Feudal.getKingdoms()){
//					if(k.getName().equalsIgnoreCase(args[2])){
//						defender = k;
//					}
//				}
//				if(defender == null){
//					//Gets kingdom by players name
//					String uuid = getPlayerByUUID(args[2]);
//					if(uuid != null){
//						defender = Feudal.getPlayerKingdom(uuid);
//					}
//				}
//				if(defender == null){
//					p.sendMessage(this.dm.getTCM("commands.2773"));
//					return true;
//				}
//
//				Battle battle = Feudal.getChallenge(attacker, defender);
//				if(battle == null){
//					battle = Feudal.getChallenge(defender, attacker);
//				}
//				if(battle != null){
//					battle.win(attacker, "\u00a74\u00a7lForce win by: \u00a77" + p.getName(), false);
//					p.sendMessage(this.dm.getTCM("commands.2783").replace("{1}", defender.getName() + "").replace("{0}", attacker.getName() + ""));
//				}else{
//					p.sendMessage(this.dm.getTCM("commands.2785").replace("{1}", defender.getName() + "").replace("{0}", attacker.getName() + ""));
//				}
//			}else{
//				p.sendMessage(this.dm.getTCM("commands.2788"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.kingdoms.forcewin",
//				"commandHelp.140")};
//	}
//
//}
