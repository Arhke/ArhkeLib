//package com.Arhke.WRCore.District.Commands.DistrictCommand.admin;
//
//import java.util.ArrayList;
//
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.TUsers.classes.Profession;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class SetProfessionLevelCommand extends SubCommandsBase{
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.admin.character.setprofessionlevel")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			if(args.length == 2 || args.length == 3){
//				int level = 0;
//				try{
//					level = Integer.parseInt(args[1]);
//				}catch(Exception e){
//					p.sendMessage(this.dm.getTCM("commands.522"));
//					return true;
//				}
//				User u = null;
//				if(args.length == 3){
//					String uuid = getPlayerByUUID(args[2]);
//					if(uuid == null){
//						p.sendMessage(this.dm.getTCM("a.12").replace("%p%", args[1]));
//						return true;
//					}else{
//						u = Feudal.getUser(uuid);
//						if(u == null){
//							p.sendMessage(this.dm.getTCM("commands.534").replace("{0}", args[1] + ""));
//							return true;
//						}
//					}
//				}else{
//					u = Feudal.getUser(p.getUniqueId().toString());
//					if(u == null){
//						p.sendMessage(this.dm.getTCM("a.ufail"));
//						return true;
//					}
//				}
//
//				if(u.getProfession() != null){
//					if(level < 0){
//						p.sendMessage(this.dm.getTCM("commands.548"));
//					}else if(u.getProfession().getMaxLevel() < level){
//						p.sendMessage(this.dm.getTCM("commands.550").replace("{0}", u.getProfession().getMaxLevel() + ""));
//					}else{
//						u.getProfession().setLevel(level);
//						if(level >= u.getProfession().getMaxLevel()){
//							u.getProfession().setMax(true);
//						}else{
//							u.getProfession().setMax(false);
//							ArrayList<Profession> remove = new ArrayList<Profession>();
//							for(Profession pro : u.getPastProfessions()){
//								if(pro.getType().equals(u.getProfession().getType())){
//									remove.add(pro);
//								}
//							}
//							for(Profession pro : remove){
//								u.getPastProfessions().remove(pro);
//							}
//						}
//						u.save(true);
//						p.sendMessage(this.dm.getTCM("commands.554").replace("{1}", level + "").replace("{0}", u.getName() + ""));
//					}
//				}else{
//					p.sendMessage(this.dm.getTCM("commands.557").replace("{0}", u.getName() + ""));
//				}
//			}else{
//				p.sendMessage(this.dm.getTCM("commands.560"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.character.setprofessionlevel",
//				"commandHelp.124")};
//	}
//
//}
