package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.DiamondOreBlock;
import btw.block.blocks.OreBlockStaged;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DiamondOreBlock.class)
public class DiamondOreBlockMixin extends OreBlockStaged {
    public DiamondOreBlockMixin(int iBlockID) {
        super(iBlockID);
    }
    @Inject(method = "idDropped", at = @At("RETURN"), cancellable = true)
    private void dropSoulforgedSteelAfterWither(int iMetadata, Random rand, int iFortuneModifier, CallbackInfoReturnable<Integer> cir){
        if(WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            cir.setReturnValue(BTWItems.soulFlux.itemID);
        }
    }

    @Override
    public int idDroppedOnConversion(int var1) {
        return Item.diamond.itemID;
    }
}
