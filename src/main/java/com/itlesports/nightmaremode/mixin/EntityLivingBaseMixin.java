package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements EntityAccessor {
    @Shadow public abstract boolean isEntityAlive();

    @Shadow public abstract void addPotionEffect(PotionEffect par1PotionEffect);

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.85f))
    private float modifyWitherSkeletonSight(float constant){
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
        if(thisObj.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return constant;
        }
        if(thisObj instanceof EntitySkeleton skeleton && skeleton.getSkeletonType()==1){
            return 0.6f;
        } else{return 0.85f;}
    }

    @Redirect(method = "updatePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnParticle(Ljava/lang/String;DDDDDD)V"))
    private void removePotionParticlesConfig(World world, String s, double d, double par2, double par4, double par6, double par8, double par10){
        if(NightmareMode.potionParticles){
            world.spawnParticle(s,d,par2,par4,par6,par8,par10);
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void manageBloodMoonKills(DamageSource source, CallbackInfo ci){
        if(source.getEntity() instanceof EntityPlayer player && Objects.equals(source.damageType, "player")){
            if(NightmareUtils.isWearingFullBloodArmor(player)){
                int chance = NightmareUtils.getIsBloodMoon() ? 2 : 3;
                if(this.rand.nextInt(chance) == 0){
                    PotionEffect activeStrengthPotion = player.getActivePotionEffect(Potion.damageBoost);
                    if(activeStrengthPotion != null && activeStrengthPotion.getAmplifier() == 0){

                        if(rand.nextInt(5) == 0 && activeStrengthPotion.getDuration() <= 100){
                            this.addPotion(player,Potion.damageBoost.id, 1);
                        }

                        this.addPotion(player,Potion.regeneration.id, 0);
                    } else {
                        this.addPotion(player, Potion.damageBoost.id, 0);
                    }
                }
                player.heal(rand.nextInt(2)+1);
            }
            if(NightmareUtils.isHoldingBloodSword(player)){
                player.getHeldItem().setItemDamage(Math.max(player.getHeldItem().getItemDamage() - this.rand.nextInt(4) - 1, 0));
                this.mendTools(player);
                if (this.rand.nextInt(24) == 0) {
                    this.dropItem(NMItems.bloodOrb.itemID,1);
                }
            }
        }
    }


    @Unique private void addPotion(EntityLivingBase entity, int potionID, int amp){
        entity.addPotionEffect(new PotionEffect(potionID, 100, amp));
    }
    @Unique private void mendTools(EntityPlayer player){
        for(ItemStack stack : player.inventory.mainInventory){
            if (stack == null) continue;
            if (stack.getMaxStackSize() != 1) continue;
            if(NightmareUtils.bloodTools.contains(stack.itemID)){
                if(rand.nextInt(4) == 0) continue;
                this.increaseDurabilityIfPossible(stack);
            }
        }
    }

    @Unique private void increaseDurabilityIfPossible(ItemStack stack){
        int i = rand.nextInt(4) + 1;
        stack.setItemDamage(Math.max(stack.getItemDamage() - i,0));
    }
}