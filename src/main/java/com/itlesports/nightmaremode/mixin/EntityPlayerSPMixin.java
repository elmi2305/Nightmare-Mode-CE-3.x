package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.NightmareModeAddon;
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
        // #TODO: Make this music not play when dragon is dead
        if (par2World.provider.dimensionId == 1) {
            NMUtils.forcePlayMusic(NightmareModeAddon.NM_BOSS_MUSIC.sound(), true);
        }
        else {
            NMUtils.shushMusic();
        }
    }
}

