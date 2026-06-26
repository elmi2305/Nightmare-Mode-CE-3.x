package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.elements.NMEvents;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
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
    private void manageBloodRain(World w, double par2, double par4, double par6, CallbackInfo ci){
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        if (NMUtils.getIsBloodMoon() || NMUtils.isNearActiveRitual(p, 128)) {
            this.particleRed = 1.0f;
            this.particleGreen = 0.05f;
            this.particleBlue = 0.05f;
        } else if(NMEvents.SimpleEvent.SLIME_RAIN.isActive()){
            this.particleRed = 0.05f;
            this.particleGreen = 0.95f;
            this.particleBlue = 0.05f;
        }
    }
}
