package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentStronghold;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentStronghold.class)
public class ComponentStrongholdMixin {
    @ModifyArg(method = "placeDoor", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStronghold;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"),index = 1)
    private int StoneBricksOnDoorways(int blockID){
        if(blockID == Block.stoneBrick.blockID){
            return 1155; // infested cracked
        }
        return blockID;
    }
}
