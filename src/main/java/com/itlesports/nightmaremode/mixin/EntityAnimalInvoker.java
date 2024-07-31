package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityAnimal.class)
public interface EntityAnimalInvoker {
    @Invoker("onNearbyPlayerStartles")
    void invokeOnNearbyPlayerStartles(EntityPlayer player);
}
