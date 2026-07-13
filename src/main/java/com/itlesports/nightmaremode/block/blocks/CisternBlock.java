package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class CisternBlock extends BlockCauldron implements ITileEntityProvider {
    private final Random random = new Random();
    private Icon innerIcon;
    private Icon topIcon;
    private Icon bottomIcon;

    public CisternBlock(int id) {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setStepSound(soundMetalFootstep);
        this.initBlockBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new CisternTileEntity();
    }

    @Override
    public int idDropped(int meta, Random random, int fortune) {
        return this.blockID;
    }

    @Override
    public int idPicked(World world, int x, int y, int z) {
        return this.blockID;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!(tile instanceof CisternTileEntity)) {
            return false;
        }
        CisternTileEntity cistern = (CisternTileEntity) tile;
        if (world.isRemote) {
            return true;
        }

        ItemStack held = player.inventory.getCurrentItem();
        if (player.isSneaking()) {
            cistern.stir();
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.splash", 0.2F, 1.2F + world.rand.nextFloat() * 0.2F);
            return true;
        }

        if (held == null) {
            ItemStack output = cistern.removeFirstOutput();
            if (output != null) {
                givePlayerStackOrDrop(world, player, output);
            }
            return true;
        }

        if (held.itemID == Item.bucketWater.itemID) {
            if (cistern.addFluid(CisternTileEntity.FLUID_WATER)) {
                if (!player.capabilities.isCreativeMode) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketEmpty));
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.splash", 0.4F, 1.0F);
            }
            return true;
        }

        if (held.itemID == Item.bucketEmpty.itemID && cistern.getFluid() == CisternTileEntity.FLUID_WATER) {
            cistern.drainFluid();
            if (!player.capabilities.isCreativeMode) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketWater));
            }
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.splash", 0.4F, 0.8F);
            return true;
        }

        if (cistern.insertInput(held)) {
            if (!player.capabilities.isCreativeMode) {
                --held.stackSize;
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.2F, 1.0F);
            return true;
        }

        return true;
    }

    @Override
    public void fillWithRain(World world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof CisternTileEntity && world.rand.nextInt(20) == 0) {
            ((CisternTileEntity) tile).addFluid(CisternTileEntity.FLUID_WATER);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int meta) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof CisternTileEntity) {
            CisternTileEntity cistern = (CisternTileEntity) tile;
            for (int i = 0; i < cistern.getSizeInventory(); ++i) {
                ItemStack stack = cistern.getStackInSlot(i);
                if (stack != null) {
                    this.dropInventoryStack(world, x, y, z, stack);
                }
            }
            world.func_96440_m(x, y, z, blockID);
        }
        super.breakBlock(world, x, y, z, blockID, meta);
    }

    private void dropInventoryStack(World world, int x, int y, int z, ItemStack stack) {
        float fx = this.random.nextFloat() * 0.8F + 0.1F;
        float fy = this.random.nextFloat() * 0.8F + 0.1F;
        float fz = this.random.nextFloat() * 0.8F + 0.1F;
        while (stack.stackSize > 0) {
            int count = this.random.nextInt(21) + 10;
            if (count > stack.stackSize) {
                count = stack.stackSize;
            }
            stack.stackSize -= count;
            ItemStack drop = new ItemStack(stack.itemID, count, stack.getItemDamage());
            if (stack.hasTagCompound()) {
                drop.setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
            }
            EntityItem entity = new EntityItem(world, x + fx, y + fy, z + fz, drop);
            float velocity = 0.05F;
            entity.motionX = this.random.nextGaussian() * velocity;
            entity.motionY = this.random.nextGaussian() * velocity + 0.2F;
            entity.motionZ = this.random.nextGaussian() * velocity;
            world.spawnEntityInWorld(entity);
        }
    }

    private static void givePlayerStackOrDrop(World world, EntityPlayer player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItem(stack);
        } else if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.topIcon : side == 0 ? this.bottomIcon : this.blockIcon;
    }

    @Override
    public void registerIcons(IconRegister register) {
        this.innerIcon = register.registerIcon("cauldron_inner");
        this.topIcon = register.registerIcon("cauldron_top");
        this.bottomIcon = register.registerIcon("cauldron_bottom");
        this.blockIcon = register.registerIcon("cauldron_side");
    }

    public Icon getInnerIcon() {
        return this.innerIcon;
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 24;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof CisternTileEntity) {
            return ((CisternTileEntity) tile).getFluid();
        }
        return 0;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity entity) {
        AxisAlignedBB tempBox = this.getCollisionBoundingBoxFromPool(world, x, y, z);
        tempBox.addToListIfIntersects(box, list);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        return renderer.renderBlockCauldron(this, x, y, z);
    }
}
