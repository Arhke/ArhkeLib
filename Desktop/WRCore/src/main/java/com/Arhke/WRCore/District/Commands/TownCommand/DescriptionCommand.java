//package com.Arhke.WRCore.District.Commands.DistrictCommand;
//
//import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
//import org.bukkit.entity.Player;
//
//import com.Arhke.WRCore.District.Commands.Arg;
//import com.Arhke.WRCore.Lib.Base.Command;
//import com.Arhke.WRCore.Lib.Utils.HelpMessage;
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.kingdoms.Rank;
//import com.Arhke.WRCore.District.TUsers.classes.Profession.Type;
//
///**
// *
// * @author Michael Forseth
// *
// */
//public class DescriptionCommand extends SubCommandsBase {
//
//	@Override
//	public CommandType getType() {
//		return CommandType.PLAYER;
//	}
//
//	@Override
//	public boolean run(String[] args, Player p) {
//		if(isArgument(args[0])){
//			if(!p.hasPermission("feudal.commands.user.kingdoms.description")){
//				p.sendMessage("&cYou do not have permission.");
//				return true;
//			}
//
//			TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
//			if(u == null){
//				p.sendMessage(this.dm.getTCM("commands.2242"));
//				return true;
//			}
//			if(u.getTown() == null || u.getTown().getKingdom() == null){
//				p.sendMessage(this.dm.getTCM("commands.2246"));
//				return true;
//			}
//			Kingdom kingdom = u.getTown().getKingdom();
//			if(kingdom == null){
//				p.sendMessage(this.dm.getTCM("commands.2251"));
//				return true;
//			}
//			Rank rank = kingdom.getRank(u.getUUID());
//			if(rank == null){
//				p.sendMessage(this.dm.getTCM("commands.2256"));
//				return true;
//			}
//
//			if(args.length > 1){
//				if(rank.equals(Rank.EXECUTIVE) || rank.equals(Rank.LEADER)){
//					String description = args[1];
//					if(args.length > 2){
//						for(int i = 2; i < args.length; i++){
//							description += " " + args[i];
//						}
//					}
//					kingdom.setDescription(description);
//					/*try {
//						kingdom.save();
//					} catch (Exception e) {
//						p.sendMessage(this.dm.getTCM("commands.2272"));
//						ErrorManager.error(35, e);
//						return true;
//					}*/
//					p.sendMessage(this.dm.getTCM("commands.2276").replace("{0}", description + ""));
//				}else{
//					p.sendMessage(this.dm.getTCM("commands.2278"));
//				}
//			}else{
//				p.sendMessage(this.dm.getTCM("commands.2281").replace("{0}", kingdom.getDescription() + ""));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public HelpMessage[] getHelpMessage() {
//		return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.description",
//				"commandHelp.70")};
//	}
//
//}
