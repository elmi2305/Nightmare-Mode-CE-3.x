package com.itlesports.nightmaremode.block.blocks;

import api.block.MechanicalBlock;
import api.block.util.MechPowerUtils;
import api.item.util.ItemUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import com.itlesports.nightmaremode.block.tileEntities.EnderAssemblerTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerEnderAssembler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BlockEnderAssembler extends BlockContainer implements MechanicalBlock {
    @Environment(EnvType.CLIENT)
    private Icon[] sideIcons;

    public BlockEnderAssembler(int id) {
        super(id, Material.iron);
        this.setHardness(6.0F).setResistance(30.0F).setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.initBlockBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        this.setUnlocalizedName("ifhyEnderAssembler");
        this.setTextureName("nightmare:ifhyEnderAssembler");
    }

    @Override public TileEntity createNewTileEntity(World world) { return new EnderAssemblerTileEntity(); }

    @Override public boolean renderAsNormalBlock() { return false; }
    @Override public boolean isOpaqueCube() { return false; }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.sideIcons = new Icon[] {
                register.registerIcon("nightmare:ifhyEnderAssemblerBottom"),
                register.registerIcon("nightmare:ifhyEnderAssemblerTop"),
                register.registerIcon("nightmare:ifhyEnderAssemblerSide"),
                register.registerIcon("nightmare:ifhyEnderAssemblerSide"),
                register.registerIcon("nightmare:ifhyEnderAssemblerSide"),
                register.registerIcon("nightmare:ifhyEnderAssemblerSide")
        };
        this.blockIcon = this.sideIcons[2];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.sideIcons != null && side >= 0 && side < this.sideIcons.length
                ? this.sideIcons[side] : this.blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return side == 1 || super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        renderBlocks.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        return renderBlocks.renderStandardBlock(this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int metadata, float brightness) {
        renderBlocks.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, metadata);
    }

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
