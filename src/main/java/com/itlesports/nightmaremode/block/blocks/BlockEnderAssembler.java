package com.itlesports.nightmaremode.block.blocks;

import api.block.MechanicalBlock;
import api.block.util.MechPowerUtils;
import api.item.util.ItemUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.tileEntities.EnderAssemblerTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerEnderAssembler;
import net.minecraft.src.*;

public class BlockEnderAssembler extends BlockContainer implements MechanicalBlock {
    public BlockEnderAssembler(int id) {
        super(id, Material.iron);
        this.setHardness(6.0F).setResistance(30.0F).setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName("ifhyEnderAssembler");
        this.setTextureName("nightmare:ifhyEnderAssembler");
    }

    @Override public TileEntity createNewTileEntity(World world) { return new EnderAssemblerTileEntity(); }
    @Override public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hx, float hy, float hz) {
        if (!world.isRemote && world.getBlockTileEntity(x, y, z) instanceof EnderAssemblerTileEntity tile) {
            BTWMod.serverOpenCustomInterface((EntityPlayerMP)player, new ContainerEnderAssembler(player.inventory, tile), ContainerEnderAssembler.ID);
        }
        return true;
    }
    @Override public void breakBlock(World world, int x, int y, int z, int id, int meta) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof EnderAssemblerTileEntity assembler) {
            for (int slot = 0; slot < assembler.getSizeInventory(); ++slot) {
                ItemStack stack = assembler.getStackInSlot(slot);
                if (stack != null) ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, stack, 1);
            }
        }
        super.breakBlock(world, x, y, z, id, meta);
    }

    @Override public boolean canInputMechanicalPower() { return true; }
    @Override public boolean canOutputMechanicalPower() { return false; }
    @Override public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) { return facing == 0 || facing == 1; }
    @Override public boolean isInputtingMechanicalPower(World world, int x, int y, int z) {
        return MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, 0)
                || MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, 1);
    }
    @Override public boolean isOutputtingMechanicalPower(World world, int x, int y, int z) { return false; }
    @Override public void overpower(World world, int x, int y, int z) {
        this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        world.setBlockWithNotify(x, y, z, 0);
    }
}
