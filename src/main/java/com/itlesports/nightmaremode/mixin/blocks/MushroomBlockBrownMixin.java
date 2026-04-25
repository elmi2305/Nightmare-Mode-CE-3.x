package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MushroomBlockBrown;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(MushroomBlockBrown.class)
public class MushroomBlockBrownMixin {
    @ModifyArg(method = "checkForSpread", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 0))
    private int increaseChanceOfMushroomSpreading(int bound){
        return 7;
    }
    @ModifyConstant(method = "checkForSpread", constant = @Constant(intValue = 4,ordinal = 0))
    private int decreaseMushroomDetectionRange(int constant){
        return 2;
    }
    @ModifyConstant(method = "checkForSpread", constant = @Constant(intValue = 4,ordinal = 1))
    private int increaseAttemptsToSpread(int constant){
        return 6;
    }
//    @Inject(method = "updateTick", at = @At("HEAD"))
//    private  void  a(World world, int x, int y, int z, Random rand, CallbackInfo ci){
//        System.out.println("hi " + x + " " + y + " " + z);
//    }
//    @Redirect(method = "canBlockStayDuringGenerate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/WorldProvider;dimensionId:I", opcode = Opcodes.GETFIELD))
//    private int growBrownMushroomsInNether(WorldProvider instance){
//        return 0; // grows in all dims where the provider generates them
//    }
    @Inject(method = "canBlockStayDuringGenerate", at = @At("HEAD"),cancellable = true)
    private void netherGrowth(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(world.provider.dimensionId == -1 && j > 24 && j < 127 && (world.getBlockId(i, j - 1, k)) != 0){
            cir.setReturnValue(true);
        }
    }
}
