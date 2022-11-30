package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.Lib.Base.Base;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author William Lin
 *
 */
public enum Rank {
	/**
	 * Leader has all privilages.
	 * Executive can do everything except kick leader and can not kick other executatives.  They can not promote others to executive.
	 * 
	 * Members can invite and kick guests.  They can not claim land or unclaim land. They can start challenges.  They can not take from treasury.
	 * Guests can not start challenges.
	 * 
	 * Everyone can give to treasury.
	 * 
	 * Higher rank you are, the less reputation subtraction there is for leaving.
	 */
	LEADER(3, "Leader"), EXECUTIVE(2, "Executive"), MEMBER(1, "Member"), GUEST(0, "Guest");
	public final int id;
	public final String name;
	List<Integer> perms = new ArrayList<>();

	Rank(int RankID, String Name) {
		this.name = Name;
		this.id = RankID;
	}
	public static Rank getRank(int id){
		for(Rank rank: Rank.values()){
			if(rank.getID() == id){
				return rank;
			}
		}
		return null;
	}

	public String getName() {
		return this.name;
	}
	public int getID() {
		return this.id;
	}
	public boolean checkPerm(Player player, RankPerm perm){
		if (checkPerm(perm))
			return true;
		player.sendMessage(ChatColor.RED + "You don't have permission to " + perm.getDesc() + "!");
		return false;
	}
	public boolean checkPerm(RankPerm perm){
		return this == Rank.LEADER || this.perms.stream().anyMatch(i -> i == perm.getID());
	}
	public void setPerm(List<String> perms){
		this.perms.clear();
		for(String perm:perms){
			try{
				this.perms.add(RankPerm.valueOf(perm.toUpperCase()).getID());
			}catch(IllegalArgumentException e){
				Base.error("Incorrect Perm Config " + perm + " for " + this.getName());
				e.printStackTrace();
			}
		}
	}

	public static Rank fromString(String string) {
		try{
			return Rank.valueOf(string.toUpperCase());
		}catch(IllegalArgumentException e){
			return Rank.GUEST;
		}
	}
	

	public enum RankPerm{

		BUILD("Build", 1),
		KICK("Kick Members", 4),
		INVITE("Invite Members", 5),
		DEPOSIT("Deposit Money to Bank", 7),
		WITHDRAW("Withdraw Money from Bank", 8),
		VIEWBANK("View Town Bank balance", 9),
		OPENTOWN("Open/Close Town", 10),
		PROMOTE("Promote Town Members", 11),
		DEMOTE("Demote Town Members", 12),
		CHANGENAME("Change Town Name", 13),
		CLAIM("Claim Land", 14),
		UNCLAIM("UnClaim Land", 15),
		SETHOME("Set the Home Plot for the Town", 17),
		CHALLENGEKINGDOM("Challenge a kingdom", 18),
		ACCEPTCHALLENGE("Accept a Challenge", 19),
		SURRENDERCHALLENGE("Surrender your kingdom", 20),
		ADJUSTBANTIME("Adjust the WarTime for your kingdom", 21),


		NEUTRAL("Neutral Other Kingdoms", 100),
		ALLY("Ally Other Kingdoms", 101),
		ENEMY("Enemy Other Kingdoms", 102),
		INVITETOWNS("Invite Towns", 103),
		COFFERVIEW("View Kingdom Treasury", 104),
		COFFERDEPOSIT("Deposit into Kingdom Treasury", 105),
		COFFERWITHDRAW("Withdraw from Kingdom Treasury",106),
		KICKTOWN("Kick towns from kingdom",107),
		TOGGLESHIELD("Toggle Raid Shields On and Off", 108),
		DEMOTEKINGDOM("Demote a player in your kingdom", 109),
		PROMOTEKINGDOM("Promote a player in your kingdom", 110),
		CHANGENAMEKINGDOM("Change the kingdom name", 111),
		BUILDKINGDOM("Build On All Kingdom Land", 112),
		JOINKINGDOM("Join a Kingdom", 113),
		LEAVEKINGDOM("Leave Your Kingdom", 114),
		CREATEKINGDOM("Create a Kingdom from Town", 115),
		BANTIME("Set War Bantime for your kingdom", 116);

		private String permDesc;
		private int permID;

		RankPerm(String PermDesc, int PermID) {
			permDesc = PermDesc;
			permID = PermID;
		}
		public int getID() {
			return permID;
		}
		public String getDesc() {
			return permDesc;
		}

	}

}
