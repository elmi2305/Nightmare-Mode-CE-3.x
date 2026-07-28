package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.DailyGrowthCropsBlock;
import btw.block.blocks.WheatCropTopBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.item.items.ItemScythe;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WheatCropTopBlock.class)
public abstract class WheatCropTopBlockMixin extends DailyGrowthCropsBlock {
    protected WheatCropTopBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        ItemStack heldStack = player.getCurrentEquippedItem();
        return heldStack != null && heldStack.getItem() instanceof ItemScythe ? 10.0F : 0.0F;
    }


    /** Wheat is harvested with a scythe, so saws cannot collect it. */
    @Inject(method = "doesBlockDropAsItemOnSaw", at = @At("HEAD"), cancellable = true)
    private void preventSawHarvest(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Redirect(
            method = "updateFlagForGrownToday",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Block;getIsFertilizedForPlantGrowth(Lnet/minecraft/src/World;III)Z"
            )
    )
    private boolean onlyUseMatchingFertilizer(
            Block farmland,
            World world,
            int x,
            int y,
            int z
    ) {
        return farmland.getIsFertilizedForPlantGrowth(world, x, y, z)
                && ChunkAttributeManager.hasEffectiveFertilizer(
                        world,
                        x,
                        y,
                        z,
                        (Block)(Object)this
                );
    }
}
