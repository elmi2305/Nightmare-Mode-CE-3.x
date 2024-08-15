package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityAICreeperSwell;
import net.minecraft.src.EntitySenses;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAICreeperSwell.class)
public class EntityAICreeperSwellMixin {
    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z"))
    private boolean canAlwaysSee(EntitySenses instance, Entity entity){
        return true;
    }
}
