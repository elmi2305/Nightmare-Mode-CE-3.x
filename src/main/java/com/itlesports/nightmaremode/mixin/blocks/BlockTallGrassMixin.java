package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.item.NMItems;
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

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"))
    private void dropPlantFiber(World world, int x, int y, int z, int metadata, float chance, int fortuneModifier, CallbackInfo ci) {
        if (!world.isRemote && world.rand.nextFloat() <= 0.08f) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.plantFiber));
        }
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
