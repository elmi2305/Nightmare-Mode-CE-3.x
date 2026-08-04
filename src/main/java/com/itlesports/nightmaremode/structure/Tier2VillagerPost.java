package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;

import java.util.Random;

public class Tier2VillagerPost extends NetherVillagerPost {
    public static final int MIN_CHUNKS_APART = 4;
    public static final int MAX_CHUNKS_APART = 12;
    private static final VillagerOffset[] VILLAGER_OFFSETS = {
            new VillagerOffset( 7.5D, -0.5D,7.5D),
            new VillagerOffset( 7.5D, -0.5D,-6.5D),
            new VillagerOffset(-6.5D, -0.5D,7.5D),
            new VillagerOffset(-6.5D, -0.5D,-6.5D)
    };
    public Tier2VillagerPost() {}

    public Tier2VillagerPost(Random random, int x, int z) {
        super(random, x, z, 30, 23, 30);
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
        tierTwoBlocks[0] = block(Block.thinGlass.blockID,0);
        tierTwoBlocks[1] = block(0, 0);
        tierTwoBlocks[2] = block(Block.netherrack.blockID, 3);
        tierTwoBlocks[3] = block(Block.slowSand.blockID, 0);
        tierTwoBlocks[4] = block(Block.netherFence.blockID, 0);
        tierTwoBlocks[5] = block(Block.netherBrick.blockID, 0);
        tierTwoBlocks[6] = block(Block.glowStone.blockID,0);
        tierTwoBlocks[7] = block(BTWBlocks.quartzMouldingAndDecorative.blockID, 12);
        tierTwoBlocks[8] = block(Block.obsidian.blockID, 0);
        tierTwoBlocks[9] = block(NMBlocks.netherProgressionGems.blockID, NMBlocks.META_PURPLE_GEM);
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
    protected VillagerOffset[] getVillagerOffsets() {
        return VILLAGER_OFFSETS;
    }

    @Override
    protected int getTier() {
        return 2;
    }
}
