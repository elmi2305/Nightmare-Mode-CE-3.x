package com.itlesports.nightmaremode.mixin;

import btw.entity.LightningBoltEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*
* modifies onDeath() to spawn a lightning bolt and a (non-functional) explosion. nothing actually happens to the player
* items or entities around the player. the blocks also do not get broken
*/
@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends Entity{
    @Shadow public abstract World getEntityWorld();
    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }
    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;addStat(Lnet/minecraft/src/StatBase;I)V", shift = At.Shift.AFTER))
    private void smitePlayer(DamageSource par1DamageSource, CallbackInfo ci){
        Entity lightningbolt = new LightningBoltEntity(this.getEntityWorld(), this.posX, this.posY-0.5, this.posZ);
        getEntityWorld().addWeatherEffect(lightningbolt);
        
        // SUMMONS EXPLOSION. currently only visual. IG it's meant to scare the player? doesnt actually get saved to world data
        double par2 = this.posX;
        double par4 = this.posY;
        double par6 = this.posZ;
        float par8 = 3.0f;
        this.worldObj.createExplosion(null, par2, par4, par6, par8, true); // THIS IS PURELY COSMETIC. can't implement world saving properly
    }
}
