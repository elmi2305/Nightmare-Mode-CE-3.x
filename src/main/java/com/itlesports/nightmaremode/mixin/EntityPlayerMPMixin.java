package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.World;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityPlayer;

// MODIFY updateGloomState() in EntityPlayerMP

// This mixin modifies code pertaining to the player effects while in gloom for a long time.
@Mixin(EntityPlayerMP.class)

public abstract class EntityPlayerMPMixin extends EntityPlayer{
    public EntityPlayerMPMixin(World par1World, String par2Str) {
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

