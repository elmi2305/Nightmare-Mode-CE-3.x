package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.GuiSelectWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiSelectWorld.class)
public interface GuiSelectWorldAccess {
    @Invoker("func_82311_i")
    static String functionI(GuiSelectWorld gui){
        throw new AssertionError();
    }
    @Invoker("func_82314_j")
    static String[] functionJ(GuiSelectWorld gui){
        throw new AssertionError();
    }
}
