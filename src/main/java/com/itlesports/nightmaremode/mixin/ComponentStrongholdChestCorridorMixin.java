package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.ComponentStrongholdChestCorridor;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentStrongholdChestCorridor.class)
public class ComponentStrongholdChestCorridorMixin {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdChestCorridor;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"))
    private void manageBlackstoneBricks(ComponentStrongholdChestCorridor corridor, World world, int blockId, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        corridor.placeBlockAtCurrentPosition(world,1125,2,localX,localY,localZ,boundingBox);
        // slabs
    }
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdChestCorridor;fillWithBlocks(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;IIIIIIIIZ)V"),index = 8)
    private int blackstoneBrickBlocksBelowChest(int par3){
        return 1155; // blackstone
    }
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdChestCorridor;fillWithBlocks(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;IIIIIIIIZ)V"),index = 9)
    private int blackstoneBrickBlocksBelowChest2(int par3){
        return 1155; // blackstone
    }
}
