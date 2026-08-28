package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.BlockGrass;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import java.util.Random;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import com.itlesports.nightmaremode.network.PollutionVisualNet;
import net.minecraft.src.IBlockAccess;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.itlesports.nightmaremode.util.NMFields.CRIMSON_COLOR;

@Mixin(BlockGrass.class)
public class BlockGrassMixin extends Block {

    protected BlockGrassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getBlockColor", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass0(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getRenderColor", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass1(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "colorMultiplier", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass2(IBlockAccess blockAccess, int x, int y, int z, CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
            return;
        }
        cir.setReturnValue(PollutionVisualNet.tintColor(cir.getReturnValue(), x, z));
    }

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void decayPollutedGrass(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        float pollution = ChunkPollutionManager.get(world, x, z);
        if (pollution < ChunkPollutionManager.PASSIVE_SPREAD_THRESHOLD) return;
        if (!world.isRemote && random.nextFloat() < ChunkPollutionManager.getVegetationDecayChance(pollution)) {
            BlockGrass grass = (BlockGrass)(Object)this;
            if (grass.isSparse(world, x, y, z)) {
                world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
            } else {
                grass.setSparse(world, x, y, z);
            }
        }
        ci.cancel();
    }
}
