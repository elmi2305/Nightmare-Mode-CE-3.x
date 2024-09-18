package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.ComponentScatteredFeatureDesertPyramid;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
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
        ComponentScatteredFeatureDesertPyramid thisObj = (ComponentScatteredFeatureDesertPyramid)(Object)this;
        // par4 default is 10. increasing par4 moves forward in +z
        // par6 default is 10. increasing par6 moves forward in +x
        // thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 10, boundingBox);

        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 10, boundingBox);
        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 11, boundingBox);
        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 9, boundingBox);

        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 9, boundingBox);
        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 11, boundingBox);

        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 9, boundingBox);
        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 10, boundingBox);
        thisObj.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 11, boundingBox);
    }
}
