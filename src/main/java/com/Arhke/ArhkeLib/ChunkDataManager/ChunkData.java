package com.Arhke.ArhkeLib.ChunkDataManager;

import com.Arhke.ArhkeLib.FileIO.YamlSerializable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class ChunkData implements YamlSerializable {
    protected World world;
    protected int x, z;
    protected static final ArrayList<Block> marks = new ArrayList<>();
    protected static final Map<PlayerContainer, Long> timePlayer = new HashMap<>();
    public ChunkData(){}
    public ChunkData(Chunk chunk) {
        world = chunk.getWorld();
        x = chunk.getX();
        z = chunk.getZ();
    }
    public World getWorld() {
        return world;
    }
    public int getZ() {
        return z;
    }
    public int getX() {
        return x;
    }
    protected void setZ(int z){
        this.z = z;
    }
    protected void setX(int x){
        this.x = x;
    }
    protected void setWorld(World world){
        this.world = world;
    }
    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        final ChunkData other = (ChunkData) obj;

        return other.getWorld().equals(this.getWorld()) && other.getX() == x && other.getZ() == z;


    }

    /**
     * Check if this land is connected to another piece of land.
     * @param land2
     * @return
     */
    public boolean isConnected(ChunkData land2) {
        if(land2.getWorld().equals(getWorld())){
            if(land2.getX() == this.x){
                return land2.getZ() - 1 == this.z || land2.getZ() + 1 == this.z;
            }else if(land2.getZ() == this.z){
                return land2.getX() - 1 == this.x || land2.getX() + 1 == this.x;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public boolean isIn(Location location){
        return Objects.equals(location.getWorld(), this.getWorld()) &&
                location.getChunk().getX() == this.getX() && location.getChunk().getZ() == this.getZ();
    }

    /**
     * Get all land marks. Marks are blocks used to show a claim.
     * @return
     */
    public static ArrayList<Block> getMarks(){
        return marks;
    }




    public double distanceSquared(Chunk chunk) {
        if(!chunk.getWorld().equals(this.getWorld())) {
            return Double.MAX_VALUE;
        }
        return Math.pow((chunk.getX()-x), 2) + Math.pow((chunk.getZ()-z), 2);
    }
    public double distanceSquared(ChunkData land) {
        if(!land.getWorld().equals(this.getWorld())) {
            return Double.MAX_VALUE;
        }
        return Math.pow((land.getX()-x), 2) + Math.pow((land.getZ()-z), 2);
    }

    public void show(JavaPlugin jp,Player p) {

        List<PlayerContainer> remove = new ArrayList<PlayerContainer>();
        for(PlayerContainer uuid : timePlayer.keySet()) {
            if(System.currentTimeMillis() - timePlayer.get(uuid) > 9000) {
                remove.add(uuid);
            }
        }
        for(PlayerContainer u : remove) {
            timePlayer.remove(u);
        }

        PlayerContainer pc = new PlayerContainer(p.getUniqueId(), this);
        for(PlayerContainer con : timePlayer.keySet()) {
            if(con.equals(pc)) {
                return;
            }
        }

        timePlayer.put(pc, System.currentTimeMillis());

        int y = p.getEyeLocation().getBlockY();
        Chunk c = p.getWorld().getChunkAt(x, z);
        final Block[] blocks = new Block[]{c.getBlock(0, y, 0),
                c.getBlock(2, y, 0),
                c.getBlock(4, y, 0),
                c.getBlock(6, y, 0),
                c.getBlock(8, y, 0),
                c.getBlock(10, y, 0),
                c.getBlock(12, y, 0),
                c.getBlock(14, y, 0),
                c.getBlock(15, y, 1),
                c.getBlock(15, y, 3),
                c.getBlock(15, y, 5),
                c.getBlock(15, y, 7),
                c.getBlock(15, y, 9),
                c.getBlock(15, y, 11),
                c.getBlock(15, y, 13),
                c.getBlock(15, y, 15),
                c.getBlock(13, y, 15),
                c.getBlock(11, y, 15),
                c.getBlock(9, y, 15),
                c.getBlock(7, y, 15),
                c.getBlock(5, y, 15),
                c.getBlock(3, y, 15),
                c.getBlock(1, y, 15),
                c.getBlock(0, y, 2),
                c.getBlock(0, y, 4),
                c.getBlock(0, y, 6),
                c.getBlock(0, y, 8),
                c.getBlock(0, y, 10),
                c.getBlock(0, y, 12),
                c.getBlock(0, y, 14)};
        for(Block b : blocks){
            if(b.getType().equals(Material.AIR)){
                if(!marks.contains(b)) {
                    marks.add(b);
                    p.sendBlockChange(b.getLocation(), Material.WHITE_WOOL.createBlockData());
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(jp, new Runnable(){
            @Override
            public void run() {
                for(Block b : blocks){
                    if(marks.contains(b)){
                        p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
                        marks.remove(b);
                    }
                }
            }
        }, 150);

    }
    private static class PlayerContainer{

        private final UUID uuid;
        private final ChunkData cData;

        public PlayerContainer(UUID uuid, ChunkData land) {
            this.uuid = uuid;
            this.cData = land;
        }

        public boolean equals(Object object) {
            if(object instanceof PlayerContainer) {
                return uuid.equals(((PlayerContainer) object).uuid) && cData.equals(((PlayerContainer) object).cData);
            }
            return false;
        }

    }
}
