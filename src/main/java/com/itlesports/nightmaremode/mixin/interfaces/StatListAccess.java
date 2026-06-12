package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.StatList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StatList.class)
public interface StatListAccess {
    @Accessor("oneShotStats")
    static Map getOneShotStats(){
        throw new AssertionError();
    };
}
