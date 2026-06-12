package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.MapGenStructureIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MapGenStructureIO.class)
public interface MapGenStructureIOAccess {
    @Invoker("func_143031_a")
    static void invokeFunction(Class className, String string){}
    @Invoker("func_143034_b")
    static void invokeFunctionB(Class className, String string){}
}
