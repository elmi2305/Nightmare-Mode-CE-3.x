package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntityFX;
import net.minecraft.src.EntityRainFX;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRainFX.class)
public class EntityRainFXMixin extends EntityFX {
    public EntityRainFXMixin(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageBloodRain(World par1World, double par2, double par4, double par6, CallbackInfo ci){
        if (NMUtils.getIsBloodMoon()) {
            this.particleRed = 1.0f;
            this.particleGreen = 0.05f;
            this.particleBlue = 0.05f;
        }
    }
}
