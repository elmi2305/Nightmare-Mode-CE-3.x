package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import net.minecraft.src.Block;

import java.util.Random;

public class Tier3VillagerPost extends NetherVillagerPost {
    public static final int MIN_CHUNKS_APART = 4;
    public static final int MAX_CHUNKS_APART = 12;
    private static final VillagerOffset[] VILLAGER_OFFSETS = {
            new VillagerOffset(0.0D, -2.0D, 0.0D),
            new VillagerOffset(0.0D, -2.0D, -13.0D),
            new VillagerOffset(-13.0D, -2.0D, 0.0D),
            new VillagerOffset(-13.0D, -2.0D, -13.0D)
    };
    public Tier3VillagerPost() {}

    public Tier3VillagerPost(Random random, int x, int z) {
        super(random, x, z, 49, 24, 49);
        this.coordBaseMode = 0;
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
        tierThreeBlocks[0] = block(0, 0);
        tierThreeBlocks[1] = block(Block.netherrack.blockID, 4);
        tierThreeBlocks[2] = block(Block.obsidian.blockID, 0);
        tierThreeBlocks[3] = block(Block.netherBrick.blockID, 0);
        tierThreeBlocks[4] = block(Block.glowStone.blockID, 0);
        tierThreeBlocks[5] = block(Block.netherrack.blockID, 0);
        tierThreeBlocks[6] = block(Block.fire.blockID, 0);
        tierThreeBlocks[7] = block(Block.blockNetherQuartz.blockID, 1);
        tierThreeBlocks[8] = block(Block.netherFence.blockID, 0);
        tierThreeBlocks[9] = block(Block.stoneSingleSlab.blockID, 14);
        tierThreeBlocks[10] = block(NMBlock.slowSand.blockID, 0);
        tierThreeBlocks[11] = block(Block.enchantmentTable.blockID, 0);
        tierThreeBlocks[12] = block(BTWBlocks.quartzMouldingAndDecorative.blockID, 12);
        tierThreeBlocks[13] = block(BTWBlocks.deepStrataRoughStone.blockID, 0);
        tierThreeBlocks[14] = block(NMBlocks.netherProgressionGems.blockID, 2);
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
        return 8;
    }

    @Override
    protected VillagerOffset[] getVillagerOffsets() {
        return VILLAGER_OFFSETS;
    }

    @Override
    protected int getTier() {
        return 3;
    }
}
