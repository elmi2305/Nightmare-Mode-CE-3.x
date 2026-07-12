package com.itlesports.nightmaremode.block.blocks.templates;

import api.block.blocks.GroundCoverBlock;
import btw.client.render.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class NMBlockGroundLayer extends GroundCoverBlock {
    protected int ticksExisted;
    private boolean driesOut;

    public NMBlockGroundLayer(int iBlockID, Material material) {
        super(iBlockID, material);
        this.setTickRandomly(true);
    }
    private int dropItemID;
    public NMBlockGroundLayer setDropItemID(int iDropItemID) {
        this.dropItemID = iDropItemID;
        return this;
    }
    public NMBlockGroundLayer setDriesOut(boolean b) {
        this.driesOut = b;
        return this;
    }


    @Environment(EnvType.CLIENT)
    protected Icon[] icons;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister reg) {
        icons = new Icon[3];
        icons[0] = reg.registerIcon("nightmare:" + this.textureName+"_meta_0");
        icons[1] = reg.registerIcon("nightmare:" + this.textureName+"_meta_1");
        icons[2] = reg.registerIcon("nightmare:" + this.textureName+"_meta_2");
        this.blockIcon = reg.registerIcon("nightmare:honey_meta_0");
        super.registerIcons(reg);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess access, int x, int y, int z, int side) {
        int meta = access.getBlockMetadata(x, y, z);
        return icons[Math.min(meta, icons.length - 1)];
    }

    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        return icons[Math.min(meta, icons.length - 1)];
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random rand) {
        this.ticksExisted += 20;

        int meta = w.getBlockMetadata(x,y,z);


        if(!this.driesOut) return;
        // 100 * 4 = 400 ticks, which is 20 seconds
        w.scheduleBlockUpdate(x, x, y, z, this.tickRate(w) * 2);

        if(this.ticksExisted >= 20){
            if(meta == 2){
//                this.clientBreakBlock(w,x,y,z,this.blockID,meta);
                w.destroyBlock(x, y, z, false);
            }
            w.setBlockMetadataWithNotify(x,y,z,Math.min(meta + 1,2));
            this.ticksExisted = 0;
        }
        super.updateTick(w, x, y, z, rand);
    }


    @Override
    public int tickRate(World par1World) {
        return 10;
    }

    @Override
    public String getModId() {
        return "nightmare";
    }

    @Override
    protected void dropItemsIndividually(World w, int x, int y, int z, int id, int count, int meta, float chance) {
        if(w.getBlockMetadata(x,y,z) != 0) return;
        super.dropItemsIndividually(w, x, y, z, id, count, meta, chance);
    }

    @Override
    protected void dropBlockAsItem_do(World w, int x, int y, int z, ItemStack stack) {
        if(w.getBlockMetadata(x,y,z) != 0) return;

        super.dropBlockAsItem_do(w, x, y, z, stack);
    }

    @Override
    public void dropBlockAsItemWithChance(World w, int x, int y, int z, int par5, float par6, int par7) {
        if(w.getBlockMetadata(x,y,z) != 0) return;

        super.dropBlockAsItemWithChance(w, x, y, z, par5, par6, par7);
    }

    @Override
    public int idDropped(int meta, Random par2Random, int par3) {
        if(meta != 0) return 0;
        return this.dropItemID;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        return super.getPlayerRelativeBlockHardness(player, world, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        IBlockAccess blockAccess = renderBlocks.blockAccess;
        if (blockAccess.getBlockId(x, y - 1, z) != 0) {
            float fVisualOffset = 0.0f;
            int iBlockBelowID = blockAccess.getBlockId(x, y - 1, z);
            Block blockBelow = Block.blocksList[iBlockBelowID];
            int meta = blockAccess.getBlockMetadata(x, y, z);
            if (blockBelow != null && (fVisualOffset = blockBelow.groundCoverRestingOnVisualOffset(blockAccess, x, y - 1, z)) < 0.0f) {
                --y;
                fVisualOffset += 1.0f;
            }
            float fHeight = 0.125f;
            renderBlocks.setRenderBounds(0.0, fVisualOffset, 0.0, 1.0, fHeight + fVisualOffset, 1.0);
            RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, x, y, z, icons[Math.min(meta, icons.length - 1)]);
        }
        return true;
    }
}
