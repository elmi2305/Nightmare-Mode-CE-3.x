package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class EasyNaturalDropsMixin {
    @Unique private final ThreadLocal<Boolean> matureCropDrop = new ThreadLocal<>();

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"))
    private void rememberCropMaturity(World world, int x, int y, int z, int metadata, float chance, int fortune, CallbackInfo ci) {
        Block block = (Block)(Object)this;
        this.matureCropDrop.set(block instanceof CropsBlock crop ? crop.getGrowthLevel(metadata) >= 7
                : block instanceof BlockCrops && metadata >= 7);
    }

    @Inject(method = "dropBlockAsItemWithChance", at = @At("RETURN"))
    private void clearCropMaturity(World world, int x, int y, int z, int metadata, float chance, int fortune, CallbackInfo ci) {
        this.matureCropDrop.remove();
    }

    @ModifyVariable(method = "dropBlockAsItem_do", at = @At("HEAD"), argsOnly = true)
    private ItemStack improveNaturalResources(ItemStack stack, World world, int x, int y, int z, ItemStack original) {
        if (stack == null || world.isRemote || !BalanceProfile.isEasy()) return stack;
        Block block = (Block)(Object)this;
        boolean ore = block == Block.oreCoal || block == Block.oreLapis || block == Block.oreRedstone
                || block == Block.oreRedstoneGlowing || block == Block.oreNetherQuartz;
        boolean crop = Boolean.TRUE.equals(this.matureCropDrop.get());
        boolean cropProduce = stack.getItem() instanceof ItemFood || stack.itemID == Item.wheat.itemID
                || stack.itemID == btw.item.BTWItems.hemp.itemID;
        boolean gravel = block == Block.gravel && stack.itemID == NMItems.flintChip.itemID;
        if (!(ore && !(stack.getItem() instanceof ItemBlock) || crop && cropProduce || gravel)) return stack;
        ItemStack result = stack.copy();
        result.stackSize *= ore || gravel ? 3 : 2;
        return result;
    }
}
