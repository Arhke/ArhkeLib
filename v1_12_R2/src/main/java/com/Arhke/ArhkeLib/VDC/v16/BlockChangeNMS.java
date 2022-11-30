//package com.Arhke.ArhkeLib.Lib.Utils;
//
//import org.bukkit.World;
//
//public class BlockChangeNMS {
//    public static void setBlockInNativeWorld(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
//        net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) world).getHandle();
//        BlockPosition bp = new BlockPosition(x, y, z);
//        IBlockData ibd = net.minecraft.server.v1_14_R1.Block.getByCombinedId(blockId + (data << 12));
//        nmsWorld.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
//    }
//    public static void setBlockInNativeChunk(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
//        net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) world).getHandle();
//        net.minecraft.server.v1_14_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
//        BlockPosition bp = new BlockPosition(x, y, z);
//        IBlockData ibd = net.minecraft.server.v1_14_R1.Block.getByCombinedId(blockId + (data << 12));
//        nmsChunk.setType(bp, ibd, applyPhysics);
//    }
//    public static void setBlockInNativeChunkSection(World world, int x, int y, int z, int blockId, byte data) {
//        net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) world).getHandle();
//        net.minecraft.server.v1_14_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
//        IBlockData ibd = net.minecraft.server.v1_14_R1.Block.getByCombinedId(blockId + (data << 12));
//
//        ChunkSection cs = nmsChunk.getSections()[y >> 4];
//        if (cs == nmsChunk.a()) {
//            cs = new ChunkSection(y >> 4 << 4);
//            nmsChunk.getSections()[y >> 4] = cs;
//        }
//        cs.setType(x & 15, y & 15, z & 15, ibd);
//    }
//    public static void setBlockInNativeDataPalette(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
//        net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) world).getHandle();
//        net.minecraft.server.v1_14_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
//        IBlockData ibd = net.minecraft.server.v1_14_R1.Block.getByCombinedId(blockId + (data << 12));
//
//        ChunkSection cs = nmsChunk.getSections()[y >> 4];
//        if (cs == nmsChunk.a()) {
//            cs = new ChunkSection(y >> 4 << 4);
//            nmsChunk.getSections()[y >> 4] = cs;
//        }
//
//        if (applyPhysics)
//            cs.getBlocks().setBlock(x & 15, y & 15, z & 15, ibd);
//        else
//            cs.getBlocks().b(x & 15, y & 15, z & 15, ibd);
//    }
//}
