//package com.Arhke.WRCore.District.placeholders;
//
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.Plugin;
//
//import com.Arhke.WRCore.District.core.Feudal;
//import com.Arhke.WRCore.District.kingdoms.Kingdom;
//import com.Arhke.WRCore.District.kingdoms.Rank;
//import com.Arhke.WRCore.District.TUsers.classes.Profession;
//import com.Arhke.WRCore.District.util.ErrorManager;
//import me.clip.placeholderapi.external.EZPlaceholderHook;
//
//public class FeudalPlaceholder extends EZPlaceholderHook{
//
//	public FeudalPlaceholder(Plugin plugin) {
//		super(plugin, "feudal");
//	}
//
//	@Override
//	public String onPlaceholderRequest(Player p, String id) {
//		try{
//			//placeholder: %feudal_kingdom%
//			if(p == null){
//				return "";
//			}
//			TUser u = getPlugin().getTUserManager().getTUser(p.getUniqueId());
//			if(u == null){
//				return "";
//			}
//			Kingdom k = null;
//			if(!u.getKingdomUUID().equals("")){
//				k = Feudal.getKingdom(u.getKingdomUUID());
//			}
//
//			if(id.equalsIgnoreCase("kingdom")){
//				if(k != null){
//					return k.getName();
//				}else{
//					return "";
//				}
//			}else if(id.equalsIgnoreCase("reputation")){
//				return u.getReputation()+"";
//			}else if(id.equalsIgnoreCase("rank") || id.equalsIgnoreCase("rank_less")){
//				if(k != null){
//					if(k.isMember(u.getUUID())){
//						Rank r = k.getMembers().get(u.getUUID());
//						if(r.equals(Rank.GUEST)){
//							return Feudal.getMessage("placeholder.guest");
//						}else if(r.equals(Rank.MEMBER)){
//							return Feudal.getMessage("placeholder.member");
//						}else if(r.equals(Rank.EXECUTIVE)){
//							return Feudal.getMessage("placeholder.executive");
//						}else{
//							return Feudal.getMessage("placeholder.leader");
//						}
//					}
//				}
//				if(id.equalsIgnoreCase("rank_less")) {
//					return Feudal.getMessage("pholder.rankless");
//				}else {
//					return "";
//				}
//			}else if(id.equalsIgnoreCase("profession")){
//				if(u.getProfession() != null && !u.getProfession().getType().equals(Profession.Type.NONE)){
//					return u.getProfession().getType().getNameLang();
//				}else{
//					return "";
//				}
//			}else if(id.equalsIgnoreCase("profession_xp")){
//				if(u.getProfession() != null){
//					return u.getProfession().getXPToNextLevel() + "";
//				}else{
//					return "";
//				}
//			}else if(id.equalsIgnoreCase("profession_level")){
//				if(u.getProfession() != null){
//					return u.getProfession().getLevel() + "";
//				}else{
//					return "";
//				}
//			}else if(id.equalsIgnoreCase("treasury")){
//				if(k != null){
//					return Feudal.round(k.getTreasury())+"";
//				}
//				return "0";
//			}else if(id.equalsIgnoreCase("chat")){//Chat channel
//				if(u.getChat() == 1) {//kingdom
//					return Feudal.getMessage("chat.kingdom");
//				}else if(u.getChat() == 2) {//ally
//					return Feudal.getMessage("chat.ally");
//				}else {//normal
//					return Feudal.getMessage("chat.normal");
//				}
//			}else if(id.equalsIgnoreCase("kingdom_less")){
//				if(k != null){
//					return k.getName();
//				}else{
//					return Feudal.getMessage("pholder.kingdomless");
//				}
//			}else if(id.equalsIgnoreCase("kingdom_members")){
//				if(k != null){
//					return k.getMembersString();
//				}else{
//					return "";
//				}
//			}
//
//			return null;//Keep placeholder as normal since it is not found.
//		}catch(Exception e){
//			ErrorManager.error(78, e);
//			return null;
//		}
//	}
//
//}
