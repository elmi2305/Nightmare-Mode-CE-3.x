package com.itlesports.nightmaremode.block.blocks;

import api.block.util.Flammability;
import btw.block.blocks.HopperBlock;
import com.itlesports.nightmaremode.block.tileEntities.CisternDrainTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.CisternInterfaceTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/** Hopper-shaped, non-inventory fluid input/output attachment for cisterns. */
public class BlockCisternDrain extends HopperBlock {
    public BlockCisternDrain(int id) {
        super(id);
        this.setHardness(4.0F);
        this.setResistance(20.0F);
        this.setFireProperties(Flammability.NONE);
        this.setPicksEffectiveOn();
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName("ifhyCisternDrain");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new CisternDrainTileEntity();
    }

    @Override public void onBlockAdded(World world, int x, int y, int z) {}
    @Override public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {}
    @Override public void updateTick(World world, int x, int y, int z, java.util.Random random) {}
    @Override public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {}
    @Override public void onArrowCollide(World world, int x, int y, int z, EntityArrow arrow) {}

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int metadata) {
        world.removeBlockTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            CisternTileEntity above = this.resolveCistern(world, x, y + 1, z);
            CisternTileEntity below = this.resolveCistern(world, x, y - 1, z);
            String connection = above != null ? "draining cistern above"
                    : below != null ? "filling cistern below" : "disconnected";
            player.sendChatToPlayer(ChatMessageComponent.createFromText("Cistern Drain: " + connection));
        }
        return true;
    }

    private CisternTileEntity resolveCistern(World world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof CisternTileEntity cistern) return cistern;
        if (tile instanceof CisternInterfaceTileEntity cisternInterface) return cisternInterface.getCistern();
        return null;
    }

    @Override public boolean hasComparatorInputOverride() { return false; }
    @Override public int getComparatorInputOverride(World world, int x, int y, int z, int side) { return 0; }
    @Override public boolean canInputMechanicalPower() { return false; }
    @Override public boolean isInputtingMechanicalPower(World world, int x, int y, int z) { return false; }
    @Override public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) { return false; }
    @Override public void overpower(World world, int x, int y, int z) {}
    @Override public int getHarvestToolLevel(IBlockAccess access, int x, int y, int z) { return 2; }
    @Override public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int metadata, float chance) { return false; }
}
