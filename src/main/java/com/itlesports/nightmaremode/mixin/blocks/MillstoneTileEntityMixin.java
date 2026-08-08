package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MillstoneBlock;
import btw.block.tileentity.MillstoneTileEntity;
import btw.crafting.manager.MillStoneCraftingManager;
import com.itlesports.nightmaremode.skill.SkillLockedBulkCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Potion;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MillstoneTileEntity.class)
public abstract class MillstoneTileEntityMixin extends TileEntity {


    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 200))
    private int fasterMillstones(int constant){
        return constant * 8;
    }

    @Redirect(
            method = {"grindContents", "validateContentsForGrinding"},
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/crafting/manager/MillStoneCraftingManager;hasRecipeForSingleIngredient(Lnet/minecraft/src/ItemStack;)Z"))
    private boolean filterLockedMillstoneRecipe(MillStoneCraftingManager manager, ItemStack input) {
        return SkillLockedBulkCrafting.hasSingleIngredientRecipe(manager, input, this.worldObj);
    }

    @Redirect(
            method = "grindContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/crafting/manager/MillStoneCraftingManager;getCraftingResult(Lnet/minecraft/src/ItemStack;)Ljava/util/List;"))
    private List<ItemStack> filterLockedMillstoneResult(MillStoneCraftingManager manager, ItemStack input) {
        return SkillLockedBulkCrafting.getSingleIngredientResult(manager, input, this.worldObj);
    }
}
