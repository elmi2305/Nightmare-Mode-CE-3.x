package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.JungleSpiderEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JungleSpiderEntity.class)
public class JungleSpiderEntityMixin extends EntitySpider{
    public JungleSpiderEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "attackEntityAsMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",ordinal = 0))
    private void addAdditionalEffects(Entity targetEntity, CallbackInfoReturnable<Boolean> cir){
        if(targetEntity instanceof EntityPlayer targetPlayer && targetPlayer.worldObj.getDifficulty() == Difficulties.HOSTILE){
            if(NightmareUtils.getWorldProgress(targetPlayer.worldObj) >=1){
                targetPlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,100,1));
                targetPlayer.addPotionEffect(new PotionEffect(Potion.poison.id,100,1));
            }
            targetPlayer.addPotionEffect(new PotionEffect(Potion.blindness.id,60,0));
        }
    }
    @Inject(method = "dropsSpiderEyes", at = @At("RETURN"), cancellable = true)
    private void dropSpiderEyes(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void scaleHealth(CallbackInfo ci){
        if (this.worldObj != null) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double)11.0F + NightmareUtils.getWorldProgress(this.worldObj) * 4);
        }
    }
}
