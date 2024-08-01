package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntitySheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntitySheep.class)
public class EntitySheepMixin {
    @ModifyConstant(method = "interact", constant = @Constant(intValue = 1,ordinal = 1))
    private int modifyWoolCount(int constant){
        return 3;
    }
}
