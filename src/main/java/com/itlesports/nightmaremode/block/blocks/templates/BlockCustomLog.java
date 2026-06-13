package com.itlesports.nightmaremode.block.blocks.templates;

import api.block.util.Flammability;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BlockCustomLog extends BlockRotatedPillar {
    public static final String[] woodType = new String[]{"void", "flower"};

    @Environment(value = EnvType.CLIENT)
    private Icon[] sideIcons;

    @Environment(value = EnvType.CLIENT)
    private Icon[] topIcons;

    @Override
    public String getModId() {
        return "nightmare";
    }

    public BlockCustomLog(int blockID) {
        super(blockID, BTWBlocks.logMaterial);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(1.25f);
        this.setResistance(3.33f);
        this.setAxesEffectiveOn();
        this.setChiselsEffectiveOn();
        this.setBuoyant();
        this.setFireProperties(Flammability.LOGS);
        this.setStepSound(soundWoodFootstep);
        this.setUnlocalizedName("customLog");
    }

    @Override
    public boolean canEndermenPickUpBlock(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return this.blockID;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int metadata) {
        int leafCheckRange = 4;
        int chunkCheckRange = leafCheckRange + 1;

        if (world.checkChunksExist(x - chunkCheckRange, y - chunkCheckRange, z - chunkCheckRange, x + chunkCheckRange, y + chunkCheckRange, z + chunkCheckRange)) {
            for (int dx = -leafCheckRange; dx <= leafCheckRange; ++dx) {
                for (int dy = -leafCheckRange; dy <= leafCheckRange; ++dy) {
                    for (int dz = -leafCheckRange; dz <= leafCheckRange; ++dz) {
                        int offsetBlockID = world.getBlockId(x + dx, y + dy, z + dz);
                        Block offsetBlock = Block.blocksList[offsetBlockID];

                        if (offsetBlock == null || !offsetBlock.isLeafBlock(world, x + dx, y + dy, z + dz)) {
                            continue;
                        }

                        int offsetMetadata = world.getBlockMetadata(x + dx, y + dy, z + dz);
                        if ((offsetMetadata & 8) != 0) {
                            continue;
                        }

                        world.setBlockMetadataWithNotify(x + dx, y + dy, z + dz, offsetMetadata | 8, 4);
                    }
                }
            }
        }
    }

    @Override
    public boolean getCanBlockBeIncinerated(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int metadata, float chanceOfDrop) {
        this.dropItemsIndividually(world, x, y, z, BTWItems.sawDust.itemID, 6, 0, chanceOfDrop);
        this.dropItemsIndividually(world, x, y, z, BTWItems.bark.itemID, 1, metadata & 3, chanceOfDrop);
        return true;
    }

    @Override
    public void onDestroyedByFire(World world, int x, int y, int z, int fireAge, boolean forcedFireSpread) {
        this.convertToSmouldering(world, x, y, z);
    }

    @Override
    public int rotateMetadataAroundYAxis(int metadata, boolean reverse) {
        int axisAlignment = metadata & 0xC;
        if (axisAlignment != 0) {
            if (axisAlignment == 4) {
                axisAlignment = 8;
            } else if (axisAlignment == 8) {
                axisAlignment = 4;
            }
            metadata = metadata & 0xFFFFFFF3 | axisAlignment;
        }
        return metadata;
    }

    @Override
    public int getFurnaceBurnTime(int itemDamage) {
        return BlockWood.getFurnaceBurnTimeByWoodType(itemDamage) * 4;
    }

    @Override
    public boolean isLog(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canSupportLeaves(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }

    public void convertToSmouldering(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.smolderingLog.blockID, metadata);
    }

    public static int limitToValidMetadata(int metadata) {
        return metadata & 3;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return renderer.renderBlockLog(this, x, y, z);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int x, int y, int z, boolean firstPassResult) {
        this.renderCookingByKiLnOverlay(renderBlocks, x, y, z, firstPassResult);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 0));
        list.add(new ItemStack(blockID, 1, 1));
//        list.add(new ItemStack(blockID, 1, 2));
//        list.add(new ItemStack(blockID, 1, 3));
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        this.sideIcons = new Icon[woodType.length];
        this.topIcons = new Icon[woodType.length];

        for (int i = 0; i < this.sideIcons.length; ++i) {
            this.sideIcons[i] = iconRegister.registerIcon(this.getTextureName() + "_" + woodType[i]);
            this.topIcons[i] = i == 0
                    ? iconRegister.registerIcon(this.getTextureName() + "_" + woodType[i] + "_top")
                    : iconRegister.registerIcon("nightmare:" + woodType[i] + "_log_top");
        }
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    protected Icon getSideIcon(int index) {
        return this.sideIcons[index];
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    protected Icon getEndIcon(int index) {
        return this.topIcons[index];
    }
}