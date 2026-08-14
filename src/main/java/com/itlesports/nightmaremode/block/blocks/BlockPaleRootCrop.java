package com.itlesports.nightmaremode.block.blocks;

import api.block.blocks.CropsBlock;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BlockPaleRootCrop extends CropsBlock {
    @Environment(EnvType.CLIENT) private Icon[] growthIcons;

    public BlockPaleRootCrop(int id) {
        super(id);
        this.setUnlocalizedName("ifhyPaleRootCrop");
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!this.updateIfBlockStays(world, x, y, z) || this.isFullyGrown(world, x, y, z)) return;
        long radiusSq = (long)x * x + (long)z * z;
        if (world.provider.dimensionId == 1 && radiusSq >= 1000L * 1000L) {
            this.attemptToGrow(world, x, y, z, random);
        }
    }

    @Override protected boolean canGrowOnBlock(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == NMBlocks.endFarmland.blockID;
    }
    @Override protected int getCropItemID() { return NMItems.paleRoot.itemID; }
    @Override protected int getSeedItemID() { return NMItems.paleRootSeeds.itemID; }
    @Override public int quantityDropped(Random random) { return 2 + random.nextInt(3); }
    @Override public float getBaseGrowthChance(World world, int x, int y, int z) { return 0.12F; }
    @Override protected int getLightLevelForGrowth() { return 0; }
    @Override protected boolean canGrowAtCurrentLightLevel(World world, int x, int y, int z) { return true; }

    @Override @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.growthIcons = new Icon[8];
        for (int stage = 0; stage < this.growthIcons.length; ++stage) {
            this.growthIcons[stage] = register.registerIcon("nightmare:ifhyPaleRootCrop_stage_" + stage);
        }
        this.blockIcon = this.growthIcons[7];
    }
    @Override @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.growthIcons[Math.max(0, Math.min(7, metadata & 7))];
    }
}
