package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityBat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityBat.class)
public class EntityBatMixin {
    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 1,ordinal = 0))
    private int increaseDrops(int constant){
        EntityBat thisObj = (EntityBat)(Object)this;
        return thisObj.rand.nextInt(4)==0 ? 3 : 2;
    }
}
