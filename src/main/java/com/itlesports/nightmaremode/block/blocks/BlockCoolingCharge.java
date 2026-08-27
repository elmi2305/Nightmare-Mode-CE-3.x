package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

/** A placed hot charge that advances only while touching water and drops hot if broken early. */
public class BlockCoolingCharge extends Block {
    private final int hotItemID;
    private final int cooledItemID;
    private final String iconName;

    public BlockCoolingCharge(int id, int hotItemID, int cooledItemID, String name, String iconName) {
        super(id, Material.rock);
        this.hotItemID = hotItemID;
        this.cooledItemID = cooledItemID;
        this.iconName = iconName;
        this.setHardness(1.0F);
        this.setResistance(4.0F);
        this.setStepSound(Block.soundStoneFootstep);
        this.setUnlocalizedName(name);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
        world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!this.touchesWater(world, x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
            return;
        }

        int progress = world.getBlockMetadata(x, y, z) + 1;
        if (progress < 15) {
            world.setBlockMetadataWithNotify(x, y, z, progress, 2);
            world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
            return;
        }

        world.setBlockToAir(x, y, z);
        world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.25D, z + 0.5D,
                new ItemStack(this.cooledItemID, 1, 0)));
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 0.8F, 0.8F);
    }

    private boolean touchesWater(World world, int x, int y, int z) {
        return world.getBlockMaterial(x + 1, y, z) == Material.water
                || world.getBlockMaterial(x - 1, y, z) == Material.water
                || world.getBlockMaterial(x, y + 1, z) == Material.water
                || world.getBlockMaterial(x, y - 1, z) == Material.water
                || world.getBlockMaterial(x, y, z + 1) == Material.water
                || world.getBlockMaterial(x, y, z - 1) == Material.water;
    }

    @Override public int idDropped(int metadata, Random random, int fortune) { return this.hotItemID; }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon(this.iconName);
    }
}
