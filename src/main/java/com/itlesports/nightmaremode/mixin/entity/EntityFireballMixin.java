package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntityFireball;
import net.minecraft.src.EntityLargeFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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
}
