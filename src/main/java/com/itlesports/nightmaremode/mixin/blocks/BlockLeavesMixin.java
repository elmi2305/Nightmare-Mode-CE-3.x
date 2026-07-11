package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.CRIMSON_COLOR;

@Mixin(BlockLeaves.class)
public class BlockLeavesMixin extends BlockLeavesBase {

    public BlockLeavesMixin(int par1, Material par2Material, boolean par3) {
        super(par1, par2Material, par3);
    }

    @Override
    public boolean isBreakableBarricade(World world, int i, int j, int k, boolean advancedBreaker) {
        return false;
    }

    @Inject(method = "idDropped", at= @At("RETURN"),cancellable = true)
    private void allowAppleDrops(int metadata, Random rand, int fortuneModifier, CallbackInfoReturnable<Integer> cir){
        if(rand.nextInt(8) == 0){
            cir.setReturnValue(NMItems.twig.itemID);
        }
        cir.setReturnValue(NMItems.leaf.itemID);
    }

    @Override
    public int tickRate(World w) {
        return 4;
    }
}
