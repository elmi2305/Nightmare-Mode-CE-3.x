package com.itlesports.nightmaremode.mixin;

import api.item.items.SeedItem;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SeedItem.class)
public class SeedItemMixin {
    @Inject(method = "onItemUse", at = @At("RETURN"))
    private void trackCropPlanting(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                   int facing, float clickX, float clickY, float clickZ,
                                   CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            SkillHandler.incrementCropsPlanted(player);
        }
    }
}
