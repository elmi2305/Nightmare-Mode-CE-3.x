package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.EntityPlayer;

/* MODIFY updateGloomState() in EntityPlayerSP
* THIS CODE IS ONLY RELEVANT FOR CAVE SOUNDS WHILE IN GLOOM. This code is made to mimic the code in EntityPlayerMP and
* EntityPlayerMPMixin. This can be taken away at any time to effectively achieve the same effect with less clock cycles
*/
@Mixin(EntityPlayerSP.class)

public abstract class EntityPlayerSPMixin extends EntityPlayer{
    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }
    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter+=5;
        }
    }
//    @Inject(method = "updateGloomState", at = @At(value = "FIELD", ordinal = 2))
//    protected void incrementInGloomCounter(CallbackInfo ci) {
//        inGloomCounter+=6;
//    }
}

