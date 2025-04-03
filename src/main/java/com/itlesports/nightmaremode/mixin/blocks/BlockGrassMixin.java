package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.BlockGrass;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockGrass.class)
public class BlockGrassMixin extends Block {

    protected BlockGrassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }
//
//    public boolean isFallingBlock() {
//        return NightmareMode.isAprilFools;
//    }
//
//
//    public void onBlockAdded(World world, int i, int j, int k) {
//        if (NightmareMode.isAprilFools) {
//            this.scheduleCheckForFall(world, i, j, k);
//        }
//    }
//
//    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
//        if (NightmareMode.isAprilFools) {
//            this.scheduleCheckForFall(world, i, j, k);
//        }
//    }
//
//    @Inject(method = "updateTick", at = @At("TAIL"))
//    private void setGravity(World world, int x, int y, int z, Random rand, CallbackInfo ci){
//        if(NightmareMode.isAprilFools){
//            this.scheduleCheckForFall(world,x,y,z);
//        }
//    }
//
//    public int tickRate(World par1World) {
//        if (NightmareMode.isAprilFools) {
//            return 2;
//        }
//        return super.tickRate(par1World);
//    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getBlockColor", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass0(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(14163743);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getRenderColor", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass1(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(14163743);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "colorMultiplier", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass2(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(14163743);
        }
    }
}
