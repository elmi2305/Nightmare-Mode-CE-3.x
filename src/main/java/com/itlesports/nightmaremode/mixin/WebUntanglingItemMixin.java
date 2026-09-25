package com.itlesports.nightmaremode.mixin;

import btw.item.items.WebUntanglingItem;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WebUntanglingItem.class)
public class WebUntanglingItemMixin {
    @Inject(method = "onEaten", at = @At("RETURN"), cancellable = true)
    private void returnSpiderSilk(ItemStack stack, World world, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(new ItemStack(NMItems.spiderSilk));
    }
}
