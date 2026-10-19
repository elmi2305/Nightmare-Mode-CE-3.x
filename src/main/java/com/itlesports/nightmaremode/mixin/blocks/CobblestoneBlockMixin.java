package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.CobblestoneBlock;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CobblestoneBlock.class)
public class CobblestoneBlockMixin extends Block {
    protected CobblestoneBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true)
    private void dropStoneOnBreak(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        // only hand mining loses the mortar; rock items do not use cobblestone strata metadata.
        if (!world.isRemote) this.dropBlockAsItem_do(world, x, y, z, new ItemStack(BTWItems.stone));
        ci.cancel();
    }

    @Override
    public boolean canEndermenPickUpBlock(World world, int x, int y, int z) {
        return true;
    }
}
