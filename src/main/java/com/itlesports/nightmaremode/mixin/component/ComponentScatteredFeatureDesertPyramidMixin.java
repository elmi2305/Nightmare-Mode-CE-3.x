package com.itlesports.nightmaremode.mixin.component;

import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
        if (world.getDifficulty() == Difficulties.HOSTILE) {
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


//    @Redirect(method = "addComponentParts",
//            at = @At(value = "INVOKE",
//                    target = "Lnet/minecraft/src/ItemEnchantedBook;func_92114_b(Ljava/util/Random;)Lnet/minecraft/src/WeightedRandomChestContent;"))
//    private WeightedRandomChestContent increaseManuscriptChance(ItemEnchantedBook instance, Random par1Random){
//        return instance.func_92112_a(par1Random,1,1,8);
//    }
}
