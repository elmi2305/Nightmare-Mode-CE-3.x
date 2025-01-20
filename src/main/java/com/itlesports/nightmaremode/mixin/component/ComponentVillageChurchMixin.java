package com.itlesports.nightmaremode.mixin.component;

import net.minecraft.src.ComponentVillageChurch;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentVillageChurch.class)
public class ComponentVillageChurchMixin {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentVillageChurch;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V",ordinal = 48))
    private void noChurchLadders(ComponentVillageChurch instance, World world, int i, int j, int k, int l, int m, StructureBoundingBox structureBoundingBox){
    }
}
