package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.NightmareVillager;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements IMerchant, INpc {

    @Shadow protected MerchantRecipeList buyingList;
    @Shadow public static Map<Integer, Class> professionMap;
    public EntityVillagerMixin(World par1World) {
        super(par1World);
    }


    @Inject(method = "updateAITick", at = @At("HEAD"))
    private void resetVillagerTrades(CallbackInfo ci){
        if (this.worldObj.getTotalWorldTime() % 48000==0) {
            this.buyingList = null;
        }
        if(this.ticksExisted % 20 != 0) return;
        if(NightmareUtils.getIsBloodMoon()){
            this.heal(20f);
        }
    }

    @Inject(method = "<clinit>", at = @At("TAIL"),remap = false)
    private static void addNightmareVillagerProfession(CallbackInfo ci){
        professionMap.put(5, NightmareVillager.class);
    }
}
