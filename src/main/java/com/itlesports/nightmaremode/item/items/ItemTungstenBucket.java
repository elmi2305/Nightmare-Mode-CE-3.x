package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NetherItem;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;

public class ItemTungstenBucket extends NetherItem {
    private final boolean containsLava;

    public ItemTungstenBucket(int id, boolean containsLava) {
        super(id);
        this.containsLava = containsLava;
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, !this.containsLava);
        if (hit == null || hit.typeOfHit != EnumMovingObjectType.TILE) {
            return stack;
        }

        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;
        if (!world.canMineBlock(player, x, y, z)) {
            return stack;
        }

        if (!this.containsLava) {
            if (!player.canPlayerEdit(x, y, z, hit.sideHit, stack)
                    || world.getBlockMaterial(x, y, z) != Material.lava
                    || world.getBlockMetadata(x, y, z) != 0) {
                return stack;
            }
            world.setBlockToAir(x, y, z);
            return player.capabilities.isCreativeMode ? stack : new ItemStack(NMItems.tungstenLavaBucket);
        }

        if (hit.sideHit == 0) --y;
        if (hit.sideHit == 1) ++y;
        if (hit.sideHit == 2) --z;
        if (hit.sideHit == 3) ++z;
        if (hit.sideHit == 4) --x;
        if (hit.sideHit == 5) ++x;
        if (!player.canPlayerEdit(x, y, z, hit.sideHit, stack) || !this.tryPlaceLava(world, x, y, z)) {
            return stack;
        }
        return player.capabilities.isCreativeMode ? stack : new ItemStack(NMItems.tungstenBucket);
    }

    private boolean tryPlaceLava(World world, int x, int y, int z) {
        Material material = world.getBlockMaterial(x, y, z);
        if (!world.isAirBlock(x, y, z) && material.isSolid()) {
            return false;
        }
        if (!world.isRemote && !material.isLiquid() && !world.isAirBlock(x, y, z)) {
            world.destroyBlock(x, y, z, true);
        }
        world.setBlock(x, y, z, Block.lavaMoving.blockID, 0, 3);
        return true;
    }
}
