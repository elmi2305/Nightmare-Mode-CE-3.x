package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockRailBase;
import net.minecraft.src.Container;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityMinecartChest;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IInventory;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.World;

import java.util.List;
import java.util.Random;

public class BlockStationRail extends BlockRailBase {
    private Icon occupiedIcon;

    public BlockStationRail(int id) {
        super(id, true);
        this.setHardness(0.7F);
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName("ifhyStationRail");
        this.setTextureName("nightmare:ifhyStationRail");
        this.setTickRandomly(true);
    }

    @Override
    public int tickRate(World world) {
        return 10;
    }
//
//    @Override
//    public boolean canProvidePower() {
//        return true;
//    }
//
//    @Override
//    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
//        return this.hasChestCart(blockAccess.getBlockMetadata(x, y, z)) ? 15 : 0;
//    }
//
//    @Override
//    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
//        return side == 1 && this.hasChestCart(blockAccess.getBlockMetadata(x, y, z)) ? 15 : 0;
//    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        List carts = this.getChestCarts(world, x, y, z);
        return carts.isEmpty() ? 0 : Container.calcRedstoneFromInventory((IInventory)carts.get(0));
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (!world.isRemote) {
            this.updateChestCartState(world, x, y, z);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            this.updateChestCartState(world, x, y, z);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (world.isRemote || !(entity instanceof EntityMinecart minecart)) {
            return;
        }
        if (world.isBlockGettingPowered(x, y - 1, z)) {
            double speed = Math.sqrt(minecart.motionX * minecart.motionX + minecart.motionZ * minecart.motionZ);
            if (speed > 0.01D) {
                minecart.motionX += minecart.motionX / speed * 0.08D;
                minecart.motionZ += minecart.motionZ / speed * 0.08D;
            } else {
                int direction = world.getBlockMetadata(x, y, z) & 7;
                if (direction == 1 || direction == 2 || direction == 3) {
                    minecart.motionX = 0.08D;
                } else {
                    minecart.motionZ = 0.08D;
                }
            }
        } else {
            minecart.motionX = 0.0D;
            minecart.motionY = 0.0D;
            minecart.motionZ = 0.0D;
        }
        this.updateChestCartState(world, x, y, z);
    }

    private void updateChestCartState(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        boolean occupied = !this.getChestCarts(world, x, y, z).isEmpty();
        boolean wasOccupied = this.hasChestCart(metadata);
        if (occupied != wasOccupied) {
            int updated = occupied ? metadata | 8 : metadata & 7;
            world.setBlockMetadataWithNotify(x, y, z, updated, 3);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        }
        if (occupied) {
            world.func_96440_m(x, y, z, this.blockID);
            world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
        }
    }

    private List getChestCarts(World world, int x, int y, int z) {
        float inset = 0.125F;
        return world.getEntitiesWithinAABB(
                EntityMinecartChest.class,
                AxisAlignedBB.getAABBPool().getAABB(
                        x + inset, y, z + inset,
                        x + 1 - inset, y + 1, z + 1 - inset));
    }

    private boolean hasChestCart(int metadata) {
        return (metadata & 8) != 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("rail_detector");
        this.occupiedIcon = register.registerIcon("rail_detector_powered");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.hasChestCart(metadata) ? this.occupiedIcon : this.blockIcon;
    }
}
