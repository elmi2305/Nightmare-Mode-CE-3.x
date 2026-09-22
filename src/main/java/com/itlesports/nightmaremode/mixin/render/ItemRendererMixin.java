package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.StorageColor;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Redirect(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;isBurning()Z"))
    private boolean avoidRenderingFireOverlayIfImmuneToFire(EntityClientPlayerMP player){
        if(player.isPotionActive(Potion.fireResistance)){
            return false;
        }
        return player.isBurning();
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void beginChestHeldRender(EntityLivingBase entity, ItemStack stack, int pass, CallbackInfo ci) {
        StorageColor.beginChestItemRender(stack);
    }

    @Inject(method = "renderItem", at = @At("TAIL"))
    private void endChestHeldRender(EntityLivingBase entity, ItemStack stack, int pass, CallbackInfo ci) {
        StorageColor.endChestItemRender();
    }
}
