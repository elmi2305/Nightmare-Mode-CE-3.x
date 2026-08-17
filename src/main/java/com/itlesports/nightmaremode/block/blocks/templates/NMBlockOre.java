package com.itlesports.nightmaremode.block.blocks.templates;

import api.block.blocks.OreBlockStaged;
import com.itlesports.nightmaremode.util.NMFields;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public abstract class NMBlockOre extends OreBlockStaged {
    @Environment(value=EnvType.CLIENT)
    private Icon[] iconByMetadataArray;

    public NMBlockOre(int iBlockID) {
        super(iBlockID);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public float getExplosionResistance(Entity entity, World world, int i, int j, int k) {
        return this.getExplosionResistance(entity);
    }
    @Override
    @Environment(value= EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.iconByMetadataArray = new Icon[16];
        super.registerIcons(register);
        this.iconByMetadataArray[0] = this.blockIcon;
        this.iconByMetadataArray[1] = register.registerIcon(this.getTextureName() + "_strata_2");
        this.iconByMetadataArray[2] = register.registerIcon(this.getTextureName() + "_strata_3");
        for (int iTempIndex = 3; iTempIndex < 16; ++iTempIndex) {
            this.iconByMetadataArray[iTempIndex] = this.blockIcon;
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        return this.iconByMetadataArray[iMetadata];
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    public abstract boolean canBeMined(IBlockAccess world, int i, int j, int k);
}
