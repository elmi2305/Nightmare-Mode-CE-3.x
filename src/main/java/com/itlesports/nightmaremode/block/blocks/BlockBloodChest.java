package com.itlesports.nightmaremode.block.blocks;


import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
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
        int blockIdNorth = world.getBlockId(x, y, z - 1);
        int blockIdSouth = world.getBlockId(x, y, z + 1);
        int blockIdWest = world.getBlockId(x - 1, y, z);
        int blockIdEast = world.getBlockId(x + 1, y, z);

        byte facing = 0;

        // Determine block facing based on player rotation
        int rotation = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (rotation == 0) {
            facing = 2; // North
        } else if (rotation == 1) {
            facing = 5; // East
        } else if (rotation == 2) {
            facing = 3; // South
        } else if (rotation == 3) {
            facing = 4; // West
        }

        if (blockIdNorth != this.blockID && blockIdSouth != this.blockID &&
                blockIdWest != this.blockID && blockIdEast != this.blockID) {
            world.setBlockMetadataWithNotify(x, y, z, facing, 3);
        } else {
            // Handle double chest linking along Z axis
            if ((blockIdNorth == this.blockID || blockIdSouth == this.blockID) && (facing == 4 || facing == 5)) {
                if (blockIdNorth == this.blockID) {
                    world.setBlockMetadataWithNotify(x, y, z - 1, facing, 3);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z + 1, facing, 3);
                }
                world.setBlockMetadataWithNotify(x, y, z, facing, 3);
            }

            // Handle double chest linking along X axis
            if ((blockIdWest == this.blockID || blockIdEast == this.blockID) && (facing == 2 || facing == 3)) {
                if (blockIdWest == this.blockID) {
                    world.setBlockMetadataWithNotify(x - 1, y, z, facing, 3);
                } else {
                    world.setBlockMetadataWithNotify(x + 1, y, z, facing, 3);
                }
                world.setBlockMetadataWithNotify(x, y, z, facing, 3);
            }
        }

        // Set custom name from item stack, if present
        if (itemStack.hasDisplayName()) {
            TileEntityBloodChest tile = (TileEntityBloodChest) world.getBlockTileEntity(x, y, z);
            tile.setChestGuiName(itemStack.getDisplayName());
        }
    }


    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
    }

    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
        TileEntityBloodChest var7 = (TileEntityBloodChest) par1World.getBlockTileEntity(par2, par3, par4);
        if (var7 != null) {
            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
                ItemStack var9 = var7.getStackInSlot(var8);
                if (var9 != null) {
                    float var10 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.random.nextFloat() * 0.8F + 0.1F;

                    EntityItem var14;
                    for (float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; par1World.spawnEntityInWorld(var14)) {
                        int var13 = this.random.nextInt(21) + 10;
                        if (var13 > var9.stackSize) {
                            var13 = var9.stackSize;
                        }

                        var9.stackSize -= var13;
                        var14 = new EntityItem(par1World, (double) ((float) par2 + var10), (double) ((float) par3 + var11), (double) ((float) par4 + var12), new ItemStack(var9.itemID, var13, var9.getItemDamage()));
                        float var15 = 0.05F;
                        var14.motionX = (double) ((float) this.random.nextGaussian() * var15);
                        var14.motionY = (double) ((float) this.random.nextGaussian() * var15 + 0.2F);
                        var14.motionZ = (double) ((float) this.random.nextGaussian() * var15);
                        if (var9.hasTagCompound()) {
                            var14.getEntityItem().setTagCompound((NBTTagCompound) var9.getTagCompound().copy());
                        }
                    }
                }
            }

            par1World.func_96440_m(par2, par3, par4, par5);
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
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
        if (blockAccess.getBlockId(i, j, k - 1) == this.blockID) {
            return AxisAlignedBB.getAABBPool().getAABB((double) 0.0625F, (double) 0.0F, (double) 0.0F, (double) 0.9375F, (double) 0.875F, (double) 0.9375F);
        } else if (blockAccess.getBlockId(i, j, k + 1) == this.blockID) {
            return AxisAlignedBB.getAABBPool().getAABB((double) 0.0625F, (double) 0.0F, (double) 0.0625F, (double) 0.9375F, (double) 0.875F, (double) 1.0F);
        } else if (blockAccess.getBlockId(i - 1, j, k) == this.blockID) {
            return AxisAlignedBB.getAABBPool().getAABB((double) 0.0F, (double) 0.0F, (double) 0.0625F, (double) 0.9375F, (double) 0.875F, (double) 0.9375F);
        } else {
            return blockAccess.getBlockId(i + 1, j, k) == this.blockID ? AxisAlignedBB.getAABBPool().getAABB((double) 0.0625F, (double) 0.0F, (double) 0.0625F, (double) 1.0F, (double) 0.875F, (double) 0.9375F) : AxisAlignedBB.getAABBPool().getAABB((double) 0.0625F, (double) 0.0F, (double) 0.0625F, (double) 0.9375F, (double) 0.875F, (double) 0.9375F);
        }
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