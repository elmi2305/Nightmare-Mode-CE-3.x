package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.AITasks.EntityAIChasePlayer;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySheep.class)
public abstract class EntitySheepMixin extends EntityAnimal {
    @Shadow public abstract int getFleeceColor();

    public EntitySheepMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,4);
        this.targetTasks.addTask(12, new EntityAIChasePlayer(this, 1.35f));
    }

    @Inject(method = "dropFewItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySheep;entityDropItem(Lnet/minecraft/src/ItemStack;F)Lnet/minecraft/src/EntityItem;"))
    private void chanceToDropMoreWool(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if(bKilledByPlayer){
            if(this.rand.nextInt(4) == 0){
                this.entityDropItem(new ItemStack(BTWItems.wool.itemID, 1, BlockColored.getDyeFromBlock(this.getFleeceColor())), 0.0F);
                if(this.rand.nextInt(4) == 0){
                    this.entityDropItem(new ItemStack(BTWItems.wool.itemID, 1, BlockColored.getDyeFromBlock(this.getFleeceColor())), 0.0F);
                }
            }
        }
    }
    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageJumpAttackAtPlayer(CallbackInfo ci){
        if (NMUtils.getIsMobEclipsed(this)) {
            if(this.getAttackTarget() instanceof EntityPlayer player){
                double dist = Math.sqrt(this.getDistanceSqToEntity(player));
                if(dist < 1.3){
                    player.attackEntityFrom(DamageSource.causeMobDamage(this), 6f);
                    player.flingAwayFromEntity(this,1f);
                }
                else if(dist < 7){
                    this.rotationPitch = 90f;
                }
            }

        }
        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 8 + NMUtils.getWorldProgress() * 5;
        double eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 2.5 : 1;
        if(this.getMaxHealth() != originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier);
        }
    }

    @ModifyConstant(method = "updateHungerState", constant = @Constant(intValue = 24000))
    private int eclipseSheepFurRegen(int constant){
        if(NMUtils.getIsMobEclipsed(this)){
            return 20;
        }
        return constant;
    }
    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((8d + NMUtils.getWorldProgress() * 4) * NMUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23F * (1 + (NMUtils.getNiteMultiplier() - 1) / 20));
    }
}
