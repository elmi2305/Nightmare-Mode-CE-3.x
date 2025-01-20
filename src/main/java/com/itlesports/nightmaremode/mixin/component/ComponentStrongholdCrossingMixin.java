package com.itlesports.nightmaremode.mixin.component;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentStrongholdCrossing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentStrongholdCrossing.class)
public class ComponentStrongholdCrossingMixin {
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdCrossing;fillWithBlocks(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;IIIIIIIIZ)V"),index = 8)
    private int replaceSlabs(int id){
        if(id == Block.stoneSingleSlab.blockID){
            return 0; // air because I can't set it to blackstone slabs
        } else if(id == Block.stoneDoubleSlab.blockID){
            return 1155; // blackstone
        }
        return id;
    }
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdCrossing;fillWithBlocks(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;IIIIIIIIZ)V"),index = 9)
    private int replaceSlabs1(int id){
        if(id == Block.stoneSingleSlab.blockID){
            return 0; // air because I can't set it to blackstone slabs
        } else if(id == Block.stoneDoubleSlab.blockID){
            return 1155; // blackstone
        }
        return id;
    }
}
