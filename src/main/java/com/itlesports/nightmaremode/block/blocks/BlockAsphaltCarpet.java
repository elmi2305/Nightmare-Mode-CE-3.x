package com.itlesports.nightmaremode.block.blocks;

import btw.entity.mob.JungleSpiderEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;


public class BlockAsphaltCarpet extends Block {
    public BlockAsphaltCarpet(int par1) {
        super(par1, Material.rock);
        this.initBlockBounds((double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F, (double)0.0625f, (double)1.0F);
        this.setTickRandomly(true);
        this.func_111047_d(0);
    }
    public Icon getIcon(int par1, int par2) {
        return NMBlocks.blockAsphalt.getIcon(par1, par2);
    }

    protected void func_111047_d(int par1) {
        byte var2 = 0;
        float var3 = (float)((1 + var2)) / 16f;
        this.setBlockCollisionBounds(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
    }

    public void setBlockCollisionBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = (double)minX;
        this.minY = (double)minY;
        this.minZ = (double)minZ;
        this.maxX = (double)maxX;
        this.maxY = (double)maxY;
        this.maxZ = (double)maxZ;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

        return AxisAlignedBB.getAABBPool().getAABB(
                (double)x + this.minX,
                (double)y + this.minY,
                (double)z + this.minZ,
                (double)x + this.maxX,
                (double)y + this.maxY,
                (double)z + this.maxZ
        );
    }


    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public void setBlockBoundsForItemRender() {
        this.func_111047_d(0);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        this.func_111047_d(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return super.canPlaceBlockAt(par1World, par2, par3, par4) && this.canBlockStay(par1World, par2, par3, par4);
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        this.func_111046_k(par1World, par2, par3, par4);
    }

    private boolean func_111046_k(World par1World, int par2, int par3, int par4) {
        if (!this.canBlockStay(par1World, par2, par3, par4)) {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
            return false;
        } else {
            return true;
        }
    }

    public boolean canBlockStay(World par1World, int par2, int par3, int par4) {
        return !par1World.isAirBlock(par2, par3 - 1, par4);
    }

    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }

    public int damageDropped(int par1) {
        return par1;
    }


    public void registerIcons(IconRegister par1IconRegister) {}
    @Override
    public float getMovementModifier(World world, int i, int j, int k) {
        return NMBlocks.blockAsphalt.getMovementModifier(world,i,j,k);
    }

    @Override
    public boolean canPathThroughBlock(IBlockAccess blockAccess, int x, int y, int z, Entity entity, PathFinder pathFinder) {
        return true;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        return false;
    }
}
