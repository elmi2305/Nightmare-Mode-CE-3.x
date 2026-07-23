package com.itlesports.nightmaremode.mixin.render;

import net.minecraft.src.EntityVillager;
import net.minecraft.src.RenderVillager;
import net.minecraft.src.ResourceLocation;
import com.itlesports.nightmaremode.util.interfaces.VillagerHunger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderVillager.class)
public class RenderVillagerMixin {
    @Unique private static final ResourceLocation NIGHTMARE_VILLAGER = new ResourceLocation("nightmare:textures/entity/nmVillager.png");
    @Unique private static final ResourceLocation HUNGRY_FARMER = new ResourceLocation("nightmare:textures/entity/villager_hungry_farmer.png");
    @Unique private static final ResourceLocation HUNGRY_LIBRARIAN = new ResourceLocation("nightmare:textures/entity/villager_hungry_librarian.png");
    @Unique private static final ResourceLocation HUNGRY_PRIEST = new ResourceLocation("nightmare:textures/entity/villager_hungry_priest.png");
    @Unique private static final ResourceLocation HUNGRY_SMITH = new ResourceLocation("nightmare:textures/entity/villager_hungry_smith.png");
    @Unique private static final ResourceLocation HUNGRY_BUTCHER = new ResourceLocation("nightmare:textures/entity/villager_hungry_butcher.png");
    @Unique private static final ResourceLocation HUNGRY_NIGHTMARE = new ResourceLocation("nightmare:textures/entity/nmVillagerHungry.png");

    @Inject(method = "func_110902_a", at = @At("HEAD"),cancellable = true)
    private void renderCustomNightmareVillager(EntityVillager par1EntityVillager, CallbackInfoReturnable<ResourceLocation> cir){
        if (((VillagerHunger) par1EntityVillager).nightmareMode$isHungry()) {
            switch (par1EntityVillager.getProfession()) {
                case 0: cir.setReturnValue(HUNGRY_FARMER); return;
                case 1: cir.setReturnValue(HUNGRY_LIBRARIAN); return;
                case 2: cir.setReturnValue(HUNGRY_PRIEST); return;
                case 3: cir.setReturnValue(HUNGRY_SMITH); return;
                case 4: cir.setReturnValue(HUNGRY_BUTCHER); return;
                case 5: cir.setReturnValue(HUNGRY_NIGHTMARE); return;
                default: break;
            }
        }
        if(par1EntityVillager.getProfession() == 5){
            cir.setReturnValue(NIGHTMARE_VILLAGER);
        }
    }
}
