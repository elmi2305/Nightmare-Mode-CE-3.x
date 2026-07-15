package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Icon;
import net.minecraft.src.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureMap.class)
public abstract class TextureMapMixin {
    @Shadow
    public abstract Icon registerIcon(String textureName);

    @Shadow
    public abstract int getTextureType();

    @Inject(method = "registerIcons", at = @At("TAIL"))
    private void registerCustomIcons(CallbackInfo ci){
        if (this.getTextureType() == 0) {
            NMFields.ICON_SLURRY = this.registerIcon("nightmare:slurry");
            NMFields.ICON_BRINE = this.registerIcon("nightmare:brine");
            NMFields.ICON_ACID = this.registerIcon("nightmare:acid");
        }
    }
}
