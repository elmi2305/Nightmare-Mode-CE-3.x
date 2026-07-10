package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import api.block.blocks.DailyGrowthCropsBlock;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DailyGrowthCropsBlock.class)
public abstract class DailyGrowthCropsBlockMixin extends CropsBlock {
    protected DailyGrowthCropsBlockMixin(int iBlockID) {
        super(iBlockID);
    }


}
