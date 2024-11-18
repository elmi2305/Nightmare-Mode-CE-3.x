package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerSP.class)

public abstract class EntityPlayerSPMixin extends EntityPlayer{
    /* MODIFY updateGloomState() in EntityPlayerSP
     * THIS CODE IS ONLY RELEVANT FOR CAVE SOUNDS WHILE IN GLOOM. This code is made to mimic the code in EntityPlayerMP and
     * EntityPlayerMPMixin. This can be taken away at any time to effectively achieve the same effect
     */

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }
    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter+=5;
        }
    }
}

