package com.itlesports.nightmaremode.block.blocks;


import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.util.StorageColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BlockBloodChest extends BlockContainer {
    private final Random random = new Random();
    public final int chestType;

    public BlockBloodChest(int par1, int par2) {
        super(par1, Material.iron);
        this.chestType = par2;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockMaterial(BTWBlocks.plankMaterial);
        this.setHardness(10f);
        this.setResistance(25f);
        this.setAxesEffectiveOn();
        this.setBuoyant();
        this.initBlockBounds((double) 0.0625F, (double) 0.0F, (double) 0.0625F, (double) 0.9375F, (double) 0.875F, (double) 0.9375F);
        this.setStepSound(soundMetalFootstep);
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @Override
    public int getRenderType() {
        return 22;
    }

    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        super.onBlockAdded(par1World, par2, par3, par4);
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack) {
        byte facing;
        int rotation = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (rotation == 0) {
            facing = 2;
        } else if (rotation == 1) {
            facing = 5;
        } else if (rotation == 2) {
            facing = 3;
        } else {
            facing = 4;
        }
        world.setBlockMetadataWithNotify(x, y, z, facing, 3);

        // Set custom name from item stack, if present
        if (itemStack.hasDisplayName()) {
            TileEntityBloodChest tile = (TileEntityBloodChest) world.getBlockTileEntity(x, y, z);
            tile.setChestGuiName(itemStack.getDisplayName());
        }
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof TileEntityBloodChest chest && StorageColor.hasChestItemColor(itemStack)) {
            chest.nm$setChestColor(StorageColor.getChestItemColor(itemStack));
        } else if (tile instanceof TileEntityBloodChest chest && StorageColor.hasStorageItemColor(itemStack)) {
            chest.nm$setStorageColor(StorageColor.getStorageItemColor(itemStack));
        }
    }


    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
    }

    public void breakBlock(World w, int x, int y, int z, int par5, int par6) {
        TileEntityBloodChest te = (TileEntityBloodChest) w.getBlockTileEntity(x, y, z);
        if (te != null) {
            for (int var8 = 0; var8 < te.getSizeInventory(); ++var8) {
                ItemStack stack = te.getStackInSlot(var8);
                if (stack != null) {
                    float var10 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.random.nextFloat() * 0.8F + 0.1F;

                    EntityItem itemEntity;
                    for (float var12 = this.random.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; w.spawnEntityInWorld(itemEntity)) {
                        int var13 = this.random.nextInt(21) + 10;
                        if (var13 > stack.stackSize) {
                            var13 = stack.stackSize;
                        }

                        stack.stackSize -= var13;
                        itemEntity = new EntityItem(w, (double) ((float) x + var10), (double) ((float) y + var11), (double) ((float) z + var12), new ItemStack(stack.itemID, var13, stack.getItemDamage()));
                        float velMultiplier = 0.05F;
                        itemEntity.motionX = (double) ((float) this.random.nextGaussian() * velMultiplier);
                        itemEntity.motionY = (double) ((float) this.random.nextGaussian() * velMultiplier + 0.2F);
                        itemEntity.motionZ = (double) ((float) this.random.nextGaussian() * velMultiplier);
                        if (stack.hasTagCompound()) {
                            itemEntity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                        }
                    }
                }
            }
            // notify comparators that this block changed
            w.func_96440_m(x, y, z, par5);
        }

        super.breakBlock(w, x, y, z, par5, par6);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        // Fix: prevent shift-click block placement from replacing the chest
        if (world.isRemote) {
            return true;
        }
        IInventory inventory = this.getInventory(world, x, y, z);
        if (inventory != null) {
            player.displayGUIChest(inventory);
        }

        return true;
    }
    public IInventory getInventory(World world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (!(tile instanceof TileEntityBloodChest)) {
            return null;
        }

        // Blocked by solid block above?
        if (world.isBlockRedstoneConductor(x, y + 1, z)) {
            return null;
        }

        // Blocked by ocelot?
        if (isOcelotBlockingChest(world, x, y, z)) {
            return null;
        }

        return (TileEntityBloodChest) tile;
    }


    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBloodChest();
    }

    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
        return AxisAlignedBB.getAABBPool().getAABB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    protected boolean canSilkHarvest(int iMetadata) {
        return true;
    }

    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 2;
    }

    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        this.dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 6, 0, fChanceOfDrop);
        this.dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
        return true;
    }

    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
        return blockAccess.getBlockId(i - 1, j, k) != this.blockID && blockAccess.getBlockId(i + 1, j, k) != this.blockID && blockAccess.getBlockId(i, j, k - 1) != this.blockID && blockAccess.getBlockId(i, j, k + 1) != this.blockID;
    }

    public int rotateMetadataAroundYAxis(int iMetadata, boolean bReverse) {
        return Block.rotateFacingAroundY(iMetadata, bReverse);
    }

    public boolean canSupportFallingBlocks(IBlockAccess blockAccess, int i, int j, int k) {
        return true;
    }

    public boolean canProvidePower() {
        return this.chestType == 0;
    }

    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        if (!this.canProvidePower()) {
            return 0;
        } else {
            int var6 = ((TileEntityBloodChest) par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).numUsingPlayers;
            return MathHelper.clamp_int(var6, 0, 15);
        }
    }

    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return par5 == 1 ? this.isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5) : 0;
    }

    private static boolean isOcelotBlockingChest(World par0World, int par1, int par2, int par3) {
        for (Object var5 : par0World.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getAABBPool().getAABB((double) par1, (double) (par2 + 1), (double) par3, (double) (par1 + 1), (double) (par2 + 2), (double) (par3 + 1)))) {
            if (((EntityOcelot) var5).isSitting()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
        return Container.calcRedstoneFromInventory(this.getInventory(par1World, par2, par3, par4));
    }

    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k) {
        return false;
    }

    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("nightmare:chestBlood_particle");
    }
}
