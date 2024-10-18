package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAICreeperSwell;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntitySenses;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAICreeperSwell.class)
public class EntityAICreeperSwellMixin {
    @Shadow public EntityLivingBase creeperAttackTarget;

    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls(EntitySenses senses, Entity entity){
        if (this.creeperAttackTarget.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return true;
        }
        return senses.canSee(this.creeperAttackTarget);
    }
}
