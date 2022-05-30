package com.Arhke.ArhkeLib.Lib.Utils.Directions;

import org.bukkit.block.BlockFace;

public enum Direction {
    N(BlockFace.NORTH, "North", 'N', 0, -1),
    E(BlockFace.EAST, "East", 'E', 1, 0),
    S(BlockFace.SOUTH, "South", 'S', 0, 1),
    W(BlockFace.WEST, "West", 'W', -1, 0),
    NONE(BlockFace.SELF, "None", '+', 0, 0);

    String name;
    int x, z;
    char code;
    BlockFace bf;
    Direction(BlockFace bf, String name, char code, int x, int z){
        this.code = code;
        this.name = name;
        this.x = x; this.z = z;
        this.bf = bf;
    }
    public static Direction directionOf(float yaw) {
        while(yaw < 0){
            yaw += 360;
        }
        while(yaw > 360){
            yaw -= 360;
        }
        if(315 < yaw || yaw <= 45){
            return Direction.S;
        }else if(45 < yaw && yaw <= 135){
            return Direction.W;
        }else if(135 < yaw && yaw <= 225){
            return Direction.N;
        }else if(225 < yaw){
            return Direction.E;
        }else{
            return Direction.NONE;
        }
    }
    public String getName(){
        return name;
    }
    public char getCode() {
        return code;
    }
    public int getX(){
        return x;
    }
    public int getZ() {
        return z;
    }
    public BlockFace getBlockFace() {
        return bf;
    }
}
