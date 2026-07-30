package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import com.itlesports.nightmaremode.util.interfaces.IHighSpeedMinecart;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockRailBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemMinecart;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.List;

public class ItemHighSpeedMinecart extends ItemMinecart implements INetherItem {
    public ItemHighSpeedMinecart(int id, int minecartType) {
        super(id, minecartType);
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int facing, float clickX, float clickY, float clickZ) {
        if (!BlockRailBase.isRailBlock(world.getBlockId(x, y, z))) {
            return false;
        }
        if (!world.isRemote) {
            EntityMinecart minecart = EntityMinecart.createMinecart(world, x + 0.5F, y + 0.5F, z + 0.5F, this.minecartType);
            ((IHighSpeedMinecart) minecart).nightmareMode$setHighSpeed(true);
            if (stack.hasDisplayName()) {
                minecart.setMinecartName(stack.getDisplayName());
            }
            world.spawnEntityInWorld(minecart);
        }
        --stack.stackSize;
        return true;
    }

    @Override
    public boolean onItemUsedByBlockDispenser(ItemStack stack, World world, int x, int y, int z, int facing) {
        int[] offsetX = {0, 0, 0, 0, -1, 1};
        int[] offsetY = {-1, 1, 0, 0, 0, 0};
        int[] offsetZ = {0, 0, -1, 1, 0, 0};
        double targetX = x + offsetX[facing] + 0.5D;
        double targetY = y + offsetY[facing] + 0.5D;
        double targetZ = z + offsetZ[facing] + 0.5D;
        List carts = world.getEntitiesWithinAABB(EntityMinecart.class,
                AxisAlignedBB.getAABBPool().getAABB(targetX - 0.5D, targetY - 0.5D, targetZ - 0.5D,
                        targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D));
        if (carts != null && !carts.isEmpty()) {
            return false;
        }

        EntityMinecart minecart = EntityMinecart.createMinecart(world, targetX, targetY, targetZ, this.minecartType);
        ((IHighSpeedMinecart) minecart).nightmareMode$setHighSpeed(true);
        world.spawnEntityInWorld(minecart);
        ((Entity) minecart).setVelocity(offsetX[facing], offsetY[facing], offsetZ[facing]);
        world.playAuxSFX(1000, x, y, z, 0);
        return true;
    }
}
