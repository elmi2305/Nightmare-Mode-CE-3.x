package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerSP.class)

public abstract class EntityPlayerSPMixin extends EntityPlayer{
//    @Unique private boolean shouldDealGloomDamage = false;

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }
    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter+=5;
        }
    }

//    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
//    private void manageNetherGloomOnHardmodeTransition(CallbackInfo ci){
//        if(this.dimension == -1 && this.getGloomLevel() == 3 && this.inGloomCounter == 0 && !MinecraftServer.getIsServer()){
//            this.shouldDealGloomDamage = true; // initiate damage dealing thing
//        }
//        if(this.shouldDealGloomDamage){
//            if(this.worldObj.getTotalWorldTime() % 100 == 0){
//                this.damageEntity(DamageSource.magic,1f);
//            }
//        }
//    }
}

