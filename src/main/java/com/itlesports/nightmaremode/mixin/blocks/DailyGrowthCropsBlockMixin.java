package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import api.block.blocks.DailyGrowthCropsBlock;
import com.itlesports.nightmaremode.util.NMEvents;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DailyGrowthCropsBlock.class)
public abstract class DailyGrowthCropsBlockMixin extends CropsBlock {
    protected DailyGrowthCropsBlockMixin(int iBlockID) {
        super(iBlockID);
    }
    @Inject(method = "attemptToGrow", at = @At(value = "INVOKE", target = "Lapi/block/blocks/DailyGrowthCropsBlock;incrementGrowthLevel(Lnet/minecraft/src/World;III)V"))
    private void increaseGrowth(World world, int x, int y, int z, Random rand, CallbackInfo ci)
    {
        if(rand.nextBoolean() && !this.isFullyGrown(world, x, y, z)){
            this.incrementGrowthLevel(world,x,y,z);
        }
    }
    @Redirect(method = "attemptToGrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;getWorldTime()J"))
    private long a(WorldInfo instance)
    {
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive()){
            return 5000;
        }
        return instance.getWorldTime();
    }
    @Inject(method = "getBaseGrowthChance", at = @At("HEAD"),cancellable = true)
    private void growDuringHarvest(World world, int i, int j, int k, CallbackInfoReturnable<Float> cir){
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive()){
            cir.setReturnValue(1f);
        }
    }
    @Inject(method = "getHasGrownToday(I)Z", at = @At("HEAD"),cancellable = true,remap = false)
    private void canGrowDuringHarvest(int iMetadata, CallbackInfoReturnable<Boolean> cir)
    {
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getHasGrownToday(Lnet/minecraft/src/IBlockAccess;III)Z", at = @At("HEAD"),cancellable = true)
    private void canGrowDuringHarvest(IBlockAccess blockAccess, int i, int j, int k, CallbackInfoReturnable<Boolean> cir)
    {
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive()){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "setHasGrownToday(IZ)I", at = @At("HEAD"), cancellable = true,remap = false)
    private void b(int iMetadata, boolean bHasGrown, CallbackInfoReturnable<Integer> cir)
    {
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive()){
            cir.cancel();
        }
    }

    @Inject(method = "setHasGrownToday(Lnet/minecraft/src/World;IIIZ)V", at = @At("HEAD"), cancellable = true)
    private void b(World world, int i, int j, int k, boolean bHasGrown, CallbackInfo ci)
    {
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive()){
            ci.cancel();
        }
    }
    @Inject(method = "attemptToGrow",at = @At("HEAD"))
    private void noWeeds(World world, int x, int y, int z, Random rand, CallbackInfo ci)
    {
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive() && this.getWeedsGrowthLevel(world, x,y,z) > 0) {
            this.removeWeeds(world, x, y, z);
        }
    }

}
