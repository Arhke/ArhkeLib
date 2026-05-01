package com.Arhke.ArhkeLib.Base;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class MainBase<T extends JavaPlugin> extends Base{
    private final T plugin;
    public MainBase(T instance){
        plugin = instance;
    }
    public T getPlugin(){
        return plugin;
    }
    public void exceptDisable(String Msg){
        Bukkit.getPluginManager().disablePlugin(plugin);
        throw new RuntimeException(Msg);
    }
    public void displayMarker(String display, Location loc, LivingEntity trackPlayer, boolean springOut, int ticks, float dx, float dy, float dz) {
        if (getPlugin() instanceof PluginBase plug && plug.getHook().getProtocolLib() != null){
            displayPacketMarker(loc, display, ticks);
            return;
        }
        ArmorStand armorstand = (ArmorStand) loc.getWorld().spawnEntity(
                loc.clone().add(Math.random() * dx - dx / 2, Math.random() * dy - dy / 2, Math.random() * dz - dz / 2), EntityType.ARMOR_STAND);
        armorstand.setVisible(false);
        armorstand.setCustomName(display);
        Vector v = new Vector(0,0,0);
        if(springOut){
            v = new Vector(Math.random() * 0.4 - 0.2, 0.0, Math.random() * 0.4 - 0.2);
            armorstand.setGravity(true);
        }else {
            armorstand.setMarker(true);
            armorstand.setGravity(false);
        }
        if (trackPlayer != null)
            v.add(trackPlayer.getVelocity());
        armorstand.setVelocity(v);
        armorstand.setSmall(true);

        armorstand.setCustomNameVisible(true);
        setTicksLived(armorstand, ticks);
    }
    public void displayPacketMarker(Location loc, String display, int delay){
        this.spawn(loc.add(0, 1.5, 0), display, delay);

    }

    private static int getFakeEntityId() {
        return ThreadLocalRandom.current().nextInt(1000000, 2000000);
    }

    @SuppressWarnings("removal")
    public void spawn(Location loc, String text, int ticks) {
        info("spawn");
        int entityId = getFakeEntityId();
        UUID uuid = UUID.randomUUID();

        // 1. Create the Spawn Packet
        PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityId);
        spawnPacket.getUUIDs().write(0, uuid);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND); // Changed to ARMOR_STAND
        spawnPacket.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());

        // 2. Create the Metadata Packet (Armor Stand specific indexes)
        PacketContainer metadataPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, entityId);

        List<WrappedDataValue> metadata = new ArrayList<>();

        // Index 0: Entity State Bitmask (0x20 makes the entity invisible)
        metadata.add(new WrappedDataValue(0,
                WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x20));

        // Index 2: Custom Name (WrappedChatComponent)
        metadata.add(new WrappedDataValue(2,
                WrappedDataWatcher.Registry.getChatComponentSerializer(true),
                java.util.Optional.of(WrappedChatComponent.fromLegacyText(text).getHandle())));

        // Index 3: Custom Name Visible (Boolean)
        metadata.add(new WrappedDataValue(3,
                WrappedDataWatcher.Registry.get(Boolean.class), true));

        // Index 15: Armor Stand Specific Bitmask (0x01 = Small, 0x10 = No Baseplate)
        metadata.add(new WrappedDataValue(15,
                WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x01 | 0x10)));

        metadataPacket.getDataValueCollectionModifier().write(0, metadata);

        // 3. Send to players in range
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("arhkelib.nomarker") && p.getWorld().equals(loc.getWorld()) && p.getLocation().distanceSquared(loc) < 900) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, spawnPacket);
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, metadataPacket);
            }
        }

        // 4. Destroy Packet (Despawns the armor stand)
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketContainer destroyPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                destroyPacket.getIntLists().write(0, List.of(entityId));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, destroyPacket);
                }
            }
        }.runTaskLater(this.getPlugin(), ticks);
    }

    public void setTicksLived(Entity entity, long ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                entity.remove();
            }
        }.runTaskLater(getPlugin(), ticks);
    }

}
