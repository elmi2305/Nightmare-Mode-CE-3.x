package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.util.NMDifficultyParam;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ComponentScatteredFeatureDesertPyramid.class)
public class ComponentScatteredFeatureDesertPyramidMixin {
    @Inject(method = "addComponentParts",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/ComponentScatteredFeatureDesertPyramid;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V",
                    ordinal = 95))
    private void spawnAdditionalPressurePlates(World world, Random generatorRand, StructureBoundingBox boundingBox, CallbackInfoReturnable<Boolean> cir){
        ComponentScatteredFeatureDesertPyramid pyramid = (ComponentScatteredFeatureDesertPyramid)(Object)this;
        // par4 default is 10. increasing par4 moves forward in +z
        // par6 default is 10. increasing par6 moves forward in +x
        // pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 10, boundingBox);
        if (world.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 10, boundingBox);
            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 11, boundingBox);
            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 9, boundingBox);

            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 9, boundingBox);
            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 11, boundingBox);

            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 9, boundingBox);
            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 10, boundingBox);
            pyramid.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 11, boundingBox);
        }
    }

//    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentScatteredFeatureDesertPyramid;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"), index = 1)
//    private int changeBlockPaletteOnStandardPlace(int blockID){
//        if(blockID == Block.sandStone.blockID){
//            return NMBlocks.darkSandstone.blockID;
//        }
//        return blockID;
//    }
//
//    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentScatteredFeatureDesertPyramid;fillWithBlocks(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;IIIIIIIIZ)V"), index = 7)
//    private int changeBlockPaletteOnFillStart(int blockID){
//        if(blockID == Block.sandStone.blockID){
//            return NMBlocks.darkSandstone.blockID;
//        }
//        return blockID;
//    }
//    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentScatteredFeatureDesertPyramid;fillWithBlocks(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;IIIIIIIIZ)V"), index = 8)
//    private int changeBlockPaletteOnFillEnd(int blockID){
//        if(blockID == Block.sandStone.blockID){
//            return NMBlocks.darkSandstone.blockID;
//        }
//        return blockID;
//    }
}
