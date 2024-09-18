package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin {
    @Shadow public Entity shootingEntity;

    @Inject(method = "onUpdate",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/src/EntityArrow;shootingEntity:Lnet/minecraft/src/Entity;",
                    ordinal = 5,
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void applySlownessIfIceSkeleton(CallbackInfo ci, int var16, Vec3 var17, Vec3 var3, MovingObjectPosition var4){
        if (this.shootingEntity instanceof EntitySkeleton mySkeleton) {
            if(var4.entityHit instanceof EntityPlayer && mySkeleton.getSkeletonType()==2){
                ((EntityPlayer) var4.entityHit).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100,0));
                ((EntityPlayer) var4.entityHit).addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 140,0));
            } else if(var4.entityHit instanceof EntityPlayer && mySkeleton.getSkeletonType()==4){
                mySkeleton.setPositionAndUpdate(var4.entityHit.posX,var4.entityHit.posY,var4.entityHit.posZ);
                mySkeleton.playSound("mob.endermen.portal",20.0F,1.0F);
                mySkeleton.setCurrentItemOrArmor(0,new ItemStack(Item.swordIron));
            }
        }
    }
}
