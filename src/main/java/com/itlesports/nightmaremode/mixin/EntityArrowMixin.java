package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.EntityMagicArrow;
import com.itlesports.nightmaremode.item.NMItems;
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
    private void skeletonArrowImpactEffects(CallbackInfo ci, int var16, Vec3 var17, Vec3 var3, MovingObjectPosition var4){
        if (this.shootingEntity instanceof EntitySkeleton skeleton && var4.entityHit instanceof EntityLivingBase hitEntity) {
            if(skeleton.getSkeletonType() == NightmareMode.SKELETON_ICE){
                hitEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100,0));
                hitEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 140,0));
            } else if(skeleton.getSkeletonType() == NightmareMode.SKELETON_ENDER){
                skeleton.setPositionAndUpdate(hitEntity.posX,hitEntity.posY,hitEntity.posZ);
                skeleton.playSound("mob.endermen.portal",1.0F,1.0F);
                skeleton.setCurrentItemOrArmor(0,new ItemStack(Item.swordIron));
            } else if(skeleton.getSkeletonType() == NightmareMode.SKELETON_JUNGLE){
                if (!hitEntity.isPotionActive(Potion.moveSlowdown)) {
                    // 75% chance to apply Slowness if the target doesn't already have it
                    if (this.rand.nextFloat() < 0.75f) {
                        hitEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120, 3));
                    }
                } else {
                    // If the target already has Slowness, roll for Poison or Blindness
                    int i = this.rand.nextInt(4);
                    if (i == 0) {
                        hitEntity.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 0));
                    } else if (i == 1) {
                        hitEntity.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    }
                }
            } else if(skeleton.getSkeletonType() == NightmareMode.SKELETON_SUPERCRITICAL){
                this.worldObj.newExplosion(skeleton,this.posX,this.posY,this.posZ,1.2f,this.isBurning() && !this.isBeingRainedOn(),true);
            }
        }
        EntityArrow thisObj = ((EntityArrow)(Object)this);
        if(thisObj instanceof EntityMagicArrow){
            if(this.shootingEntity instanceof EntityPlayer player && var4.entityHit != null && this.rand.nextBoolean()){
                if (player.getHeldItem() != null && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, player.getHeldItem()) != 0) {
                    player.inventory.addItemStackToInventory(new ItemStack(NMItems.magicArrow));
                }
            }
        }
    }

    @Inject(method = "notifyCollidingBlockOfImpact", at = @At("HEAD"))
    private void supercriticalSkeletonArrowExplosion(CallbackInfo ci){
        if(this.shootingEntity instanceof EntitySkeleton skeleton && skeleton.getSkeletonType() == NightmareMode.SKELETON_SUPERCRITICAL){
            this.worldObj.newExplosion(skeleton,this.posX,this.posY,this.posZ,1.2f,false,true);
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
