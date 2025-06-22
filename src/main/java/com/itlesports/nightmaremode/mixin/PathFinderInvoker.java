package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.PathFinder;
import net.minecraft.src.PathPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PathFinder.class)
public interface PathFinderInvoker {
    @Invoker("openPoint")
    PathPoint invokeOpenPoint(int par1, int par2, int par3);
}
