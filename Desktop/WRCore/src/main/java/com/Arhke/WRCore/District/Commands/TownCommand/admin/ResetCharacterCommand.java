//package com.Arhke.WRCore.District.Commands.DistrictCommand.admin;
//
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.kingdoms.Rank;
//import com.Arhke.WRCore.Lib.sql.UserSave;
//import com.Arhke.WRCore.District.TUsers.classes.Profession;
//import com.Arhke.WRCore.District.TUsers.classes.Profession.Type;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class ResetCharacterCommand extends SubCommandsBase{
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.admin.character.resetcharacter")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			if(args.length == 2){
//				String uuid = getPlayerByUUID(args[1]);
//				if(uuid == null){
//					p.sendMessage(this.dm.getTCM("a.12").replace("%p%", args[1]));
//				}else{
//					User u = Feudal.getUser(uuid);
//					if(u != null){
//						boolean online = (u.getPlayer() != null && u.getPlayer().isOnline());
//						if(online){
//							Feudal.unloadPlayer(u.getPlayer());
//						}
//						if(!u.getKingdomUUID().equals("")){
//							Kingdom king = Feudal.getKingdom(u.getKingdomUUID());
//							Rank rank = king.getRank(u.getUUID());
//							if(rank != null && rank.equals(Rank.LEADER)){
//								Feudal.getKingdoms().remove(king);
//								king.delete(p.getName());
//								king.removeAllMembers();
//							}else{
//								king.removeMember(u.getUUID());
//							}
//							u.setKingdomUUID("");
//						}
//						if(u.getConfig() != null) {
//							u.getConfig().getFile().delete();
//						}else if(Feudal._feudal.doesUseSql()) {
//							UserSave.delete(u.getUUID());
//						}
//						u.unloadPermissions();
//						u.setProfession(new Profession(u, Type.NONE, 0, 0));
//						u.effectAttributes();
//						if(online){
//							Feudal.logPlayer(u.getPlayer().getName(), u.getPlayer().getUniqueId());
//							Feudal.loadPlayer(u.getPlayer());
//						}
//						p.sendMessage(this.dm.getTCM("").replace("{0}", u.getName() + ""));
//					}else{
//						p.sendMessage(this.dm.getTCM("").replace("{0}", args[1] + ""));
//					}
//				}
//			}else{
//				p.sendMessage(this.dm.getTCM("commands.858"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.character.resetcharacter",
//				"commandHelp.136")};
//	}
//
//}
