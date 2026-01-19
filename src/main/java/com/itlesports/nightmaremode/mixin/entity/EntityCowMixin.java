package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import api.world.difficulty.DifficultyParam;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityCow.class)
public abstract class EntityCowMixin extends KickingAnimal {
    public EntityCowMixin(World par1World) {
        super(par1World);
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((15d + NMUtils.getWorldProgress() * 5) * NMUtils.getNiteMultiplier());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,4);
    }
    @Inject(method = "interact", at = @At("HEAD"),cancellable = true)
    private void manageGatheringBloodMilk(EntityPlayer player, CallbackInfoReturnable<Boolean> cir){
        EntityCow thisObj = (EntityCow)(Object)this;
        if(NMUtils.getIsMobEclipsed(thisObj)){
            ItemStack stack = player.inventory.getCurrentItem();
            if (stack != null && stack.itemID == Item.bucketEmpty.itemID) {
                if (thisObj.gotMilk()) {
                    --stack.stackSize;
                    if (stack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(NMItems.bloodMilk));
                    } else if (!player.inventory.addItemStackToInventory(new ItemStack(NMItems.bloodMilk))) {
                        player.dropPlayerItem(new ItemStack(NMItems.bloodMilk.itemID, 1, 0));
                    }

                    this.attackEntityFrom(DamageSource.generic, 0.0F);
                } else if (this.worldObj.getDifficultyParameter(DifficultyParam.ShouldIncorrectMilkingStartleCows.class)) {
                    this.attackEntityFrom(DamageSource.causePlayerDamage(player), 0.0F);
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(this.entityAnimalInteract(player));
            }
        }
    }

    @Inject(method = "dropFewItems", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityCow;rand:Ljava/util/Random;", ordinal = 0))
    private void makeCowsDropLeather(boolean killedByPlayer, int lootingModifier, CallbackInfo ci){
        int numDrops = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier) + 1;
        for (int i = 0; i < numDrops; ++i) {
            this.dropItem(Item.leather.itemID, 1);
        }
    }

    @Inject(method = "updateHungerState", at = @At("HEAD"))
    private void updateHealthState(CallbackInfo ci){
        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 15 + NMUtils.getWorldProgress() * 5;
        double eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 2.5 : 1;
        if(this.getMaxHealth() != originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier);
        }
    }

    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
    }
    @ModifyConstant(method = "updateHungerState", constant = @Constant(intValue = 24000))
    private int getMilkFaster(int constant){
        return 4000 + this.rand.nextInt(2000);
    }
}
