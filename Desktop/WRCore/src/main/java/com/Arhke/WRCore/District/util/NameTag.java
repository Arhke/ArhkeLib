package com.Arhke.WRCore.District.util;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Main;
import me.DeeCaaD.SurvivalMechanics.PlayerQuit;
import net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_12_R1.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;

public class NameTag extends MainBase implements Listener {

    private final Scoreboard scoreboard;
    private final Team friendlyTeam;
    private final Team allyTeam;
    private final Team neutralTeam;

    public final String friendlies = "friendlies", allies = "allies", neutrals = "neutrals";
    public NameTag(Main instance)  {
        super(instance);
        // We register the events

        // We create the scoreboard and teams
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        friendlyTeam = scoreboard.registerNewTeam(friendlies);
        allyTeam = scoreboard.registerNewTeam(allies);
        neutralTeam = scoreboard.registerNewTeam(neutrals);

//        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
//        try{
//            scoreboard.getTeam(friendlies).unregister();
//        }catch(IllegalArgumentException|NullPointerException ignored){}
//        friendlyTeam = scoreboard.registerNewTeam(friendlies);
        friendlyTeam.setPrefix(ChatColor.GREEN+"");
//        try{
//            scoreboard.getTeam(allies).unregister();
//        }catch(IllegalArgumentException|NullPointerException ignored){}
//        allyTeam = scoreboard.registerNewTeam(allies);
        allyTeam.setPrefix(ChatColor.AQUA+"");
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        registerPlayerNameTag(e.getPlayer());
    }
    public void onPlayerQuit(PlayerQuitEvent e) {
        unregisterPlayerNameTag(e.getPlayer());
    }
    public void registerPlayerNameTag(final Player eventPlayer){
        eventPlayer.setScoreboard(scoreboard);
        TUser u = getPlugin().getTUserManager().getOrNewTUser(eventPlayer);
        if (u.getKingdom() == null){
            return;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<String> friendly = new ArrayList<>();
        List<String> ally = new ArrayList<>();
        new BukkitRunnable(){
            @Override

            public void run() {
                for(Player player: players)


                {
                    info(player.getName());
                    if (!player.isOnline())
                        continue;
                    TUser user;
                    synchronized(getPlugin().getTUserManager()) {
                        user = getPlugin().getTUserManager().getOrNewTUser(player);
                    }
                    if (user.getKingdom() == null){
                        return;
                    }
                    if(user.getKingdom().equals(u.getKingdom())){
                        friendly.add(player.getName());
                        if (player.isOnline()){
                            addPlayersToFriendlies(player, new ArrayList<>(Collections.singleton(eventPlayer.getName())));
                        }
                    }else if (user.getKingdom().isAllied(u.getKingdom())){
                        ally.add(player.getName());
                        addPlayersToAllies(player, new ArrayList<>(Collections.singleton(eventPlayer.getName())));
                    }
                }
                if(eventPlayer.isOnline()){
                    if (ally.size() != 0) {
                        addPlayersToAllies(eventPlayer, ally);
                    }
                    if(friendly.size() != 0) {
                        addPlayersToFriendlies(eventPlayer, friendly);
                    }

                }
            }
        }.runTaskAsynchronously(getPlugin());
    }
    public void unregisterPlayerNameTag(final Player eventPlayer){
        eventPlayer.setScoreboard(scoreboard);
        List<String> playerNames = new ArrayList<>();
        for(Player player: Bukkit.getOnlinePlayers()){
            addPlayersToNeutrals(player, Collections.singletonList(eventPlayer.getName()));
            playerNames.add(player.getName());
        }
        addPlayersToNeutrals(eventPlayer, playerNames);

    }
    public synchronized void addPlayersToFriendlies(Player player, List<String> friendlies) {
        net.minecraft.server.v1_12_R1.Scoreboard nmsScoreboard = new net.minecraft.server.v1_12_R1.Scoreboard();
        ScoreboardTeam nmsTeam = new ScoreboardTeam(nmsScoreboard, friendlyTeam.getName());
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(nmsTeam, friendlies, 3);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }

    public synchronized void addPlayersToAllies(Player player, List<String> allies) {
        net.minecraft.server.v1_12_R1.Scoreboard nmsScoreboard = new net.minecraft.server.v1_12_R1.Scoreboard();
        ScoreboardTeam nmsTeam = new ScoreboardTeam(nmsScoreboard, allyTeam.getName());
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(nmsTeam, allies, 3);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }
    public synchronized void addPlayersToNeutrals(Player player,  List<String> neutrals){
        net.minecraft.server.v1_12_R1.Scoreboard nmsScoreboard = new net.minecraft.server.v1_12_R1.Scoreboard();
        ScoreboardTeam nmsTeam = new ScoreboardTeam(nmsScoreboard, neutralTeam.getName());
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(nmsTeam, neutrals, 3);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }
    public synchronized void setKingdomsToAllies(List<Player> k1, List<Player> k2){
        List<String> player1 = new ArrayList<>();
        List<String> player2 = new ArrayList<>();
        k1.forEach(p-> player1.add(p.getName()));
        k2.forEach(p-> player2.add(p.getName()));
        k1.forEach(p->addPlayersToAllies(p, player2));
        k2.forEach(p->addPlayersToAllies(p, player1));
    }
    public synchronized void setKingdomsToNeutrals(List<Player> k1, List<Player> k2){
        List<String> player1 = new ArrayList<>();
        List<String> player2 = new ArrayList<>();
        k1.forEach(p-> player1.add(p.getName()));
        k2.forEach(p-> player2.add(p.getName()));
        k1.forEach(p->addPlayersToNeutrals(p, player2));
        k2.forEach(p->addPlayersToNeutrals(p, player1));
    }
}