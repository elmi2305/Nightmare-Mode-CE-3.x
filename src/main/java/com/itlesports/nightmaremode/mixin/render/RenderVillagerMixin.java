package com.itlesports.nightmaremode.mixin.render;

import net.minecraft.src.EntityVillager;
import net.minecraft.src.RenderVillager;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderVillager.class)
public class RenderVillagerMixin {
    @Unique private static final ResourceLocation NIGHTMARE_VILLAGER = new ResourceLocation("textures/entity/nmVillager1.png");

    @Inject(method = "func_110902_a", at = @At("HEAD"),cancellable = true)
    private void renderCustomNightmareVillager(EntityVillager par1EntityVillager, CallbackInfoReturnable<ResourceLocation> cir){
        if(par1EntityVillager.getProfession() == 5){
            cir.setReturnValue(NIGHTMARE_VILLAGER);
        }
    }
}
