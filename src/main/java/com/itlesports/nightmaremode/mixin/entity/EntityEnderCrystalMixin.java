package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderCrystal.class)
public abstract class EntityEnderCrystalMixin extends Entity{
    public EntityEnderCrystalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;setDead()V"))
    private void avengeDestroyer(DamageSource src, float par2, CallbackInfoReturnable<Boolean> cir) {
        Entity destroyer = src.getSourceOfDamage();

        if (destroyer instanceof EntityArrow) {
            destroyer = ((EntityArrow) destroyer).shootingEntity;
        } else if (destroyer instanceof EntityThrowable) {
            destroyer = ((EntityThrowable) destroyer).getThrower();
        }

        if (destroyer instanceof EntityPlayer && this.dimension != 0 && !NightmareMode.noHit && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            Entity lightningbolt = new EntityLightningBolt(this.worldObj, destroyer.posX, destroyer.posY-0.5, destroyer.posZ);
            this.worldObj.addWeatherEffect(lightningbolt);
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        if (!this.worldObj.isRemote) {
            int range = this.isRiding() ? 1 : 4;
            if(this.getDistanceSqToEntity(par1EntityPlayer) >= range) return;
            this.worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0f, true);
            this.setDead();
        }
        super.onCollideWithPlayer(par1EntityPlayer);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlock(IIII)Z"))
    private boolean spawnFireOnlyInEnd(World instance, int par1, int par2, int par3, int par4){
        if(this.dimension != 0){
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY);
            int var3 = MathHelper.floor_double(this.posZ);
            this.worldObj.setBlock(var1, var2, var3, Block.fire.blockID);
        }
        return false;
    }

    @Inject(method = "<init>(Lnet/minecraft/src/World;)V", at = @At("TAIL"))
    private void updateEndCrystalSize(World par1World, CallbackInfo ci){
        this.setSize(1.8f,3.2f);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageDespawnIfDeloaded(CallbackInfo ci){
        if(this.dimension != 1 && this.ridingEntity == null && this.ticksExisted >= 3000){this.setDead();}
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void removeChainExplosions(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(par1DamageSource.isExplosion()){
            cir.setReturnValue(false);
        }
    }
}