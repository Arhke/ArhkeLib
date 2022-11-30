//package com.Arhke.WRCore.District.Commands.DistrictCommand.console;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.TUsers.classes.Profession.Type;
//import com.Arhke.WRCore.District.util.ErrorManager;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class ServerEditXPLevelCommand extends SubCommandsBase{
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
//		if(args[0].equalsIgnoreCase("addXP") || args[0].equalsIgnoreCase("addLevel")){
//			if(args.length == 4 || args.length == 5){//f addXP/addLevel <player> profession/strength/toughness/speed/stamina/luck/all/attributes <amount>
//				String uuid = getPlayerByUUID(args[1]);
//				if(uuid != null){
//					User u = Feudal.getUser(uuid);
//					if(u != null){
//						if(!u.getProfession().getType().equals(Type.NONE)){
//							int i = 0;
//							try{
//								i = Integer.parseInt(args[3]);
//							}catch(Exception e){
//								return false;
//							}
//							if(i <= 0){
//								return false;
//							}
//							boolean followMaxLevel = false;
//							if(args.length == 5){
//								if(args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("t")){
//									followMaxLevel = true;
//								}
//							}
//							if(args[2].equalsIgnoreCase("profession") || args[2].equalsIgnoreCase("strength") || args[2].equalsIgnoreCase("toughness")
//									|| args[2].equalsIgnoreCase("speed") || args[2].equalsIgnoreCase("stamina") || args[2].equalsIgnoreCase("luck")
//									|| args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("attributes")){
//							//profession/strength/toughness/speed/stamina/luck/all/attributes
//								if(args[0].equalsIgnoreCase("addXP")){
//									if(args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("profession")){
//										u.getProfession().addXp(i, u);
//									}
//
//									try{
//										u.save(true);
//									}catch(Exception e){
//										ErrorManager.error(36, e);
//									}
//								}else if(args[0].equalsIgnoreCase("addLevel")){
//									if(args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("profession")){
//										u.getProfession().setLevel(u.getProfession().getLevel() + i);
//										if(u.getProfession().getLevel() > u.getProfession().getMaxLevel()){
//											u.getProfession().setLevel(u.getProfession().getMaxLevel());
//										}
//									}
//									try{
//										u.save(true);
//									}catch(Exception e){
//										ErrorManager.error(36, e);
//									}
//								}else{
//									Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("commands.3264"));
//								}
//							}else{
//							}
//						}else{
//						}
//					}else{
//						Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("a.12").replace("%p%", args[1]));
//					}
//				}else{
//					Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("a.12").replace("%p%", args[1]));
//				}
//			}else{
//				Bukkit.getConsoleSender().sendMessage(Feudal.getMessage("commands.3264"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//}
