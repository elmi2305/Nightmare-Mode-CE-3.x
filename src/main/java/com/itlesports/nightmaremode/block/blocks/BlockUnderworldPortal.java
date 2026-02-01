package com.itlesports.nightmaremode.block.blocks;

import net.minecraft.src.*;

import static btw.community.nightmaremode.NightmareMode.UNDERWORLD_DIMENSION;

public class BlockUnderworldPortal extends BlockBreakable {

    public BlockUnderworldPortal(int id) {
        super(id, "nightmare:underworld_portal", Material.portal, false);
        setHardness(-1.0f);
        setLightValue(0.75f);
        setTickRandomly(true);
    }

    @Override public boolean isOpaqueCube() { return false; }
    @Override public boolean renderAsNormalBlock() { return false; }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity.ridingEntity == null && entity.riddenByEntity == null && entity instanceof EntityPlayerMP player) {
            if (player.timeUntilPortal > 0) return;  // cooldown
            player.timeUntilPortal = 100;
            // transfer:
            player.mcServer.getConfigurationManager().transferPlayerToDimension(player, UNDERWORLD_DIMENSION);
        }
    }
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
        float fHalfWidth;
        float fHalfDepth;
        if (blockAccess.getBlockId(i - 1, j, k) != this.blockID && blockAccess.getBlockId(i + 1, j, k) != this.blockID) {
            fHalfWidth = 0.125F;
            fHalfDepth = 0.5F;
        } else {
            fHalfWidth = 0.5F;
            fHalfDepth = 0.125F;
        }

        return AxisAlignedBB.getAABBPool().getAABB((double)(0.5F - fHalfWidth), (double)0.0F, (double)(0.5F - fHalfDepth), (double)(0.5F + fHalfWidth), (double)1.0F, (double)(0.5F + fHalfDepth));
    }


    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        if (par1IBlockAccess.getBlockId(par2, par3, par4) == this.blockID) {
            return false;
        } else {
            boolean var6 = par1IBlockAccess.getBlockId(par2 - 1, par3, par4) == this.blockID && par1IBlockAccess.getBlockId(par2 - 2, par3, par4) != this.blockID;
            boolean var7 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4) == this.blockID && par1IBlockAccess.getBlockId(par2 + 2, par3, par4) != this.blockID;
            boolean var8 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1) == this.blockID && par1IBlockAccess.getBlockId(par2, par3, par4 - 2) != this.blockID;
            boolean var9 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1) == this.blockID && par1IBlockAccess.getBlockId(par2, par3, par4 + 2) != this.blockID;
            boolean var10 = var6 || var7;
            boolean var11 = var8 || var9;
            return var10 && par5 == 4 ? true : (var10 && par5 == 5 ? true : (var11 && par5 == 2 ? true : var11 && par5 == 3));
        }
    }
}
