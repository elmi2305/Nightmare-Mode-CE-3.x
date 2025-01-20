package com.itlesports.nightmaremode.mixin.render;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.ItemRenderer;
import net.minecraft.src.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Redirect(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;isBurning()Z"))
    private boolean avoidRenderingFireOverlayIfImmuneToFire(EntityClientPlayerMP player){
        if(player.isPotionActive(Potion.fireResistance)){
            return false;
        }
        return player.isBurning();
    }
}
