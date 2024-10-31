package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityMagmaCube;
import net.minecraft.src.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMagmaCube.class)
public class EntityMagmaCubeMixin {
    @Unique private float streakModifier = 1;
    @Unique private float splitCounter = 0;
    @Inject(method = "jump", at = @At("TAIL"))
    private void chanceToSpawnSlimeOnJump(CallbackInfo ci){
        EntityMagmaCube thisObj = (EntityMagmaCube)(Object)this;
        if (thisObj.getSlimeSize() >= 2 && this.splitCounter < 5){
            if(thisObj.rand.nextFloat()<0.2 / this.streakModifier){
                EntityMagmaCube baby = new EntityMagmaCube(thisObj.worldObj);
                int size = thisObj.getSlimeSize();
                baby.getDataWatcher().updateObject(16, (byte)(size/2));
                baby.setPositionAndUpdate(thisObj.posX,thisObj.posY,thisObj.posZ);
                thisObj.worldObj.spawnEntityInWorld(baby);
                this.streakModifier += 2 + (float) thisObj.getSlimeSize();
                this.splitCounter += 1;
            }
        }
    }
}
