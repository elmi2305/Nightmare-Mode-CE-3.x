package com.itlesports.nightmaremode.mixin.component;

import btw.block.BTWBlocks;
import net.minecraft.src.ComponentVillageHouse2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentVillageHouse2.class)
public class ComponentVillageHouse2Mixin {
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/ComponentVillageHouse2;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"), index = 1)
    private int replaceLooseBrickSlab(int blockId) {
        return blockId == BTWBlocks.looseBrickSlab.blockID ? BTWBlocks.placedBrick.blockID : blockId;
    }
}
