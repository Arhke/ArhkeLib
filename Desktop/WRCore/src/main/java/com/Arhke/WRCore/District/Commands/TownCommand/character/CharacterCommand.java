//package com.Arhke.WRCore.District.Commands.DistrictCommand.character;
//
//import java.util.ArrayList;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.TUsers.classes.Profession;
//import com.Arhke.WRCore.District.TUsers.classes.Profession.Type;
//import com.Arhke.WRCore.District.util.ErrorManager;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class CharacterCommand extends SubCommandsBase{
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.user.character.character")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			if(args.length == 2){
//				String player = args[1];
//				String uuid = null;
//				for(Player pla : Bukkit.getOnlinePlayers()){
//					if(pla.getName().equalsIgnoreCase(player)){
//						uuid = pla.getUniqueId().toString();
//						break;
//					}
//				}
//				if(uuid == null){
//					if(Feudal.getPlayerData() == null){
//						try{
//							Configs.playerData();
//						}catch(Exception e){
//							ErrorManager.error(98, e);
//						}
//					}
//					if(Feudal.getPlayerData() != null){
//						uuid = Feudal.getPlayerData().getConfig().getString(player.toLowerCase());
//					}
//				}
//				if(uuid == null){
//					p.sendMessage(this.dm.getTCM("a.12").replace("%p%", player));
//				}else{
//					User u = Feudal.getUser(uuid);
//					if(u != null){
//						p.sendMessage(this.dm.getTCM("commands.158").replace("{0}", u.getName() + ""));
//						if(u.getProfession() != null && !u.getProfession().getType().equals(Type.NONE)){
//							p.sendMessage(this.dm.getTCM("commands.160").replace("{0}", u.getProfession().getType().getNameLang() + ""));
//							p.sendMessage(this.dm.getTCM("commands.professionLevel").replace("%level%", u.getProfession().getLevel()+"").replace("%max%", u.getProfession().getMaxLevel()+"").replace("%xp%", u.getProfession().getXPToNextLevel()+"")); //Profession level: 1000 / 1000 - 10000 XP Needed
//							//Strength: 300 / 1000 - 1000XP (DMG:1.2x, HASTE:1)
//
//							if(Feudal.getConfiguration().getConfig().getBoolean("attributeCap.enable")){
//							}
//
//							Kingdom kingdom = Feudal.getPlayerKingdom(uuid);
//							if(kingdom != null){
//								p.sendMessage(this.dm.getTCM("commands.170").replace("{0}", kingdom.getName() + ""));
//							}else{
//								p.sendMessage(this.dm.getTCM("commands.172"));
//							}
//							p.sendMessage(this.dm.getTCM("commands.174").replace("{0}", u.getReputation() + ""));
//							p.sendMessage(this.dm.getTCM("commands.175").replace("{0}", getLastOnlineTime((u.getPlayer() != null && u.getPlayer().isOnline()), u.getLastOnline()) + ""));
//
//							ArrayList<Profession> past = u.getPastProfessions();
//							if(past.size() > 0){
//								String list = past.get(0).getType().getNameLang() + ":" + past.get(0).getLevel();
//								if(past.size() > 1){
//									for(int i = 1; i < past.size(); i++){
//										list += ", " + past.get(i).getType().getNameLang() + ":" + past.get(i).getLevel();
//									}
//								}
//								p.sendMessage(this.dm.getTCM("commands.185").replace("{0}", list + ""));
//							}
//						}else{
//							p.sendMessage(this.dm.getTCM("a.13"));
//						}
//					}else{
//						p.sendMessage(this.dm.getTCM("a.14").replace("%p%", player));
//					}
//				}
//			}else{
//				TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
//				if(u != null){
//					p.sendMessage(this.dm.getTCM("commands.197").replace("{0}", u.getName() + ""));
//					if(u.getProfession() != null && !u.getProfession().getType().equals(Type.NONE)){
//						p.sendMessage(this.dm.getTCM("commands.199").replace("{0}", u.getProfession().getType().getNameLang() + ""));
//						p.sendMessage(this.dm.getTCM("commands.professionLevel").replace("%level%", u.getProfession().getLevel()+"").replace("%max%", u.getProfession().getMaxLevel()+"").replace("%xp%", u.getProfession().getXPToNextLevel()+"")); //Profession level: 1000 / 1000 - 10000 XP Needed
//						//Strength: 300 / 1000 - 1000XP (DMG:1.2x, HASTE:1)
//
//						if(Feudal.getConfiguration().getConfig().getBoolean("attributeCap.enable")){
//						}
//
//						Kingdom kingdom = Feudal.getPlayerKingdom(p.getUniqueId().toString());
//						if(kingdom != null){
//							p.sendMessage(this.dm.getTCM("commands.209").replace("{0}", kingdom.getName() + ""));
//						}else{
//							p.sendMessage(this.dm.getTCM("commands.211"));
//						}
//						p.sendMessage(this.dm.getTCM("commands.213").replace("{0}", u.getReputation() + ""));
//						p.sendMessage(this.dm.getTCM("commands.214").replace("{0}", getLastOnlineTime((u.getPlayer() != null && u.getPlayer().isOnline()), u.getLastOnline()) + ""));
//
//						ArrayList<Profession> past = u.getPastProfessions();
//						if(past.size() > 0){
//							String list = past.get(0).getType().getNameLang() + ":" + past.get(0).getLevel();
//							if(past.size() > 1){
//								for(int i = 1; i < past.size(); i++){
//									list += ", " + past.get(i).getType().getNameLang() + ":" + past.get(i).getLevel();
//								}
//							}
//							p.sendMessage(this.dm.getTCM("commands.224").replace("{0}", list + ""));
//						}
//					}else{
//						p.sendMessage(Feudal.getMessage("a.13"));
//					}
//				}else{
//					p.sendMessage(Feudal.getMessage("a.ufail"));
//				}
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.user.character.character",
//				"commandHelp.30")};
//	}
//
//}
