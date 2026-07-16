package com.itlesports.nightmaremode.mixin.blocks;

import api.world.WorldUtils;
import btw.block.blocks.AnvilBlock;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.util.HammerAnvilHelper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin extends Block implements ITileEntityProvider {

    protected AnvilBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityHammerAnvil();
    }

    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void vanillaAnvilFunctionality(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick, CallbackInfoReturnable<Boolean> cir){
        if (!world.isRemote && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j + 1, k, 0)) {
            TileEntity tile = world.getBlockTileEntity(i, j, k);
            if (!(tile instanceof TileEntityHammerAnvil)) {
                tile = new TileEntityHammerAnvil();
                world.setBlockTileEntity(i, j, k, tile);
            }
            TileEntityHammerAnvil anvil = tile instanceof TileEntityHammerAnvil ? (TileEntityHammerAnvil)tile : null;
            HammerAnvilHelper.tryHammerHeldItem(world, i, j, k, player, anvil);
        }
        cir.setReturnValue(true);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        if(EntityBloodWither.isBossActive()) return 0;
        return super.quantityDropped(par1Random);
    }
}
