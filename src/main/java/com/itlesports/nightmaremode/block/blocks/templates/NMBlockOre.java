package com.itlesports.nightmaremode.block.blocks.templates;

import api.block.blocks.OreBlockStaged;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public abstract class NMBlockOre extends OreBlockStaged {
    public NMBlockOre(int iBlockID) {
        super(iBlockID);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public float getExplosionResistance(Entity entity, World world, int i, int j, int k) {
        return this.getExplosionResistance(entity);
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    public abstract boolean canBeMined(IBlockAccess world, int i, int j, int k);
}
