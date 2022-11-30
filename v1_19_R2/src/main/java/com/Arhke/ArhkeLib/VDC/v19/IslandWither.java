package com.Arhke.ArhkeLib.VDC.v19;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.Wither;

import java.util.Collection;

public class IslandWither extends Wither {
    public IslandWither(Location loc) {
        super(((CraftWorld)loc.getWorld()).getHandle());

        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        Collection goalB = (Collection)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        Collection goalC = (Collection)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        Collection targetB = (Collection)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        Collection targetC = (Collection)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, false, false));

    }
    public IslandWither(World world) {
        super(world);
        Collection goalB = (Collection)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        Collection goalC = (Collection)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        Collection targetB = (Collection)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        Collection targetC = (Collection)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, false, false));
    }
    @Override
    public void a(int i, int j){
//        Bukkit.broadcastMessage("a " + i + " "+j);
        if (i==0){
            super.a(i,j);
            super.a(1,j);
            super.a(2,j);
            return;
        }
        if((i == 1 || i == 2) && j != 0){
            return;
        }
        super.a(i,j);


    }
    @Override
    public void dropDeathLoot(boolean flag, int i){
    }
    @Override
    public SoundEffect F() {
        return null;
    }
    @Override
    public SoundEffect cf() {
        return null;
    }


}
