package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBaseInvoker {
    @Accessor("maxHurtResistantTime")
    void setHurtResistantTime(int par1);
}
