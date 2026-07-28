package com.itlesports.nightmaremode.mixin;

import api.util.color.Color;
import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDye.class)
public class ItemDyeMixin {
    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void applyNitrogenToFarmlandOrPlant(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float clickX,
            float clickY,
            float clickZ,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (stack.getItemDamage() != Color.WHITE.colorID
                || !ChunkAttributeManager.applyFertilizer(world, x, y, z, ChunkAttribute.NITROGEN)) {
            return;
        }
        if (player.capabilities == null || !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        cir.setReturnValue(true);
    }
}
