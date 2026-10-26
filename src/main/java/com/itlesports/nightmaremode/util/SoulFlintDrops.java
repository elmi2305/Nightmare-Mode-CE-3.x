package com.itlesports.nightmaremode.util;

import api.item.util.ItemUtils;
import net.minecraft.src.*;

public final class SoulFlintDrops {
    private SoulFlintDrops() {}

    public static void eject(World world, EntityPlayer player, int x, int y, int z, ItemStack stack) {
        if (world.isRemote) return;
        Vec3 eye = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLook(1.0F);
        MovingObjectPosition hit = Block.netherrack.collisionRayTrace(world, x, y, z, eye,
                eye.addVector(look.xCoord * 8.0D, look.yCoord * 8.0D, look.zCoord * 8.0D));
        if (hit == null) {
            hit = Block.netherrack.collisionRayTrace(world, x, y, z, eye,
                    Vec3.createVectorHelper(x + 0.5D, y + 0.5D, z + 0.5D));
        }
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, stack, hit == null ? 1 : hit.sideHit);
    }
}
