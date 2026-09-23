package com.itlesports.nightmaremode.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(targets = "net.minecraft.src.ContainerCreative")
public interface ContainerCreativeAccess {
    @Accessor("itemList")
    List nightmareMode$getItemList();

    @Invoker("scrollTo")
    void nightmareMode$scrollTo(float scroll);
}
