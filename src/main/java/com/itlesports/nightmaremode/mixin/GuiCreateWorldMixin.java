package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen implements GuiCreateWorldAccessor {
    @Inject(method = "updateButtonText", at = @At("HEAD"))
    private void manageDifficulty(CallbackInfo ci){
        if(this.getDifficultyID() != 2){this.setDifficultyID(2);}
    }
}
