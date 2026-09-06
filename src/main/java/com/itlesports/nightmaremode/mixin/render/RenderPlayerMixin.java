package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.rendering.armor.BackTankRenderer;
import net.minecraft.src.AbstractClientPlayer;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin {
    @Shadow private ModelBiped modelBipedMain;

    @Inject(method = "renderSpecials", at = @At("RETURN"))
    private void renderBackTank(AbstractClientPlayer player, float partialTicks, CallbackInfo ci) {
        BackTankRenderer.render(player, this.modelBipedMain, 0.0625F);
    }
}
