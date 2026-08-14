package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.*;

import java.util.Random;

public class WorldGenEnderNest extends WorldGenerator {
    @Override public boolean generate(World world, Random random, int x, int y, int z) {
        int stoneMeta = (long)x * x + (long)z * z >= 1000L * 1000L ? 1 : 0;
        for (int dx = -4; dx <= 4; ++dx) {
            for (int dz = -4; dz <= 4; ++dz) {
                for (int dy = -1; dy <= 4; ++dy) {
                    boolean shell = dy == -1 || dy == 4 || Math.abs(dx) == 4 || Math.abs(dz) == 4;
                    if (shell) world.setBlockAndMetadata(x + dx, y + dy, z + dz, Block.whiteStone.blockID, stoneMeta);
                    else world.setBlockToAir(x + dx, y + dy, z + dz);
                }
            }
        }
        world.setBlock(x, y, z, Block.mobSpawner.blockID, 0, 2);
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof TileEntityMobSpawner spawner) spawner.getSpawnerLogic().setMobID("NmEnderSilverfish");
        this.placeChest(world, random, x - 3, y, z);
        this.placeChest(world, random, x + 3, y, z);
        return true;
    }

    private void placeChest(World world, Random random, int x, int y, int z) {
        world.setBlock(x, y, z, Block.chest.blockID, 0, 2);
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!(tile instanceof TileEntityChest chest)) return;
        chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), new ItemStack(Item.enderPearl, 1 + random.nextInt(3)));
        chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), new ItemStack(Item.clay, 2 + random.nextInt(5)));
    }
}
