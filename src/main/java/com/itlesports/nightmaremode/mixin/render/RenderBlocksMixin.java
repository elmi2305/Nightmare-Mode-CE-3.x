package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.block.blocks.CisternBlock;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderBlocks.class)
public class RenderBlocksMixin {
    @Shadow public IBlockAccess blockAccess;

    @Unique private BlockCauldron nightmareMode$renderingCauldron;
    @Unique private int nightmareMode$cauldronX;
    @Unique private int nightmareMode$cauldronY;
    @Unique private int nightmareMode$cauldronZ;

    @Inject(method = "renderBlockCauldron", at = @At("HEAD"))
    private void captureRenderedCauldron(BlockCauldron block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        this.nightmareMode$renderingCauldron = block;
        this.nightmareMode$cauldronX = x;
        this.nightmareMode$cauldronY = y;
        this.nightmareMode$cauldronZ = z;
    }

    @Inject(method = "renderBlockCauldron", at = @At("RETURN"))
    private void clearRenderedCauldron(BlockCauldron block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        this.nightmareMode$renderingCauldron = null;
    }

    @ModifyArgs(method = "renderBlockCauldron", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V", ordinal = 2))
    private void clearCisternFluidTint(Args args) {
        if (!(this.nightmareMode$renderingCauldron instanceof CisternBlock) || this.blockAccess == null) {
            return;
        }

        TileEntity tile = this.blockAccess.getBlockTileEntity(this.nightmareMode$cauldronX, this.nightmareMode$cauldronY, this.nightmareMode$cauldronZ);
        if (!(tile instanceof CisternTileEntity)) {
            return;
        }

        if (((CisternTileEntity) tile).getFluid() != CisternTileEntity.FLUID_WATER) {
            args.set(0, 1.0F);
            args.set(1, 1.0F);
            args.set(2, 1.0F);
        }
    }

    @Redirect(method = "renderBlockCauldron", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/BlockFluid;getFluidIcon(Ljava/lang/String;)Lnet/minecraft/src/Icon;"))
    private Icon useCustomWaterTex(String texName) {
        Icon originalIcon = BlockFluid.getFluidIcon(texName);
        if (!(this.nightmareMode$renderingCauldron instanceof CisternBlock) || this.blockAccess == null) {
            return originalIcon;
        }

        TileEntity tile = this.blockAccess.getBlockTileEntity(this.nightmareMode$cauldronX, this.nightmareMode$cauldronY, this.nightmareMode$cauldronZ);
        if (!(tile instanceof CisternTileEntity)) {
            return originalIcon;
        }
        int fluid = ((CisternTileEntity) tile).getFluid();
        if(fluid == CisternTileEntity.FLUID_WATER){
            return originalIcon;
        }
        else if (fluid == CisternTileEntity.FLUID_SLURRY){
            return NMFields.ICON_SLURRY != null ? NMFields.ICON_SLURRY : originalIcon;
        }
        else if (fluid == CisternTileEntity.FLUID_BRINE){
            return NMFields.ICON_BRINE != null ? NMFields.ICON_BRINE : originalIcon;
        }
        else if (fluid == CisternTileEntity.FLUID_ACIDIC_WASH){
            return NMFields.ICON_ACID != null ? NMFields.ICON_ACID : originalIcon;
        }
        return originalIcon;
    }
}
