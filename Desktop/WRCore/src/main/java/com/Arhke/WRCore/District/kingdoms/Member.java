//package com.Arhke.WRCore.District.kingdoms;
//
//import java.util.UUID;
//
//import com.Arhke.WRCore.District.TUsers.TUser;
//import com.mojang.authlib.yggdrasil.response.User;
//import org.bukkit.Bukkit;
//import org.bukkit.OfflinePlayer;
//
//import com.Arhke.WRCore.District.core.Feudal;
//
///**
// * Contain member information for a kingdom
// *
// * @author Michael Forseth
// * @version Mar 9, 2018
// *
// */
//public class Member implements Comparable<Member> {
//
//	private OfflinePlayer player;
//	private TUser user;
//	private Rank rank;
//	private boolean fighter;
//
//	public Member(String uuid, Rank rank) throws Exception {
//		player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
//
//		if(player.isOnline()) {
//			user = Feudal.getUser(uuid);
//		}
//
//		this.rank = rank;
//		this.fighter = fighter;
//	}
//
//	public boolean isFighter() {
//		return fighter;
//	}
//
//	public OfflinePlayer getPlayer() {
//		return player;
//	}
//
//	public TUser getUser() {
//		if(user == null) {
//			return Feudal.getUser(player.getUniqueId().toString());
//		}
//		return user;
//	}
//
//	public Rank getRank() {
//		return rank;
//	}
//
//	public boolean isOnline() {
//		return player.isOnline();
//	}
//
//	@Override
//	public int compareTo(Member member) {
//		if(this.isOnline() && !member.isOnline()) {
//			return 1;
//		}else if(!this.isOnline() && member.isOnline()) {
//			return -1;
//		}else {
//			return this.rank.ID - member.rank.ID;
//		}
//	}
//
//	public boolean isSameMember(Member member) {
//		if(user != null && member.getUser() != null && member.getUser().getUUID().equals(this.getUser().getUUID())) {
//			return true;
//		}else {
//			return false;
//		}
//	}
//
//	public void setTempRank(Rank newRank) {
//		rank = newRank;
//	}
//
//}
