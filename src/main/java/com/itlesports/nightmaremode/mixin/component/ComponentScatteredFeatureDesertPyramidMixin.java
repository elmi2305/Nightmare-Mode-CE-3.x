package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ComponentScatteredFeatureDesertPyramid.class)
public abstract class ComponentScatteredFeatureDesertPyramidMixin extends ComponentScatteredFeature {
    @Unique private boolean journalPlaced;

    @Inject(method = "func_143012_a", at = @At("TAIL"))
    private void saveJournalPlacement(NBTTagCompound tag, CallbackInfo ci) {
        tag.setBoolean("JourneyJournalPlaced", this.journalPlaced);
    }

    @Inject(method = "func_143011_b", at = @At("TAIL"))
    private void loadJournalPlacement(NBTTagCompound tag, CallbackInfo ci) {
        this.journalPlaced = tag.getBoolean("JourneyJournalPlaced");
    }

    @Inject(method = "addComponentParts", at = @At("TAIL"))
    private void addKnowledgeBooksToTempleHampers(World world, Random random, StructureBoundingBox boundingBox, CallbackInfoReturnable<Boolean> cir) {
        for (int direction = 0; direction < 4; ++direction) {
            int x = this.getXWithOffset(10 + Direction.offsetX[direction] * 2, 10 + Direction.offsetZ[direction] * 2);
            int y = this.getYWithOffset(-11);
            int z = this.getZWithOffset(10 + Direction.offsetX[direction] * 2, 10 + Direction.offsetZ[direction] * 2);
            if (!boundingBox.isVecInside(x, y, z)) continue;
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity instanceof IInventory inventory) {
                if (!this.journalPlaced) {
                    this.journalPlaced = com.itlesports.nightmaremode.util.JourneyJournals.addToInventory(inventory, 0);
                }
                KnowledgeBookLoot.addBookIfRolled(inventory, random, NMFields.KNOWLEDGE_BOOKS_DESERT_TEMPLE, 3);
            }
        }
    }

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
            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 10, boundingBox);
            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 11, boundingBox);
            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 11, -11, 9, boundingBox);

            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 9, boundingBox);
            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 10, -11, 11, boundingBox);

            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 9, boundingBox);
            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 10, boundingBox);
            this.placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 9, -11, 11, boundingBox);
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
