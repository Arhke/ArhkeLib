package com.Arhke.WRCore.Lib.ChunkDataManager;

import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.District.util.Utils;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.*;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class ChunkData extends MainBase {
    private Map<Location, Furniture> furnitureMap = new HashMap<>();
    private Town town;
    private World world;
    private int x, z;
    private double hp = 0d;
    private static ArrayList<Block> marks = new ArrayList<Block>();
    private static Map<PlayerContainer, Long> timePlayer = new HashMap<PlayerContainer, Long>();
    public ChunkData(Chunk chunk, Main instance) {
        super(instance);
        world = chunk.getWorld();
        x = chunk.getX();
        z = chunk.getZ();
    }
    public ChunkData(DataManager dm, Main instance){
        super(instance);
        UUID uuid = dm.getUUID(townKey);
        if (uuid != null) {
            this.town = getPlugin().getTownsManager().getTown(dm.getUUID(townKey));
        }
        this.hp = dm.getDouble(0, hpKey);

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
    public boolean isIn(Location location){
        return location.getWorld().equals(this.getWorld()) &&
                location.getChunk().getX() == this.getX() && location.getChunk().getZ() == this.getZ();
    }
    public Town getTown() {
        return town;
    }
    public void setTown(Town town){
        if (this.town != null){
            this.town.unRegisterLand(this);
        }
        if (town != null){
            town.registerLand(this);
        }
        this.town = town;
    }
    public double getHP() {
        return this.hp;
    }
    public void addHP(double amount){
        hp = Math.max(0, hp+amount);
    }

    public Smeltery getSmeltery(Location loc) {
        Furniture f = furnitureMap.get(loc);
        return f instanceof Smeltery?(Smeltery)f:null;
    }
    public CauldronFurniture getCauldron(Location loc) {
        Furniture f = furnitureMap.get(loc);
        return f instanceof CauldronFurniture?(CauldronFurniture)f:null;
    }
    public Tannery getTannery(Location loc) {
        Furniture f = furnitureMap.get(loc);
        return f instanceof Tannery?(Tannery)f:null;
    }
    public Furniture getOrNewFurniture(Location loc, FurnitureType ft) {
        Furniture furniture = furnitureMap.get(loc);
        if (furniture == null){
            furnitureMap.put(loc, (furniture = ft.newInstance(getPlugin(), loc)));
        }
        return furniture;
    }
    public void removeFurniture(Location location) {
        Furniture f = furnitureMap.remove(location);
        if(f!= null){
            f.remove();
        }

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
                if(land2.getZ()-1 == this.z || land2.getZ()+1 == this.z){
                    return true;
                }else{
                    return false;
                }
            }else if(land2.getZ() == this.z){
                if(land2.getX()-1 == this.x || land2.getX()+1 == this.x){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
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
                if(!marks.contains(b)) {
                    marks.add(b);
                    p.sendBlockChange(b.getLocation(), Material.WOOL, (byte) 0);
                    //b.setType(Material.WOOL);
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
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
    public static final String hpKey = "hp", townKey = "townKey";
    public void load(FileManager fm){
        DataManager dm = fm.getDataManager();
        this.hp = dm.getDouble(0d, hpKey);
        UUID townID = dm.getUUID(townKey);
        if(townID != null){
            this.town = getPlugin().getTownsManager().getTown(townID);
            if(town != null) {
                this.town.registerLand(this);
            }
        }else{
            this.town = null;
        }
    }
    public void save(FileManager fm){
        if (town == null) {
            fm.deleteFile();
            return;
        }
        DataManager dm = fm.getDataManager();
        dm.wipeAllKeys();
        dm.set(this.town.getId().toString(), townKey);
        dm.set(this.hp, hpKey);
        fm.save();

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
