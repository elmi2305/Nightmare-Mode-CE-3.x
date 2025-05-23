package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.BlockFlower;
import net.minecraft.src.BlockMushroom;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockMushroom.class)
public class BlockMushroomMixin extends BlockFlower {
    @Unique private World worldObj;
    protected BlockMushroomMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    @Inject(method = "updateTick", at = @At("HEAD"))
    private void declareWorld(World world, int x, int y, int z, Random rand, CallbackInfo ci){
        this.worldObj = world;
    }

    @ModifyArg(method = "updateTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 1))
    private int increaseChanceToSpread(int bound){
        return 7;
    }
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 4,ordinal = 0))
    private int decreaseMushroomDetectionRange(int constant){
        if(this.worldObj != null && this.worldObj.worldInfo != null && this.worldObj.worldInfo.dimension == 0){
            return 2;
        }
        return constant ;
    }
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 4,ordinal = 1))
    private int increaseAttemptsToSpread(int constant){
        return 6;
    }
}
