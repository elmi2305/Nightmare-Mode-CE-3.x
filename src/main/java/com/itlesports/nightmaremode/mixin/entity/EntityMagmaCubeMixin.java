package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.util.elements.NMEvents;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMagmaCube.class)
public class EntityMagmaCubeMixin extends EntitySlime {
    @Unique private float streakModifier = 1;
    @Unique private float splitCounter = 0;

    public EntityMagmaCubeMixin(World par1World) {
        super(par1World);
    }


    @Inject(method = "getCanSpawnHere", at = @At("RETURN"), cancellable = true)
    private void spawnInOverworld(CallbackInfoReturnable<Boolean> cir)
    {
        if(this.dimension == 0 && !NMEvents.SimpleEvent.HELL.isActive()){
            cir.setReturnValue(false);
        }
    }

    @Unique private boolean isValidForEventLoot = false;

    @Override
    public boolean attackEntityFrom(DamageSource src, float par2) {
        this.isValidForEventLoot = src.getEntity() instanceof EntityPlayer;
        return super.attackEntityFrom(src, par2);
    }
    @Inject(method = "dropFewItems", at = @At("HEAD"),cancellable = true)
    private void doNotDropCreamOnHellSawKill(boolean par1, int par2, CallbackInfo ci){
        if(NMEvents.SimpleEvent.HELL.isActive() && !this.isValidForEventLoot){
            ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void chanceToSpawnSlimeOnJump(CallbackInfo ci){
        if (this.getSlimeSize() >= 2 && this.splitCounter < 5){
            int hellModifier = NMEvents.SimpleEvent.HELL.isActive() ? 2 : 1;

            if(this.rand.nextFloat() < 0.3d / this.streakModifier * hellModifier){
                EntityMagmaCube baby = new EntityMagmaCube(this.worldObj);

                baby.getDataWatcher().updateObject(16, (byte)(this.getSlimeSize() / 2));
                baby.setHealth((int) (baby.getSlimeSize() * NMUtils.getNiteMultiplier()));
                baby.setPositionAndUpdate(this.posX,this.posY,this.posZ);

                this.worldObj.spawnEntityInWorld(baby);
                this.streakModifier += 2 + (float) this.getSlimeSize();
                this.splitCounter += 1;
            }
        }
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,4);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseSlimeSize(World par1World, CallbackInfo ci){
        if(NMUtils.getIsMobEclipsed(this)){
            this.setSlimeSize(this.getSlimeSize() + this.rand.nextInt(5));
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((double)0.5F + (NMUtils.getNiteMultiplier() - 1) * 0.01);
    }
    @ModifyArg(method = "dropFewItems", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int increaseMagmaCreamRates(int bound){
        return bound + 1;
    }
}
