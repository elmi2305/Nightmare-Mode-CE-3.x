package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.itlesports.nightmaremode.util.NMFields.CRIMSON_COLOR;

@Mixin(BlockTallGrass.class)
public class BlockTallGrassMixin extends BlockFlower {
    protected BlockTallGrassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }


    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        if(player instanceof EntityPlayerExt ext){
            if(ext.nightmareMode$doesGrassBreakInstantly()){
                return 10f;
            }
        }
        return 0.1f;
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
    private void redGrass2(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
        }
    }
}
