package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccess {
    @Accessor("isInWeb")
    boolean getIsInWeb();
    @Accessor("fire")
    int getFire();
    @Accessor("fire")
    void invokeSetFire(int tick);
    @Accessor("isImmuneToFire")
    void setIsImmuneToFire(boolean par1);
}
