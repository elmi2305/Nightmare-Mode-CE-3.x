package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlockOre;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class ProcessingOreBlock extends NMBlockOre {
    private final int dropItemID;
    private final int requiredToolLevel;

    public ProcessingOreBlock(int id, int dropItemID, int requiredToolLevel) {
        super(id);
        this.dropItemID = dropItemID;
        this.requiredToolLevel = requiredToolLevel;
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setPicksEffectiveOn();
    }

    @Override
    public boolean canBeMined(IBlockAccess world, int i, int j, int k) {
        return true;
    }

    @Override
    public int idDropped(int metadata, Random rand, int fortune) {
        return this.dropItemID;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k) {
        return this.requiredToolLevel;
    }

    @Override
    public int idDroppedOnConversion(boolean dropPiles, int metadata) {
        return this.dropItemID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 0));
    }
}
