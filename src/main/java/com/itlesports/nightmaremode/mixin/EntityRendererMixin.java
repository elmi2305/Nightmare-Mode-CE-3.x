package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.mixin.EntityAccess;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import btw.util.status.PlayerStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements EntityAccess{
                            // MEA CODE. credit to Pot_tx
    @Shadow
    private Minecraft mc;

    @ModifyArgs(
            method = "updateCameraAndRender(F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 0)
    )
    private void slowSmoothCameraInWeb(Args args) {
        if (((EntityAccess)this.mc.thePlayer).getIsInWeb()) {
            args.set(0, (float) args.get(0) * 0.25F);
            args.set(1, (float) args.get(1) * 0.25F);
        }
    }

    @ModifyArgs(
            method = "updateCameraAndRender(F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 1)
    )
    private void slowCameraInWeb(Args args) {
        if (((EntityAccess)this.mc.thePlayer).getIsInWeb()) {
            args.set(0, (float) args.get(0) * 0.25F);
            args.set(1, (float) args.get(1) * 0.25F);

        }
    }
}
