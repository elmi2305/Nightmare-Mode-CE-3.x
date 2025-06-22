package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLiving.class)
public interface EntityLivingAccessor {
    @Accessor("persistenceRequired")
    boolean getPersistence();
}
