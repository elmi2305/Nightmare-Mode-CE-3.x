package com.itlesports.nightmaremode.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.block.blocks.UnfiredPotteryBlock;
import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;

import java.util.List;

public class BlockEnderCeramic extends BlockMetaMultiTextured {
    public BlockEnderCeramic(int id) {
        super(id, Material.clay,
                Variant.allSides("nightmare:ifhyEnderCeramicBlank").hardness(0.6F).name("ifhyEnderCeramicBlank").build(),
                Variant.allSides("nightmare:ifhyUnfiredCrucibleLiner").hardness(0.6F).name("ifhyUnfiredCrucibleLiner").build());
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override public void getSubBlocks(int id, CreativeTabs tab, List list) {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        IBlockAccess blockAccess = renderBlocks.blockAccess;
        if (blockAccess.getBlockMetadata(x, y, z) != 1) {
            return super.renderBlock(renderBlocks, x, y, z);
        }

        return UnfiredPotteryBlock.renderUnfiredCrucible(renderBlocks, blockAccess, x, y, z,
                this, this.getBlockTexture(blockAccess, x, y, z, 0));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int metadata, float brightness) {
        if (metadata != 1) {
            super.renderBlockAsItem(renderBlocks, metadata, brightness);
            return;
        }

        renderUnfiredCrucibleLinerItem(renderBlocks, this.getIcon(0, metadata));
    }

    /** The inventory equivalent of UnfiredPotteryBlock's crucible renderer. */
    @Environment(EnvType.CLIENT)
    private void renderUnfiredCrucibleLinerItem(RenderBlocks renderBlocks, Icon texture) {
        double[][] parts = {
                {0.0625, 0.0, 0.0625, 0.1875, 1.0, 0.8125},
                {0.0625, 0.0, 0.8125, 0.8125, 1.0, 0.9375},
                {0.8125, 0.0, 0.1875, 0.9375, 1.0, 0.9375},
                {0.1875, 0.0, 0.0625, 0.9375, 1.0, 0.1875},
                {0.1875, 0.0, 0.1875, 0.8125, 0.125, 0.8125},
                {0.0, 0.125, 0.0, 0.125, 0.875, 0.875},
                {0.0, 0.125, 0.875, 0.875, 0.875, 1.0},
                {0.875, 0.125, 0.125, 1.0, 0.875, 1.0},
                {0.125, 0.125, 0.0, 1.0, 0.875, 0.125}
        };
        for (double[] part : parts) {
            renderBlocks.setRenderBounds(part[0], part[1], part[2], part[3], part[4], part[5]);
            RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, texture);
        }
    }
}
