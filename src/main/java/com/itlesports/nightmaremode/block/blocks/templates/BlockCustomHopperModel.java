package com.itlesports.nightmaremode.block.blocks.templates;

import btw.block.blocks.HopperBlock;
import com.itlesports.nightmaremode.block.models.CustomHopperModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.RenderBlocks;

/** renders hopper-derived blocks with the custom three-piece funnel model. */
public abstract class BlockCustomHopperModel extends HopperBlock {
    private final CustomHopperModel model = new CustomHopperModel();
    private final String topTextureName;
    private final String bottomTextureName;
    private final String sideTextureName;
    @Environment(EnvType.CLIENT)
    private Icon topIcon;
    @Environment(EnvType.CLIENT)
    private Icon bottomIcon;
    @Environment(EnvType.CLIENT)
    private Icon sideIcon;

    protected BlockCustomHopperModel(int id, String topTextureName,
                                     String bottomTextureName, String sideTextureName) {
        super(id);
        this.topTextureName = topTextureName;
        this.bottomTextureName = bottomTextureName;
        this.sideTextureName = sideTextureName;
        this.setTextureName(sideTextureName);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.topIcon = register.registerIcon(this.topTextureName);
        this.bottomIcon = register.registerIcon(this.bottomTextureName);
        this.sideIcon = register.registerIcon(this.sideTextureName);
        this.blockIcon = this.sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        if (side == 0) {
            return this.bottomIcon;
        }
        if (side == 1) {
            return this.topIcon;
        }
        return this.sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        return this.model.renderAsBlock(renderer, this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int itemDamage, float brightness) {
        this.model.renderAsItemBlock(renderer, this, itemDamage);
    }
}
