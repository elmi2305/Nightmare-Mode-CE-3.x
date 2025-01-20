package com.itlesports.nightmaremode.mixin.component;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentStrongholdCorridor;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentStrongholdCorridor.class)
public class ComponentStrongholdCorridorMixin {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdCorridor;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"))
    private void manageBlackstoneBricks(ComponentStrongholdCorridor corridor, World world, int blockId, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        if (blockId == Block.stoneBrick.blockID) {
            corridor.placeBlockAtCurrentPosition(world, Block.stoneBrick.blockID, 8, localX, localY, localZ, boundingBox);
        } else{
            corridor.placeBlockAtCurrentPosition(world, blockId, metadata, localX, localY, localZ, boundingBox);
        }
    }
}
