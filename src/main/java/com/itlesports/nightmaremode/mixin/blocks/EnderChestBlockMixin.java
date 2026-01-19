package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.EnderChestBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;


@Mixin(EnderChestBlock.class)
public class EnderChestBlockMixin extends BlockEnderChest {
    protected EnderChestBlockMixin(int i) {
        super(i);
    }

    @Inject(method = "computeLevelOfEnderChestsAntenna", at = @At("RETURN"),cancellable = true)
    private void enderChestsAlwaysActive(World world, int i, int j, int k, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(4);
        // makes ender chests behave like in vanilla, meaning an ender beacon isn't required
    }
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSoundEffect(DDDLjava/lang/String;FF)V"))
    private void doNotPlayLoudSound(World world, double x, double y, double z, String sound, float vol, float pitch){}

    @Override
    public int idDropped(int i, Random random, int j) {
        return Block.enderChest.blockID;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 2;
    }
}
