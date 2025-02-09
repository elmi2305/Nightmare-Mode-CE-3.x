package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityAgeable;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable {
    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "isSecondaryTargetForSquid", at = @At("HEAD"),cancellable = true)
    private void squidAvoidAnimalsOnEclipse(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!NightmareUtils.getIsMobEclipsed(this));
    }
}
