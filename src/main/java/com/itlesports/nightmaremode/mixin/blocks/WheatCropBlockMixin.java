package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.DailyGrowthCropsBlock;
import btw.block.blocks.WheatCropBlock;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WheatCropBlock.class)
public abstract class  WheatCropBlockMixin extends DailyGrowthCropsBlock {
    protected WheatCropBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        ItemStack heldStack = player.getCurrentEquippedItem();
        return heldStack != null && heldStack.getItem() instanceof ItemScythe ? 10.0F : 0.0F;
    }

    @Inject(method = "incrementGrowthLevel", at = @At("HEAD"), cancellable = true)
    private void requireChunkResources(World world, int x, int y, int z, CallbackInfo ci) {
        if (!ChunkAttributeManager.canGrow(world, x, z, (Block)(Object)this)) {
            ci.cancel();
        }
    }

    @Inject(method = "incrementGrowthLevel", at = @At("TAIL"))
    private void consumeFinalBaseStage(World world, int x, int y, int z, CallbackInfo ci) {
        if ((world.getBlockMetadata(x, y, z) & 7) == 7) {
            ChunkAttributeManager.consumeForGrowth(world, x, z, (Block)(Object)this);
        }
    }

    @Redirect(
            method = "incrementGrowthLevel",
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
