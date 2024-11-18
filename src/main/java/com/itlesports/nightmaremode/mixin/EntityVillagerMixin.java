package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements IMerchant, INpc {

    @Shadow protected MerchantRecipeList buyingList;

    public EntityVillagerMixin(World par1World) {
        super(par1World);
    }
                                                            // resets villager trades every 2 days
    @Inject(method = "updateAITick", at = @At("HEAD"))
    private void resetVillagerTrades(CallbackInfo ci){
        if (this.worldObj.getTotalWorldTime()%48000==0) {
            this.buyingList = null;
        }
    }
}
