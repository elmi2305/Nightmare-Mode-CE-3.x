package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import net.minecraft.src.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KickingAnimal.class)
public interface KickingAnimalAccessor {
    @Invoker("launchKickAttack")
    void invokeLaunchKickAttack();
    @Invoker("kickAttackHitTarget")
    void invokeKickAttackHitTarget(Entity hitEntity);
}
