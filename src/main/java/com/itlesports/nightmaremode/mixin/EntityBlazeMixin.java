package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBlaze.class)
public class EntityBlazeMixin extends EntityMob{
    @Unique private boolean invisible;
    @Unique private int dashTimer = 0;

    public EntityBlazeMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        EntityBlaze thisObj = (EntityBlaze)(Object)this;
        if(thisObj.worldObj != null) {
            int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);
            thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16 + progress * (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 8 : 4));
            // 16 -> 24 -> 32 -> 40
            thisObj.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30);

            if (thisObj.rand.nextFloat() < 0.5 && progress > (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 1)) {
                thisObj.addPotionEffect(new PotionEffect(Potion.invisibility.id, 1000000, 0));
                invisible = true;
            }
        }
    }

    @ModifyConstant(method = "attackEntity", constant = @Constant(floatValue = 30.0f))
    private float invisibleBlazePassivity(float constant){
        return invisible ? 0f : 30f;
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityBlaze;attackEntityAsMob(Lnet/minecraft/src/Entity;)Z"))
    private void manageInvisibleBlazeAttack(Entity par1Entity, float par2, CallbackInfo ci){
        EntityBlaze thisObj = (EntityBlaze)(Object)this;
        if(invisible) {
            thisObj.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4);
            EntityCreeper bomb = new EntityCreeper(thisObj.worldObj);
            bomb.copyLocationAndAnglesFrom(thisObj);
            thisObj.worldObj.spawnEntityInWorld(bomb);
            thisObj.setDead();
        }
    }
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageBlazeDash(CallbackInfo ci){
        EntityBlaze thisObj = (EntityBlaze)(Object)this;
        if(thisObj.entityToAttack instanceof EntityPlayer target){
            dashTimer++;
            if (dashTimer>200){
                double var1 = target.posX - thisObj.posX;
                double var3 = target.posX - thisObj.posX;
                double var2 = target.posZ - thisObj.posZ;
                Vec3 vector = Vec3.createVectorHelper(var1, var3, var2);
                vector.normalize();
                thisObj.motionX = vector.xCoord * 0.11;
                thisObj.motionY = vector.yCoord * 0.13;
                thisObj.motionZ = vector.zCoord * 0.11;
                dashTimer = 0;
            } else if(dashTimer > 180){
                thisObj.motionX = 0;
                thisObj.motionY = 0;
                thisObj.motionZ = 0;
            }
        }
    }
}
