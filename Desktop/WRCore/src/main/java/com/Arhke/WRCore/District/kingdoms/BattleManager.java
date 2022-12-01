package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Listeners;
import com.Arhke.WRCore.Main;
import net.minecraft.server.v1_12_R1.EntityWither;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftWither;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class BattleManager extends MainBase {
    HashMap<Town, Battle> battles = new HashMap<>();
    public BattleManager(Main instance){
        super(instance);
    }

    /**
     * adds and starts a new battle
     * @param k battles for a kingdom
     * @return whether or not the battle was successfully added.
     */
    public void startBattle(Kingdom k){
        for (Town t: k.getTowns()){
            if (battles.get(t) == null) {
                Battle battle = new Battle(getPlugin(), t);
                battles.put(t, battle);
                battle.start();
            }
        }
    }

    /**
     * @param town
     * @return the defending battle associated with the town, or null if not found
     */
    public Battle getDefendingBattle(Town town){
        return battles.get(town);
    }

    public void endKingdomBattles(Kingdom kingdom) {
        Iterator<Map.Entry<Town, Battle>> iter = this.battles.entrySet().iterator();
        while(iter.hasNext()){
            Battle battle = iter.next().getValue();
            if(!battle.getDefenderTown().getKingdom().equals(kingdom))
                continue;
            battle.defenderWin();
            iter.remove();
        }
    }

    public Collection<Battle> getBattles(){
        return battles.values();
    }

    /**
     * cleans up left over battles when the server turns off
     */
    public void cleanUpWithers(){
        List<Entity> witherRemove = new ArrayList<>();
        Bukkit.getWorlds().forEach(world->{
            world.getEntities().forEach(e-> {
                if(e instanceof CraftArmorStand && e.getPassengers().stream().anyMatch(passenger-> passenger instanceof CraftWither)) {
                    witherRemove.addAll(e.getPassengers());
                    e.remove();
                }
            });


        });
        for (Entity entity : witherRemove) {
            entity.remove();
        }
    }



    public Optional<Battle> getBattleByWither(IslandWither iw){
        return this.battles.values().stream().filter(battle -> battle.getWither().equals(iw.getBukkitEntity().getUniqueId())).findFirst();
    }

}
