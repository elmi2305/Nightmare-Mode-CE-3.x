package com.itlesports.nightmaremode.mixin.component;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentStrongholdRoomCrossing;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentStrongholdRoomCrossing.class)
public class ComponentStrongholdRoomCrossingMixin {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdRoomCrossing;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"))
    private void manageBlackstoneBricks(ComponentStrongholdRoomCrossing corridor, World world, int blockId, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        if (blockId == Block.stoneBrick.blockID || blockId == Block.cobblestone.blockID || blockId == Block.planks.blockID) {
            corridor.placeBlockAtCurrentPosition(world, Block.stoneBrick.blockID, 8, localX, localY, localZ, boundingBox);
            // blackstone
        } else if (blockId == Block.stoneSingleSlab.blockID) {
            corridor.placeBlockAtCurrentPosition(world, 1125, 2, localX, localY, localZ, boundingBox);
            // blackstone slab
        } else{
            corridor.placeBlockAtCurrentPosition(world, blockId, metadata, localX, localY, localZ, boundingBox);
        }
    }
}
