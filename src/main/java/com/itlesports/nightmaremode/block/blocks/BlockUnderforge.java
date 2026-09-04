package com.itlesports.nightmaremode.block.blocks;

import btw.BTWMod;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityUnderforge;
import com.itlesports.nightmaremode.nmgui.ContainerUnderforge;
import net.minecraft.src.*;

import java.util.Random;

public class BlockUnderforge extends BlockContainer {
    private final Random random = new Random();

    public BlockUnderforge(int id) {
        super(id, Material.iron);
        setHardness(12.0F);
        setResistance(40.0F);
        setCreativeTab(CreativeTabs.tabDecorations);
        setUnlocalizedName("nmUnderforge");
        setTextureName("nightmare:nmUnderforge");
    }

    @Override public String getModId() { return "nightmare"; }
    @Override public TileEntity createNewTileEntity(World world) { return new TileEntityUnderforge(); }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && world.getBlockTileEntity(x, y, z) instanceof TileEntityUnderforge forge) {
            BTWMod.serverOpenCustomInterface((EntityPlayerMP)player,
                    new ContainerUnderforge(player.inventory, forge), ContainerUnderforge.ID);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int oldId, int metadata) {
        if (world.getBlockTileEntity(x, y, z) instanceof TileEntityUnderforge forge) {
            for (int slot = 0; slot < forge.getSizeInventory(); slot++) {
                ItemStack stack = forge.getStackInSlot(slot);
                if (stack == null) continue;
                EntityItem entity = new EntityItem(world, x + .5, y + .5, z + .5, stack.copy());
                entity.motionX = random.nextGaussian() * .04;
                entity.motionY = .15;
                entity.motionZ = random.nextGaussian() * .04;
                world.spawnEntityInWorld(entity);
            }
        }
        super.breakBlock(world, x, y, z, oldId, metadata);
    }
}
