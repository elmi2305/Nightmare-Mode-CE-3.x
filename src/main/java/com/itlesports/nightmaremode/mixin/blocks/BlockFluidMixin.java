package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockFluid.class)
public class BlockFluidMixin extends Block{
    protected BlockFluidMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    @ModifyArg(method = "registerIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/IconRegister;registerIcon(Ljava/lang/String;)Lnet/minecraft/src/Icon;",ordinal = 2))
    private String redWater1(String var1){
        if (NightmareMode.bloodmare || NightmareMode.crimson) {
            return "nightmare_water_flow";
        }
        return var1;
    }
    @ModifyArg(method = "registerIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/IconRegister;registerIcon(Ljava/lang/String;)Lnet/minecraft/src/Icon;",ordinal = 3))
    private String redWater2(String var1){
        if (NightmareMode.bloodmare || NightmareMode.crimson) {
            return "nightmare_water_still";
        }
        return var1;
    }
}
