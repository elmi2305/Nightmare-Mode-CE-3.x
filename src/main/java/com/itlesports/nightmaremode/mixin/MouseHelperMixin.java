package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.client.CarcassHarvestClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MouseHelper.class)
public class MouseHelperMixin {
    @Shadow public int deltaX;
    @Shadow public int deltaY;

    @Inject(method = "mouseXYChange", at = @At("RETURN"))
    private void lockMouseWhileHarvesting(CallbackInfo ci) {
        if (CarcassHarvestClient.isHarvesting()) {
            this.deltaX = 0;
            this.deltaY = 0;
        }
    }
}
