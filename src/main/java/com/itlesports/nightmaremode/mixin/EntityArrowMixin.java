package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.EntityMagicArrow;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin extends Entity implements EntityAccessor{
    @Shadow public Entity shootingEntity;

    public EntityArrowMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onUpdate",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/src/EntityArrow;shootingEntity:Lnet/minecraft/src/Entity;",
                    ordinal = 5), locals = LocalCapture.CAPTURE_FAILHARD)
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
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityArrow;setDead()V",ordinal = 1))
    private void magicArrowPierce(EntityArrow instance){
        if (!(instance instanceof EntityMagicArrow)) {
            instance.setDead();
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"))
    private boolean magicArrowDamageDuringPierce(Entity instance, DamageSource par1DamageSource, float par2){
        EntityArrow thisObj = ((EntityArrow)(Object)this);
        if (thisObj instanceof EntityMagicArrow) {
            ((EntityAccessor)instance).setInvulnerable(false);
            instance.attackEntityFrom(par1DamageSource,par2);
            return true;
        } else {
            return instance.attackEntityFrom(par1DamageSource, par2);
        }
    }
}
