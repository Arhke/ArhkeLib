package com.Arhke.WRCore.District;

import at.pavlov.cannons.event.ProjectileImpactEvent;
import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.*;
import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.District.util.Utils;
import com.Arhke.WRCore.District.util.Utils1_9;
import com.Arhke.WRCore.ItemSystem.utils.WrappedLocation;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.CustomEvents.MoveChunkEvent;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.Direction3D;
import com.Arhke.WRCore.Lib.Utils.RayTrace;
import com.Arhke.WRCore.Lib.Utils.Vector3D;
import com.Arhke.WRCore.Main;
import com.sun.istack.internal.NotNull;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityHuman;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DistrictListeners extends MainBase implements Listener {
    DataManager dm;
    public DistrictListeners(Main instance, DataManager dm) {
        super(instance);
        this.dm = dm;
    }
    @EventHandler
    void playerJoined(PlayerJoinEvent event)  {
        try {
            getPlugin().getTUserManager().loadPlayer(event.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    public void targetEvent(EntityTargetEvent event){
        if (event.getEntityType() != EntityType.WITHER){
            return;
        }
        Entity entity = ((CraftEntity)event.getEntity()).getHandle();
        if (!(entity instanceof IslandWither)){
            return;
        }

        if (!(event.getTarget() instanceof Player)){
            if (!(event.getTarget()instanceof IslandWither))
                return;
            double radius = 15d;
            event.setTarget(event.getEntity().getLocation().getWorld().getNearbyEntities(event.getEntity().getLocation(), radius, radius, radius)
                    .stream().filter((a)-> {
                        if(!(a instanceof LivingEntity)) {
                            return false;
                        }
                        if (!(a instanceof Player)){
                            return !(a instanceof ArmorStand) && !(((CraftEntity)a).getHandle() instanceof IslandWither);
                        }
                        Player p = (Player)a;
                        if(p.isDead() || p.getGameMode() != GameMode.SURVIVAL){
                            return false;
                        }
                        TUser user = getPlugin().getTUserManager().getOrNewTUser(p);
                        if (user.getKingdom() == null){
                            return true;
                        }
                        AtomicBoolean ret = new AtomicBoolean(false);
                        getPlugin().getBattleManager().getBattleByWither((IslandWither)entity).ifPresent(
                                battle-> ret.set(!user.getKingdom().equals(battle.getDefenderTown().getKingdom()))
                        );
                        return ret.get();
                    }).findFirst().orElse(null));
        }else{
            TUser tu = getPlugin().getTUserManager().getOrNewTUser((Player)event.getTarget());
            if(tu.getKingdom() == null){
                return;
            }
            getPlugin().getBattleManager().getBattleByWither((IslandWither)entity)
                    .ifPresent(battle->{
                        if(battle.getDefenderTown().getKingdom().equals(tu.getKingdom())){
                            double radius = 15d;

                            event.setTarget(event.getEntity().getLocation().getWorld().getNearbyEntities(event.getEntity().getLocation(), radius, radius, radius)
                                    .stream().filter((a)-> {
                                        if(!(a instanceof LivingEntity)) {
                                            return false;
                                        }
                                        if (!(a instanceof Player)){
                                            return !(a instanceof ArmorStand) && !(((CraftEntity)a).getHandle() instanceof IslandWither);
                                        }
                                        Player p = (Player)a;
                                        if(p.isDead() || p.getGameMode() != GameMode.SURVIVAL){
                                            return false;
                                        }
                                        TUser user = getPlugin().getTUserManager().getOrNewTUser(p);
                                        if (user.getKingdom() == null){
                                            return true;
                                        }
                                        return !user.getKingdom().equals(battle.getDefenderTown().getKingdom());
                                    }).findFirst().orElse(null));


                        }
                    });
        }





    }
    @EventHandler
    public void entityDeath(EntityDeathEvent event){
        Entity entity = ((CraftEntity)event.getEntity()).getHandle();
        if (!(entity instanceof IslandWither)){
            return;
        }
        getPlugin().getBattleManager().getBattleByWither((IslandWither)entity)
                .ifPresent(battle->battle.witherDeathEvent(event));
    }

    @EventHandler
    public void cannonBallHit(ProjectileImpactEvent event) {
        if (event.getImpactBlock() == null) {
            event.setCancelled(true);
            return;
        }

        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getImpactBlock());
        if (cd.getTown() == null){
            return;
        }
        Battle battle = getPlugin().getBattleManager().getDefendingBattle(cd.getTown());
        if(battle == null){
            event.setCancelled(true);
            return;
        }
        battle.warnPlayers();
        if(cd.getHP() <= 0 || cd.getTown().isOnHeart(event.getImpactBlock())){
            return;
        }


        List<Vector3D> locList = new ArrayList<>();

        //===================<Block Parsing>====================
        WrappedLocation impact = new WrappedLocation(event.getImpactBlock());
        double xSum = 0, ySum = 0, zSum = 0;
        for (int z = -2; z <= 2; z++){
            for (int y = -2; y <= 2; y++){
                for (int x = -2; x<=2; x++) {
                    Location l = impact.add(x, y, z);
                    Material material = l.getBlock().getType();
                    if(material.isBlock() && material.isSolid()){
                        locList.add(new Vector3D(x, y, z));
                        xSum+= Integer.compare(x, 0);
                        ySum+=Integer.compare(y, 0);
                        zSum+=Integer.compare(z, 0);
                    }
                }
            }
        }

        if (locList.size() == 0){
            return;
        }
        //=============<POV Casting>=======================
        Vector vProj = event.getProjectileEntity().getVelocity().clone().normalize().multiply(5);
        Direction3D direction = Direction3D.getDirectionFromCentroid(xSum + vProj.getX(), ySum + vProj.getY(), zSum + vProj.getZ());
        // directionized X then directionized Y (these two are offset by +2)
        Double[][] locMap = new Double[5][5];
        int entryCount = 0;
        for(Vector3D entry: locList){
            try{
                int x = (int)direction.getVectorX(entry)+2,
                        y = (int)direction.getVectorY(entry)+2,
                        z = (int)direction.getVectorZ(entry);
                if (locMap[x][y] == null) {
                    entryCount++;
                    locMap[x][y] = (double) z;
                }else if (direction.isPositive() && locMap[x][y] > z || !direction.isPositive() && locMap[x][y] < z){
                    locMap[x][y] = (double) z;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

        //============<Real Matrix Initialization>===============
        RealMatrix matrix = new Array2DRowRealMatrix(entryCount,3);
        entryCount = 0;
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                Double value = locMap[x+2][y+2];
                if (value == null){
                    continue;
                }
                try {
                    matrix.setRow(entryCount, direction.getDoubleArrayFromDirectionVector(x, y, value));
                    entryCount++;
                }catch(OutOfRangeException e){
                    e.printStackTrace();
                }
            }
        }

        //=============<EigenVector>======================
        matrix = matrix.transpose().multiply(matrix);
        EigenDecomposition ed = new EigenDecomposition(matrix);
        RealVector normalVector;
        try{
            normalVector = ed.getEigenvector(2);
            Bukkit.broadcastMessage(ed.getEigenvector(2).toString());
        }
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            return;
        }

//        Bukkit.broadcastMessage(event.getImpactLocation().toString());
//        Bukkit.broadcastMessage(event.getShooterUID().toString());

        //====================<Penetration Calculation>===============
        double pen = event.getProjectile().getPenetration()+(Math.random()*0.6-0.3)*event.getProjectile().getPenetration();
//        Bukkit.broadcastMessage("Pen " + pen);
        Vector projVelocity = event.getProjectileEntity().getVelocity();
        double impactCos = Math.abs(Math.cos(projVelocity.angle(new Vector(normalVector.getEntry(0), normalVector.getEntry(1), normalVector.getEntry(2)))));
        Player player = Bukkit.getPlayer(event.getShooterUID());
        if (impactCos < 0.34202014332){
            if(player != null) {

                if (Math.random() > 0.5)player.sendMessage("Ricochet ~ !");
                else player.sendMessage("We didn't even scratch them.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3, 3);
            }
            event.setCancelled(true);
            return;
        }
        else{
            pen *= impactCos;
        }


        //============<Initialize Field for Blast Resist>============
        Field durability;
        try {
            durability = net.minecraft.server.v1_12_R1.Block.class.getDeclaredField("durability");
            durability.setAccessible(true);
        }
        catch(NoSuchFieldException e){
            e.printStackTrace();
            return;
        }

        //==================<RayCasting>==============
        double startPen = pen;
        RayTrace rt = new RayTrace(projVelocity);
        Location rayLoc = event.getProjectileEntity().getLocation().clone();
        List<Block> blockList = new ArrayList<>();
//        Bukkit.broadcastMessage("New pen: "+pen);
        boolean hasReachedWall = false;
        int yetToReachWall = 0;
        while(pen>0){

            if (!rayLoc.getBlock().getType().isSolid()){
                if (hasReachedWall){
                    break;
                }else{
//                    Bukkit.broadcastMessage("Impact block not found"+ rayLoc);
                    Vector nextVector = rt.nextInterSection(rayLoc);
                    rayLoc.add(nextVector);
                    yetToReachWall++;
                    if(yetToReachWall < 10){
                        continue;
                    }else{
                        event.setCancelled(true);
                        return;
                    }
                }
            }else if(!hasReachedWall){
                hasReachedWall = true;
            }
//            Bukkit.broadcastMessage("Wall: " + rayLoc.getBlock().getType());

            Vector nextVector = rt.nextInterSection(rayLoc);
            try {
                float blastResist = (float)durability.get(net.minecraft.server.v1_12_R1.Block.getById(rayLoc.getBlock().getTypeId()));
                if (blastResist >= 1 && blastResist<=17999999){
                    pen -= Math.log(blastResist) * nextVector.length();
//                    Bukkit.broadcastMessage("BResist-After: " + Math.log(blastResist) * nextVector.length());
                }else if(blastResist>0){
                    pen-= blastResist;
//                    Bukkit.broadcastMessage("BResist: " + blastResist);
                }
                if(nextVector.length() > 0.1 && rayLoc.getBlock().getType() != Material.AIR){
                    blockList.add(rayLoc.getBlock());
                }
            } catch (IllegalAccessException|ClassCastException e) {
                e.printStackTrace();
                return;
            }
            rayLoc.add(nextVector);
        }
        double blastDamage = event.getProjectile().getExplosionPower();

        if (pen > 0) {
            //penned
            blockList.forEach(Block::breakNaturally);
            if(player != null) {
                if (Math.random() > 0.3)player.sendMessage(ChatColor.GREEN+"Penetration.");
                else if (Math.random() > 0.5) player.sendMessage(ChatColor.GREEN+"They're Hit!");
                else player.sendMessage(ChatColor.GREEN+"Good shot. Let's get them again!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 3, 3);
            }
            Bukkit.broadcastMessage("damaged: " + Math.max(0, event.getProjectile().getPenetration()/2d)+"");
            cd.addHP(-Math.max(0, event.getProjectile().getPenetration()/2d));
            if(cd.getHP() == 0){
                player.sendMessage(ChatColor.GOLD+"Their protection is gone, this is our chance!");
            }

        }else{
            //did not Pen
            if(player != null) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 3, 3);
                if (Math.random() > 0.3)player.sendMessage("We weren't able to penetrate them.");
                else if (Math.random() > 0.5) player.sendMessage("That one bounced right off.");
                else player.sendMessage("That one didn't go through.");
            }
            Bukkit.broadcastMessage("damaged: "+blastDamage*0.15);
            cd.addHP(-blastDamage*0.15);
            if(cd.getHP() == 0){
                assert player != null;
                player.sendMessage(ChatColor.GOLD+""+ ChatColor.BOLD + "Their protection is gone, this is our chance!");
            }
            List<Block> blockBreaks = new ArrayList<>();
            for(int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        Block block = new WrappedLocation(event.getImpactBlock()).add(i,j,k).getBlock();
                        try {
                            if((float) durability.get(net.minecraft.server.v1_12_R1.Block.getById(block.getTypeId())) < blastDamage){
                                blockBreaks.add(block);
                            }
                        }catch(IllegalAccessException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            new EntityExplodeEvent(event.getProjectileEntity(),event.getImpactBlock(), blockBreaks,1f);
            blockBreaks.forEach(Block::breakNaturally);


            // if block dura is less than blast then break block
        }

//        if (impactDegrees)

//        Field field=((CraftBlock)event.getImpactLocation().getBlock()).getDeclaredField("strength");
//        field.setAccessible(true);
//        field.setFloat(net.minecraft.server.Block.BED, 50.0F);
        event.setCancelled(true);
    }
    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        try {
            getPlugin().getTUserManager().unloadPlayer(event.getPlayer());
        } catch (Exception e) {
            except(event.getPlayer().getDisplayName() + "'s data could not be saved properly");
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(AsyncPlayerChatEvent event){//TAGS {PROFESSION}, {KINGDOM}, {RANK}, {RANKSPACER}

        if(event.isCancelled()) {
            return;
        }


        try{
//            String townStr = "None";
            String kingdomStr = "Wilderness";
            String rankStr = "";
            TUser u = getPlugin().getTUserManager().getOrNewTUser(event.getPlayer());
            Kingdom kingdom= u.getKingdom();
            Rank rank;
            if (kingdom != null){
                kingdomStr = kingdom.getName();
                rank = kingdom.getRank(event.getPlayer().getUniqueId());
                rankStr = rank.getName().substring(0,1);
                if (rank == Rank.EXECUTIVE){
                    rankStr = "Ex";
                }
            }


//            String message = ;
            if (u.getChat() == 2 && kingdom!=null){
                event.setCancelled(true);
                String msg = this.dm.getTCM("kingdom.chat", kingdomStr, rankStr, event.getPlayer().getDisplayName(), event.getMessage());//rank + " \u00a7e" + event.getPlayer().getDisplayName() + " \u00a75\u00a7l>> \u00a7b" + event.getMessage();
                kingdom.messageMembers(msg, false);
                info("[KC] " + msg);
                for(Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasPermission("feudal.commands.admin.kingdomchat") && !u.getKingdom().getMembers().containsKey(players.getUniqueId())) {
                        players.sendMessage("\u00a7e[Admin  TChat] \u00a7r" + msg);
                    }
                }
                return;
            }
            u.setChat((byte)0);
            event.setFormat(event.getFormat().replaceAll("\\{KINGDOM\\}", kingdomStr)
                    .replaceAll("\\{RANK\\}", rankStr));
        }catch(Exception e){
            ErrorManager.error(40, e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void joinAsync(AsyncPlayerPreLoginEvent event) {
        try {

//            if(Feudal._feudal.isSaving(event.getUniqueId().toString())) {
//                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
//                event.setKickMessage(Feudal.getMessage("saveJoinMessage"));
//                return;
//            }
//
//            Feudal.logPlayer(event.getName(), event.getUniqueId());
        }catch(Exception e){
            ErrorManager.error(5251114, e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PlayerJoinEvent event){
        try{
            getPlugin().getTUserManager().loadPlayer(event.getPlayer());

            if(event.getPlayer().hasPermission("feudal.admin")){
			/*if(!ErrorManager.isLatest()){
				event.getPlayer().sendMessage("\u00a7eA new version of Feudal is available. Get it here: \u00a7dhttp://download.feudal.coremod.com/");
			}else if(!ErrorManager.hasSpigot() && ErrorManager.isLoaded()){
				event.getPlayer().sendMessage("\u00a7cFeudal errors will still be shown in console since your Spigot contact information has not been entered in the main config (spigotUsername).  Providing your spigot username will allow the Feudal creator to contact you in the case Feudal does have an issue.");
			}*/
            }
        }catch(Exception e){
            ErrorManager.error(41, e);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent event){
        try{
            getPlugin().getTUserManager().unloadPlayer(event.getPlayer());
//            ChallengeManager.leave(event.getPlayer());


        }catch(Exception e){
            ErrorManager.error(42, e);
        }
    }
    public boolean isContainer(Material type){
        return type == Material.CHEST || Utils.isShulkerBox(type) || type == Material.TRAPPED_CHEST || type == Material.HOPPER ||
                type == Material.FURNACE || type == Material.BURNING_FURNACE || type == Material.DROPPER || type == Material.DISPENSER;
    }
    public boolean isDoor(Material type){
        return type == Material.WOOD_DOOR || type == Material.WOODEN_DOOR || type == Material.SPRUCE_DOOR || type == Material.BIRCH_DOOR ||
                type == Material.ACACIA_DOOR ||type == Material.DARK_OAK_DOOR || type == Material.JUNGLE_DOOR || type == Material.TRAP_DOOR;

    }
    public boolean isGate(Material type){
        return type == Material.FENCE_GATE || type == Material.SPRUCE_FENCE_GATE || type == Material.BIRCH_FENCE_GATE ||
                type == Material.ACACIA_FENCE_GATE || type == Material.DARK_OAK_FENCE_GATE || type == Material.JUNGLE_FENCE_GATE;
    }
    public boolean isRedStoneInteract(Material type) {
        return type == Material.WOOD_BUTTON || type == Material.STONE_BUTTON || type == Material.LEVER ;
    }
    public boolean isBucket(Material type){
        return type == Material.WATER_BUCKET ||
                type == Material.BUCKET || type == Material.LAVA_BUCKET;
    }
    public boolean isOtherInteractable(Material type){
        return type == Material.ARMOR_STAND || type == Material.ANVIL || type == Material.PAINTING ||
                type == Material.ITEM_FRAME || type == Material.BOOKSHELF || type == Material.BREWING_STAND;
    }
    public boolean isInteractable(Material type){
        return isGate(type) || isDoor(type) || type == Material.FURNACE || isRedStoneInteract(type) || isOtherInteractable(type);
    }

//    @EventHandler
//    public void
//
//    (PlayerInteractEvent event){
//        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
//
//			/*if(event.getPlayer() != null && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().equals(Material.BANNER)){
//				User u = Main.getUser(event.getPlayer().getUniqueId().toString());
//				if(u != null && !u.getKingdomUUID().equals("")){
//					Kingdom k = Main.getKingdom(u.getKingdomUUID());
//					if(k != null){
//						ItemStack item = u.getPlayer().getItemInHand();
//						BannerMeta meta = (BannerMeta) item.getItemMeta();
//
//						meta.setDisplayName("\u00a7a\u00a7l" + k.getName() + "'s banner");
//						meta.setBaseColor(k.getBannerBaseColor());
//						meta.setPatterns(k.getBannerPatterns());
//
//						item.setItemMeta(meta);
//						event.getPlayer().setItemInHand(item);
//						event.getPlayer().updateInventory();
//					}
//				}
//			}*/
//
//            if(event.getClickedBlock() == null){
//                return;
//            }
//            Material m = event.getClickedBlock().getType();
//            ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
//            if(isContainer(m) || isDoor(m) || isGate(m) || isRedStoneInteract(m) || isBucket(stack.getType()) || isOtherInteractable(m)){
//                ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getClickedBlock().getLocation());
//                Town town = cd.getTown();
//                if(town == null){
//                    if(isContainer(m)){
//                        event.setCancelled(true);
//                    }
//                    return;
//                }
//                if(isContainer(m) && !hasBuildPerm(cd, event.getPlayer()) &&
//                        getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG).getBoolean("kingdom","landProtection","chestAccess")){
//                    event.setCancelled(true);
////							event.getPlayer().sendMessage(Feudal.getMessage("land.deny.chest"));
//                    return;
//
//                }
//                if(event.getItem() != null && (event.getItem().getType() == Material.FIREBALL || event.getItem().getType() == Material.FLINT_AND_STEEL) &&
//                        Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.lighter") && !hasBuildPerm(cd, event.getPlayer())){
//                    event.setCancelled(true);
////							event.getPlayer().sendMessage(Feudal.getMessage("land.deny.fire"));
//                    return;
//                }
//                if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.interact") &&
//                        !hasBuildPerm(cd, event.getPlayer())){
//                    event.setCancelled(true);
////					event.getPlayer().sendMessage(Feudal.getMessage("land.deny.interact"));
//                }
//            }
//        }
//    }
//

//
//    @EventHandler
//    public void pistonRetract(BlockPistonRetractEvent event){
//        UtilsAbove1_7.pistonRetract(event);
//    }
//
//    @EventHandler
//    public void pistonExtend(BlockPistonExtendEvent event){
//        try{
////            if(LandManagement.blockIsOnLand(event.getBlock().getLocation())){
////                return;
////            }
////            for(Block b : event.getBlocks()){
////                if(ChunkDataManagement.piston(b.getLocation())){
////                    event.setCancelled(true);
////                    return;
////                }
////            }
//        }catch(Exception e){
//            ErrorManager.error(45, e);
//        }
//    }
//
//    @EventHandler
//    public void explode(EntityExplodeEvent event){
//        try{
//            LandManagement.explode(event);
//        }catch(Exception e){
//            ErrorManager.error(46, e);
//        }
//    }
//
//    @EventHandler
//    public void fireSpread(BlockIgniteEvent event){
//        try{
//            LandManagement.fireSpread(event);
//        }catch(Exception e){
//            ErrorManager.error(47, e);
//        }
//    }
//
//    @EventHandler
//    public void burn(BlockBurnEvent event){
//        try{
//            LandManagement.burn(event);
//        }catch(Exception e){
//            ErrorManager.error(48, e);
//        }
//    }
//
//    @EventHandler
//    public void entityChangeBlock(EntityChangeBlockEvent event){
//        try{
//            LandManagement.entityChangeBlock(event);
//        }catch(Exception e){
//            ErrorManager.error(49, e);
//        }
//    }
//
//
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void teleport(PlayerTeleportEvent event){
//        try{
//            if(!event.isCancelled()){
//                try{
////                    if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) || (!Feudal.getVersion().equals("1.8") && !Feudal.getVersion().equals("1.7") && event.getCause().equals(PlayerTeleportEvent.TeleportCause.valueOf("CHORUS_FRUIT")))){
////                       ChunkDataland = new ChunkData(event.getTo());
////                        Kingdom king = Feudal.getLandKingdom(land);
////                        if(king != null){
////                            if((king.isShielded() && Feudal.getConfiguration().getConfig().getBoolean("kingdom.shieldProtection.enderpearls"))
////                                    || (!king.isShielded() && Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.enderpearls"))){
////                                TUser u = Feudal.getUser(event.getPlayer().getUniqueId().toString());
////                                if(u != null && !u.getKingdomUUID().equals("")){
////                                    Kingdom mine = Feudal.getKingdom(u.getKingdomUUID());
////                                    if(mine != null){
////                                        if(!mine.equals(king) && !mine.isAllied(king)){
////                                            event.setCancelled(true);
////                                            return;
////                                        }
////                                    }else{
////                                        event.setCancelled(true);
////                                        return;
////                                    }
////                                }else{
////                                    event.setCancelled(true);
////                                    return;
////                                }
////                            }else {
////                                return;
////                            }
////                        }
////                    }
//                }catch(Exception e){
//                    //
//                }
//
//                if(event.getTo().getChunk().getX() != event.getFrom().getChunk().getX() || event.getTo().getChunk().getZ() != event.getFrom().getChunk().getZ()){
//                    final TUser u = getPlugin().getTUserManager().getOrNewTUser(event.getPlayer());
//                    if(u != null){
//                        if(event.getTo().getWorld().equals(event.getFrom().getWorld())){
//                            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable(){
//
//                                @Override
//                                public void run() {
////                                    u.effectAttributes();
//                                }
//
//                            }, 20);
//                        }
//                    }
//                    if(u != null && u.getTown() == null){
////                        Kingdom k = Feudal.getKingdom(u.getKingdomUUID());
////                        if(k != null){
////                            for(Battle c : Feudal.getChallenges(k)){
////                                if(c.isFighting()){
////
////                                    boolean attacker = false;
////                                    if(c.getAttacker().equals(k)){
////                                        attacker = true;
////                                    }
////                                    c.checkUser(u, attacker, event.getTo());
////                                }
////                            }
////                        }
//                    }
//                }
//            }
//
////            Sparing.teleport(event);
//
//        }catch(Exception e){
//            ErrorManager.error(51, e);
//        }
//    }
//
////    @EventHandler
////    public void onPilot(CraftPilotEvent event){
////        if (event.getReason() == CraftPilotEvent.Reason.PLAYER;
////        event.getCraft().get;
////    }
//
//    @EventHandler
//    public void command(PlayerCommandPreprocessEvent event){
//        try{
//            LandManagement.commandEvent(event);
////            Selection.commandEvent(event);
////            AttributeFixer.commandEvent(event);
//        }catch(Exception e){
//            ErrorManager.error(52, e);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void command2(PlayerCommandPreprocessEvent event) {
//        if(!event.isCancelled()) {
//            event.setCancelled(Feudal._feudal.getCommands().preprocess(event.getMessage(), event.getPlayer()));
//        }
//    }
//
//    @EventHandler
//    public void click(InventoryClickEvent event){
//        try{
////            Selection.inventoryClickEvent(event);
////            AttributeFixer.inventoryClickEvent(event);
////            XP.inventoryClick(event);
////            TrackPlayer.inventoryClickEvent(event);
////            ChangeProfession.inventoryClickEvent(event);
////            AttributeRedistribute.inventoryClickEvent(event);
//        }catch(Exception e){
//            ErrorManager.error(53, e);
//        }
//    }
//    public void arrowHit(ProjectileHitEvent event) {
//
//    }
//
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void damageByEntity(EntityDamageByEntityEvent event) {
//        if (!event.isCancelled()) {
//            TUser attacker;
//            Player damager;
//            if (event.getDamager() instanceof Player) {
//                damager = (Player) event.getDamager();
//                attacker = getPlugin().getTUserManager().getOrNewTUser(damager);
//            } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
//                damager = (Player) (((Projectile) event.getDamager()).getShooter());
//                attacker = getPlugin().getTUserManager().getOrNewTUser(damager);
//            } else {
//                return;
//            }
//            if (event.getEntity() instanceof Player) {
//                Player hurt = (Player) event.getEntity();
//                if (damager.equals(hurt)) {
//                    return;
//                }
//                TUser defender = getPlugin().getTUserManager().getOrNewTUser(hurt);
//
//                if (!canAttackEachOther(attacker, defender)) {
//                    event.setCancelled(true);
//                    if (event.getDamager() instanceof Projectile) {
//                        event.getDamager().setFireTicks(0);
//                    }
//                    damager.sendMessage("Can't attack that person");
////				damager.sendMessage(Feudal.getMessage("land.attack.ally"));
//
//                }
//            } else if (event.getEntity() instanceof ItemFrame || UtilsAbove1_7.isArmorStand(event.getEntity()) || event.getEntity() instanceof Painting) {
//                ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getEntity().getLocation());
//                Town town = cd.getTown();
//                if (town == null) {
//                    return;
//                }
//                if (Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.interact") && !hasBuildPerm(cd, damager)) {
//                    event.setCancelled(true);
//                    damager.sendMessage("You may not interact");
////					p.sendMessage(Feudal.getMessage("land.deny.interact"));
//                }
//            }
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGH)
//    public void arrow(ProjectileHitEvent event) {
//        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player &&
//                event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
//            TUser offended = getPlugin().getTUserManager().getOrNewTUser((Player) event.getHitEntity());
//            TUser offending = getPlugin().getTUserManager().getOrNewTUser((Player) event.getEntity().getShooter());
//            if (!canAttackEachOther(offending, offended)){
//                event.getEntity().setFireTicks(0);
//                event.getEntity().remove();
//            }
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGH)
//    public void brewEvent(BrewEvent event){
//        try{
//            if(!event.isCancelled()){
////                XP.brew(event);
//            }
//        }catch(Exception e){
//            ErrorManager.error(55, e);
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    @EventHandler
//    public void grow(BlockGrowEvent event){
//        try{
//            if(event.getBlock() != null){
//                if(event.getBlock().getType().equals(Material.CROPS) || event.getBlock().getType().equals(Material.POTATO) || event.getBlock().getType().equals(Material.CARROT) || event.getBlock().getType().equals(Material.getMaterial("BEETROOT_BLOCK"))){
//                    Biome b = event.getBlock().getBiome();
//                    try{
//                        if(!b.equals(Biome.FROZEN_OCEAN) && !b.equals(Biome.FROZEN_RIVER) && !b.equals(Biome.COLD_BEACH) && !b.equals(Biome.valueOf("TAIGA_COLD")) &&
//                                !b.equals(Biome.valueOf("TAIGA_COLD_HILLS")) && !b.equals(Biome.DESERT) && !b.equals(Biome.DESERT_HILLS)){
//                            if(event.getBlock().getLocation().getY() < (event.getBlock().getLocation().getWorld().getSeaLevel()-7) || (event.getBlock().getLocation().getY() > (event.getBlock().getLocation().getWorld().getMaxHeight() - 100) && event.getBlock().getLocation().getWorld().getMaxHeight() > (event.getBlock().getLocation().getWorld().getSeaLevel() + 120))){
//                                if(System.currentTimeMillis() % 5 != 0){
//                                    event.setCancelled(true);
//                                }
//                            }else if(event.getBlock().getLocation().getY() == (event.getBlock().getLocation().getWorld().getSeaLevel()+1)){
//                                if(System.currentTimeMillis() % 2 == 0){
//                                    event.getBlock().setData((byte)(event.getBlock().getData()+1));
//                                }
//                            }
//                        }else{
//                            event.setCancelled(true);
//                            event.getBlock().setType(Material.AIR);
//                        }
//                    }catch(Exception e){
//                        //
//                    }
//                }
//            }
//        }catch(Exception e){
//            ErrorManager.error(56, e);
//        }
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event){
        if (event.getBlock() == null || event.getPlayer().isOp())
            return;
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);
        Biome biome = event.getBlock().getBiome();
        if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN) && !event.getPlayer().hasPermission("build.seabed")){
            if(event.getBlock().getLocation().getY() > config.getInt("seaLevel")){
                this.dm.getTCM("land.break.seaWind");
            }else {
                this.dm.getTCM("land.break.seaCurrent");
            }
            event.setCancelled(true);
            return;
        }
        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlock().getLocation());
        Town town = cd.getTown();
        if(town == null)
            return;
        if (event.getBlock().getType().equals(Material.MOB_SPAWNER) && config.getBoolean("town.land.protectSpawners")) {
            event.setCancelled(true);
            return;
        }
        if(event.getBlock().getType().equals(Material.CHEST) || Utils.isShulkerBox(event.getBlock().getType()))
            return;
        Battle battle = getPlugin().getBattleManager().getDefendingBattle(cd.getTown());
        if(battle != null){
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0));
            battle.warnPlayers();
            if (event.getBlock().getType() == Material.LADDER){
                new BukkitRunnable(){
                    Block current = event.getBlock();
                    @Override
                    public void run(){
                        if (current.getType() == Material.LADDER){
                            current.breakNaturally(new ItemStack(Material.LADDER, 1));
                            current = current.getRelative(BlockFace.DOWN);
                        }else{
                            this.cancel();
                        }
                    }
                }.runTaskTimer(getPlugin(), 1, 5);
                event.setCancelled(true);
                return;
            }
            if (cd.getHP() == 0){
                return;
            }
        }
        if(getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG).getBoolean("town.land.protectBlockBreak")){
            if(!hasBuildPerm(cd, event.getPlayer())){
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.dm.getTCM("land.deny.break"));
                return;
            }
            if (event.getBlock().getType() == Material.LADDER){
                event.setCancelled(true);
                new BukkitRunnable(){
                    Block current = event.getBlock();
                    @Override
                    public void run(){
                        if (current.getType() == Material.LADDER){
                            current.breakNaturally(new ItemStack(Material.LADDER, 1));
                            current = current.getRelative(BlockFace.DOWN);
                        }else{
                            this.cancel();
                        }
                    }
                }.runTaskTimer(getPlugin(), 1, 5);

            }

        }


    }
    @EventHandler
    public void blockPlace(BlockPlaceEvent event){

        if(event.isCancelled() || event.getBlock() == null || event.getPlayer().isOp()) {
            return;
        }
        try{
            Field durability = net.minecraft.server.v1_12_R1.Block.class.getDeclaredField("durability");
            Field strength = net.minecraft.server.v1_12_R1.Block.class.getDeclaredField("strength");
            strength.setAccessible(true);
            durability.setAccessible(true);

            Bukkit.broadcastMessage(strength.get(net.minecraft.server.v1_12_R1.Block.getById(event.getBlock().getTypeId())).toString());
            Bukkit.broadcastMessage(durability.get(net.minecraft.server.v1_12_R1.Block.getById(event.getBlock().getTypeId())).toString());
        }catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);
        Biome biome = event.getBlock().getBiome();
        if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN) && !event.getPlayer().hasPermission("build.seabed")){
            if(event.getBlock().getLocation().getY() > config.getInt("seaLevel")){
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaWind"));
            }else {
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaCurrent"));
            }
            event.setBuild(false);
            return;

        }
        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlock().getLocation());
        Town cdTown = cd.getTown();
        if (cdTown == null) {
            return;
        }
        Battle battle = getPlugin().getBattleManager().getDefendingBattle(cd.getTown());
        if(battle != null){
            if(getPlugin().getTUserManager().getOrNewTUser(event.getPlayer()).getKingdom().equals(cd.getTown().getKingdom())){
                if (isWallBlock(event.getBlock()) && battle.needsReWarn()){
                    event.getPlayer().sendMessage(this.dm.getTCM("land.deny.placeWall"));
                    event.setCancelled(true);
                }
            }else{
                if(event.getBlock().getType() == Material.LADDER){
                    battle.warnPlayers();
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(this.dm.getTCM("land.deny.placeLadder"));
                }
            }
            return;
        }
        if (!hasBuildPerm(cd, event.getPlayer())) {


            if (config.getBoolean("town.land.protectBlockPlace")) {
                event.setBuild(false);
                event.getPlayer().sendMessage(this.dm.getTCM("land.deny.place"));
            }
        }



    }
    @EventHandler
    public void waterLavaPlace(PlayerBucketFillEvent event){
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);

        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlockClicked().getLocation());
        Town cdTown = cd.getTown();
        if (cdTown == null) {
            return;
        }

        if (!hasBuildPerm(cd, event.getPlayer())) {
            if (config.getBoolean("town.land.protectBlockPlace")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.dm.getTCM("land.deny.place"));
            }
        }
    }
    @EventHandler
    public void waterPickup(PlayerBucketEmptyEvent event){
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);

        Biome biome = event.getBlockClicked().getBiome();
        if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN) && !event.getPlayer().hasPermission("build.seabed")){
            if(event.getBlockClicked().getLocation().getY() > config.getInt("seaLevel")){
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaWind"));
                event.setCancelled(true);
                return;
            }else if(event.getBlockClicked().getLocation().getY() > config.getInt("seabed")) {
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaCurrent"));
                event.setCancelled(true);
                return;
            }

        }
        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlockClicked().getLocation());
        Town cdTown = cd.getTown();
        if (cdTown == null) {
            return;
        }

        if (!hasBuildPerm(cd, event.getPlayer())) {
            if (config.getBoolean("town.land.protectBlockPlace")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.dm.getTCM("land.deny.place"));
            }
        }
    }

    @Override
    public void exceptDisable(String Msg) {
        super.exceptDisable(Msg);
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onBlockExplode(BlockExplodeEvent event){
        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlock().getLocation());
        List<Chunk> chunks = new ArrayList<>();
        chunks.add(event.getBlock().getLocation().getChunk());
        List<Chunk> antiChunks = new ArrayList<>();

        Iterator<Block> iter = event.blockList().iterator();
        while (iter.hasNext()){
            Block block = iter.next();
            if(block.getBiome().name().contains("OCEAN")){
                iter.remove();
                continue;
            }
            if(chunks.contains(block.getChunk())){
                continue;
            }
            if(antiChunks.contains(block.getChunk())){
                iter.remove();
                continue;
            }
            ChunkData chunkData = getPlugin().getWorldDataManager().getOrNewChunkData(block.getLocation());
            if(chunkData.getTown() == null || chunkData.getTown().equals(cd.getTown()) && chunkData.getHP() == 0){
                chunks.add(block.getChunk());
                continue;
            }
            info(block.getType().toString());
            antiChunks.add(block.getChunk());
            iter.remove();
        }
    }
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event){
        if (event.getEntity() == null) {
            event.blockList().clear();
            return;
        }
        EntityType et = event.getEntityType();
        if (et != EntityType.PRIMED_TNT) {
            event.blockList().clear();
            return;
        }
        List<Chunk> breakChunks = new ArrayList<>();
        List<Chunk> halfBreakChunks = new ArrayList<>();
        List<Chunk> noBreakChunks = new ArrayList<>();
        Set<Chunk> hpDamageChunks = new HashSet<>();
        Iterator<Block> iter = event.blockList().iterator();
        
        while (iter.hasNext()){
            Block block = iter.next();
            if(block.getBiome().name().contains("OCEAN")){
                iter.remove();
                continue;
            }
            if(breakChunks.contains(block.getChunk())){
                continue;
            }
            if(halfBreakChunks.contains(block.getChunk())){
                if (!isWallBlock(block)){
                    continue;
                }
                hpDamageChunks.add(block.getChunk());
                iter.remove();
                continue;
            }
            if (noBreakChunks.contains(block.getChunk())){
                iter.remove();
                continue;
            }


            ChunkData chunkData = getPlugin().getWorldDataManager().getOrNewChunkData(block.getLocation());
            if(chunkData.getTown() == null){
                breakChunks.add(block.getChunk());
                continue;
            }
            Battle battle = getPlugin().getBattleManager().getDefendingBattle(chunkData.getTown());
            if(battle == null){
                noBreakChunks.add(block.getChunk());
                iter.remove();
                continue;
            }
            if(chunkData.getHP() == 0){
                breakChunks.add(block.getChunk());
            }else{
                halfBreakChunks.add(block.getChunk());
                if(isWallBlock(block)) {iter.remove(); hpDamageChunks.add(block.getChunk());}
            }
        }
        for (Chunk c: hpDamageChunks){
            getPlugin().getWorldDataManager().getOrNewChunkData(c).addHP(-1);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR || !isInteractable(event.getClickedBlock().getType()))return;

        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);

        Biome biome = event.getClickedBlock().getBiome();
        if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN) && !event.getPlayer().hasPermission("build.seabed")){
            if(event.getClickedBlock().getLocation().getY() > config.getInt("seaLevel")){
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaWind"));
                event.setCancelled(true);
                return;
            }else if(event.getClickedBlock().getLocation().getY() > config.getInt("seabed")) {
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaCurrent"));
                event.setCancelled(true);
                return;
            }

        }
        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getClickedBlock().getLocation());
        Town cdTown = cd.getTown();
        if (cdTown == null) {
            return;
        }
        Battle battle = getPlugin().getBattleManager().getDefendingBattle(cd.getTown());
        if(battle != null){
            if(getPlugin().getTUserManager().getOrNewTUser(event.getPlayer()).getKingdom().equals(cd.getTown().getKingdom())){
                return;
            }
            battle.warnPlayers();
            return;
        }
        if (!hasBuildPerm(cd, event.getPlayer())) {
            if (config.getBoolean("town.land.protectInteract")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.dm.getTCM("land.deny.interact"));
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event){
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);

        Biome biome = event.getRightClicked().getLocation().getBlock().getBiome();

        if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN) && !event.getPlayer().hasPermission("build.seabed")){
            if(event.getRightClicked().getLocation().getY() > config.getInt("seaLevel")){
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaWind"));
                event.setCancelled(true);
                return;
            }else if(event.getRightClicked().getLocation().getY() > config.getInt("seabed")) {
                event.getPlayer().sendMessage(this.dm.getTCM("land.place.seaCurrent"));
                event.setCancelled(true);
                return;
            }

        }
        ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getRightClicked().getLocation());
        Town cdTown = cd.getTown();
        if (cdTown == null) {
            return;
        }

        if (!hasBuildPerm(cd, event.getPlayer())) {
            if (config.getBoolean("town.land.protectBlockPlace")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.dm.getTCM("land.deny.place"));
            }
        }
    }
    @EventHandler
    public void playerMoveChunkEvent(MoveChunkEvent event) {
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);
        if(!config.getBoolean("town.land.sendTitles")){
            return;
        }
        ChunkData on = getPlugin().getWorldDataManager().getOrNewChunkData(event.getTo());
        TUser u = getPlugin().getTUserManager().getOrNewTUser(event.getPlayer());
        Kingdom kingdom = u.getKingdom();
        String color = ChatColor.GRAY.toString();
        Town cdTown = on.getTown();
        if (System.currentTimeMillis() - u.getLastLandMessage() <= 1000 || u.isLastLandTown(cdTown)) {
            return;
        }
        String name = this.dm.getTCM("land.unconquered");//"Unconquered Land";
        if (cdTown != null) {
            name = cdTown.getName();
            color = ChatColor.WHITE.toString();
        }
        if (kingdom != null && cdTown != null) {
            if(kingdom.equals(cdTown.getKingdom())){
                color = ChatColor.AQUA.toString();
            } else if (kingdom.isAllied(cdTown.getKingdom())) {
                color = ChatColor.GREEN.toString();
            }
        }

        Utils1_9.sendTitle(event.getPlayer(), this.dm.getTCM("land.enter", color, name), "");
        u.setLastLandMessage(System.currentTimeMillis());
        u.setLastLandTown(cdTown);
    }
//    @
    
    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        try{
            TUser u = getPlugin().getTUserManager().getOrNewTUser(event.getEntity());
            if(u.getKingdom() == null)return;
            ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(event.getEntity().getLocation());
            if (cd.getTown() == null)return;
            if (cd.getTown().getKingdom() == null)return;
            if(!u.getKingdom().equals(cd.getTown().getKingdom()))return;
            Battle battle = getPlugin().getBattleManager().getDefendingBattle(cd.getTown());
            if (battle == null)return;
            IslandWither wither = (IslandWither)((CraftEntity)Bukkit.getEntity(battle.getWither())).getHandle();
            wither.damageEntity(DamageSource.playerAttack((EntityHuman)((CraftEntity)event.getEntity()).getHandle()), wither.getMaxHealth()*0.02f);
        }catch(Exception e){
            ErrorManager.error(67, e);
        }

    }
    @EventHandler(ignoreCancelled = true)
    public void potionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            if (event.getPotion().getItem() == null) {
                return;
            }
            AtomicBoolean cancel = new AtomicBoolean(false);
            Player damager = (Player) event.getPotion().getShooter();
            TUser attacker = getPlugin().getTUserManager().getOrNewTUser(damager);
            event.getAffectedEntities().forEach((e) -> {
                if (!(e instanceof Player)) {
                    ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(e.getLocation());
                    Town cdTown = cd.getTown();
                    if (cdTown == null) {
                        return;
                    }
                    if(!hasBuildPerm(cd, damager)){
                        cancel.set(true);
                        event.setIntensity(e, 0);
                    }
                }else{
                    Player hurt = (Player) e;
                    TUser defender = getPlugin().getTUserManager().getOrNewTUser(hurt);
                    if (damager.equals(hurt)) {
                        return;
                    }
                    if(!canAttackEachOther(defender, attacker)){
                        cancel.set(true);
                        event.setIntensity(e, 0);
                    }
                }
            });
            if (cancel.get()) {
                ((Player) event.getPotion().getShooter()).sendMessage(this.dm.getTCM("land.deny.kill"));
            }

        }

    }
    @EventHandler
    public void piston(BlockPistonExtendEvent event){
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);
        Town t = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlock().getLocation()).getTown();
        for(Block b:event.getBlocks()) {

            Biome biome = b.getBiome();
            if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN)) {
                if (event.getBlock().getLocation().getY() > config.getInt("seaLevel")) {
                    this.dm.getTCM("land.break.seaWind");
                    event.setCancelled(true);
                    return;
                } else if (event.getBlock().getLocation().getY() > config.getInt("seabed")) {
                    this.dm.getTCM("land.break.seaCurrent");
                    event.setCancelled(true);
                    return;
                }
            }
            ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(b.getRelative(event.getDirection()).getLocation());
            Town town = cd.getTown();
            if (town == null)
                continue;
            if (!town.equals(t)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void piston(BlockPistonRetractEvent event){
        ConfigManager config = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);
        Town t = getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlock().getLocation()).getTown();
        for(Block b:event.getBlocks()) {
            Biome biome = b.getBiome();
            if ((biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN)) {
                if (event.getBlock().getLocation().getY() > config.getInt("seaLevel")) {
                    this.dm.getTCM("land.break.seaWind");
                    event.setCancelled(true);
                    return;
                } else if (event.getBlock().getLocation().getY() > config.getInt("seabed")) {
                    this.dm.getTCM("land.break.seaCurrent");
                    event.setCancelled(true);
                    return;
                }
            }
            ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(b.getLocation());
            Town town = cd.getTown();
            if (town == null)
                continue;
            if (!town.equals(t)) {
                event.setCancelled(true);
                return;
            }
        }
    }










    /**
     * Checks Whether or not the specified user has permission to build in the specified Chunk
     * @param cd
     * @param player
     * @return
     */
    private boolean hasBuildPerm(ChunkData cd, Player player) {

        if(player.hasPermission("districts.admin.passProtection")){
            return true;
        }
        Town cdTown  = cd.getTown();
        if(cdTown == null) {
            return true;
        }
        TUser user = getPlugin().getTUserManager().getOrNewTUser(player);
        if(user == null){
            return false;
        }
        Kingdom kingdom = user.getKingdom();

        return kingdom != null && kingdom.equals(cdTown.getKingdom()) &&
                kingdom.getRank(player.getUniqueId()).checkPerm(Rank.RankPerm.BUILDKINGDOM);
    }
    private boolean canAttackEachOther(@NotNull TUser u1, @NotNull TUser u2){
        if (u1.equals(u2))
            return true;
        return u1.getKingdom() == null || u2.getKingdom() == null ||
                (!u1.getKingdom().equals(u2.getKingdom()) && !u1.getKingdom().isAllied(u2.getKingdom()));
    }





    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamage(EntityDamageByEntityEvent event){
        if (((CraftEntity)event.getEntity()).getHandle() instanceof IslandWither){
            Player player;
            if (event.getDamager() instanceof Projectile){
                if(((Projectile)event.getDamager()).getShooter() instanceof Player){
                    player = (Player)((Projectile)event.getDamager()).getShooter();
                }else{
                    return;
                }
            }else if (event.getDamager() instanceof Player){
                player = (Player)event.getDamager();
            }else return;
            Kingdom k = getPlugin().getTUserManager().getOrNewTUser(player).getKingdom();
            if (k == null){
                return;
            }
            getPlugin().getBattleManager().getBattleByWither((IslandWither)((CraftEntity)event.getEntity()).getHandle()).ifPresent(battle->{
                if (battle.getDefenderTown().getKingdom().equals(k)){
                    event.setDamage(0);
                }

            });
        }
        if(event.getDamager() instanceof Player){
            Player damager = (Player)event.getDamager();
            TUser uDamager = getPlugin().getTUserManager().getOrNewTUser(damager);
            if(event.getEntity() instanceof Player){
                Player damaged = (Player)event.getEntity();
                TUser uDamaged = getPlugin().getTUserManager().getOrNewTUser(damaged);
                if (!canAttackEachOther(uDamager, uDamaged)){
                    event.setCancelled(true);
                }
            }

        }else if(event.getDamager() instanceof Projectile){
            Projectile projectile = (Projectile)event.getDamager();
            if(projectile.getShooter() != null && projectile.getShooter() instanceof Player){
                Player damager = (Player)projectile.getShooter();
                TUser uDamager = getPlugin().getTUserManager().getOrNewTUser(damager);
                if(event.getEntity() instanceof Player){
                    Player damaged = (Player)event.getEntity();
                    TUser uDamaged = getPlugin().getTUserManager().getOrNewTUser(damaged);
                    if (!canAttackEachOther(uDamager, uDamaged)){
                        projectile.remove();
                        event.setCancelled(true);
                    }
                }
            }
        }


    }

    private boolean isWallBlock(Block block){
        return block.getType() == Material.SMOOTH_BRICK || block.getType() == Material.OBSIDIAN;
    }

}
