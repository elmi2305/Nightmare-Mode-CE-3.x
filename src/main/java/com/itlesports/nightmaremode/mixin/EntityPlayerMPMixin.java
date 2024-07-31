package com.itlesports.nightmaremode.mixin;

import btw.entity.LightningBoltEntity;
import btw.item.BTWItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityPlayerMP.class)

public abstract class EntityPlayerMPMixin extends EntityPlayer {
    @Unique int steelModifier;
    public EntityPlayerMPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }
    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter+=5; // gloom goes up 6x faster
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addStat(Lnet/minecraft/src/StatBase;I)V", shift = At.Shift.AFTER))
    private void smitePlayer(DamageSource par1DamageSource, CallbackInfo ci){
        Entity lightningbolt = new LightningBoltEntity(this.getEntityWorld(), this.posX, this.posY-0.5, this.posZ);
        getEntityWorld().addWeatherEffect(lightningbolt);

        // SUMMONS EXPLOSION. explosion does tile and entity damage. effectively kills all dropped items.
        double par2 = this.posX;
        double par4 = this.posY;
        double par6 = this.posZ;
        float par8 = 3.0f;
        this.worldObj.createExplosion(null, par2, par4, par6, par8, true); // THIS IS PURELY COSMETIC. can't implement world saving properly
    }

        // makes lightning give a few other effects with higher amplifier
    @Inject(method = "onStruckByLightning",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void givePlayerSlowness(LightningBoltEntity boltEntity, CallbackInfo ci){
        if(Objects.equals(this.getCurrentItemOrArmor(1), new ItemStack(BTWItems.plateBoots))){
            steelModifier += 1;
        }
        if(Objects.equals(this.getCurrentItemOrArmor(2), new ItemStack(BTWItems.plateLeggings))) {
            steelModifier += 3;
        }
        if(Objects.equals(this.getCurrentItemOrArmor(3), new ItemStack(BTWItems.plateBreastplate))) {
            steelModifier += 5;
        }
        if(Objects.equals(this.getCurrentItemOrArmor(4), new ItemStack(BTWItems.plateHelmet))) {
            steelModifier += 1;
        }


        this.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),120 - steelModifier * 11,10 - steelModifier,true));
        this.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(),800 - steelModifier * 50,3,true));
        this.addPotionEffect(new PotionEffect(Potion.confusion.getId(),300 - steelModifier * 29,0,true));
        this.addPotionEffect(new PotionEffect(Potion.blindness.getId(),300 - steelModifier * 29,0,true));
        this.addPotionEffect(new PotionEffect(Potion.weakness.getId(),800 - steelModifier * 79,1,true));
    }
}

