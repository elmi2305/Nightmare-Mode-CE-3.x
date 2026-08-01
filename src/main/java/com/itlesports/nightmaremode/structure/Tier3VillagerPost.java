package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;

import java.util.Random;

public class Tier3VillagerPost extends NetherVillagerPost {
    public static final int MIN_CHUNKS_APART = 4;
    public static final int MAX_CHUNKS_APART = 12;
    public Tier3VillagerPost() {}

    public Tier3VillagerPost(Random random, int x, int z) {
        super(random, x, z, 49, 24, 49);
    }
    private static PaletteEntry[] tierThreeBlocks;

    @Override
    protected PaletteEntry[] getPalette() {
        if(tierThreeBlocks == null) {
            tierThreeBlocks = createPalette();
        }
        return tierThreeBlocks;
    }
    private static PaletteEntry[] createPalette() {
        tierThreeBlocks = new PaletteEntry[21];
        tierThreeBlocks[0] = block(Block.netherrack.blockID, 4);
        tierThreeBlocks[1] = block(0, 0);
        tierThreeBlocks[2] = block(Block.blockNetherQuartz.blockID, 0);
        tierThreeBlocks[3] = block(Block.netherFence.blockID, 0);
        tierThreeBlocks[4] = block(Block.netherBrick.blockID, 0);
        tierThreeBlocks[5] = block(Block.glowStone.blockID, 0);
        tierThreeBlocks[6] = block(BTWBlocks.quartzMouldingAndDecorative.blockID, 12); // quartz pillar
        tierThreeBlocks[7] = block(Block.obsidian.blockID, 0);
        tierThreeBlocks[8] = block(Block.endPortalFrame.blockID, 0);
        tierThreeBlocks[9] = block(Block.stoneSingleSlab.blockID, 0);
        tierThreeBlocks[10] = block(Block.netherrack.blockID, 0);
        tierThreeBlocks[11] = block(Block.enchantmentTable.blockID, 0);
        tierThreeBlocks[12] = block(Block.beacon.blockID, 0);
        tierThreeBlocks[13] = block(Block.oreDiamond.blockID, 0);
        tierThreeBlocks[14] = block(Block.wood.blockID, 0);
        tierThreeBlocks[15] = block(Block.chest.blockID, 0);
        tierThreeBlocks[16] = block(Block.oreCoal.blockID, 0);
        tierThreeBlocks[17] = block(Block.hay.blockID, 0);
        tierThreeBlocks[18] = block(Block.oreEmerald.blockID, 0);


        return tierThreeBlocks;
    }
    @Override
    protected String getStructurePath() {
        return "structures/tier3villagerpost.nbt";
    }

    @Override
    protected int getVillagerProfession() {
        return 3;
    }

    @Override
    protected double getVillagerHorizontalOffset() {
        return 8.0D;
    }

    @Override
    protected double getVillagerVerticalOffset() {
        return 8.0D;
    }

    @Override
    protected int getTier() {
        return 3;
    }
}
