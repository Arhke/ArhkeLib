//package com.Arhke.WRCore.District.Commands.DistrictCommand.character;
//
//import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.TUsers.ChangeProfession;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class ChangeProfessionCommand extends SubCommandsBase {
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.user.character.changeprofession")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			if(args.length == 2){
//				if(p.hasPermission("feudal.commands.admin.character.changeprofession")){
//					String uuid = getPlayerByUUID(args[1]);
//					if(uuid == null){
//						p.sendMessage(this.dm.getTCM("a.12").replace("%p%", args[1]));
//					}else{
//						User u = Feudal.getUser(uuid);
//						if(u != null){
//							new ChangeProfession(p, u);
//						}else{
//							p.sendMessage(this.dm.getTCM("commands.498").replace("{0}", args[1] + ""));
//						}
//					}
//				}else{
//					p.sendMessage(this.dm.getTCM("commands.502"));
//				}
//			}else{
//				TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
//				if(u != null){
//					new ChangeProfession(p, u);
//				}else{
//					p.sendMessage(this.dm.getTCM("a.ufail"));
//				}
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.user.character.changeprofession",
//				"commandHelp.32")};
//	}
//
//}
