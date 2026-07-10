package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.FarmlandBlock;
import btw.block.blocks.FarmlandBlockBase;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends FarmlandBlockBase {
    @Shadow
    protected abstract void setWeedsGrowthLevel(World world, int i, int j, int k, int iGrowthLevel);

    protected FarmlandBlockMixin(int iBlockID) {
        super(iBlockID);
    }

}
