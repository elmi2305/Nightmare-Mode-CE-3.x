package com.itlesports.nightmaremode.mixin;

import api.entity.mob.KickingAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KickingAnimal.class)
public interface KickingAnimalAccessor {
    @Invoker("launchKickAttack")
    void invokeLaunchKickAttack();
}
