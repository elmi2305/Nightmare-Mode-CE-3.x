package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.MillstoneBlock;
import com.itlesports.nightmaremode.block.tileEntities.ObsidianMillstoneTileEntity;
import com.itlesports.nightmaremode.mixin.blocks.MillstoneBlockAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/** A millstone with standard mechanics and recipes, but obsidian-specific art. */
public class BlockObsidianMillstone extends MillstoneBlock {
    public BlockObsidianMillstone(int blockID) {
        super(blockID);
        this.setUnlocalizedName("ifhyObsidianMillstone");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ObsidianMillstoneTileEntity();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        Icon[] off = new Icon[6];
        Icon[] offFull = new Icon[6];
        Icon[] on = new Icon[6];
        Icon[] onFull = new Icon[6];

        this.blockIcon = register.registerIcon("nightmare:ifhyObsidianMillstone");
        offFull[0] = off[0] = register.registerIcon("nightmare:ifhyObsidianMillstoneBottom");
        offFull[1] = off[1] = register.registerIcon("nightmare:ifhyObsidianMillstoneTop");
        on[0] = onFull[0] = register.registerIcon("nightmare:ifhyObsidianMillstoneBottomPowered");
        on[1] = onFull[1] = register.registerIcon("nightmare:ifhyObsidianMillstoneTopPowered");

        Icon side = this.blockIcon;
        Icon sideFull = register.registerIcon("nightmare:ifhyObsidianMillstoneFull");
        Icon sidePowered = register.registerIcon("nightmare:ifhyObsidianMillstonePowered");
        Icon sidePoweredFull = register.registerIcon("nightmare:ifhyObsidianMillstonePoweredFull");
        for (int sideIndex = 2; sideIndex <= 5; ++sideIndex) {
            off[sideIndex] = side;
            offFull[sideIndex] = sideFull;
            on[sideIndex] = sidePowered;
            onFull[sideIndex] = sidePoweredFull;
        }

        MillstoneBlockAccessor accessor = (MillstoneBlockAccessor)this;
        accessor.nightmareMode$setIconsBySide(off);
        accessor.nightmareMode$setIconsBySideFull(offFull);
        accessor.nightmareMode$setIconsBySideOn(on);
        accessor.nightmareMode$setIconsBySideOnFull(onFull);
    }
}
