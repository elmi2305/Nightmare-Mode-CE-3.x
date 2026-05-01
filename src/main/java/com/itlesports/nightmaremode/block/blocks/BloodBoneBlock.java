package com.itlesports.nightmaremode.block.blocks;

import api.util.MiscUtils;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodBone;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class BloodBoneBlock extends NMBlockTileEntity {
    private byte netherStarSide = 4; // resets to 4 when the game is restarted. this is west

    public BloodBoneBlock(int i) {
        super(i, Material.rock);
        this.setLightOpacity(0);
    }

    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int facing, float par6, float par7, float par8, int par9) {
        EntityPlayer placer = w.getClosestPlayer(x, y, z, 12);

        int iFacing = 4;
        if(placer != null) {
            iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(placer);
        }

        this.netherStarSide = (byte) iFacing;
        return super.onBlockPlaced(w, x, y, z, facing, par6, par7, par8, par9);
    }

    public byte getNetherStarSide() {
        return netherStarSide;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBloodBone();
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        // prevents rendering it while placed. tile entity does render
        return false;
    }

    @Override
    public boolean canBlockBePulledByPiston(World world, int i, int j, int k, int iToFacing) {
        return false;
    }

    @Override
    public void onBlockDestroyedByPlayer(World w, int x, int y, int z, int par5) {
        EntityPlayer p = w.getClosestPlayer(x,y,z, 6);


        if(p != null && !p.capabilities.isCreativeMode){
            w.addWeatherEffect(new EntityLightningBolt(w, p.posX, p.posY, p.posZ));
            w.newExplosion(null, x,y,z, 2f, false,false);
        }

        super.onBlockDestroyedByPlayer(w, x, y, z, par5);
    }

    @Override
    public void breakBlock(World w, int x, int y, int z, int id, int meta) {
        TileEntityBloodBone te =  (TileEntityBloodBone) w.getBlockTileEntity(x, y, z);
        if(te.isActive()){
            te.cancelRitual();
        }
        super.breakBlock(w, x, y, z, id, meta);
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World w, EntityPlayer p, int x, int y, int z, int iMetadata) {

        if(p != null && !p.capabilities.isCreativeMode){
            w.addWeatherEffect(new EntityLightningBolt(w, p.posX, p.posY, p.posZ));
            w.newExplosion(null, x,y,z, 2f, false,false);
        }

        super.onBlockDestroyedWithImproperTool(w, p, x, y, z, iMetadata);
    }


    @Override
    public boolean canBlockBePushedByPiston(World world, int i, int j, int k, int iToFacing) {
        return false;
    }
}
