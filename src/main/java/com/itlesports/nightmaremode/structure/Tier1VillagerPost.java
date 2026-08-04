package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.Block;

import java.util.Random;

public class Tier1VillagerPost extends NetherVillagerPost {
    public static final int MIN_CHUNKS_APART = 8;
    public static final int MAX_CHUNKS_APART = 16;
    private static final VillagerOffset[] VILLAGER_OFFSETS = {
            new VillagerOffset(5.5D, -3.0D, 5.5D),
            new VillagerOffset(5.5D, -3.0D, -5.5D),
            new VillagerOffset(-5.5D, -3.0D, 5.5D),
            new VillagerOffset(-5.5D, -3.0D, -5.5D)
    };
    private static PaletteEntry[] tierOneBlocks;

    public Tier1VillagerPost() {}

    @Override
    protected PaletteEntry[] getPalette() {
        if(tierOneBlocks == null) {
            tierOneBlocks = createPalette();
        }
        return tierOneBlocks;
    }
    private static PaletteEntry[] createPalette() {
        tierOneBlocks = new PaletteEntry[21];
        tierOneBlocks[1] = block(0, 0);
        tierOneBlocks[2] = block(Block.netherBrick.blockID, 0);
        tierOneBlocks[3] = block(Block.obsidian.blockID, 0);
        tierOneBlocks[4] = block(Block.glowStone.blockID, 0);
        tierOneBlocks[0] = block(Block.netherrack.blockID, 2);
        return tierOneBlocks;
    }

    public Tier1VillagerPost(Random random, int x, int z) {
        super(random, x, z, 17, 6, 17);
    }

    @Override
    protected String getStructurePath() {
        return "structures/tier1villagerpost.nbt";
    }

    @Override
    protected int getVillagerProfession() {
        return 6;
    }

    @Override
    protected VillagerOffset[] getVillagerOffsets() {
        return VILLAGER_OFFSETS;
    }

    @Override
    protected int getTier() {
        return 1;
    }
}
