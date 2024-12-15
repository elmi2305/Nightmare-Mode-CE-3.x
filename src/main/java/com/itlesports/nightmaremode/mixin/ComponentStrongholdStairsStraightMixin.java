package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentStrongholdStairsStraight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentStrongholdStairsStraight.class)
public class ComponentStrongholdStairsStraightMixin {
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdStairsStraight;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"),index = 1)
    private int addBlackstoneStairs(int blockID){
        if(blockID == Block.stairsCobblestone.blockID){
            return 1134; // blackstone brick stairs
        } else if(blockID == Block.stoneBrick.blockID){
            return 1155; // cracked blackstone brick
        }
        return blockID;
    }
}
