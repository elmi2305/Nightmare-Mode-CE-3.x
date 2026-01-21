package com.itlesports.nightmaremode.mixin;

import api.item.tag.Tag;
import btw.item.BTWItems;
import btw.item.Filtering;
import net.minecraft.src.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Filtering.class)
public class FilteringMixin {
    @Mutable
    @Shadow @Final public static Tag filteredAsPowder;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void fixHopperBug(CallbackInfo ci){
        filteredAsPowder.add(BTWItems.enderSlag, BTWItems.chickenFeed, BTWItems.mashedMelon, BTWItems.wheat).add(Block.gravel);
    }
}
