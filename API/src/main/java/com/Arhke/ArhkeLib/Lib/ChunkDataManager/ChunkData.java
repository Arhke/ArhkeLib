package com.Arhke.ArhkeLib.Lib.ChunkDataManager;

import com.Arhke.ArhkeLib.ArhkeLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class ChunkData<T>{
    private final World world;
    private final int x, z;
    private T data;
    private boolean initialized = false;
    private static final ArrayList<Block> marks = new ArrayList<>();
    private static final Map<PlayerContainer, Long> timePlayer = new HashMap<>();
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
    protected void setData(T o){this.initialized = true;this.data = o;}
    protected T getData(){return this.data;}
    protected boolean isInitialized(){return this.initialized;}
    


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
        return location.getWorld().equals(this.getWorld()) &&
                location.getChunk().getX() == this.getX() && location.getChunk().getZ() == this.getZ();
    }

    /**
     * Get all land marks. Marks are blocks used to show a claim.
     * @return
     */
    public static ArrayList<Block> getMarks(){
        return marks;
    }

	/*/**
	 * Visually mark some land.
	 * @param c
	 * @param y
	 * @deprecated
	 *
	public static void markChunk(Chunk c, int y) {
		final Block blocks[] = new Block[]{c.getBlock(0, y, 0),
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
			if(b != null && b.getType().equals(Material.AIR)){
				marks.add(b);
				b.setType(Material.FENCE);
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Feudal._feudal, new Runnable(){
			@Override
			public void run() {
				for(Block b : blocks){
					if(b != null && marks.contains(b)){
						b.setType(Material.AIR);
						marks.remove(b);
					}
				}
			}
		}, 150);
	}*/



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

    @SuppressWarnings("deprecation")
    public void show(Player p) {

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
                    p.sendBlockChange(b.getLocation(), Material.WHITE_WOOL, (byte) 0);
                    //b.setType(Material.WOOL);
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(ArhkeLib.getPlugin(), new Runnable(){
            @Override
            public void run() {
                for(Block b : blocks){
                    if(b != null && marks.contains(b)){
                        p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                        marks.remove(b);
                    }
                }
            }
        }, 150);

    }
    private static class PlayerContainer{

        private UUID uuid;
        private ChunkData cData;

        public PlayerContainer(UUID uuid, ChunkData land) {
            this.uuid = uuid;
            this.cData = land;
        }

        public boolean equals(Object object) {
            if(object instanceof PlayerContainer) {
                if(uuid.equals(((PlayerContainer) object).uuid) && cData.equals(((PlayerContainer) object).cData)) {
                    return true;
                }
            }
            return false;
        }

    }
}