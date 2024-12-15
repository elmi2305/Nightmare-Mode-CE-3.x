package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.StructurePieceBlockSelector;
import net.minecraft.src.StructureStrongholdStones;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StructureStrongholdStones.class)
public abstract class StructureStrongholdStonesMixin extends StructurePieceBlockSelector {

    @Inject(method = "selectBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/StructureStrongholdStones;selectedBlockMetaData:I",ordinal = 0, shift = At.Shift.AFTER))
    private void replaceCracked(Random rand, int par2, int par3, int par4, boolean tru, CallbackInfo ci){
        this.selectedBlockMetaData = 10; // cracked blackstone
    }
    @Inject(method = "selectBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/StructureStrongholdStones;selectedBlockMetaData:I",ordinal = 1, shift = At.Shift.AFTER))
    private void replaceMossy(Random rand, int par2, int par3, int par4, boolean tru, CallbackInfo ci){
        this.selectedBlockMetaData = 9; // mossy
    }
    @Inject(method = "selectBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/StructureStrongholdStones;selectedBlockId:I",ordinal = 1, shift = At.Shift.AFTER))
    private void replaceInfested(Random rand, int par2, int par3, int par4, boolean tru, CallbackInfo ci){
        this.selectedBlockId = 1153; // infested blackstone brick
    }
    @Inject(method = "selectBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/StructureStrongholdStones;selectedBlockId:I",ordinal = 2, shift = At.Shift.AFTER))
    private void replaceInfestedMossy(Random rand, int par2, int par3, int par4, boolean tru, CallbackInfo ci){
        this.selectedBlockId = 1154; // infested mossy blackstone brick
    }
    @Inject(method = "selectBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/StructureStrongholdStones;selectedBlockId:I",ordinal = 3, shift = At.Shift.AFTER))
    private void replaceInfestedCracked(Random rand, int par2, int par3, int par4, boolean tru, CallbackInfo ci){
        this.selectedBlockId = 1155; // infested cracked blackstone brick
    }
    @Inject(method = "selectBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/StructureStrongholdStones;selectedBlockMetaData:I",ordinal = 5, shift = At.Shift.AFTER))
    private void replaceStoneBrick(Random rand, int par2, int par3, int par4, boolean tru, CallbackInfo ci){
        this.selectedBlockMetaData = 8; // blackstone brick
    }

}
