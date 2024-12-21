package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentStrongholdStairs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentStrongholdStairs.class)
public class ComponentStrongholdStairsMixin {
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdStairs;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"),index = 1)
    private int addBlackstoneStairs(int blockID){
        if(blockID == Block.stoneBrick.blockID){
            return 1155; // infested cracked brick
        } else if(blockID == Block.stoneSingleSlab.blockID){
            return 1125; // blackstone slab
        }
        return blockID;
    }

    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdStairs;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"),index = 2)
    private int addBlackstoneStairMetadata(int metadata){
        return 2;
    }
}

