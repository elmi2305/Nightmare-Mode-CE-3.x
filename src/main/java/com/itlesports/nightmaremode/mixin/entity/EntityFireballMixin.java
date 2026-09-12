package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.entity.variants.EntitySiegeFireball;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFireball;
import net.minecraft.src.EntityLargeFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFireball.class)
public class EntityFireballMixin {
    @ModifyConstant(method = "onUpdate", constant = @Constant(floatValue = 0.2f))
    private float accelerationModifier(float constant){
        EntityFireball thisObj = (EntityFireball)(Object)this;
        if(thisObj instanceof EntityLargeFireball){
            return 0.5f;
        }
        return 0.3f;
    }

    // it's vague what this method does. it's used to implement ghast rage mode. without it the fireballs would collide with one another
    // potentially hitting the ghast, or making most of the fireballs miss.
    @ModifyConstant(method = "onUpdate", constant = @Constant(intValue = 25))
    private int largeFireballIgnoresItself(int constant){
        EntityFireball thisObj = (EntityFireball)(Object)this;
        return thisObj instanceof EntityLargeFireball ? 300 : 25;
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;canBeCollidedWith()Z"))
    private boolean siegeFireballsIgnoreEachOther(Entity collisionCandidate) {
        EntityFireball self = (EntityFireball)(Object)this;
        return !(self instanceof EntitySiegeFireball && collisionCandidate instanceof EntitySiegeFireball)
                && collisionCandidate.canBeCollidedWith();
    }
}
