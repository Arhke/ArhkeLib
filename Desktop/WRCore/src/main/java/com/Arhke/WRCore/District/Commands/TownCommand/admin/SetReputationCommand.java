//package com.Arhke.WRCore.District.Commands.DistrictCommand.admin;
//
//import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.util.ErrorManager;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class SetReputationCommand extends SubCommandsBase {
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.admin.character.setreputation")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//			if(args.length == 3){
//				int reputation = 0;
//				try{
//					reputation = Integer.parseInt(args[2]);
//				}catch(Exception e){
//					p.sendMessage(this.dm.getTCM("commands.2989"));
//					return true;
//				}
//				if(reputation < Feudal.getConfiguration().getConfig().getInt("reputation.min")){
//					p.sendMessage(this.dm.getTCM("commands.2993").replace("{0}", Feudal.getConfiguration().getConfig().getInt("reputation.min") + ""));
//				}else if(reputation > Feudal.getConfiguration().getConfig().getInt("reputation.max")){
//					p.sendMessage(this.dm.getTCM("commands.2995").replace("{0}", Feudal.getConfiguration().getConfig().getInt("reputation.max") + ""));
//				}else{
//					String uuid = getPlayerByUUID(args[1]);
//					if(uuid == null){
//						p.sendMessage(this.dm.getTCM("a.12").replace("%p%", args[1]));
//					}else{
//						User u = Feudal.getUser(uuid);
//						if(u != null){
//							int change = reputation - u.getReputation();
//							u.effectReputation(change, this.dm.getTCM("reputation.changed").replace("%player%", p.getName()));
//							try{
//								u.save(true);
//							}catch(Exception e){
//								ErrorManager.error(35, e);
//								p.sendMessage(this.dm.getTCM("commands.3009"));
//								return true;
//							}
//							p.sendMessage(this.dm.getTCM("commands.3012").replace("{1}", reputation + "").replace("{0}", u.getName() + ""));
//						}else{
//							p.sendMessage(this.dm.getTCM("a.12").replace("%p%", args[1]));
//						}
//					}
//				}
//			}else{
//				p.sendMessage(this.dm.getTCM("commands.3019"));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.character.setReputation",
//				"commandHelp.138")};
//	}
//
//}
