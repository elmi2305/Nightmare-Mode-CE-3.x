package com.itlesports.nightmaremode.mixin.render;

import btw.block.blocks.WaterBlockFlowing;
import btw.block.blocks.WaterBlockStationary;
import com.itlesports.nightmaremode.NMUtils;
import com.prupe.mcpatcher.ctm.CTMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixin {
    @Shadow public abstract Icon getIconSafe(Icon par1Icon);

//    @Redirect(method = "renderBlockFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderBlocks;getBlockIcon(Lnet/minecraft/src/Block;Lnet/minecraft/src/IBlockAccess;IIII)Lnet/minecraft/src/Icon;"))
//    private Icon manageWaterIcon(RenderBlocks instance, Block par1Block, IBlockAccess par2IBlockAccess, int par3, int par4, int par5, int par6){
//        if(par1Block instanceof WaterBlockFlowing  || par1Block instanceof WaterBlockStationary){
//            return CTMUtils.getBlockIcon(this.getIconSafe(par1Block.getBlockTexture(par2IBlockAccess, par3, par4, par5, par6)), instance, par1Block, par2IBlockAccess, par3, par4, par5, par6);
//        }
//
//        // failsafe
//        return (instance.getBlockIcon(par1Block,par2IBlockAccess,par3,par4,par5,par6));
//    }
//
//
//    @Inject(method = "renderBlockFluids", at = @At("HEAD"))
//    private void incrementWaterFadeOnBloodMoon(Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
//        if (NMUtils.getIsBloodMoon()) {
//            waterFade ++;
//        }
//    }
//    private static int waterFade = 0;
//
//    @Redirect(
//            method = "renderBlockFluids",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V"
//            )
//    )
//    private void redirectTessellatorSetColorOpaque_F(Tessellator tess, float r, float g, float b) {
//        if (NMUtils.getIsBloodMoon()) {
//            float fade = (float) waterFade / 1000;
//
//            float newR = r * (1.0f - fade) + 1.0f * fade;
//            float newG = g * (1.0f - fade) + 0.0f * fade;
//            float newB = b * (1.0f - fade) + 0.0f * fade;
//
//            tess.setColorOpaque_F(newR, newG, newB);
//        } else {
//            tess.setColorOpaque_F(r, g, b);
//        }
//    }
}
