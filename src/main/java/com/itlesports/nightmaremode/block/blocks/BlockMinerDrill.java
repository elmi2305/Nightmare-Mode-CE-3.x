package com.itlesports.nightmaremode.block.blocks;

import api.item.util.ItemUtils;
import api.util.MiscUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.tileEntities.MinerDrillTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerMinerDrill;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

public class BlockMinerDrill extends BlockContainer {
    public BlockMinerDrill(int id) {
        super(id, BTWBlocks.netherRockMaterial);
        this.setHardness(6.0F);
        this.setResistance(20.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName("ifhyMinerDrill");
        this.setTextureName("nightmare:ifhyMinerDrill");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new MinerDrillTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int facing = MiscUtils.convertOrientationToFlatBlockFacingReversed(placer);
        world.setBlockMetadataWithNotify(x, y, z, facing, 3);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof MinerDrillTileEntity drill) {
            BTWMod.serverOpenCustomInterface((EntityPlayerMP)player,
                    new ContainerMinerDrill(player.inventory, drill), ContainerMinerDrill.ID);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int metadata) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof MinerDrillTileEntity drill) {
            ItemStack fuel = drill.getStackInSlot(0);
            if (fuel != null) ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, fuel, 1);
        }
        super.breakBlock(world, x, y, z, blockId, metadata);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if ((world.getBlockMetadata(x, y, z) & 8) != 0) {
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "minecart.base",
                    1.0F + random.nextFloat() * 0.1F,
                    0.75F + random.nextFloat() * 0.1F);
        }
    }
}
