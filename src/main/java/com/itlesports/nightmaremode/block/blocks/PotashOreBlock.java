package com.itlesports.nightmaremode.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import java.util.Random;

public class PotashOreBlock extends ProcessingOreBlock {
    public PotashOreBlock(int id) {
        super(id, BTWItems.potash.itemID, 1);
        this.setHardness(2.0F);
        this.setUnlocalizedName("ifhyPotashOre");
        this.setTextureName("nightmare:ifhyPotashOre");
    }

    @Override public boolean hasStrata() { return false; }
    @Override public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) { return 1; }
    @Override public int quantityDropped(Random random) { return 1 + random.nextInt(3); }
    @Override public int quantityDroppedOnConversion(Random random) { return this.quantityDropped(random); }
    @Override public int idDroppedOnStonePickConversion(int meta, Random random, int fortune) { return BTWItems.potash.itemID; }
    @Override public int quantityDroppedOnStonePickConversion(Random random) { return this.quantityDropped(random); }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) { this.blockIcon = register.registerIcon(this.getTextureName()); }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) { return this.blockIcon; }
}
