package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends EntityPlayer{
    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void playMusicInTheEnd(Minecraft par1Minecraft, World par2World, Session par3Session, int par4, CallbackInfo ci) {
        if (par2World.provider.dimensionId == 1) {
            NightmareUtils.forcePlayMusic("nightmare_mode:nmBoss", true);
        }
        else {
            NightmareUtils.shushMusic();
        }
    }

    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter += 5;
        }
    }
}

