package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityEgg.class)
public abstract class EntityEggMixin extends EntityThrowable {
    public EntityEggMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "onImpact", constant = @Constant(intValue = 4, ordinal = 0))
    private int modifyAmountOfBonusChicken(int constant) {
        if(this.rand.nextInt(10) == 0){
            return 3;
        }
        return 2;
    }
    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityChicken;getTicksForChildToGrow()I"))
    private int makeBabiesGrowFaster(EntityChicken instance){
        return (int)(instance.getTicksForChildToGrow() / 2);
    }

    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 0))
    private int increaseChickenChance(int bound){
        return 4;
    }
    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 1))
    private int increaseMultiChickenChance(int bound){
        return 4;
    }
}
