package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;

import java.util.Random;

public class Tier2VillagerPost extends NetherVillagerPost {
    public static final int MIN_CHUNKS_APART = 4;
    public static final int MAX_CHUNKS_APART = 12;
    public Tier2VillagerPost() {}

    public Tier2VillagerPost(Random random, int x, int z) {
        super(random, x, z, 29, 14, 29);
    }


    private static PaletteEntry[] tierTwoBlocks;

    @Override
    protected PaletteEntry[] getPalette() {
        if(tierTwoBlocks == null) {
            tierTwoBlocks = createPalette();
        }
        return tierTwoBlocks;
    }
    private static PaletteEntry[] createPalette() {
        tierTwoBlocks = new PaletteEntry[21];
        tierTwoBlocks[0] = block(Block.netherrack.blockID, 3);
        tierTwoBlocks[1] = block(0, 0);
        tierTwoBlocks[2] = block(Block.blockNetherQuartz.blockID, 0);
        tierTwoBlocks[3] = block(Block.netherFence.blockID, 0);
        tierTwoBlocks[4] = block(Block.netherBrick.blockID, 0);
        tierTwoBlocks[5] = block(Block.glowStone.blockID, 0);
        tierTwoBlocks[6] = block(BTWBlocks.quartzMouldingAndDecorative.blockID, 12); // quartz pillar
        tierTwoBlocks[7] = block(Block.obsidian.blockID, 0);
        tierTwoBlocks[8] = block(Block.endPortalFrame.blockID, 0);
        tierTwoBlocks[9] = block(Block.stoneSingleSlab.blockID, 0);
        tierTwoBlocks[10] = block(Block.netherrack.blockID, 0);
        tierTwoBlocks[11] = block(Block.enchantmentTable.blockID, 0);
        tierTwoBlocks[12] = block(Block.beacon.blockID, 0);
        tierTwoBlocks[13] = block(Block.oreDiamond.blockID, 0);
        tierTwoBlocks[14] = block(Block.wood.blockID, 0);
        tierTwoBlocks[15] = block(Block.chest.blockID, 0);
        tierTwoBlocks[16] = block(Block.oreCoal.blockID, 0);
        tierTwoBlocks[17] = block(Block.hay.blockID, 0);
        tierTwoBlocks[18] = block(Block.oreEmerald.blockID, 0);


        return tierTwoBlocks;
    }
    @Override
    protected String getStructurePath() {
        return "structures/tier2villagerpost.nbt";
    }

    @Override
    protected int getVillagerProfession() {
        return 7;
    }

    @Override
    protected double getVillagerHorizontalOffset() {
        return 8.0D;
    }

    @Override
    protected double getVillagerVerticalOffset() {
        return 3.0D;
    }

    @Override
    protected int getTier() {
        return 2;
    }
}
