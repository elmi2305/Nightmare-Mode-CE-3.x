package com.itlesports.nightmaremode.mixin.entity;

import api.achievement.AchievementEventDispatcher;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.InfiniteArrowEntity;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.entity.EntityMagicArrow;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin extends Entity implements EntityAccessor{
    @Shadow public Entity shootingEntity;

    public EntityArrowMixin(World par1World) {
        super(par1World);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityArrow;playSound(Ljava/lang/String;FF)V"))
    private void onlyPlayBowHitIfNotInfiniteArrow(EntityArrow instance, String s, float v, float b){
        if(instance instanceof InfiniteArrowEntity) return;
        instance.playSound(s,v,b);
    }

    @Inject(method = "onUpdate",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/src/EntityArrow;shootingEntity:Lnet/minecraft/src/Entity;",
                    ordinal = 5, opcode = Opcodes.GETFIELD), locals = LocalCapture.CAPTURE_FAILHARD)
    private void skeletonArrowImpactEffects(CallbackInfo ci, int var16, Vec3 var17, Vec3 var3, MovingObjectPosition mop) {
        if (this.shootingEntity instanceof EntitySkeleton skeleton && mop.entityHit instanceof EntityLivingBase hitEntity) {
            int id = skeleton.getSkeletonType().id();

            if (id == NightmareMode.SKELETON_ICE) {
                hitEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 0));
                hitEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 140, 0));

            } else if (id == NightmareMode.SKELETON_ENDER) {
                skeleton.setPositionAndUpdate(hitEntity.posX, hitEntity.posY, hitEntity.posZ);
                skeleton.playSound("mob.endermen.portal", 1.0F, 1.0F);
                skeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));

            } else if (id == NightmareMode.SKELETON_JUNGLE) {
                if (!hitEntity.isPotionActive(Potion.moveSlowdown)) {
                    if (this.rand.nextFloat() < 0.75f) {
                        hitEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120, 3));
                    }
                } else {
                    int i = this.rand.nextInt(4);
                    if (i == 0) {
                        hitEntity.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 0));
                    } else if (i == 1) {
                        hitEntity.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    }
                }

            } else if (id == NightmareMode.SKELETON_SUPERCRITICAL) {
                this.worldObj.newExplosion(skeleton, this.posX, this.posY, this.posZ, 1.2f, this.isBurning() && !this.isBeingRainedOn(), true);
            }
        }
    }


    @Inject(method = "notifyCollidingBlockOfImpact", at = @At("HEAD"))
    private void supercriticalSkeletonArrowExplosion(CallbackInfo ci){
        if(this.shootingEntity instanceof EntitySkeleton skeleton && skeleton.getSkeletonType().id() == NightmareMode.SKELETON_SUPERCRITICAL){
            this.worldObj.newExplosion(skeleton,this.posX,this.posY,this.posZ,1.2f,this.isBurning() && !this.isBeingRainedOn(),true);
        }
    }
}
