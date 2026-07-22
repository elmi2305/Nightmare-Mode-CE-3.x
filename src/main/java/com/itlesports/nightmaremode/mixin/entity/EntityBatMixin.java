package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntityAmbientCreature;
import net.minecraft.src.EntityBat;
import net.minecraft.src.World;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBat.class)
public class EntityBatMixin extends EntityAmbientCreature {
    public EntityBatMixin(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void tickBatCarcass(CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            carcass.nm$tickCarcass();
            ci.cancel();
        }
    }

    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 1,ordinal = 0))
    private int increaseDrops(int constant){
        return this.rand.nextInt(4) == 0 ? 3 : 2;
    }

    @Inject(method = "checkForScrollDrop", at = @At(value = "HEAD"), cancellable = true)
    private void reduceScrollDrops(CallbackInfo ci){
        ci.cancel();
    }
}
