package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.util.StorageColor;
import com.itlesports.nightmaremode.util.interfaces.IColoredChest;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockChest.class)
public class BlockChestMixin {
    @Inject(method = "onBlockPlacedBy", at = @At("TAIL"))
    private void restoreChestColor(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack,
                                   CallbackInfo ci) {
        if (world.isRemote) return;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof IColoredChest chest && StorageColor.hasChestItemColor(stack)) {
            chest.nm$setChestColor(StorageColor.getChestItemColor(stack));
        }
        StorageColor.mixAdjacentChestColors(world, x, y, z);
    }

}
