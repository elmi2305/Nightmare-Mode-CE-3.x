package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityCreeper.class)
public interface EntityCreeperAccessor {
    @Accessor("fuseTime")
    int getFuseTime();
    @Accessor("fuseTime")
    void setFuseTime(int par1);
}
