package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin {
    @Shadow protected abstract void setSlimeSize(int iSize);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeSize(World par1World, CallbackInfo ci){
        EntitySlime thisObj = (EntitySlime)(Object)this;
        if(thisObj instanceof EntityMagmaCube magmaCube && thisObj.dimension==0) {
            this.setSlimeSize(2);
            magmaCube.addPotionEffect(new PotionEffect(Potion.resistance.id,10000000,0));
        }
    }
}
