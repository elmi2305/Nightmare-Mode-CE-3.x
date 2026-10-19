package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.CampfireBlock;
import btw.block.tileentity.CampfireTileEntity;
import com.itlesports.nightmaremode.util.NMCookedFoodTracking;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE",
            target = "Lbtw/block/tileentity/CampfireTileEntity;setCookStack(Lnet/minecraft/src/ItemStack;)V"))
    private void creditCookedFoodPickup(CampfireTileEntity tile, ItemStack replacement, World world,
                                        int x, int y, int z, EntityPlayer player, int side,
                                        float hitX, float hitY, float hitZ) {
        if (!world.isRemote && replacement == null) {
            NMCookedFoodTracking.creditCampfirePickup(player, tile.getCookStack());
        }
        tile.setCookStack(replacement);
    }
}
